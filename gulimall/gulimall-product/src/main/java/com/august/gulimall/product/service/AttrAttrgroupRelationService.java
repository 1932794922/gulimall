package com.august.gulimall.product.service;

import com.august.gulimall.common.service.CrudService;
import com.august.gulimall.product.dto.AttrAttrgroupRelationDTO;
import com.august.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * 属性&属性分组关联
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
public interface AttrAttrgroupRelationService extends CrudService<AttrAttrgroupRelationEntity, AttrAttrgroupRelationDTO> {

    /**
     * 获取属性分组
     * @param wrapper
     * @return
     */
    AttrAttrgroupRelationEntity getOne(LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper);
}