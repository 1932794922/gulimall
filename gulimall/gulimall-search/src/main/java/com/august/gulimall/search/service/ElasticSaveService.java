package com.august.gulimall.search.service;

import com.august.gulimall.common.to.esmodel.SkuEsModel;

import java.io.IOException;
import java.util.List;

public interface ElasticSaveService {
    /**
     * 上架商品
     * @param skuEsModels
     */
    void productStatusUp(List<SkuEsModel> skuEsModels);
}
