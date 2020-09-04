package com.garen.community.controller;

import com.garen.community.entity.DiscussPost;
import com.garen.community.entity.Page;
import com.garen.community.entity.User;
import com.garen.community.service.DiscussPostService;
import com.garen.community.service.LikeService;
import com.garen.community.service.UserService;
import com.garen.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private LikeService likeService;
    
    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name = "orderMode", defaultValue = "0") int orderMode){
        //model和page都是DispatcherServlet 自动实例化，page自动注入到Model中，所以thymeleaf可以直接访问Page对象中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode=" + orderMode);
        List<DiscussPost> posts = discussPostService.findDiscussPosts(0, page.getOffset(),page.getLimit(),orderMode);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if(posts != null){
            for (DiscussPost post : posts) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);
                
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }
        System.out.println();
        
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("orderMode", orderMode);
        return "/index";
    }
    @RequestMapping(path="/error",method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }
    
    @RequestMapping(path="/denied",method = RequestMethod.GET)
    public String getDeniedPage(){
        return "/error/404";
    }
}
