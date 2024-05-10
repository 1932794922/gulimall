package com.august.gulimall.ware.dao;

import com.august.gulimall.common.dao.BaseDao;
import com.august.gulimall.ware.entity.PurchaseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.context.annotation.Primary;

/**
 * 采购信息
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Mapper
@Primary
public interface PurchaseDao extends BaseDao<PurchaseEntity> {
	
}