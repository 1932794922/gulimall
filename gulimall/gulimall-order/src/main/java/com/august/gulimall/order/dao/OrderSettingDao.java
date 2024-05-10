package com.august.gulimall.order.dao;

import com.august.gulimall.common.dao.BaseDao;
import com.august.gulimall.order.entity.OrderSettingEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@Mapper
public interface OrderSettingDao extends BaseDao<OrderSettingEntity> {
	
}