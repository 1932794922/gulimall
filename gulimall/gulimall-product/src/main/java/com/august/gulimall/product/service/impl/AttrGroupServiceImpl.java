package com.august.gulimall.product.service.impl;

import com.august.gulimall.common.page.PageData;
import com.august.gulimall.product.dto.AttrAttrgroupRelationDTO;
import com.august.gulimall.product.dto.AttrDTO;
import com.august.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.august.gulimall.product.service.AttrAttrgroupRelationService;
import com.august.gulimall.product.service.AttrService;
import com.august.gulimall.product.vo.AttrGroupWithAttrsVO;
import com.august.gulimall.product.vo.SkuItemVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import com.august.gulimall.product.dao.AttrGroupDao;
import com.august.gulimall.product.dto.AttrGroupDTO;
import com.august.gulimall.product.entity.AttrGroupEntity;
import com.august.gulimall.product.service.AttrGroupService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 属性分组
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@Service
public class AttrGroupServiceImpl extends CrudServiceImpl<AttrGroupDao, AttrGroupEntity, AttrGroupDTO> implements AttrGroupService {

    @Resource
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Resource
    AttrService attrService;

    @Override
    public QueryWrapper<AttrGroupEntity> getWrapper(Map<String, Object> params) {
        String id = (String) params.get("attrGroupId");

        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "attr_group_id", id);

        return wrapper;
    }


    @Override
    public PageData<AttrGroupDTO> pageList(Map<String, Object> params, Long catelogId) {
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (catelogId == 0) {
            // 查询所有,如果没有关键字，直接返回
            if (StringUtils.isBlank(key)) {
                return page(params);
            }
            //如果有关键字，根据关键字查询
            //select * from pms_attr_group where attr_group_id = ? or attr_group_name like ?
            wrapper.eq("attr_group_id", key).or().like("attr_group_name", key);
        } else {
            //获取查询关键字key
            //根据关键字查询
            if (StringUtils.isNotBlank(key)) {
                //select * from pms_attr_group where catelog_id = ? and (attr_group_id = ? or attr_group_name like ?)
                wrapper.eq("catelog_id", catelogId).and((obj) -> {
                    obj.eq("attr_group_id", key).or().like("attr_group_name", key);
                });
            } else {
                //根据分类id查询
                wrapper.eq("catelog_id", catelogId);
            }
        }
        IPage<AttrGroupEntity> page = baseDao.selectPage(
                getPage(params, null, false),
                wrapper
        );
        return getPageData(page, currentDtoClass());
    }

    @Override
    public void attrRelation(List<AttrAttrgroupRelationDTO> attrAttrgroupRelationDTOList) {
        // 转为实体类
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntityList = attrAttrgroupRelationDTOList.stream().map((attrAttrgroupRelationDTO) -> {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attrAttrgroupRelationDTO.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attrAttrgroupRelationDTO.getAttrId());
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());
        attrAttrgroupRelationService.insertBatch(attrAttrgroupRelationEntityList);
    }

    @Override
    public List<AttrGroupWithAttrsVO> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        // 1.查询当前分类下的所有属性分组
        List<AttrGroupEntity> attrGroupEntityList = baseDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        // 2.查询每个属性分组的所有属性
        List<AttrGroupWithAttrsVO> attrGroupWithAttrsVOList = attrGroupEntityList.stream().map((attrGroupEntity) -> {
            AttrGroupWithAttrsVO attrGroupWithAttrsVO = new AttrGroupWithAttrsVO();
            BeanUtils.copyProperties(attrGroupEntity, attrGroupWithAttrsVO);
            // 3.查询属性
            List<AttrDTO> attrRelation = attrService.getAttrRelation(attrGroupWithAttrsVO.getAttrGroupId());
            attrGroupWithAttrsVO.setAttrs(attrRelation);
            return attrGroupWithAttrsVO;
        }).collect(Collectors.toList());

        return attrGroupWithAttrsVOList;
    }

    @Override
    public List<SkuItemVO.SpuItemAttrGroupVO> getAttrGroupWithAttrs(Long spuId, Long catalogId) {
        // 查出当前spuId对应的所有属性信息以及当前分组下的所有属性信息
        AttrGroupDao attrGroupDao = this.baseDao;
        List<SkuItemVO.SpuItemAttrGroupVO> result = attrGroupDao.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
        return result;
    }
}