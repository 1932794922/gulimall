package com.august.gulimall.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.august.gulimall.common.constant.ProductConstant;
import com.august.gulimall.common.page.PageData;
import com.august.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.august.gulimall.product.dao.AttrGroupDao;
import com.august.gulimall.product.dto.*;
import com.august.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.august.gulimall.product.entity.AttrGroupEntity;
import com.august.gulimall.product.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import com.august.gulimall.product.dao.AttrDao;
import com.august.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品属性
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@Service
public class AttrServiceImpl extends CrudServiceImpl<AttrDao, AttrEntity, AttrDTO> implements AttrService {

    @Resource
    CategoryService categoryService;

    @Resource
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Resource
    AttrGroupService attrGroupService;

    @Resource
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;


    @Override
    public QueryWrapper<AttrEntity> getWrapper(Map<String, Object> params) {
        String id = (String) params.get("id");

        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public PageData<AttrDTO> baseList(Map<String, Object> params, Long catelogId, String attrType) {
        LambdaQueryWrapper<AttrEntity> wrapper = new LambdaQueryWrapper<>();
        //拼装查询类型条件 0-销售属性，1-基本属性
        wrapper.eq(AttrEntity::getAttrType, "base".equalsIgnoreCase(attrType) ?
                ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        String key = (String) params.get("key");
        if (catelogId == 0) {
            // 查询所有,如果没有关键字，直接返回
            if (StringUtils.isNotBlank(key)) {
                wrapper.eq(AttrEntity::getAttrId, key).or().like(AttrEntity::getAttrName, key);
            }
        } else {
            //获取查询关键字key
            //根据关键字查询
            if (StringUtils.isNotBlank(key)) {
                wrapper.eq(AttrEntity::getCatelogId, catelogId).and((obj) -> {
                    obj.eq(AttrEntity::getAttrName, key);
                });
            } else {
                //根据分类id查询
                wrapper.eq(AttrEntity::getCatelogId, catelogId);
            }
        }
        IPage<AttrEntity> page = baseDao.selectPage(
                getPage(params, null, false),
                wrapper
        );
        List<AttrEntity> records = page.getRecords();
        //catelogName groupName
        List<AttrDTO> collect = records.stream().map((attrEntity) -> {
            AttrDTO attrDTO = new AttrDTO();
            BeanUtil.copyProperties(attrEntity, attrDTO);
            //设置分组和分类信息
            if ("base".equalsIgnoreCase(attrType)) {
                LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper1 = new LambdaQueryWrapper<>();
                wrapper1.eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId());
                AttrAttrgroupRelationEntity one = attrAttrgroupRelationService.getOne(wrapper1);
                AttrAttrgroupRelationDTO relationDTO = null;
                if (one != null) {
                    relationDTO = new AttrAttrgroupRelationDTO();
                    BeanUtil.copyProperties(one, relationDTO);
                }
                if (relationDTO != null) {
                    Long attrGroupId = relationDTO.getAttrGroupId();
                    AttrGroupDTO attrGroupDTO = attrGroupService.get(attrGroupId);
                    attrDTO.setGroupName(attrGroupDTO.getAttrGroupName());
                }
            }

            Long catelogId1 = attrEntity.getCatelogId();
            CategoryDTO categoryDTO = categoryService.get(catelogId1);
            if (categoryDTO != null) {
                attrDTO.setCatelogName(categoryDTO.getName());
            }
            return attrDTO;
        }).collect(Collectors.toList());
        IPage<AttrDTO> pageDTO = new PageDTO<>();
        BeanUtil.copyProperties(page, pageDTO);
        pageDTO.setRecords(collect);
        return getPageData(pageDTO, currentDtoClass());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void saveDetail(AttrDTO dto) {
        //保存基本数据
        save(dto);
        //判断是否要报错分组关联关系
        if (dto.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            //保存关联关系
            AttrAttrgroupRelationDTO relationDTO = new AttrAttrgroupRelationDTO();
            relationDTO.setAttrGroupId(dto.getAttrGroupId());
            relationDTO.setAttrId(dto.getAttrId());
            attrAttrgroupRelationService.save(relationDTO);
        }
    }

    @Override
    public AttrDTO getDetail(Long id) {
        AttrEntity attrEntity = this.baseDao.selectById(id);
        Long catelogId = attrEntity.getCatelogId();
        List<Long> paths = categoryService.findCatelogPath(catelogId);
        Collections.reverse(paths);
        AttrDTO attrDTO = new AttrDTO();
        BeanUtil.copyProperties(attrEntity, attrDTO);
        attrDTO.setCatelogPath(paths);
        //查询组id
        LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId());
        AttrAttrgroupRelationEntity one = attrAttrgroupRelationService.getOne(wrapper);
        if (one != null) {
            attrDTO.setAttrGroupId(one.getAttrGroupId());
        }
        return attrDTO;
    }

    @Override
    public List<AttrDTO> getAttrRelation(Long attrGroupId) {
        List<AttrDTO> attrDTOList = new ArrayList<>();
        //通过attrGroupId查询pms_attr_attrgroup_relation表得到attrid,再去查pms_attr表
        LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper = new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                .eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupId);
        List<AttrAttrgroupRelationEntity> list = attrAttrgroupRelationDao.selectList(wrapper);
        List<Long> attrIds = list.stream().map((attrAttrgroupRelation) -> {
            Long attrId = attrAttrgroupRelation.getAttrId();
            return attrId;
        }).collect(Collectors.toList());
        if (attrIds.size() == 0) {
            return attrDTOList;
        }
        List<AttrEntity> attrEntities = this.baseDao.selectBatchIds(attrIds);
        attrDTOList = attrEntities.stream().map((attrEntity) -> {
            AttrDTO attrDTO = new AttrDTO();
            BeanUtil.copyProperties(attrEntity, attrDTO);
            return attrDTO;
        }).collect(Collectors.toList());
        return attrDTOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void attrRelationDelete(List<AttrAttrgroupRelationDTO> attrAttrgroupRelationDTOList) {
        LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper = new LambdaQueryWrapper<>();
        //delete from pms_attr_attrgroup_relation where (attr_id = 1 and attr_group_id = 1) or (attr_id = 2 and attr_group_id = 2)
        //TODO:删除操作 LambdaQueryWrapper学习
        //1. 获取attrId和attrGroupId
        attrAttrgroupRelationDTOList.forEach((attrAttrgroupRelationDTO) -> {
            //2. 拼装条件
            wrapper.or(obj -> obj
                    .eq(AttrAttrgroupRelationEntity::getAttrId, attrAttrgroupRelationDTO.getAttrId())
                    .eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrAttrgroupRelationDTO.getAttrGroupId()));
        });
        //3. 删除
        attrAttrgroupRelationDao.delete(wrapper);
    }

    @Override
    public PageData<AttrDTO> getNotAttrRelation(Map<String, Object> params, Long attrGroupId) {
        // 1.通过attrGroupId 先查询当前属性组所在的分类
        AttrGroupDTO attrGroupDTO = attrGroupService.get(attrGroupId);
        // 2.当前分组只能关联别的分组没有引用的属性
        // 2.1 先查询当前分类下的所有分组
        Long catelogId = attrGroupDTO.getCatelogId();
        // 2.2 查询这些分组关联的属性
        Map<String, Object> w = new HashMap<>();
        w.put("catelogId", catelogId);
        List<AttrGroupDTO> attrGroupDTOList = attrGroupService.list(w);
        List<Long> attrGroupIds = attrGroupDTOList.stream().map((attrGroup) -> {
            Long attrGroupId1 = attrGroup.getAttrGroupId();
            return attrGroupId1;
        }).collect(Collectors.toList());
        LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper = new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                .in(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupIds);
        List<AttrAttrgroupRelationEntity> list = attrAttrgroupRelationDao.selectList(wrapper);
        List<Long> attrIds = list.stream().map((attrAttrgroupRelation) -> {
            Long attrId = attrAttrgroupRelation.getAttrId();
            return attrId;
        }).collect(Collectors.toList());
        // 2.3 从当前分类的所有属性中移除这些属性
        String key = (String) params.get("key");
        LambdaQueryWrapper<AttrEntity> wrapper1 = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(key)) {
            wrapper1.and(obj -> obj
                    .eq(AttrEntity::getAttrId, key)
                    .or()
                    .like(AttrEntity::getAttrName, key));
        }
        wrapper1.eq(AttrEntity::getCatelogId, catelogId)
                .eq(AttrEntity::getAttrType, ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (attrIds.size() > 0) {
            wrapper1.notIn(AttrEntity::getAttrId, attrIds);
        }
        IPage<AttrEntity> page = baseDao.selectPage(
                getPage(params, null, false),
                wrapper1
        );
        IPage<AttrDTO> pageDTO = new PageDTO<>();
        BeanUtil.copyProperties(page, pageDTO);
        return getPageData(pageDTO, currentDtoClass());
    }

    @Override
    public List<Long> selectSearchAttrIds(List<Long> attrIds) {
        LambdaQueryWrapper<AttrEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(AttrEntity::getAttrId, attrIds);
        wrapper.eq(AttrEntity::getSearchType, ProductConstant.AttrEnum.SEARCH_TYPE.getCode());
        List<AttrEntity> entities = baseDao.selectList(wrapper);
        List<Long> ids = entities.stream().map(AttrEntity::getAttrId).collect(Collectors.toList());
        return ids;
    }
}