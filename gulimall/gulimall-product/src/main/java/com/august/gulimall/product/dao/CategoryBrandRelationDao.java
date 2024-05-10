package com.august.gulimall.product.dao;

import com.august.gulimall.common.dao.BaseDao;

import com.august.gulimall.product.entity.CategoryBrandRelationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2023-04-02
 */
@Mapper
@Repository
public interface CategoryBrandRelationDao extends BaseDao<CategoryBrandRelationEntity> {
	
}