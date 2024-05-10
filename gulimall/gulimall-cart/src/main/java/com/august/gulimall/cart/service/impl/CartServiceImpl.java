package com.august.gulimall.cart.service.impl;

import cn.hutool.json.JSON;
import cn.hutool.log.Log;
import com.august.gulimall.cart.feign.ProductFeignService;
import com.august.gulimall.cart.interceptor.CartInterceptor;
import com.august.gulimall.cart.service.CartService;
import com.august.gulimall.cart.vo.Cart;
import com.august.gulimall.cart.vo.CartItem;
import com.august.gulimall.cart.vo.UserInfoTO;
import com.august.gulimall.common.exception.RenException;
import com.august.gulimall.common.to.SkuInfoTO;
import com.august.gulimall.common.utils.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartServiceImpl implements CartService {


    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ProductFeignService productFeignService;

    @Resource
    private ThreadPoolExecutor executor;

    private final String CART_PREFIX = "gulimall:cart:";


    @Override
    public CartItem addToCart(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String res = (String) cartOps.get(skuId.toString());
        if (StringUtils.isBlank(res)) {
            // 如果之前购物从没有此商品，就新增
            CartItem cartItem = new CartItem();
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                // 1.远程查询当前skuId对应商品信息
                Result<SkuInfoTO> skuInfo = productFeignService.getSkuInfo(skuId);
                if (skuInfo.getCode() != 200) {
                    log.error("远程查询商品信息失败");
                    throw new RenException(500, "远程查询商品信息失败");
                }
                SkuInfoTO data = skuInfo.getData();
                // 设置购物项信息
                cartItem.setCheck(true);
                cartItem.setImage(data.getSkuDefaultImg());
                cartItem.setPrice(data.getPrice());
                cartItem.setTitle(data.getSkuTitle());
                cartItem.setCount(num);
                cartItem.setSkuId(skuId);
            }, executor);
            // 2.远程查询sku的组合信息
            Result<List<String>> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
            if (skuSaleAttrValues.getCode() != 200) {
                log.error("远程查询sku的组合信息失败");
                throw new RenException(500, "远程查询sku的组合信息失败");
            }
            List<String> data = skuSaleAttrValues.getData();

            cartItem.setSkuAttr(data);

            CompletableFuture.allOf(getSkuInfoTask).join();

            // 转为json，默认是jdk序列化,存入redis,
            ObjectMapper objectMapper = new ObjectMapper();
            String s = null;
            try {
                s = objectMapper.writeValueAsString(cartItem);
            } catch (JsonProcessingException e) {
                log.error("cartItem 序列化失败 {}", e.getMessage());
            }
            cartOps.put(skuId.toString(), s);
            return cartItem;
        } else {
            // 购物车有此商品，修改数量
            ObjectMapper objectMapper = new ObjectMapper();
            CartItem cartItem = null;
            try {
                cartItem = objectMapper.readValue(res, CartItem.class);
                cartItem.setCount(cartItem.getCount() + num);
                cartOps.put(skuId.toString(), objectMapper.writeValueAsString(cartItem));
                return cartItem;
            } catch (JsonProcessingException e) {
                log.error("cartItem 反序列化失败 {}", e.getMessage());
            }
            return null;
        }
    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String str = (String) cartOps.get(skuId.toString());
        if (str == null) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        CartItem cartItem = null;
        try {
            cartItem = objectMapper.readValue(str, CartItem.class);
        } catch (JsonProcessingException e) {
            log.error("cartItem 反序列化失败 {}", e.getMessage());
        }
        return cartItem;
    }

    @Override
    public Cart getCart() {
        Cart cart = new Cart();
        UserInfoTO userInfoTo = CartInterceptor.threadLocal.get();
        //获取临时购物车的数据
        List<CartItem> tempCartItems = getCartItems(CART_PREFIX + userInfoTo.getUserKey());
        if (tempCartItems != null) {
            cart.setItems(tempCartItems);
        }
        //判断用户是否登录
        if (userInfoTo.getUserId() != null) {
            // 1.登录了
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            // 3.获取登录购物车数据【已经合并过临时购物车】
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
            // 2.如果临时购物车有数据要合并
            String tempCartKey = CART_PREFIX + userInfoTo.getUserKey();
            if (tempCartItems != null) {
                for (CartItem tempCartItem : tempCartItems) {
                    // 因为登录了，所以是合并到登录购物车
                    addToCart(tempCartItem.getSkuId(), tempCartItem.getCount());
                }
                // 合并完后清空临时购物车
                clearCart(tempCartKey);
            }
        }
        return cart;
    }

    @Override
    public void clearCart(String cartKey) {
        stringRedisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer checked) {
        //查询购物车里面的商品
        CartItem cartItem = getCartItem(skuId);
        //修改商品状态
        cartItem.setCheck(checked == 1);

        //序列化存入redis中
        ObjectMapper objectMapper = new ObjectMapper();
        String redisValue = null;
        try {
            redisValue = objectMapper.writeValueAsString(cartItem);
            if (redisValue == null) {
                return;
            }
            BoundHashOperations<String, Object, Object> cartOps = getCartOps();
            cartOps.put(skuId.toString(), redisValue);
        } catch (JsonProcessingException e) {
            log.error("cartItem 序列化失败 {}", e.getMessage());
        }

    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        //查询购物车里面的商品
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        //序列化存入redis中
        ObjectMapper objectMapper = new ObjectMapper();
        String redisValue = null;
        try {
            redisValue = objectMapper.writeValueAsString(cartItem);
            if (redisValue == null) {
                return;
            }
            cartOps.put(skuId.toString(), redisValue);
        } catch (JsonProcessingException e) {
            log.error("cartItem 序列化失败 {}", e.getMessage());
        }

    }

    @Override
    public void deleteIdCartInfo(Integer skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItem> getUserCartItems() {
        UserInfoTO userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() == null) {
            log.info("用户未登录");
            return null;
        } else {
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            List<CartItem> cartItems = getCartItems(cartKey);
            if (cartItems == null) {
                return null;
            }
            // 获取被选中的购物项
            List<CartItem> collect = cartItems.stream()
                    .filter(CartItem::getCheck)
                    .peek(item -> {
                        Result<BigDecimal> price = productFeignService.getPrice(item.getSkuId());
                        if (price.getCode() == 200) {
                            // 更新为最新价格
                            item.setPrice(price.getData());
                        } else {
                            log.error("远程调用商品服务查询价格失败");
                        }
                    })
                    .collect(Collectors.toList());
            return collect;
        }

    }


    /**
     * 获取到要操作的购物车
     *
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTO userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId() != null) {
            // 如果用户登录了
            // gulimall:cart:1
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        } else {
            // 没登陆，也会有userKey
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }
        // redis存的数据类型是Hash， boundHashOps绑定这个key
        BoundHashOperations<String, Object, Object> operations = stringRedisTemplate.boundHashOps(cartKey);
        return operations;

    }

    /**
     * 获取购物项
     *
     * @param cartKey
     * @return
     */
    private List<CartItem> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> operations = stringRedisTemplate.boundHashOps(cartKey);
        // 获取绑定key的值
        List<Object> values = operations.values();
        if (!CollectionUtils.isEmpty(values)) {
            List<CartItem> collect = values.stream().map((obj) -> {
                String str = (String) obj;
                ObjectMapper objectMapper = new ObjectMapper();
                CartItem cartItem = null;
                try {
                    cartItem = objectMapper.readValue(str, CartItem.class);
                } catch (JsonProcessingException e) {
                    log.error("cartItem 反序列化失败 {}", e.getMessage());
                }
                return cartItem;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }
}