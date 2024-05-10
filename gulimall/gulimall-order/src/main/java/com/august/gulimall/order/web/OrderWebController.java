package com.august.gulimall.order.web;

import com.august.gulimall.order.service.OrderService;
import com.august.gulimall.order.vo.OrderConfirmVO;
import com.august.gulimall.order.vo.OrderSubmitVO;
import com.august.gulimall.order.vo.SubmitOrderResponseVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;

/**
 * @author xzy
 */
@Controller
public class OrderWebController {

    @Resource
    private OrderService orderService;

    @GetMapping("{page}.html")
    public String test(@PathVariable String page){
        return page;
    }

    /**
     * 去结算确认页
     *
     * @param model
     */
    @GetMapping("/toTrade")
    public String toTrade(Model model) {
        OrderConfirmVO orderConfirmVo = orderService.confirmOrder();
        model.addAttribute("confirmOrderData", orderConfirmVo);
        return "confirm";
    }

    /**
     * 下单功能
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVO vo, Model model, RedirectAttributes attributes) {
        SubmitOrderResponseVO responseVo = orderService.submitOrder(vo);
        if (responseVo.getCode() == 200) {
            // 下单成功回到支付页
            model.addAttribute("submitOrderResp", responseVo);
            return "pay";
        } else {
            // 失败回到确认页
            String msg = "下单失败";
            switch (responseVo.getCode()) {
                case 1:
                    msg += "订单信息过期，请刷新再次提交";
                    break;
                case 2:
                    msg += "订单商品价格发生变化，请确认后再次提交";
                    break;
                case 3:
                    msg += "库存锁定失败，商品库存不足";
                    break;
                default:
                    break;
            }
            attributes.addFlashAttribute("msg", msg);
            return "redirect:http://order.xx.com/toTrade";
        }
    }
}