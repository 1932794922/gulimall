package com.august.gulimall.ware.controller;

import com.august.gulimall.common.annotation.LogOperation;
import com.august.gulimall.common.constant.Constant;
import com.august.gulimall.common.constant.WareConstant;
import com.august.gulimall.common.page.PageData;
import com.august.gulimall.common.utils.ExcelUtils;
import com.august.gulimall.common.utils.Result;
import com.august.gulimall.common.validator.AssertUtils;
import com.august.gulimall.common.validator.ValidatorUtils;
import com.august.gulimall.common.validator.group.AddGroup;
import com.august.gulimall.common.validator.group.DefaultGroup;
import com.august.gulimall.common.validator.group.UpdateGroup;
import com.august.gulimall.ware.dto.PurchaseDTO;
import com.august.gulimall.ware.excel.PurchaseExcel;
import com.august.gulimall.ware.service.PurchaseService;
import com.august.gulimall.ware.vo.MergeVO;
import com.august.gulimall.ware.vo.PurchaseDoneVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 采购信息
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@RestController
@RequestMapping("ware/purchase")
@Api(tags="采购信息")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    //@RequiresPermissions("ware:purchase:page")
    public Result<PageData<PurchaseDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<PurchaseDTO> page = purchaseService.page(params);

        return new Result<PageData<PurchaseDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    //@RequiresPermissions("ware:purchase:info")
    public Result<PurchaseDTO> get(@PathVariable("id") Long id){
        PurchaseDTO data = purchaseService.get(id);

        return new Result<PurchaseDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    //@RequiresPermissions("ware:purchase:save")
    public Result save(@RequestBody PurchaseDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        dto.setCreateTime(new Date(System.currentTimeMillis()));
        dto.setUpdateTime(new Date(System.currentTimeMillis()));
        dto.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
        purchaseService.save(dto);
        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    //@RequiresPermissions("ware:purchase:update")
    public Result update(@RequestBody PurchaseDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        purchaseService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    //@RequiresPermissions("ware:purchase:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        purchaseService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    //@RequiresPermissions("ware:purchase:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<PurchaseDTO> list = purchaseService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, PurchaseExcel.class);
    }

   @ApiOperation("查询未领取的采购单")
   @LogOperation("查询未领取的采购单")
    @GetMapping("unreceive/list")
    public Result<PageData<PurchaseDTO>> unReceiveList() {
        PageData<PurchaseDTO> pageData = purchaseService.unReceiveList();
       return new Result<PageData<PurchaseDTO>>().ok(pageData);
    }

    @ApiOperation("合并采购需求")
    @LogOperation("合并采购需求")
    @PostMapping("merge")
    public Result mergePurchase(@RequestBody MergeVO mergeVO) {
        purchaseService.mergePurchase(mergeVO);
        return new Result();
    }



    @ApiOperation("领取采购单")
    @LogOperation("领取采购单")
    @PostMapping("/received")
    public Result received(@RequestBody List<Long> ids){
        purchaseService.received(ids);
        return new Result();
    }

    @ApiOperation("完成采购单")
    @LogOperation("完成采购单")
    @PostMapping("/done")
    public Result finish(@RequestBody PurchaseDoneVO doneVo){
        purchaseService.done(doneVo);

         return new Result();
    }


}