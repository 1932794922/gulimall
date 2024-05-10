package com.august.gulimall.common.constant;

import lombok.Data;
import lombok.Getter;

/**
 * @author : Crazy_August
 * @description : 商品常量
 * @Time: 2023-04-03   21:41
 */
public class ProductConstant {


    /**
     * 商品属性
     */
    public enum AttrEnum {
        /**
         * 基本属性
         */
        ATTR_TYPE_BASE(1, "基本属性"),
        /**
         * 销售属性
         */
        ATTR_TYPE_SALE(0, "销售属性"),
        SEARCH_TYPE(1, "可检索"),

        UNSERACH_TYPE(0, "不可检索");

        private final int code;
        private final String msg;

        AttrEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }


    public enum StatusEnum {
        /**
         * 新建
         */
        NEW_SPU(0, "新建"),
        SPU_UP(1, "商品上架"),
        SPU_DOWN(2, "商品下架"),
        ;
        private int code;

        private String msg;



        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

         StatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }
}
