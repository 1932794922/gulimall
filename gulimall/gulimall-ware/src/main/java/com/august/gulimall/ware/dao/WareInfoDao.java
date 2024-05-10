package com.august.gulimall.ware.dao;

import com.august.gulimall.common.dao.BaseDao;
import com.august.gulimall.ware.entity.WareInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库信息
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Mapper
public interface WareInfoDao extends BaseDao<WareInfoEntity> {
	
}