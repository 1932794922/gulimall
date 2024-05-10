package com.august.gulimall.coupon.service.impl;

import com.august.gulimall.common.page.PageData;
import com.august.gulimall.common.utils.ConvertUtils;
import com.august.gulimall.coupon.dao.SeckillSkuRelationDao;
import com.august.gulimall.coupon.dto.SeckillSkuRelationDTO;
import com.august.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.august.gulimall.coupon.service.SeckillSkuRelationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import com.august.gulimall.coupon.dao.SeckillSessionDao;
import com.august.gulimall.coupon.dto.SeckillSessionDTO;
import com.august.gulimall.coupon.entity.SeckillSessionEntity;
import com.august.gulimall.coupon.service.SeckillSessionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 秒杀活动场次
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Service
public class SeckillSessionServiceImpl extends CrudServiceImpl<SeckillSessionDao, SeckillSessionEntity, SeckillSessionDTO> implements SeckillSessionService {

    @Resource
    SeckillSkuRelationDao seckillSkuRelationDao;


    @Override
    public QueryWrapper<SeckillSessionEntity> getWrapper(Map<String, Object> params) {
        String id = (String) params.get("id");
        QueryWrapper<SeckillSessionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        return wrapper;
    }

    @Override
    public PageData<SeckillSessionDTO> queryPage(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<SeckillSessionDTO> getLast3DaysSession() {
        LambdaQueryWrapper<SeckillSessionEntity> wrapper = new LambdaQueryWrapper<>();
        LocalDateTime startTime = LocalDate.now().atStartOfDay();
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now().plusDays(3), LocalTime.MAX);
        startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        wrapper.between(SeckillSessionEntity::getStartTime, startTime, endTime);
        List<SeckillSessionEntity> list = this.baseDao.selectList(wrapper);
        if (list == null || list.size() == 0) {
            return null;
        }
        List<SeckillSessionDTO> sessionDTOS = ConvertUtils.sourceToTarget(list, SeckillSessionDTO.class);
        //获取每个场次的商品信息
        List<Long> ids = sessionDTOS.stream().map(SeckillSessionDTO::getId).collect(Collectors.toList());
        LambdaQueryWrapper<SeckillSkuRelationEntity> wrapper1 = new LambdaQueryWrapper<SeckillSkuRelationEntity>().in(
                SeckillSkuRelationEntity::getPromotionSessionId, ids);
        List<SeckillSkuRelationEntity> entityList = seckillSkuRelationDao.selectList(wrapper1);
        Map<Long, List<SeckillSkuRelationEntity>> collect = entityList.stream().collect(Collectors.groupingBy(SeckillSkuRelationEntity::getPromotionSessionId));
        sessionDTOS.forEach(item -> {
            item.setRelationDTOS(ConvertUtils.sourceToTarget(collect.get(item.getId()), SeckillSkuRelationDTO.class));
        });
        return sessionDTOS;
    }
}