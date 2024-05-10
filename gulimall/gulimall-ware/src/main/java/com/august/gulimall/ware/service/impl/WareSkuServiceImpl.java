package com.august.gulimall.ware.service.impl;

import com.august.gulimall.common.exception.RenException;
import com.august.gulimall.common.to.SkuHasStore;
import com.august.gulimall.common.to.mq.OrderTO;
import com.august.gulimall.common.to.mq.StockDetailTO;
import com.august.gulimall.common.to.mq.StockLockedTO;
import com.august.gulimall.common.utils.Result;
import com.august.gulimall.ware.constant.WareConstant;
import com.august.gulimall.ware.dao.WareOrderTaskDetailDao;
import com.august.gulimall.ware.dto.WareOrderTaskDTO;
import com.august.gulimall.ware.dto.WareOrderTaskDetailDTO;
import com.august.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.august.gulimall.ware.entity.WareOrderTaskEntity;
import com.august.gulimall.ware.feign.OrderFeignService;
import com.august.gulimall.ware.feign.ProductFeignService;
import com.august.gulimall.ware.service.WareOrderTaskDetailService;
import com.august.gulimall.ware.service.WareOrderTaskService;
import com.august.gulimall.ware.vo.OrderItemVO;
import com.august.gulimall.ware.vo.OrderVO;
import com.august.gulimall.ware.vo.SkuWareHasStockVO;
import com.august.gulimall.ware.vo.WareSkuLockVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import com.august.gulimall.ware.dao.WareSkuDao;
import com.august.gulimall.ware.dto.WareSkuDTO;
import com.august.gulimall.ware.entity.WareSkuEntity;
import com.august.gulimall.ware.service.WareSkuService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品库存
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Service
public class WareSkuServiceImpl extends CrudServiceImpl<WareSkuDao, WareSkuEntity, WareSkuDTO> implements WareSkuService {


    @Resource
    WareSkuDao wareSkuDao;
    @Resource
    ProductFeignService productFeignService;

    @Resource
    WareOrderTaskService wareOrderTaskService;

    @Resource
    private WareOrderTaskDetailService wareOrderTaskDetailService;

    @Resource
    private WareOrderTaskDetailDao wareOrderTaskDetailDao;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    OrderFeignService orderFeignService;

    @Override
    public QueryWrapper<WareSkuEntity> getWrapper(Map<String, Object> params) {
        //wareId: 3
        //skuId: 11
        String wareId = (String) params.get("wareId");
        String skuId = (String) params.get("skuId");
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(skuId), "sku_id", skuId);
        wrapper.eq(StringUtils.isNotBlank(wareId), "ware_id", wareId);
        return wrapper;
    }


    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1、判断如果还没有这个库存记录新增
        List<WareSkuEntity> entities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (entities == null || entities.size() == 0) {
            WareSkuEntity skuEntity = new WareSkuEntity();
            skuEntity.setSkuId(skuId);
            skuEntity.setStock(skuNum);
            skuEntity.setWareId(wareId);
            skuEntity.setStockLocked(0);
            //TODO 远程查询sku的名字，如果失败，整个事务无需回滚
            //1、自己catch异常
            //TODO 还可以用什么办法让异常出现以后不回滚？高级
            try {
                Result info = productFeignService.info(skuId);
                if (info.getCode() == 200) {
                    Map<String, Object> skuInfo = (Map<String, Object>) info.getData();
                    skuEntity.setSkuName((String) skuInfo.get("skuName"));
                }
            } catch (Exception e) {
            }
            wareSkuDao.insert(skuEntity);
        } else {
            //2、如果有这个库存记录，修改库存信息
            UpdateWrapper<WareSkuEntity> wareSkuEntityUpdateWrapper = new UpdateWrapper<>();
            //加库存 skuNum + 原来的库存
            wareSkuEntityUpdateWrapper.eq("sku_id", skuId).eq("ware_id", wareId).setSql("stock = stock + " + skuNum);
            wareSkuDao.update(null, wareSkuEntityUpdateWrapper);
        }
    }

    @Override
    public List<SkuHasStore> hasStock(List<Long> skuIds) {
        //stock - stock_locked
        //SELECT SUM(stock - stock_locked) FROM wms_ware_sku WHERE sku_id IN (4,1) GROUP BY sku_id
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        wrapper.in("sku_id", skuIds);
        wrapper.groupBy("sku_id");
        wrapper.select("sku_id", "sum(stock - stock_locked) as stock");
        List<WareSkuEntity> entities = baseDao.selectList(wrapper);
        List<SkuHasStore> collect = entities.stream().map(item -> {
            SkuHasStore skuHasStore = new SkuHasStore();
            skuHasStore.setSkuId(item.getSkuId());
            skuHasStore.setStore(item.getStock());
            return skuHasStore;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 为订单锁定库存
     *
     * @param vo
     * @return
     */
    @Transactional
    @Override
    public Boolean orderLockStock(WareSkuLockVO vo) {
        // 保存库存工作单，为了追溯
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSn());
        wareOrderTaskService.insert(taskEntity);
        List<OrderItemVO> locks = vo.getLocks();

        // 1.找到每个商品在那个仓库有库存
        List<SkuWareHasStockVO> collect = locks.stream().map(item -> {
            SkuWareHasStockVO stock = new SkuWareHasStockVO();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            //TODO: 查询这个商品在哪里有库存
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(skuId);
            stock.setWareId(wareIds);
            return stock;
        }).collect(Collectors.toList());

        // 2.锁定库存
        for (SkuWareHasStockVO hasStock : collect) {
            boolean skuStocked = false;
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();
            if (wareIds == null || wareIds.size() == 0) {
                // 没有仓库有这个商品
                throw new RenException(Math.toIntExact(skuId), "没有仓库有这个商品");
            }

            for (Long wareId : wareIds) {
                // 锁库存，如果成功就返回1，失败返回0
                Long count = wareSkuDao.lockStock(skuId, wareId, hasStock.getNum());
                if (count > 0) {
                    skuStocked = true;
                    WareOrderTaskDetailDTO entiey = new WareOrderTaskDetailDTO(null,
                            skuId, "", hasStock.getNum(),
                            taskEntity.getId(), wareId, 1);
                    wareOrderTaskDetailService.save(entiey);
                    // TODO 告诉mq锁成功
                    StockLockedTO lockedTo = new StockLockedTO();
                    lockedTo.setId(taskEntity.getId());
                    StockDetailTO detailTo = new StockDetailTO();
                    BeanUtils.copyProperties(entiey, detailTo);
                    lockedTo.setDetailTo(detailTo);
                    //发送到库存延迟队列
                    rabbitTemplate.convertAndSend(WareConstant.MQ_STOCK_EXCHANGE, WareConstant.MQ_STOCK_LOCKED_DELAY_ROUTING_KEY, lockedTo);
                    break;
                } else {
                    //当前仓库锁失败，重试下一个仓库
                }
            }
            if (!skuStocked) {
                // 当前商品所有仓库都没库存
                throw new RenException(Math.toIntExact(skuId), "商品库存不足");
            }
        }
        // 3.全部锁定成功
        return true;
    }

    /**
     * 1、查询数据库关于这个订单锁定库存信息
     * 没有：无需解锁，因为自己库存锁定失败，自动都回滚了
     * 有：证明库存锁定成功了
     * 解锁：订单状况order表
     * 1、没有这个订单说明下订单服务调的其他服务失败，导致订单回滚但库存服务没回滚，
     * 因为不用分布式事务，所以必须解锁库存
     * 2、有这个订单，不一定解锁库存
     * 订单状态：已取消：解锁库存
     * 已支付：无需解锁库存
     */
    @Override
    public void unlockStock(StockLockedTO to) {
        //库存工作单的id
        StockDetailTO detail = to.getDetailTo();
        Long detailId = detail.getId();
        WareOrderTaskDetailEntity taskDetailInfo = wareOrderTaskDetailService.selectById(detailId);
        if (taskDetailInfo != null) {
            Long id = to.getId();
            WareOrderTaskEntity wareOrderTask = wareOrderTaskService.selectById(id);
            // 得到订单号，然后根据订单号查状态
            String orderSn = wareOrderTask.getOrderSn();
            Result<OrderVO> r = orderFeignService.getOrderStatus(orderSn);
            if (r.getCode() == 200) {
                OrderVO data = r.getData();
                // 4代表已关闭
                if (data == null || data.getStatus() == 4) {
                    // 订单不存在或者状态为已关闭，调用unlockStock方法解锁库存
                    // 必须是已锁定未解锁状态
                    if (taskDetailInfo.getLockStatus() == 1) {
                        unLockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum(), detailId);
                    }
                }
            } else {
                throw new RuntimeException("远程服务失败");
            }
        } else {
            // 无需解锁
        }
    }

    // 防止订单服务卡顿，导致订单状态改不了，库存消息优先到期，查订单状态新建状态，什么都不做就走了
    // 防止卡顿的取消订单，解锁不了库存
    @Transactional
    @Override
    public void unlockStock(OrderTO to) {
        String orderSn = to.getOrderSn();
        //查一下最新状态，防止重复解锁库存
        WareOrderTaskEntity task = wareOrderTaskService.getOrderTaskByOrderSn(orderSn);
        Long id = task.getId();
        // 按照工作单找到所有没有解锁的库存，进行解锁
        List<WareOrderTaskDetailEntity> entitys =
                wareOrderTaskDetailDao.selectList(new QueryWrapper<WareOrderTaskDetailEntity>()
                        .eq("task_id", id).eq("lock_status", 1));
        for (WareOrderTaskDetailEntity entity : entitys) {
            unLockStock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum(), entity.getId());
        }
    }

    private void unLockStock(Long skuId, Long wareId, Integer skuNum, Long detailId) {
        // 库存解锁
        wareSkuDao.unlockStock(skuId, wareId, skuNum);
        // 更新库存工作单状态
        WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
        entity.setId(detailId);
        // 变为已解锁状态
        entity.setLockStatus(2);
        wareOrderTaskDetailService.updateById(entity);
    }
}