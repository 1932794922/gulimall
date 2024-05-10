package com.august.gulimall.product.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author : Crazy_August
 * @description :
 * @Time: 2023-04-23   18:15
 */
@NoArgsConstructor
@Data
public class SpuInfoSaveVO {
    /**
     * spuName
     */
    @JsonProperty("spuName")
    private String spuName;
    /**
     * spuDescription
     */
    @JsonProperty("spuDescription")
    private String spuDescription;
    /**
     * catalogId
     */
    @JsonProperty("catalogId")
    private Long catalogId;
    /**
     * brandId
     */
    @JsonProperty("brandId")
    private Long brandId;
    /**
     * weight
     */
    @JsonProperty("weight")
    private BigDecimal weight;
    /**
     * publishStatus
     */
    @JsonProperty("publishStatus")
    private int publishStatus;
    /**
     * decript
     */
    @JsonProperty("decript")
    private List<String> decript;
    /**
     * images
     */
    @JsonProperty("images")
    private List<String> images;
    /**
     * bounds
     */
    @JsonProperty("bounds")
    private Bounds bounds;
    /**
     * baseAttrs
     */
    @JsonProperty("baseAttrs")
    private List<BaseAttrs> baseAttrs;
    /**
     * skus
     */
    @JsonProperty("skus")
    private List<Skus> skus;

    /**
     * Bounds
     */
    @NoArgsConstructor
    @Data
    public static class Bounds {
        /**
         * buyBounds
         */
        @JsonProperty("buyBounds")
        private BigDecimal buyBounds;
        /**
         * growBounds
         */
        @JsonProperty("growBounds")
        private BigDecimal growBounds;
    }

    /**
     * BaseAttrs
     */
    @NoArgsConstructor
    @Data
    public static class BaseAttrs {
        /**
         * attrId
         */
        @JsonProperty("attrId")
        private Long attrId;
        /**
         * attrValues
         */
        @JsonProperty("attrValues")
        private String attrValues;
        /**
         * showDesc
         */
        @JsonProperty("showDesc")
        private int showDesc;
    }

    /**
     * Skus
     */
    @NoArgsConstructor
    @Data
    public static class Skus {
        /**
         * attr
         */
        @JsonProperty("attr")
        private List<Attr> attr;
        /**
         * skuName
         */
        @JsonProperty("skuName")
        private String skuName;
        /**
         * price
         */
        @JsonProperty("price")
        private BigDecimal price;
        /**
         * skuTitle
         */
        @JsonProperty("skuTitle")
        private String skuTitle;
        /**
         * skuSubtitle
         */
        @JsonProperty("skuSubtitle")
        private String skuSubtitle;
        /**
         * images
         */
        @JsonProperty("images")
        private List<Skus.Images> images;
        /**
         * descar
         */
        @JsonProperty("descar")
        private List<String> descar;
        /**
         * fullCount
         */
        @JsonProperty("fullCount")
        private int fullCount;
        /**
         * discount
         */
        @JsonProperty("discount")
        private BigDecimal discount;
        /**
         * countStatus
         */
        @JsonProperty("countStatus")
        private int countStatus;
        /**
         * fullPrice
         */
        @JsonProperty("fullPrice")
        private BigDecimal fullPrice;
        /**
         * reducePrice
         */
        @JsonProperty("reducePrice")
        private BigDecimal reducePrice;
        /**
         * priceStatus
         */
        @JsonProperty("priceStatus")
        private int priceStatus;
        /**
         * memberPrice
         */
        @JsonProperty("memberPrice")
        private List<MemberPrice> memberPrice;

        @Data
        public static class MemberPrice{
            /**
             * id
             */
            @JsonProperty("id")
            private Long id;
            /**
             * name
             */
            @JsonProperty("name")
            private String name;
            /**
             * price
             */
            @JsonProperty("price")
            private BigDecimal price;
        }

        /**
         * Attr
         */
        @NoArgsConstructor
        @Data
        public static class Attr {
            /**
             * attrId
             */
            @JsonProperty("attrId")
            private Long attrId;
            /**
             * attrName
             */
            @JsonProperty("attrName")
            private String attrName;
            /**
             * attrValue
             */
            @JsonProperty("attrValue")
            private String attrValue;
        }

        /**
         * Images
         */
        @NoArgsConstructor
        @Data
        public static class Images {
            /**
             * imgUrl
             */
            @JsonProperty("imgUrl")
            private String imgUrl;
            /**
             * defaultImg
             */
            @JsonProperty("defaultImg")
            private int defaultImg;
        }
    }
}
