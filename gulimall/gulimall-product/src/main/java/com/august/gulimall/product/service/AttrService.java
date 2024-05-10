package com.august.gulimall.product.service;

import com.august.gulimall.common.page.PageData;
import com.august.gulimall.common.service.CrudService;
import com.august.gulimall.product.dto.AttrAttrgroupRelationDTO;
import com.august.gulimall.product.dto.AttrDTO;
import com.august.gulimall.product.entity.AttrEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
public interface AttrService extends CrudService<AttrEntity, AttrDTO> {

    /**
     * 获取分类规格参数
     *
     * @param params
     * @param attrType
     * @return
     */
    PageData<AttrDTO> baseList(Map<String, Object> params, Long catelogId, String attrType);

    /**
     * 保存属性和属性分组关联关系
     * @param dto
     */
    void saveDetail(AttrDTO dto);

    /**
     * 查询
     * @param id
     * @return
     */
    AttrDTO getDetail(Long id);

    /**
     * 根据分组id查询关联的所有属性
     * @param attrGroupId
     * @return
     */
    List<AttrDTO> getAttrRelation(Long attrGroupId);

    /**
     * 删除属性与分组的关联关系
     * @param attrAttrgroupRelationDTOList
     */
    void attrRelationDelete(List<AttrAttrgroupRelationDTO> attrAttrgroupRelationDTOList);

    /**
     * 根据分组id获取当前分组没有关联的属性
     * @param attrGroupId
     * @return
     */
    PageData<AttrDTO> getNotAttrRelation(Map<String, Object> params,Long attrGroupId);

    /**
     * 查询商品可以检索的属性
     * @param attrIds
     * @return
     */
    List<Long> selectSearchAttrIds(List<Long> attrIds);
}