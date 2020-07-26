package com.garen.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Title : feat:Register
 * @Author : garen_hou
 * @Email : garen2994@hotmail.com
 * @Date :  2020/7/26 9:33 下午
 */
@Controller
public class LoginController {
    /**
     * @description 获取注册页面
     * @param
     * @return java.lang.String
     */
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }
}
