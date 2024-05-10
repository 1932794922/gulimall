package com.august.gulimall.product.vo;

import com.august.gulimall.product.entity.SkuImagesEntity;
import com.august.gulimall.product.entity.SkuInfoEntity;
import com.august.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVO {
    /**
     * 1.sku基本信息获取 pms_sku_info
     */
    private SkuInfoEntity info;

    Boolean hasStock = true;

    /**
     * 2.sku图片信息 pms_sku_images
     */
    private List<SkuImagesEntity> images;
    /**
     * 3.获取spu的销售属性
     */
    List<SkuItemSaleAttrVO> saleAttr;
    /**
     * 4.获取spu的介绍 pms_spu_info_desc
     */
    private SpuInfoDescEntity desp;
    /**
     * 5.获取spu的规格参数信息
     */
    private List<SpuItemAttrGroupVO> groupAttrs;

    private SeckillSkuVO seckillSkuVO;


    @Data
    public static class SkuItemSaleAttrVO {
        private Long attrId;
        private String attrName;
        private List<AttrValueWithSkuIdVO> attrValues;
    }

    @Data
    public static class AttrValueWithSkuIdVO {
        private String attrValue;
        private String skuIds;
    }


    @Data
    public static class SpuItemAttrGroupVO {
        private String groupName;
        private List<SpuBaseAttrVO> attrs;

    }

    @Data
    public static class SpuBaseAttrVO {
        private String attrName;
        private String attrValue;
    }


}