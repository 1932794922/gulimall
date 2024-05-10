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
import com.august.gulimall.product.dto.AttrDTO;
import com.august.gulimall.product.entity.ProductAttrValueEntity;
import com.august.gulimall.product.excel.AttrExcel;
import com.august.gulimall.product.service.AttrService;
import com.august.gulimall.product.service.CategoryService;
import com.august.gulimall.product.service.ProductAttrValueService;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * 商品属性
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@RestController
@RequestMapping("product/attr")
@Api(tags = "商品属性")
public class AttrController {
    @Resource
    private AttrService attrService;

    @Resource
    CategoryService categoryService;

    @Resource
    ProductAttrValueService productAttrValueService;

    @ApiOperation("获取分类规格参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "catelogId", value = "分类id", paramType = "path", required = true, dataType = "long"),
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType = "String"),
    })
    @GetMapping("{attrType}/list/{catelogId}")
    public Result<PageData<AttrDTO>> baseList(@ApiIgnore @RequestParam Map<String, Object> params,
                                              @PathVariable("catelogId") Long catelogId,
                                              @PathVariable("attrType") String attrType) {
        PageData<AttrDTO> page = attrService.baseList(params, catelogId,attrType);
        return new Result<PageData<AttrDTO>>().ok(page);
    }


    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType = "String")
    })
    //@RequiresPermissions("product:attr:page")
    public Result<PageData<AttrDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<AttrDTO> page = attrService.page(params);

        return new Result<PageData<AttrDTO>>().ok(page);
    }


    @GetMapping("{id}")
    @ApiOperation("信息")
    //@RequiresPermissions("product:attr:info")
    public Result<AttrDTO> get(@PathVariable("id") Long id) {
        AttrDTO data = attrService.getDetail(id);

        return new Result<AttrDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    //@RequiresPermissions("product:attr:save")
    public Result save(@RequestBody AttrDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        attrService.saveDetail(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    //@RequiresPermissions("product:attr:update")
    public Result update(@RequestBody AttrDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        attrService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    //@RequiresPermissions("product:attr:delete")
    public Result delete(@RequestBody Long[] ids) {
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        attrService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    //@RequiresPermissions("product:attr:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<AttrDTO> list = attrService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, AttrExcel.class);
    }
    @ApiOperation("根据spuId查询规格参数信息")
    @LogOperation("根据spuId查询规格参数信息")
    @GetMapping("/base/listforspu/{spuId}")
    public Result<List<ProductAttrValueEntity>> baseAttrListForSpu(@PathVariable("spuId") Long spuId){

        List<ProductAttrValueEntity> entities = productAttrValueService.baseAttrListForSpu(spuId);

        return new Result<List<ProductAttrValueEntity>>().ok(entities);
    }

    @ApiOperation("修改spu规格参数信息")
    @LogOperation("修改spu规格参数信息")
    @PostMapping("/update/{spuId}")
    public Result updateSpuAttr(@PathVariable("spuId") Long spuId,
                           @RequestBody List<ProductAttrValueEntity> entities){
        productAttrValueService.updateSpuAttr(spuId,entities);
        return new Result();
    }
}