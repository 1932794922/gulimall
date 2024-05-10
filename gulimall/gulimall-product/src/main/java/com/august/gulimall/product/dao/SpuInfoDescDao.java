package com.august.gulimall.product.dao;

import com.august.gulimall.common.dao.BaseDao;
import com.august.gulimall.product.entity.SpuInfoDescEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * spu信息介绍
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@Mapper
@Repository
public interface SpuInfoDescDao extends BaseDao<SpuInfoDescEntity> {
	
}