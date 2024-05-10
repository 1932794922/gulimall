package com.august.gulimall.order.service.impl;

import com.august.gulimall.common.exception.RenException;
import com.august.gulimall.common.to.MemberTO;
import com.august.gulimall.common.to.mq.OrderTO;
import com.august.gulimall.common.utils.Result;
import com.august.gulimall.order.constant.OrderConstant;
import com.august.gulimall.order.entity.OrderItemEntity;
import com.august.gulimall.order.enume.OrderStatusEnum;
import com.august.gulimall.order.feign.CartFeignService;
import com.august.gulimall.order.feign.MemberFeignService;
import com.august.gulimall.order.feign.ProductFeignService;
import com.august.gulimall.order.feign.WmsFeignService;
import com.august.gulimall.order.interceptor.LoginUserInterceptor;
import com.august.gulimall.order.service.OrderItemService;
import com.august.gulimall.order.to.OrderCreateTo;
import com.august.gulimall.order.to.SpuInfoTO;
import com.august.gulimall.order.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import com.august.gulimall.order.dao.OrderDao;
import com.august.gulimall.order.dto.OrderDTO;
import com.august.gulimall.order.entity.OrderEntity;
import com.august.gulimall.order.service.OrderService;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 订单
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@Service
public class OrderServiceImpl extends CrudServiceImpl<OrderDao, OrderEntity, OrderDTO> implements OrderService {

    private ThreadLocal<OrderSubmitVO> confirmVoThreadLocal = new ThreadLocal<>();
    @Resource
    MemberFeignService memberFeignService;
    @Resource
    CartFeignService cartFeignService;
    @Resource
    WmsFeignService wmsFeignService;


    @Resource
    private ThreadPoolExecutor executor;

    @Resource
    //Bean named 'redisTemplate' is expected to be of type 'org.springframework.data.redis.core.StringRedisTemplate'
    // but was actually of type 'org.springframework.data.redis.core.RedisTemplate'
    //private StringRedisTemplate redisTemplate;
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private OrderItemService orderItemService;

    @Resource
    ProductFeignService productFeignService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public QueryWrapper<OrderEntity> getWrapper(Map<String, Object> params) {
        String id = (String) params.get("id");

        QueryWrapper<OrderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    @Transactional
    public OrderConfirmVO confirmOrder() {
        OrderConfirmVO orderConfirmVo = new OrderConfirmVO();

        // 从threadLocal获得用户
        MemberTO memberRespVo = LoginUserInterceptor.loginUser.get();

        // 获取当前线程请求头信息(解决Feign异步调用丢失请求头问题)
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        //开启第一个异步任务
        CompletableFuture<Void> AddressFuture = CompletableFuture.runAsync(() -> {
            //每一个线程都来共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);

            // 1.远程查询根据用户id获得地址信息
            Result<List<MemberAddressVO>> result = memberFeignService.getAddressById(memberRespVo.getId());
            if (result.getCode() == 200) {
                orderConfirmVo.setMemberAddressVos(result.getData());
            }
        }, executor);

        //开启第二个异步任务
        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            //每一个线程都来共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);

            // 2.远程查询购物车选中的购物项
            Result<List<OrderItemVO>> result = cartFeignService.getCurrentUserCartItems();
            List<OrderItemVO> data = result.getData();
            if (data != null) {
                orderConfirmVo.setItems(data);
            }
        }, executor).thenRunAsync(() -> {
            List<OrderItemVO> items = orderConfirmVo.getItems();
            if (items == null) {
                return;
            }
            List<Long> collect = items.stream().map(OrderItemVO::getSkuId).collect(Collectors.toList());
            // 远程查是否有库存
            Result<List<SkuStockVO>> skuHasStock = wmsFeignService.getSkuHasStock(collect);
            List<SkuStockVO> data = skuHasStock.getData();
            if (data != null) {
                Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuStockVO::getSkuId,
                        v -> v.getStore() > 0));
                orderConfirmVo.setStocks(map);
            }
        }, executor);


        // 3.查询用户积分
        Integer integration = memberRespVo.getIntegration();
        orderConfirmVo.setIntegration(integration);
        // 4.其他数据自动计算
        // 5.TODO：仿重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().
                set(OrderConstant.USER_ORDER_TOKEN_PREFIX +
                                memberRespVo.getId(), token, 30,
                        TimeUnit.MINUTES);
        orderConfirmVo.setOrderToken(token);
        CompletableFuture.allOf(AddressFuture, cartFuture).join();
        return orderConfirmVo;
    }

    //@GlobalTransactional // 分布式事务seata, 为了保证高并发，不推荐使用seata
    @Override
    @Transactional
    public SubmitOrderResponseVO submitOrder(OrderSubmitVO vo) {
        SubmitOrderResponseVO responseVo = new SubmitOrderResponseVO();
        //获取当前用户登录的信息
        MemberTO memberRespVo = LoginUserInterceptor.loginUser.get();
        // 用threadLocal，可以不用老是传参数
        confirmVoThreadLocal.set(vo);
        // 使用lua脚本进行删除，保证原子性
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = vo.getOrderToken(); //保证接口幂等性
        //通过lua脚本原子验证令牌和删除令牌，0代表失败，1代表成功
        Long result = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId()), orderToken);
        // 验证成功
        if (result == 1) {
            // 创建订单
            OrderCreateTo order = createOrder();
            //2、验证价格
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                // 3.保存订单
                saveOrder(order);
                // 4.库存锁定
                WareSkuLockVO lockVo = new WareSkuLockVO();
                lockVo.setOrderSn(order.getOrder().getOrderSn());
                List<OrderItemVO> locks = order.getOrderItems().stream().map((item) -> {
                    OrderItemVO itemVo = new OrderItemVO();
                    itemVo.setSkuId(item.getSkuId());
                    itemVo.setCount(item.getSkuQuantity());
                    itemVo.setTitle(item.getSkuName());
                    return itemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(locks);
                // 远程调用锁库存
                //TODO 调用远程锁定库存的方法
                //出现的问题：扣减库存成功了，但是由于网络原因超时，出现异常，导致订单事务回滚，库存事务不回滚(解决方案：seata)
                //为了保证高并发，不推荐使用seata，因为是加锁，并行化，提升不了效率,可以发消息给库存服务
                Result r = wmsFeignService.orderLockStock(lockVo);
                if (r.getCode() == 200) {
                    // 锁成功
                    responseVo.setCode(200);
                    responseVo.setOrder(order.getOrder());
                    // TODO 订单创建完成发送消息给mq
                    rabbitTemplate.convertAndSend(OrderConstant.MQ_ORDER_EXCHANGE, OrderConstant.MQ_ORDER_CREATE_DELAY_ROUTING_KEY, order.getOrder());
                    return responseVo;
                } else {
                    // 锁失败
                    String msg = r.getMsg();
                    responseVo.setCode(3);
                    // 抛出异常让事务回滚，不然如果锁库失败，订单还是创建了
                    throw new RenException(500, msg);
                }
            } else {
                // 价格校验失败
                responseVo.setCode(2);
                return responseVo;
            }
        } else {
            // 验证失败
            responseVo.setCode(1);
            return responseVo;
        }
    }

    @Override
    public void closeOrder(OrderEntity order) {
        // 查询当前订单的最新状态
        OrderEntity orderEntity = this.baseDao.selectById(order.getId());
        if (orderEntity.getStatus().equals(OrderStatusEnum.CREATE_NEW.getCode())) {
            // 关单
            OrderEntity entity = new OrderEntity();
            entity.setStatus(OrderStatusEnum.CANCLED.getCode());
            entity.setId(orderEntity.getId());
            this.updateById(entity);
            OrderTO orderTo = new OrderTO();
            BeanUtils.copyProperties(orderEntity, orderTo);
            // 立即发给mq
            rabbitTemplate.convertAndSend(OrderConstant.MQ_ORDER_EXCHANGE, OrderConstant.MQ_OTHER_RELEASE_ROUTING_KEY, orderTo);
        }
    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        OrderEntity orderEntity = this.baseDao.selectOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        return orderEntity;
    }


    /**
     * 保存订单
     *
     * @param order
     */
    private void saveOrder(OrderCreateTo order) {
        OrderEntity order1 = order.getOrder();
        order1.setModifyTime(new Date());
        this.baseDao.insert(order1);
        List<OrderItemEntity> orderItems = order.getOrderItems();
        orderItemService.insertBatch(orderItems);
    }

    private OrderCreateTo createOrder() {
        OrderCreateTo orderCreateTo = new OrderCreateTo();
        // 1.生成订单号，IdWorder类用的是雪花算法
        String orderSn = IdWorker.getTimeId();
        // 2.创建订单
        OrderEntity orderEntity = builderOrder(orderSn);
        //3、获取到所有的订单项
        List<OrderItemEntity> orderItemEntities = builderOrderItems(orderSn);

        // 4.验价
        computePrice(orderEntity, orderItemEntities);

        orderCreateTo.setOrder(orderEntity);
        orderCreateTo.setOrderItems(orderItemEntities);

        return orderCreateTo;
    }

    /**
     * 构建订单数据
     *
     * @param orderSn
     * @return
     */
    private OrderEntity builderOrder(String orderSn) {
        //获取当前用户登录信息
        MemberTO memberResponseVo = LoginUserInterceptor.loginUser.get();
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setMemberId(memberResponseVo.getId());
        orderEntity.setOrderSn(orderSn);
        orderEntity.setMemberUsername(memberResponseVo.getUsername());

        OrderSubmitVO orderSubmitVo = confirmVoThreadLocal.get();

        //远程获取收货地址和运费信息
        Result<FareVO> result = wmsFeignService.getFare(orderSubmitVo.getAddrId());
        FareVO fareResp = result.getData();

        //获取到运费信息
        BigDecimal fare = fareResp.getFare();
        orderEntity.setFreightAmount(fare);

        //获取到收货地址信息
        MemberAddressVO address = fareResp.getAddress();
        //设置收货人信息
        orderEntity.setReceiverName(address.getName());
        orderEntity.setReceiverPhone(address.getPhone());
        orderEntity.setReceiverPostCode(address.getPostCode());
        orderEntity.setReceiverProvince(address.getProvince());
        orderEntity.setReceiverCity(address.getCity());
        orderEntity.setReceiverRegion(address.getRegion());
        orderEntity.setReceiverDetailAddress(address.getDetailAddress());
        //设置订单相关的状态信息
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);
        orderEntity.setConfirmStatus(0);
        return orderEntity;
    }


    /**
     * 构建所有订单项数据
     *
     * @return
     */
    private List<OrderItemEntity> builderOrderItems(String orderSn) {

        List<OrderItemEntity> orderItemEntityList = new ArrayList<>();
        //最后确定每个购物项的价格
        Result<List<OrderItemVO>> result = cartFeignService.getCurrentUserCartItems();
        List<OrderItemVO> currentCartItems = result.getData();
        if (currentCartItems != null && currentCartItems.size() > 0) {
            orderItemEntityList = currentCartItems.stream().map((items) -> {
                //构建订单项数据
                OrderItemEntity orderItemEntity = builderOrderItem(items);
                orderItemEntity.setOrderSn(orderSn);

                return orderItemEntity;
            }).collect(Collectors.toList());
        }

        return orderItemEntityList;
    }

    /**
     * 构建每一个订单项
     *
     * @param items
     * @return
     */
    private OrderItemEntity builderOrderItem(OrderItemVO items) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        //1、商品的spu信息
        Long skuId = items.getSkuId();
        //获取spu的信息
        Result<SpuInfoTO> result = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoTO spuInfoData = result.getData();
        if (spuInfoData != null) {
            orderItemEntity.setSpuId(spuInfoData.getId());
            orderItemEntity.setSpuName(spuInfoData.getSpuName());
            orderItemEntity.setSpuBrand(spuInfoData.getBrandName());
            orderItemEntity.setCategoryId(spuInfoData.getCatalogId());
        }
        //2、商品的sku信息
        orderItemEntity.setSkuId(skuId);
        orderItemEntity.setSkuName(items.getTitle());
        orderItemEntity.setSkuPic(items.getImage());
        orderItemEntity.setSkuPrice(items.getPrice());
        orderItemEntity.setSkuQuantity(items.getCount());
        //使用StringUtils.collectionToDelimitedString将list集合转换为String
        String skuAttrValues = StringUtils.join(items.getSkuAttrValues(), ";");
        orderItemEntity.setSkuAttrsVals(skuAttrValues);

        //3、商品的优惠信息[不做]

        //4、商品的积分信息
        orderItemEntity.setGiftGrowth(items.getPrice().multiply(new BigDecimal(items.getCount())).intValue());
        orderItemEntity.setGiftIntegration(items.getPrice().multiply(new BigDecimal(items.getCount())).intValue());

        //5、订单项的价格信息
        orderItemEntity.setPromotionAmount(BigDecimal.ZERO);
        orderItemEntity.setCouponAmount(BigDecimal.ZERO);
        orderItemEntity.setIntegrationAmount(BigDecimal.ZERO);
        //当前订单项的实际金额.总额 - 各种优惠价格
        //原来的价格
        BigDecimal origin = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity().toString()));
        //原价减去优惠价得到最终的价格
        BigDecimal subtract = origin.subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(subtract);
        return orderItemEntity;
    }

    /**
     * 计算价格的方法
     *
     * @param orderEntity
     * @param orderItemEntities
     */
    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {

        //总价
        BigDecimal total = new BigDecimal("0.0");
        //优惠价
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal intergration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");

        //积分、成长值
        Integer integrationTotal = 0;
        Integer growthTotal = 0;

        //订单总额，叠加每一个订单项的总额信息
        for (OrderItemEntity orderItem : orderItemEntities) {
            //优惠价格信息
            coupon = coupon.add(orderItem.getCouponAmount());
            promotion = promotion.add(orderItem.getPromotionAmount());
            intergration = intergration.add(orderItem.getIntegrationAmount());

            //总价
            total = total.add(orderItem.getRealAmount());

            //积分信息和成长值信息
            integrationTotal += orderItem.getGiftIntegration();
            growthTotal += orderItem.getGiftGrowth();

        }
        //1、订单价格相关的
        orderEntity.setTotalAmount(total);
        //设置应付总额(总额+运费)
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setCouponAmount(coupon);
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(intergration);

        //设置积分成长值信息
        orderEntity.setIntegration(integrationTotal);
        orderEntity.setGrowth(growthTotal);
        //设置删除状态(0-未删除，1-已删除)
        orderEntity.setDeleteStatus(0);

    }
}