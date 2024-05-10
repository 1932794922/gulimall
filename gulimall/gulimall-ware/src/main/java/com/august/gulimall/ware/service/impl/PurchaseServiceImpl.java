package com.august.gulimall.ware.service.impl;

import com.august.gulimall.common.constant.WareConstant;
import com.august.gulimall.common.exception.RenException;
import com.august.gulimall.common.page.PageData;
import com.august.gulimall.common.utils.ConvertUtils;
import com.august.gulimall.ware.dao.PurchaseDetailDao;
import com.august.gulimall.ware.entity.PurchaseDetailEntity;
import com.august.gulimall.ware.service.PurchaseDetailService;
import com.august.gulimall.ware.service.WareSkuService;
import com.august.gulimall.ware.vo.MergeVO;
import com.august.gulimall.ware.vo.PurchaseDoneVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import com.august.gulimall.ware.dao.PurchaseDao;
import com.august.gulimall.ware.dto.PurchaseDTO;
import com.august.gulimall.ware.entity.PurchaseEntity;
import com.august.gulimall.ware.service.PurchaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 采购信息
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Service
public class PurchaseServiceImpl extends CrudServiceImpl<PurchaseDao, PurchaseEntity, PurchaseDTO> implements PurchaseService {


    @Resource
    PurchaseDetailService purchaseDetailService;

    @Resource
    PurchaseDetailDao purchaseDetailDao;

    @Resource
    WareSkuService wareSkuService;

    @Override
    public QueryWrapper<PurchaseEntity> getWrapper(Map<String, Object> params) {
        String id = (String) params.get("id");

        QueryWrapper<PurchaseEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public PageData<PurchaseDTO> unReceiveList() {
        LambdaQueryWrapper<PurchaseEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseEntity::getStatus, 0).or().eq(PurchaseEntity::getStatus, 1);
        List<PurchaseEntity> purchaseEntities = this.baseDao.selectList(wrapper);
        //TODO: 分页
        IPage<PurchaseDTO> pageDTO = new PageDTO<>();
        pageDTO.setRecords(ConvertUtils.sourceToTarget(purchaseEntities, currentDtoClass()));
        pageDTO.setTotal(purchaseEntities.size());
        BeanUtils.copyProperties(purchaseEntities, pageDTO);
        return getPageData(pageDTO, currentDtoClass());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void mergePurchase(MergeVO mergeVO) {

        Long purchaseId = mergeVO.getPurchaseId();
        ;
        if (purchaseId == null) {
            //1、新建一个
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date(System.currentTimeMillis()));
            purchaseEntity.setUpdateTime(new Date(System.currentTimeMillis()));
            this.insert(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        List<Long> items = Arrays.asList(mergeVO.getItems());
        //TODO 确认采购单状态是0,1才可以合并
        LambdaQueryWrapper<PurchaseDetailEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseDetailEntity::getStatus, items);
        List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailDao.selectList(wrapper);
        // 确定是0或者1的采购单
        if (purchaseDetailEntities.size() == 0) {
            throw new RenException("采购单状态不正确");
        }
        //判断是不是都是0 或者 1状态,如果不是抛出异常
        purchaseDetailEntities.forEach(item -> {
            int status = item.getStatus();
            if (status != WareConstant.PurchaseDetailStatusEnum.CREATED.getCode()
                    && status != WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode()) {
                throw new RenException("采购单状态不正确");
            }
        });

        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream().map(i -> {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setId(i);
            detailEntity.setPurchaseId(finalPurchaseId);
            detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return detailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date(System.currentTimeMillis()));
        this.updateById(purchaseEntity);
    }

    /**
     * @param ids 采购单id
     */
    @Override
    public void received(List<Long> ids) {
        //1、确认当前采购单是新建或者已分配状态
        List<PurchaseEntity> purchaseEntities = baseDao.selectList(new LambdaQueryWrapper<PurchaseEntity>().in(PurchaseEntity::getId, ids));
        List<PurchaseEntity> collect = purchaseEntities.stream().filter(item -> {
            return item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                    item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode();
        }).map(item -> {
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            item.setUpdateTime(new Date(System.currentTimeMillis()));
            return item;
        }).collect(Collectors.toList());
        //2、改变采购单的状态
        this.updateBatchById(collect);
        //3、改变采购项的状态
        collect.forEach((item) -> {
            List<PurchaseDetailEntity> entities = purchaseDetailService.listDetailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> detailEntities = entities.stream().map(entity -> {
                PurchaseDetailEntity entity1 = new PurchaseDetailEntity();
                entity1.setId(entity.getId());
                entity1.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return entity1;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(detailEntities);
        });
    }

    @Override
    public void done(PurchaseDoneVO doneVo) {
        Long id = doneVo.getId();
        //2、改变采购项的状态
        boolean flag = true;
        List<PurchaseDoneVO.PurchaseItemDoneVO> items = doneVo.getItems();
        //过滤掉只处理 正在采购 和 采购失败的
        List<PurchaseDoneVO.PurchaseItemDoneVO> collect = items.stream().filter(item -> {
            //查询采购项的状态
            PurchaseDetailEntity entity = purchaseDetailDao.selectById(item.getItemId());
            if(entity == null){
                return false;
            }
            return entity.getStatus() == WareConstant.PurchaseDetailStatusEnum.BUYING.getCode() ||
                    entity.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode();
        }).collect(Collectors.toList());

        List<PurchaseDetailEntity> updates = new ArrayList<>();
        for (PurchaseDoneVO.PurchaseItemDoneVO item : collect) {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            if(item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()){
                flag = false;
                detailEntity.setStatus(item.getStatus());
            }else{
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                //3、将成功采购的进行入库
                PurchaseDetailEntity entity = purchaseDetailDao.selectById(item.getItemId());
                wareSkuService.addStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum());
            }
            detailEntity.setId(item.getItemId());
            updates.add(detailEntity);
        }
        purchaseDetailService.updateBatchById(updates);
        //1、改变采购单状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag?WareConstant.PurchaseStatusEnum.FINISH.getCode():WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        purchaseEntity.setUpdateTime(new Date(System.currentTimeMillis()));
        this.updateById(purchaseEntity);

    }
}