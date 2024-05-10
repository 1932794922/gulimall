package com.august.gulimall.search.controller;

import com.august.gulimall.common.annotation.LogOperation;
import com.august.gulimall.common.to.esmodel.SkuEsModel;
import com.august.gulimall.common.utils.Result;
import com.august.gulimall.search.service.ElasticSaveService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : Crazy_August
 * @description : es控制器
 * @Time: 2023-05-02   16:05
 */
@RestController
@RequestMapping("/search/save")
public class ElasticSaveController {

    @Resource
    ElasticSaveService elasticSaveService;


    @ApiOperation("上架商品")
    @LogOperation("上架商品")
    @PostMapping("/product")
    public Result productStatusUp(@RequestBody  List<SkuEsModel> skuEsModels) {
        elasticSaveService.productStatusUp(skuEsModels);
        return new Result().ok();
    }

}
