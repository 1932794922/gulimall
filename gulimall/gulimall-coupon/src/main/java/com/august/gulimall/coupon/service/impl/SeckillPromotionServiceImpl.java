package com.august.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import com.august.gulimall.coupon.dao.SeckillPromotionDao;
import com.august.gulimall.coupon.dto.SeckillPromotionDTO;
import com.august.gulimall.coupon.entity.SeckillPromotionEntity;
import com.august.gulimall.coupon.service.SeckillPromotionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 秒杀活动
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Service
public class SeckillPromotionServiceImpl extends CrudServiceImpl<SeckillPromotionDao, SeckillPromotionEntity, SeckillPromotionDTO> implements SeckillPromotionService {

    @Override
    public QueryWrapper<SeckillPromotionEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<SeckillPromotionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


}