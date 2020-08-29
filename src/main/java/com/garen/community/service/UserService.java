package com.garen.community.service;

import com.garen.community.dao.LoginTicketMapper;
import com.garen.community.dao.UserMapper;
import com.garen.community.entity.LoginTicket;
import com.garen.community.entity.User;
import com.garen.community.util.CommunityConstant;
import com.garen.community.util.CommunityUtil;
import com.garen.community.util.MailClient;
import com.garen.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.Thymeleaf;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private MailClient mailClient;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    @Autowired
    private RedisTemplate redisTemplate;
    
//    @Autowired
//    private LoginTicketMapper loginTicketMapper;
    
    //注入固定的值而不是Bean时,用@Value
    @Value("${community.path.domain}")
    private String domain;
    
    @Value("${server.servlet.context-path}")
    private String contextPath;
    
    //重构：用Redis实现的缓存来缓存用户信息
    public User findUserById(int id) {
//        return userMapper.selectById(id);
        User user = getCache(id);
        if (user == null) {
            user = initCache(id);
        }
        return user;
    }
    /* *********Register and Activation Model***********/
    
    /**
     * @description 用map存错误信息 返回给客户端
     * @param user
     * @return java.util.Map<java.lang.String, java.lang.Object>
     */
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        if (user == null) {
            throw new IllegalArgumentException("Illegal Argument");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "用户名不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }
        //验证账号
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该用户名已存在!");
            return map;
        }
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册!");
            return map;
        }
        
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0); //普通用户
        user.setStatus(0); //未激活
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        
        //发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        //http://localhost:8080/community/activation/101/code
        //Mybatis回填自动生成id
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "Account Activation", content);
        return map;
    }
    
    /**
     * @description 激活判断
     * @param userId
     * @param code
     * @return int
     */
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            clearCache(userId); //有修改就清理缓存
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }
    
    /* ********Login Model********** */
    
    /**
     * @description 返回的错误信息用Map存
     * @param username
     * @param password
     * @return java.util.Map<java.lang.String, java.lang.Object>
     */
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        //空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "用户名不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        //验证
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该用户不存在！");
            return map;
        }
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号尚未激活！");
            return map;
        }
        
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码错误！");
            return map;
        }
        
        //生成登陆凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
//        loginTicketMapper.insertLoginTicket(loginTicket);
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);
        map.put("ticket", loginTicket.getTicket());
        
        return map;
    }
    //log out
    public void logout(String ticket){
//        loginTicketMapper.updateStatus(ticket, 1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }
    
    public LoginTicket findLoginTicket(String ticket){
//        return loginTicketMapper.selectByTicket(ticket);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }
    /* *******upload header******* */
    public int updateHeader(int userId, String headerUrl){
//        return userMapper.updateHeader(userId, headerUrl);
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;
    }
    
    /* ******Reset Password******** */
    public Map<String,Object> resetPassword(String email, String password) {
        Map<String, Object> map = new HashMap<>();
        if(StringUtils.isBlank(email)){
            map.put("emailMsg", "邮箱不能为空!");
        }
        User user = userMapper.selectByEmail(email);
        if(user == null) {
            map.put("emailMsg", "该邮箱未注册过!");
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg", "密码不能为空!");
        }
        if(user != null && !StringUtils.isBlank(password)){
            userMapper.updatePassword(user.getId(), CommunityUtil.md5(password+ user.getSalt()));
            user = userMapper.selectByEmail(email);
            System.out.println("密码更改成功");
            map.put("user",user);
        }
        return map;
    }
    /* ******Update Password******** */
    public Map<String,Object> updatePassword(int userId, String oldPassword,String newPassword){
        User user = userMapper.selectById(userId);
        Map<String,Object> map = new HashMap<>();
        if(!user.getPassword().equals(CommunityUtil.md5(oldPassword+user.getSalt()))){
            map.put("oldPasswordMsg", "原密码错误!");
        }
        if(StringUtils.isBlank(newPassword)){
            map.put("newPasswordMsg", "新密码不能为空");
    
        }
        if(user.getPassword().equals(CommunityUtil.md5(oldPassword + user.getSalt()))){
            userMapper.updatePassword(user.getId(), newPassword);
            user = userMapper.selectById(userId);
            map.put("user",user);
        }
        return map;
    }
    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }
    
    //1. 优先从缓存中取值
    private User getCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User)redisTemplate.opsForValue().get(redisKey);
    }
    //2.取不到时初始化缓存数据
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS); //3600s失效
        return user;
    }
    // 3.数据变更时清除缓存数据
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }
}
