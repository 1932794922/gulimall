package com.august.gulimall.coupon.controller;

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
import com.august.gulimall.coupon.dto.SeckillSessionDTO;
import com.august.gulimall.coupon.excel.SeckillSessionExcel;
import com.august.gulimall.coupon.service.SeckillSessionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 秒杀活动场次
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@RestController
@RequestMapping("coupon/seckillsession")
@Api(tags="秒杀活动场次")
public class SeckillSessionController {
    @Autowired
    private SeckillSessionService seckillSessionService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    //@RequiresPermissions("coupon:seckillsession:page")
    public Result<PageData<SeckillSessionDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<SeckillSessionDTO> page = seckillSessionService.page(params);

        return new Result<PageData<SeckillSessionDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    //@RequiresPermissions("coupon:seckillsession:info")
    public Result<SeckillSessionDTO> get(@PathVariable("id") Long id){
        SeckillSessionDTO data = seckillSessionService.get(id);

        return new Result<SeckillSessionDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    //@RequiresPermissions("coupon:seckillsession:save")
    public Result save(@RequestBody SeckillSessionDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        seckillSessionService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    //@RequiresPermissions("coupon:seckillsession:update")
    public Result update(@RequestBody SeckillSessionDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        seckillSessionService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    //@RequiresPermissions("coupon:seckillsession:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        seckillSessionService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    //@RequiresPermissions("coupon:seckillsession:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<SeckillSessionDTO> list = seckillSessionService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, SeckillSessionExcel.class);
    }


    /**
     * 查询最近三天的秒杀商品
     */
    @GetMapping("last3daysSession")
    @ApiOperation("查询最近三天的秒杀商品")
    public Result<List<SeckillSessionDTO>> getLast3DaysSession(){
        List<SeckillSessionDTO> session = seckillSessionService.getLast3DaysSession();
        return new Result<List<SeckillSessionDTO>>().ok(session);
    }
}