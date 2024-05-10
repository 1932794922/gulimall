package com.august.gulimall.product.feign;

import com.august.gulimall.common.to.SkuHasStore;
import com.august.gulimall.common.to.esmodel.SkuEsModel;
import com.august.gulimall.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * es远程调用
 *
 * @author xzy
 */
@FeignClient("gulimall-search")
public interface ElasticSearchFeignService {

     /**
      * 上架商品
      *
      * @param skuEsModels skuEsModels
      * @return Result
      */
     @PostMapping("/search/save/product")
     Result productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);

}
