package com.garen.community.controller;

import com.garen.community.entity.DiscussPost;
import com.garen.community.entity.User;
import com.garen.community.service.DiscussPostService;
import com.garen.community.service.UserService;
import com.garen.community.util.CommunityUtil;
import com.garen.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;
    
    //当前用户注入
    @Autowired
    private HostHolder hostHolder;
    
    @Autowired
    private UserService userService;
    
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "您还没有登录，请先登录哦");
        }
        
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        
        //报错情况统一处理
        return CommunityUtil.getJSONString(0, "发布成功");
    }
    
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPostById(@PathVariable("discussPostId") int discussPostId, Model model) {
        //帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
    
        User usr = userService.findUserById(post.getUserId());
        model.addAttribute("user",usr);
        
        return "site/discuss-detail";
        
    }
}
