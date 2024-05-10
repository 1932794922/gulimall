package com.august.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import com.august.gulimall.coupon.dao.SkuBoundsDao;
import com.august.gulimall.coupon.dto.SkuBoundsDTO;
import com.august.gulimall.coupon.entity.SkuBoundsEntity;
import com.august.gulimall.coupon.service.SkuBoundsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 商品sku积分设置
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Service
public class SkuBoundsServiceImpl extends CrudServiceImpl<SkuBoundsDao, SkuBoundsEntity, SkuBoundsDTO> implements SkuBoundsService {

    @Override
    public QueryWrapper<SkuBoundsEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<SkuBoundsEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


}