package com.august.gulimall.product.dao;

import com.august.gulimall.common.dao.BaseDao;
import com.august.gulimall.product.entity.SpuCommentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 商品评价
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@Mapper
@Repository
public interface SpuCommentDao extends BaseDao<SpuCommentEntity> {
	
}