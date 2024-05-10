package com.august.gulimall.product.web;

import com.august.gulimall.product.service.SkuInfoService;
import com.august.gulimall.product.vo.SkuItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;

/**
 * @author : Crazy_August
 * @description : 商品详情
 * @Time: 2023-05-11   15:42
 */
@Controller
@Slf4j
public class ItemController {

    @Resource
    private SkuInfoService skuInfoService;

    /**
     * 展示当前sku的详情
     */
    @GetMapping(value = "/{skuId}.html")
    public String item(@PathVariable Long skuId, Model model) {
        SkuItemVO vo = skuInfoService.item(skuId);
        model.addAttribute("item", vo);
        return "item";
    }
}
