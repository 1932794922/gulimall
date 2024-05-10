package com.august.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import com.august.gulimall.product.dao.SkuImagesDao;
import com.august.gulimall.product.dto.SkuImagesDTO;
import com.august.gulimall.product.entity.SkuImagesEntity;
import com.august.gulimall.product.service.SkuImagesService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * sku图片
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@Service
public class SkuImagesServiceImpl extends CrudServiceImpl<SkuImagesDao, SkuImagesEntity, SkuImagesDTO> implements SkuImagesService {

    @Override
    public QueryWrapper<SkuImagesEntity> getWrapper(Map<String, Object> params) {
        String id = (String) params.get("id");

        QueryWrapper<SkuImagesEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public void saveBatch(List<SkuImagesEntity> collect1) {
        this.insertBatch(collect1);
    }

    @Override
    public List<SkuImagesEntity> getImagesBySkuId(Long spuId) {
        LambdaQueryWrapper<SkuImagesEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuImagesEntity::getSkuId, spuId);
        List<SkuImagesEntity> list = this.baseDao.selectList(wrapper);
        return list;
    }
}