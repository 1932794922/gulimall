package com.august.gulimall.auth.web;

import com.august.gulimall.auth.service.LoginService;
import com.august.gulimall.auth.vo.UserLoginVO;
import com.august.gulimall.auth.vo.UserRegistVO;
import com.august.gulimall.common.constant.AuthServerConstant;
import com.august.gulimall.common.exception.RenException;
import com.august.gulimall.common.to.MemberTO;
import com.august.gulimall.common.utils.Result;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : Crazy_August
 * @description : 登录控制器
 * @Time: 2023-05-12   13:49
 */
@Controller
public class LoginController {

    @Resource
    private LoginService loginService;

    @GetMapping("/login.html")
    public String loginPage(HttpSession session) {
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute == null) {
            // 没登陆去登录页
            return "login";
        } else {
            // 已经登录不能访问登录页，直接去首页
            return "redirect:http://xx.com";
        }
    }


    @ResponseBody
    @GetMapping("/email/sendCode")
    public Result sendEmail(@RequestParam("email") String email) {
        loginService.sendEmail(email);
        return new Result();
    }

    /**
     * TODO: 重定向携带数据：利用session原理，将数据放在session中。
     * TODO:只要跳转到下一个页面取出这个数据以后，session里面的数据就会删掉
     * TODO：分布下session问题
     * RedirectAttributes：重定向也可以保留数据，不会丢失，如果存在model中会丢失
     * 用户注册
     *
     * @return
     */
    @PostMapping("/regist")
    public String regist(@Valid UserRegistVO vo, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            Map<String, Object> errors = result.getFieldErrors().stream().collect(
                    Collectors.toMap(FieldError::getField,
                            FieldError::getDefaultMessage
                    ));
            attributes.addFlashAttribute("errors", errors);
            // 校验出错，返回注册页
            //效验出错回到注册页面，因为这是post请求不能直接写return "redict:reg"; 因为回到注册页是get请求
            // 也不能写"redict:/reg.html"; 不然访问的不是域名而是192.168.193.123:20000...
            // 重定向可以解决表单重复提交，因为这样用户刷新的是重定向的页面，而不是提交表单的页面
            //attributes.addFlashAttribute("errors", result.getFieldErrors());
            return "redirect:http://auth.xx.com/reg.html";
        }
        try {
            loginService.registr(vo);
        } catch (RenException e) {
            HashMap<String, String> errors = new HashMap<>();
            if (e.getCode() == 4000) {
                errors.put("code", e.getMsg());
            } else if (e.getCode() == 4001) {
                errors.put("userName", e.getMsg());
            } else if (e.getCode() == 4002) {
                errors.put("phone", e.getMsg());
            } else if (e.getCode() == 4003) {
                errors.put("email", e.getMsg());
            }
            // 这里的异常是自定义异常，所以不会被springboot处理，所以要自己处理
            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.xx.com/reg.html";
        }
        return "redirect:http://auth.xx.com/login.html";
    }


    // 因为页面不是前后端分离，传来的是k-v，所以没用@RequestBody; RedirectAttributes用来存放错误消息
    @PostMapping("/login")
    public String login(UserLoginVO vo, RedirectAttributes attributes, HttpSession session) {
        try {
            Result<MemberTO> member = loginService.login(vo);
            // 调用远程登录
            if (member.getCode() == 200) {
                MemberTO data = member.getData();
                // 成功登录，用户信息放入session，因为整合了SpringSession会把值存入redis
                session.setAttribute(AuthServerConstant.LOGIN_USER, data);
                return "redirect:http://xx.com";
            }
        } catch (RenException e) {
            attributes.addFlashAttribute("errors", e.getMsg());
            return "redirect:http://auth.xx.com/login.html";
        }
        return "redirect:http://auth.xx.com/login.html";
    }
}