package com.august.gulimall.order.dao;

import com.august.gulimall.common.dao.BaseDao;
import com.august.gulimall.order.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.context.annotation.Primary;

/**
 * 订单
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@Mapper
@Primary
public interface OrderDao extends BaseDao<OrderEntity> {
	
}