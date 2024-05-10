package com.august.gulimall.product.service.impl;

import com.august.gulimall.common.utils.ConvertUtils;
import com.august.gulimall.product.service.CategoryBrandRelationService;
import com.august.gulimall.product.vo.Catelog2VO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import com.august.gulimall.product.dao.CategoryDao;
import com.august.gulimall.product.dto.CategoryDTO;
import com.august.gulimall.product.entity.CategoryEntity;
import com.august.gulimall.product.service.CategoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 商品三级分类
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl extends CrudServiceImpl<CategoryDao, CategoryEntity, CategoryDTO> implements CategoryService {

    @Resource
    private CategoryBrandRelationService categoryBrandRelationService;


    @Resource
    StringRedisTemplate stringRedisTemplate;


    @Override
    public QueryWrapper<CategoryEntity> getWrapper(Map<String, Object> params) {
        String id = (String) params.get("id");

        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public List<CategoryDTO> getListWithTree() {
        //1. 查询所有分类
        List<CategoryEntity> categoryEntities = baseDao.selectList(null);
        //2. 组装成父子的树形结构
        List<CategoryDTO> categoryDTOS = ConvertUtils.sourceToTarget(categoryEntities, currentDtoClass());
        List<CategoryDTO> menus = categoryDTOS.stream().filter(categoryDTO ->
                        //2.1 找到所有的一级分类
                        categoryDTO.getParentCid() == 0
                )
                .peek(menu -> {
                    //2.2 为一级分类设置子菜单
                    menu.setChildren(getChildren(categoryDTOS, menu));
                }).sorted(
                        Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort()))
                ).collect(Collectors.toList());
        return menus;
    }

    //@Caching(evict = {
    //        @CacheEvict(value = {"category"}, key = "'getLevelOne'"),
    //        @CacheEvict(value = {"category"}, key = "'getCatalogJson'")
    //})
    @CacheEvict(value = {"category"}, allEntries = true)
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void deleteByIds(Long[] ids) {
        //TODO 检查当前删除的菜单，是否被别的地方引用
        baseDao.deleteBatchIds(Arrays.asList(ids));

    }

    @Override
    public List<Long> findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        //通过id找到当前分类
        CategoryEntity categoryEntity = baseDao.selectById(catelogId);
        paths.add(categoryEntity.getCatId());
        //迭代查询当前分类的父分类
        while (categoryEntity.getParentCid() != 0) {
            categoryEntity = baseDao.selectById(categoryEntity.getParentCid());
            paths.add(categoryEntity.getCatId());
        }
        return paths;
    }

    @Override
    //@Caching(evict = {
    //        @CacheEvict(value = {"category"}, key = "'getLevelOne'"),
    //        @CacheEvict(value = {"category"}, key = "'getCatalogJson'")
    //})
    @CacheEvict(value = {"category"}, allEntries = true)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateCascade(CategoryDTO dto) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setCatId(dto.getCatId());
        categoryEntity.setName(dto.getName());
        this.updateById(categoryEntity);
        //更新所有关联的数据
        categoryBrandRelationService.updateCategory(dto.getCatId(), dto.getName());
        //TODO:更新其他关联表

    }

    @Cacheable(value = {"category"}, key = "#root.methodName",sync = true)
    @Override
    public List<CategoryEntity> getLevelOne() {

        LambdaQueryWrapper<CategoryEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryEntity::getParentCid, 0);
        List<CategoryEntity> categorys = baseDao.selectList(wrapper);
        return categorys;
    }

    /**
     * springcache的方式
     *
     * @return
     */
    @Cacheable(value = {"category"}, key = "#root.methodName",sync = true)
    @Override
    public Map<String, List<Catelog2VO>> getCatalogJson() {
        System.out.println("查询了数据库");
        Map<String, List<Catelog2VO>> catalogJsonFromDb = getCatalogJsonFromDb();
        return catalogJsonFromDb;
    }

    /**
     * 手动添加缓存
     */
    //@Override
    public Map<String, List<Catelog2VO>> getCatalogJsonWithManual() {
        //1. 先查询缓存，如果缓存中有，直接返回
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        ObjectMapper objectMapper = new ObjectMapper();
        if (StringUtils.isNotBlank(catalogJson)) {
            Map<String, List<Catelog2VO>> map = null;
            try {
                map = objectMapper.readValue(catalogJson, Map.class);
                return map;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //2. 如果缓存中没有，查询数据库
        Map<String, List<Catelog2VO>> catalogJsonFromDb = getCatalogJsonFromDb();
        //3. 将数据库查询出来的数据，放入缓存中
        try {
            String catalogJsonStr = objectMapper.writeValueAsString(catalogJsonFromDb);
            stringRedisTemplate.opsForValue().set("catalogJson", catalogJsonStr, 1, TimeUnit.DAYS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return catalogJsonFromDb;
    }

    /**
     * 从数据库中查询分类数据
     *
     * @return
     */
    public Map<String, List<Catelog2VO>> getCatalogJsonFromDb() {
        //1. 查出所有分类
        List<CategoryEntity> categoryEntities = baseDao.selectList(null);
        //2. 组装成父子的树形结构
        if (categoryEntities == null) {
            return null;
        }
        //获取所有一级分类
        List<CategoryEntity> categoryLevelOneEntities = categoryEntities.stream().filter(categoryEntity -> categoryEntity.getParentCid() == 0 || categoryEntity.getCatLevel() == 1).collect(Collectors.toList());
        //获取所有二级分类
        List<CategoryEntity> categoryLevelTwoEntities = categoryEntities.stream().filter(categoryEntity -> categoryEntity.getCatLevel() == 2).collect(Collectors.toList());
        //获取所有三级分类
        List<CategoryEntity> categoryLevelThreeEntities = categoryEntities.stream().filter(categoryEntity -> categoryEntity.getCatLevel() == 3).collect(Collectors.toList());
        //3. 封装数据
        Map<String, List<Catelog2VO>> collect = categoryLevelOneEntities.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //3.1 封装一级分类
            //查询一级分类下的所有二级分类
            List<Catelog2VO> collect2vo = null;
            if (categoryLevelTwoEntities.size() != 0) {
                collect2vo = categoryLevelTwoEntities.stream().map(item -> {
                    //3.2 封装二级分类
                    //查询二级分类下的所有三级分类
                    List<Catelog2VO.Category3Vo> collect3vo = null;
                    if (categoryLevelThreeEntities.size() != 0) {
                        collect3vo = categoryLevelThreeEntities.stream().map(item2 -> {
                            //3.3 封装三级分类
                            if (item2.getParentCid().equals(item.getCatId())) {
                                return new Catelog2VO.Category3Vo(item.getCatId().toString(), item2.getCatId().toString(), item2.getName());
                            }
                            return null;
                        }).filter(
                                Objects::nonNull
                        ).collect(Collectors.toList());
                    }
                    if (v.getCatId().equals(item.getParentCid())) {
                        return new Catelog2VO(v.getCatId().toString(), collect3vo, item.getCatId().toString(), item.getName());
                    }
                    return null;
                }).filter(
                        Objects::nonNull
                ).collect(Collectors.toList());
            }
            if (collect2vo == null) {
                collect2vo = new ArrayList<>();
            }
            return collect2vo;
        }));
        return collect;
    }

    /**
     * 递归查找所有菜单的子菜单
     *
     * @param all
     * @param root
     * @return
     */
    private List<CategoryDTO> getChildren(List<CategoryDTO> all, CategoryDTO root) {
        List<CategoryDTO> children = all.stream().filter(categoryDTO -> categoryDTO.getParentCid().equals(root.getCatId()))
                .map(menu -> {
                    menu.setChildren(getChildren(all, menu));
                    return menu;
                }).sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort()))).collect(Collectors.toList());
        return children;
    }
}