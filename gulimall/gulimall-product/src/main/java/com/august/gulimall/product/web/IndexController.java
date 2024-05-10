package com.august.gulimall.product.web;

import com.august.gulimall.product.entity.CategoryEntity;
import com.august.gulimall.product.service.CategoryService;
import com.august.gulimall.product.vo.Catelog2VO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author : Crazy_August
 * @description : 首页控制器
 * @Time: 2023-05-02   21:56
 */
@Controller
public class IndexController {


    @Resource
    CategoryService categoryService;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {
        //查询一级分类数据
        List<CategoryEntity> categories = categoryService.getLevelOne();
        model.addAttribute("categories", categories);
        return "index";
    }

    @GetMapping("/index/catalog.json")
    @ResponseBody
    public Map<String, List<Catelog2VO>> getCatalogJson() {
        Map<String, List<Catelog2VO>> map = categoryService.getCatalogJson();
        return map;
    }
}
