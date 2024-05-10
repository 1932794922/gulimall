package com.august.gulimall.product.service;

import com.august.gulimall.common.page.PageData;
import com.august.gulimall.common.service.CrudService;
import com.august.gulimall.product.dto.AttrAttrgroupRelationDTO;
import com.august.gulimall.product.dto.AttrGroupDTO;
import com.august.gulimall.product.entity.AttrGroupEntity;
import com.august.gulimall.product.vo.AttrGroupWithAttrsVO;
import com.august.gulimall.product.vo.SkuItemVO;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
public interface AttrGroupService extends CrudService<AttrGroupEntity, AttrGroupDTO> {

    PageData<AttrGroupDTO> pageList(Map<String, Object> params, Long catelogId);

    /**
     * 添加属性与分组关联关系
     * @param attrAttrgroupRelationDTOList
     */
    void attrRelation(List<AttrAttrgroupRelationDTO> attrAttrgroupRelationDTOList);

    List<AttrGroupWithAttrsVO> getAttrGroupWithAttrsByCatelogId(Long catelogId);

    /**
     * 获取spuId对应的属性分组与属性
     * @param spuId
     * @param catalogId
     * @return
     */
    List<SkuItemVO.SpuItemAttrGroupVO> getAttrGroupWithAttrs(Long spuId, Long catalogId);
}