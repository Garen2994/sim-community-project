package com.garen.community.controller;

import com.garen.community.entity.User;
import com.garen.community.service.UserService;
import com.garen.community.util.CommunityConstant;
import com.google.code.kaptcha.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @Title : feat:Register
 * @Author : garen_hou
 * @Email : garen2994@hotmail.com
 * @Date :  2020/7/26 9:33 下午
 */
@Controller
public class LoginController implements CommunityConstant {
    @Autowired
    private UserService userService;
    
    @Autowired
    private Producer kaptchaProducer;
    
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    /**
     * @param
     * @return java.lang.String
     * @description 获取注册页面
     */
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";
    }
    /**
     * @description //TODO
     * @param
     * @return java.lang.String
     */
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功! 请尽快激活您的账号~");
            model.addAttribute("target", "/index");//跳转目标设置
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            
            return "/site/register";
        }
    }
    
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg","激活成功！您的账号可以正常使用啦~");
            model.addAttribute("target","/login");
        } else if(result == ACTIVATION_REPEAT){
            model.addAttribute("msg","激活无效！该账号已经激活过啦~");
            model.addAttribute("target","/index");
        } else{
            model.addAttribute("msg","激活失败！您提供的激活码不正确~");
            model.addAttribute("target","/index");
            
        }
        return "/site/operate-result";
    }
    
    //验证码生成
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    //向浏览器输出图片 手动response
    // 存放到服务器端
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        String text = kaptchaProducer.createText(); //根据配置生成字符串
        BufferedImage image = kaptchaProducer.createImage(text);
        session.setAttribute("kaptcha", text);
//        System.out.println(session.getAttribute("kaptcha"));
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败！" + e.getMessage());
        }
    }
}