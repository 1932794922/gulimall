package com.august.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import com.august.gulimall.coupon.dao.CategoryBoundsDao;
import com.august.gulimall.coupon.dto.CategoryBoundsDTO;
import com.august.gulimall.coupon.entity.CategoryBoundsEntity;
import com.august.gulimall.coupon.service.CategoryBoundsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 商品分类积分设置
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Service
public class CategoryBoundsServiceImpl extends CrudServiceImpl<CategoryBoundsDao, CategoryBoundsEntity, CategoryBoundsDTO> implements CategoryBoundsService {

    @Override
    public QueryWrapper<CategoryBoundsEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<CategoryBoundsEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


}