package com.august.gulimall.product.service;

import com.august.gulimall.common.page.PageData;
import com.august.gulimall.common.service.CrudService;
import com.august.gulimall.product.dto.BrandDTO;
import com.august.gulimall.product.dto.CategoryBrandRelationDTO;
import com.august.gulimall.product.entity.BrandEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
public interface BrandService extends CrudService<BrandEntity, BrandDTO> {

    /**
     * 分页查询
     * @param params
     * @return
     */
    PageData<BrandDTO> queryPage(Map<String, Object> params);

    /**
     * 更新还需要更新其他表的关联关系
     * @param brandEntity
     */
    void updateCascade(BrandEntity brandEntity);

    /**
     *  根据分类id获取分类关联的品牌
     * @param brandId
     * @return
     */
    List<CategoryBrandRelationDTO> brandsListByCatId(Long catId);

    List<BrandDTO> brandsListBybrandId(Long catId);
}