package com.august.gulimall.product.dao;

import com.august.gulimall.common.dao.BaseDao;
import com.august.gulimall.product.entity.AttrGroupEntity;
import com.august.gulimall.product.vo.SkuItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 属性分组
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */

@Mapper
@Repository
public interface AttrGroupDao extends BaseDao<AttrGroupEntity> {

    /**
     * 获取spuId对应的属性分组与属性
     * @param spuId
     * @param catalogId
     * @return
     */
    List<SkuItemVO.SpuItemAttrGroupVO> getAttrGroupWithAttrsBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);

}