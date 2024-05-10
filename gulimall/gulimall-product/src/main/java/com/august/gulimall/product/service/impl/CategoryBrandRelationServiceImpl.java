package com.august.gulimall.product.service.impl;

import com.august.gulimall.common.utils.ConvertUtils;
import com.august.gulimall.product.dao.CategoryBrandRelationDao;
import com.august.gulimall.product.dto.CategoryBrandRelationDTO;
import com.august.gulimall.product.entity.CategoryBrandRelationEntity;
import com.august.gulimall.product.service.BrandService;
import com.august.gulimall.product.service.CategoryBrandRelationService;
import com.august.gulimall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2023-04-02
 */
@Service
@Transactional(readOnly = true)
public class CategoryBrandRelationServiceImpl extends CrudServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity, CategoryBrandRelationDTO> implements CategoryBrandRelationService {

    @Resource
    BrandService brandService;

    @Resource
    CategoryService categoryService;

    @Override
    public QueryWrapper<CategoryBrandRelationEntity> getWrapper(Map<String, Object> params) {
        String id = (String) params.get("id");

        QueryWrapper<CategoryBrandRelationEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public List<CategoryBrandRelationDTO> catelogList(Long brandId) {
        LambdaQueryWrapper<CategoryBrandRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CategoryBrandRelationEntity::getBrandId, brandId);
        List<CategoryBrandRelationEntity> selectList = this.baseDao.selectList(queryWrapper);
        List<CategoryBrandRelationDTO> dtoList = ConvertUtils.sourceToTarget(selectList, CategoryBrandRelationDTO.class);
        return dtoList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public void saveDetail(CategoryBrandRelationDTO dto) {
        Long brandId = dto.getBrandId();
        Long catelogId = dto.getCatelogId();
        String brandName = brandService.get(brandId).getName();
        String catelogName = categoryService.get(catelogId).getName();
        dto.setBrandName(brandName);
        dto.setCatelogName(catelogName);
        this.save(dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public void updateBrand(Long brandId, String name) {
        CategoryBrandRelationEntity entity = new CategoryBrandRelationEntity();
        entity.setBrandName(name);
        this.baseDao.update(entity,
                new LambdaQueryWrapper<CategoryBrandRelationEntity>()
                        .eq(CategoryBrandRelationEntity::getBrandId, brandId));
    }

    @Override
    public void updateCategory(Long catId, String name) {
        CategoryBrandRelationEntity entity = new CategoryBrandRelationEntity();
        entity.setCatelogName(name);
        this.baseDao.update(entity,
                new LambdaQueryWrapper<CategoryBrandRelationEntity>()
                        .eq(CategoryBrandRelationEntity::getCatelogId, catId));
    }
}