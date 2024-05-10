package com.august.gulimall.product.service;

import com.august.gulimall.common.service.CrudService;
import com.august.gulimall.product.dto.CategoryDTO;
import com.august.gulimall.product.entity.CategoryEntity;
import com.august.gulimall.product.vo.Catelog2VO;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
public interface CategoryService extends CrudService<CategoryEntity, CategoryDTO> {

    /**
     * 得到树形列表
     *
     * @return {@link List}<{@link CategoryDTO}>
     */
    List<CategoryDTO> getListWithTree();

    /**
     *  通过ids删除
     *
     * @param ids id
     */
    void deleteByIds(Long[] ids);

    /**
     * 通过id得到完整路径
     * @param catelogId
     * @return
     */
    List<Long> findCatelogPath(Long catelogId);

    /**
     * 更新全部级联表
     * @param dto
     */
    void updateCascade(CategoryDTO dto);

    /**
     * 得到一级分类
     * @return
     */
    List<CategoryEntity> getLevelOne();

    /**
     * 得到所有分类
     * @return
     */
    Map<String, List<Catelog2VO>> getCatalogJson();
}