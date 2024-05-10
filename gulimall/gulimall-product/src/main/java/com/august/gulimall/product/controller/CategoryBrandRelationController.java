package com.august.gulimall.product.controller;

import com.august.gulimall.common.annotation.LogOperation;
import com.august.gulimall.common.constant.Constant;
import com.august.gulimall.common.page.PageData;
import com.august.gulimall.common.utils.ExcelUtils;
import com.august.gulimall.common.utils.Result;
import com.august.gulimall.common.validator.AssertUtils;
import com.august.gulimall.common.validator.ValidatorUtils;
import com.august.gulimall.common.validator.group.AddGroup;
import com.august.gulimall.common.validator.group.DefaultGroup;
import com.august.gulimall.common.validator.group.UpdateGroup;

import com.august.gulimall.product.dto.BrandDTO;
import com.august.gulimall.product.dto.CategoryBrandRelationDTO;
import com.august.gulimall.product.excel.CategoryBrandRelationExcel;
import com.august.gulimall.product.service.BrandService;
import com.august.gulimall.product.service.CategoryBrandRelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2023-04-02
 */
@RestController
@RequestMapping("product/categorybrandrelation")
@Api(tags = "")
public class CategoryBrandRelationController {
    @Resource
    private CategoryBrandRelationService categoryBrandRelationService;

    @Resource
    private BrandService brandService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType = "String")
    })
    //@RequiresPermissions("ware:categorybrandrelation:page")
    public Result<PageData<CategoryBrandRelationDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<CategoryBrandRelationDTO> page = categoryBrandRelationService.page(params);

        return new Result<PageData<CategoryBrandRelationDTO>>().ok(page);
    }


    @GetMapping("catelog/list")
    @ApiOperation("获取指定品牌关联的分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "brandId", value = "品牌id", paramType = "query", required = true, dataType = "long")
    })
    //@RequiresPermissions("ware:categorybrandrelation:page")
    public Result<List<CategoryBrandRelationDTO>> catelogList(@RequestParam Long brandId) {
        List<CategoryBrandRelationDTO> catelogList = categoryBrandRelationService.catelogList(brandId);

        return new Result<List<CategoryBrandRelationDTO>>().ok(catelogList);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    //@RequiresPermissions("ware:categorybrandrelation:info")
    public Result<CategoryBrandRelationDTO> get(@PathVariable("id") Long id) {
        CategoryBrandRelationDTO data = categoryBrandRelationService.get(id);

        return new Result<CategoryBrandRelationDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    //@RequiresPermissions("ware:categorybrandrelation:save")
    public Result save(@RequestBody CategoryBrandRelationDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        categoryBrandRelationService.save(dto);

        return new Result();
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    //@RequiresPermissions("ware:categorybrandrelation:save")
    public Result saveDetail(@RequestBody CategoryBrandRelationDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        categoryBrandRelationService.saveDetail(dto);
        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    //@RequiresPermissions("ware:categorybrandrelation:update")
    public Result update(@RequestBody CategoryBrandRelationDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        categoryBrandRelationService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    //@RequiresPermissions("ware:categorybrandrelation:delete")
    public Result delete(@RequestBody Long[] ids) {
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        categoryBrandRelationService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    //@RequiresPermissions("ware:categorybrandrelation:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<CategoryBrandRelationDTO> list = categoryBrandRelationService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, CategoryBrandRelationExcel.class);
    }



    /**
     * 获取品牌关联的分类
     * @param brandId
     * @return
     */
    @GetMapping("brands/list")
    public Result<List<CategoryBrandRelationDTO>> brandsListByCatId(@RequestParam Long catId) {
        List<CategoryBrandRelationDTO> brandList = brandService.brandsListByCatId(catId);

        return new Result<List<CategoryBrandRelationDTO>>().ok(brandList);
    }

}