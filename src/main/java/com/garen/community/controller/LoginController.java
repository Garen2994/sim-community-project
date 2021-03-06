package com.garen.community.controller;

import com.garen.community.entity.User;
import com.garen.community.service.UserService;
import com.garen.community.util.CommunityConstant;
import com.garen.community.util.CommunityUtil;
import com.garen.community.util.MailClient;
import com.garen.community.util.RedisKeyUtil;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Title : feat:Register
 * @Author : garen_hou
 * @Email : garen2994@hotmail.com
 * @Date :  2020/7/26 9:33 下午
 */
@Controller
public class LoginController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private Producer kaptchaProducer;
    
    @Value("server.servlet.context-path")
    private String contextPath;
    
    @Autowired
    private MailClient mailClient;
    
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * @description 获取注册页面
     * @param
     * @return java.lang.String
     */
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";
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
    public void getKaptcha(HttpServletResponse response /*HttpSession session*/){
        String text = kaptchaProducer.createText(); //根据配置生成字符串
        BufferedImage image = kaptchaProducer.createImage(text);
        //重构： 验证码不再存入session
//        session.setAttribute("kaptcha", text);
        //验证码的归属
        String kaptchaOwner = CommunityUtil.generateUUID();  //临时给客户端一个凭证，所以用Cookie保存
        Cookie cookie  = new Cookie("kaptchaOwner",kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        
        //将验证码存入Redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        //设置验证码有效时间为60s
        redisTemplate.opsForValue().set(redisKey,text,60, TimeUnit.SECONDS);
        
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败！" + e.getMessage());
        }
    }
    
    /**
     * @description 获取登陆页面
     * @param
     * @return java.lang.String
     */
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }
    //路径可以相同 请求方式不相同
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberMe, Model model, /*HttpSession session,*/
                        HttpServletResponse response,
                        @CookieValue("kaptchaOwner") String kaptchaOwner){
        //check verify code
//        String kaptcha = (String)session.getAttribute("kaptcha");
        String kaptcha = null;
        if(StringUtils.isNotBlank(kaptchaOwner)){
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }
        
        
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg", "验证码错误！");
            return "/site/login";
        }
        //check account and password
        int expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_TIME;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if(map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            //跳转至首页
            return "redirect:/index";
        } else{
            //错误消息带回登陆界面
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }
    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }
    
    /* *****forget password***** */
    // 忘记密码页面
    @RequestMapping(path = "/forget", method = RequestMethod.GET)
    public String getForgetPage() {
        return "/site/forget";
    }
    
    @RequestMapping(path = "/forget/code",method = RequestMethod.GET)
    @ResponseBody
    public String getForgetCode(String email, HttpSession session){
        if(StringUtils.isBlank(email)){
            return CommunityUtil.getJSONString(1,"邮箱不能为空！");
        }
        Context context = new Context();
        context.setVariable("email", email);
        String code = CommunityUtil.generateUUID().substring(0, 4);
        context.setVariable("verifyCode", code);
        String content = templateEngine.process("/mail/forget", context);
        mailClient.sendMail(email, "找回密码", content);
    
        // 保存验证码
        session.setAttribute("verifyCode", code);
        return CommunityUtil.getJSONString(0);
    }
    
    // Reset password
    @RequestMapping(path = "/forget/password", method = RequestMethod.POST)
    public String resetPassword(String email, String verifyCode, String password, Model model, HttpSession session) {
        String code = (String) session.getAttribute("verifyCode");
        if (StringUtils.isBlank(verifyCode) || StringUtils.isBlank(code) || !code.equalsIgnoreCase(verifyCode)) {
            model.addAttribute("codeMsg", "验证码错误!");
            return "/site/forget";
        }
        
        Map<String, Object> map = userService.resetPassword(email, password);
        if (map.containsKey("user")) {
            return "redirect:/login";
        } else {
            model.addAttribute("emailMsg", map.get("emailMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/forget";
        }
    }
}