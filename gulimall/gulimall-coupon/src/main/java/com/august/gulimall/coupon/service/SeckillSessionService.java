package com.august.gulimall.coupon.service;

import com.august.gulimall.common.page.PageData;
import com.august.gulimall.common.service.CrudService;
import com.august.gulimall.coupon.dto.SeckillSessionDTO;
import com.august.gulimall.coupon.entity.SeckillSessionEntity;

import java.util.List;
import java.util.Map;

/**
 * 秒杀活动场次
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
public interface SeckillSessionService extends CrudService<SeckillSessionEntity, SeckillSessionDTO> {

    PageData<SeckillSessionDTO> queryPage(Map<String, Object> params);

    List<SeckillSessionDTO> getLast3DaysSession();
}