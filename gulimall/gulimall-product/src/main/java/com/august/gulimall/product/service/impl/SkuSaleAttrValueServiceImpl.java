package com.august.gulimall.product.service.impl;

import com.august.gulimall.product.vo.SkuItemVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import com.august.gulimall.product.dao.SkuSaleAttrValueDao;
import com.august.gulimall.product.dto.SkuSaleAttrValueDTO;
import com.august.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.august.gulimall.product.service.SkuSaleAttrValueService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * sku销售属性&值
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@Service
public class SkuSaleAttrValueServiceImpl extends CrudServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity, SkuSaleAttrValueDTO> implements SkuSaleAttrValueService {

    @Resource
    SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Override
    public QueryWrapper<SkuSaleAttrValueEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<SkuSaleAttrValueEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public void saveBatch(List<SkuSaleAttrValueEntity> collect2) {

        this.insertBatch(collect2);
    }

    @Override
    public List<SkuItemVO.SkuItemSaleAttrVO> getSaleAttrsBySpuId(Long spuId) {
        return skuSaleAttrValueDao.getSaleAttrsBySpuId(spuId);
    }

    @Override
    public  List<String> getSkuSaleAttrValuesAsStringList(Long skuId) {
        //QueryWrapper<SkuSaleAttrValueEntity> wrapper = new QueryWrapper<SkuSaleAttrValueEntity>().eq("sku_id", skuId);
        QueryWrapper<SkuSaleAttrValueEntity> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT CONCAT(attr_name, ':', attr_value)")
                .eq("sku_id", skuId);
        List<Object> objs = this.baseDao.selectObjs(wrapper);
        if (objs == null || objs.size() == 0) {
            return Collections.emptyList();
        }
        List<String> skuSaleAttrValues = objs.stream().map(obj -> (String) obj).collect(Collectors.toList());
        return skuSaleAttrValues;
    }
}