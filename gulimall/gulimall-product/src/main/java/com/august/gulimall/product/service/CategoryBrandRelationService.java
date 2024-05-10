package com.august.gulimall.product.service;

import com.august.gulimall.common.page.PageData;
import com.august.gulimall.common.service.CrudService;
import com.august.gulimall.product.dto.CategoryBrandRelationDTO;
import com.august.gulimall.product.entity.CategoryBrandRelationEntity;

import java.util.List;


/**
 * 
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2023-04-02
 */
public interface CategoryBrandRelationService extends CrudService<CategoryBrandRelationEntity, CategoryBrandRelationDTO> {

    /**
     * 获取品牌关联的分类
     * @param brandId
     * @return
     */
    List<CategoryBrandRelationDTO> catelogList(Long brandId);

    void saveDetail(CategoryBrandRelationDTO dto);

    /**
     * 更新品牌名
     * @param brandId
     * @param name
     */
    void updateBrand(Long brandId, String name);

    /**
     * 更新分类名
     * @param catId
     * @param name
     */
    void updateCategory(Long catId, String name);

}