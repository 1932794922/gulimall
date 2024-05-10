package com.august.gulimall.product.service.impl;

import com.august.gulimall.common.page.PageData;
import com.august.gulimall.product.dao.CategoryBrandRelationDao;
import com.august.gulimall.product.dto.CategoryBrandRelationDTO;
import com.august.gulimall.product.entity.CategoryBrandRelationEntity;
import com.august.gulimall.product.service.CategoryBrandRelationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import com.august.gulimall.product.dao.BrandDao;
import com.august.gulimall.product.dto.BrandDTO;
import com.august.gulimall.product.entity.BrandEntity;
import com.august.gulimall.product.service.BrandService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 品牌
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@Service
@Transactional(readOnly = true)
public class BrandServiceImpl extends CrudServiceImpl<BrandDao, BrandEntity, BrandDTO> implements BrandService {


    @Resource
    private CategoryBrandRelationDao categoryBrandRelationDao;
    @Resource
    private CategoryBrandRelationService categoryBrandRelationService;


    @Resource
    BrandService brandService;

    @Override
    public QueryWrapper<BrandEntity> getWrapper(Map<String, Object> params) {
        String id = (String) params.get("brandId");
        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();

        wrapper.eq(StringUtils.isNotBlank(id), "brand_id", id);

        return wrapper;
    }


    @Override
    public PageData<BrandDTO> queryPage(Map<String, Object> params) {
        //获取关键字
        String brandId = (String) params.get("brandId");
        if (StringUtils.isNotBlank(brandId)) {


            //查询关键字信息
            LambdaQueryWrapper<BrandEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BrandEntity::getBrandId, brandId)
                    .or((obj) -> {
                        obj.like(BrandEntity::getName, brandId)
                                .or()
                                .like(BrandEntity::getDescript, brandId);
                    });
            IPage<BrandEntity> page = baseDao.selectPage(
                    getPage(params, null, false),
                    queryWrapper);
            return getPageData(page, currentDtoClass());
        }
        return page(params);

    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateCascade(BrandEntity brandEntity) {
        //保证冗余字段的数据一致
        this.updateById(brandEntity);
        //更新其他关联表的数据
        if (StringUtils.isNotBlank(brandEntity.getName())) {
            //同步更新其他关联表中的数据
            categoryBrandRelationService.updateBrand(brandEntity.getBrandId(), brandEntity.getName());
            //TODO:更新其他关联表
        }
    }

    @Override
    public List<CategoryBrandRelationDTO> brandsListByCatId(Long catId) {
        LambdaQueryWrapper<CategoryBrandRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CategoryBrandRelationEntity::getCatelogId, catId);
        Map<String, Object> map = new HashMap<>();
        map.put("params", queryWrapper);
        List<CategoryBrandRelationEntity> list = categoryBrandRelationDao.selectList(queryWrapper);
        //转换成DTO
        List<CategoryBrandRelationDTO> collect = list.stream().map(item -> {
            CategoryBrandRelationDTO categoryBrandRelationDTO = new CategoryBrandRelationDTO();
            BeanUtils.copyProperties(item, categoryBrandRelationDTO);
            return categoryBrandRelationDTO;
        }).collect(Collectors.toList());

        return collect;
    }

    @Override
    public List<BrandDTO> brandsListBybrandId(Long catId) {

        return null;
    }

}