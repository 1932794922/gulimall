package com.august.gulimall.seckill.controller;

import com.august.gulimall.common.utils.Result;
import com.august.gulimall.seckill.dto.SeckillSessionRedisDTO;
import com.august.gulimall.seckill.service.SeckillService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xzy
 */
@Controller
@RequestMapping("/seckill")
public class SecKillController {

    @Resource
    private SeckillService secKillService;

    /**
     * 当前时间可以参与秒杀的商品信息
     * @return Result<List<SeckillSessionRedisDTO>>
     */
    @GetMapping("getCurrentSeckillSkus")
    @ResponseBody
    public Result<List<SeckillSessionRedisDTO>> getCurrentSeckillSkus() {
        //获取到当前可以参加秒杀商品的信息
        List<SeckillSessionRedisDTO> vos = secKillService.getCurrentSeckillSkus();
        return new Result<List<SeckillSessionRedisDTO>>().ok(vos);
    }

    @ResponseBody
    @GetMapping(value = "/getSeckillSkuInfo/{skuId}")
    public Result getSeckillSkuInfo(@PathVariable("skuId") Long skuId) {
        SeckillSessionRedisDTO to = secKillService.getSeckillSkuInfo(skuId);
        return new Result<SeckillSessionRedisDTO>().ok(to);
    }

    @GetMapping("/kill")
    public String kill(@RequestParam("killId") String killId,
                       @RequestParam("key") String key,
                       @RequestParam("num") Integer num,
                       Model model) {
        String orderSn = null;
        try {
            //orderSn = secKillService.kill(killId, key, num);
            model.addAttribute("orderSn", orderSn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }


}
