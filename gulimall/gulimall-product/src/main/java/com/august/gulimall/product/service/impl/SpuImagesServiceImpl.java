package com.august.gulimall.product.service.impl;

import com.august.gulimall.product.entity.SkuImagesEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import com.august.gulimall.product.dao.SpuImagesDao;
import com.august.gulimall.product.dto.SpuImagesDTO;
import com.august.gulimall.product.entity.SpuImagesEntity;
import com.august.gulimall.product.service.SpuImagesService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import springfox.documentation.schema.Collections;

import java.util.List;
import java.util.Map;

/**
 * spu图片
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@Service
public class SpuImagesServiceImpl extends CrudServiceImpl<SpuImagesDao, SpuImagesEntity, SpuImagesDTO> implements SpuImagesService {

    @Override
    public QueryWrapper<SpuImagesEntity> getWrapper(Map<String, Object> params) {
        String id = (String) params.get("id");

        QueryWrapper<SpuImagesEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public void saveImages(Long spuId, List<String> images) {
        if (CollectionUtils.isEmpty(images)) {
            return;
        }
        images.forEach(image -> {
            SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
            spuImagesEntity.setSpuId(spuId);
            spuImagesEntity.setImgUrl(image);
            spuImagesEntity.setDefaultImg(0);
            baseDao.insert(spuImagesEntity);
        });
    }

    @Override
    public List<SpuImagesEntity> getImagesBySpuId(Long spuId) {
        LambdaQueryWrapper<SpuImagesEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpuImagesEntity::getSpuId, spuId);
        List<SpuImagesEntity> entities = this.baseDao.selectList(wrapper);
        return entities;
    }
}