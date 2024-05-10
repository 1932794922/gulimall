package com.august.gulimall.coupon.dao;

import com.august.gulimall.common.dao.BaseDao;
import com.august.gulimall.coupon.entity.CouponEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Mapper
public interface CouponDao extends BaseDao<CouponEntity> {
	
}