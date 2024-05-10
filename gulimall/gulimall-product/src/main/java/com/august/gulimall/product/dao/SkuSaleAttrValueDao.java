package com.august.gulimall.product.dao;

import com.august.gulimall.common.dao.BaseDao;
import com.august.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.august.gulimall.product.vo.SkuItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * sku销售属性&值
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@Mapper
@Repository
public interface SkuSaleAttrValueDao extends BaseDao<SkuSaleAttrValueEntity> {

    /**
     * 通过spuId获取销售属性列表
     * @param spuId
     * @return
     */
    List<SkuItemVO.SkuItemSaleAttrVO> getSaleAttrsBySpuId(@Param("spuId") Long spuId);
}