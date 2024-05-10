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
import com.august.gulimall.product.dto.AttrAttrgroupRelationDTO;
import com.august.gulimall.product.dto.AttrDTO;
import com.august.gulimall.product.dto.AttrGroupDTO;
import com.august.gulimall.product.excel.AttrGroupExcel;
import com.august.gulimall.product.service.AttrGroupService;
import com.august.gulimall.product.service.AttrService;
import com.august.gulimall.product.service.CategoryService;
import com.august.gulimall.product.vo.AttrGroupWithAttrsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * 属性分组
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@RestController
@RequestMapping("product/attrgroup")
@Api(tags = "属性分组")
public class AttrGroupController {
    @Resource
    private AttrGroupService attrGroupService;


    @Resource
    private CategoryService categoryService;

    @Resource
    private AttrService attrService;


    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType = "String")
    })
    //@RequiresPermissions("product:attrgroup:page")
    public Result<PageData<AttrGroupDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<AttrGroupDTO> page = attrGroupService.page(params);

        return new Result<PageData<AttrGroupDTO>>().ok(page);
    }


    @GetMapping("list/{catelogId}")
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType = "String")
    })
    public Result<PageData<AttrGroupDTO>> pageList(@ApiIgnore @RequestParam Map<String, Object> params,
                                                   @PathVariable("catelogId") Long catelogId) {
        PageData<AttrGroupDTO> page = attrGroupService.pageList(params, catelogId);

        return new Result<PageData<AttrGroupDTO>>().ok(page);
    }


    ///product/attrgroup/{attrgroupId}/attr/relation
    @GetMapping("{attrGroupId}/attr/relation")
    @ApiOperation("获取属性分组的关联的所有属性")
    public Result<List<AttrDTO>> attrRelationByAttrGroupId(@PathVariable("attrGroupId") Long attrGroupId) {
        List<AttrDTO> page = attrService.getAttrRelation(attrGroupId);
        return new Result<List<AttrDTO>>().ok(page);
    }


    @GetMapping("{attrGroupId}/noattr/relation")
    @ApiOperation("获取属性分组没有关联的其他属性")
    public Result<PageData<AttrDTO>> notAttrRelationByAttrGroupId(@RequestParam Map<String, Object> params,@PathVariable("attrGroupId") Long attrGroupId) {
        PageData<AttrDTO> attrDTOList = attrService.getNotAttrRelation(params,attrGroupId);
        return new Result<PageData<AttrDTO>>().ok(attrDTOList);
    }

    @PostMapping("attr/relation/delete")
    @ApiOperation("删除属性与分组的关联关系")
    public Result attrRelationDelete(@RequestBody List<AttrAttrgroupRelationDTO>  attrAttrgroupRelationDTOList) {
        attrService.attrRelationDelete(attrAttrgroupRelationDTOList);
        return new Result().ok();
    }




    @GetMapping("{id}")
    @ApiOperation("信息")
    //@RequiresPermissions("product:attrgroup:info")
    public Result<AttrGroupDTO> get(@PathVariable("id") Long id) {
        AttrGroupDTO data = attrGroupService.get(id);
        Long catelogId = data.getCatelogId();
        List<Long> paths = categoryService.findCatelogPath(catelogId);
        Collections.reverse(paths);
        data.setCatelogPath(paths);
        return new Result<AttrGroupDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    //@RequiresPermissions("product:attrgroup:save")
    public Result save(@RequestBody AttrGroupDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        attrGroupService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    //@RequiresPermissions("product:attrgroup:update")
    public Result update(@RequestBody AttrGroupDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        attrGroupService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    //@RequiresPermissions("product:attrgroup:delete")
    public Result delete(@RequestBody Long[] ids) {
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        attrGroupService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    //@RequiresPermissions("product:attrgroup:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<AttrGroupDTO> list = attrGroupService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, AttrGroupExcel.class);
    }



    //attrgroup/attr/relation
    @PostMapping("/attr/relation")
    @ApiOperation("添加属性与分组的关联关系")
    public Result attrRelation(@RequestBody List<AttrAttrgroupRelationDTO>  attrAttrgroupRelationDTOList) {
        attrGroupService.attrRelation(attrAttrgroupRelationDTOList);
        return new Result().ok();
    }


    @GetMapping("{catelogId}/withattr")
    @ApiOperation("获取分类下所有分组&关联属性")
    public Result<List<AttrGroupWithAttrsVO>> getAttrGroupWithAttrsByCatelogId(@PathVariable("catelogId") Long catelogId) {
        List<AttrGroupWithAttrsVO> attrGroupWithAttrsDTOList = attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
        return new Result().ok(attrGroupWithAttrsDTOList);
    }
}