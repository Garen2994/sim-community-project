package com.garen.community.controller;

import com.garen.community.entity.DiscussPost;
import com.garen.community.entity.Page;
import com.garen.community.entity.User;
import com.garen.community.service.DiscussPostService;
import com.garen.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;
    
    @Autowired
    private UserService userService;
    
    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        //model和page都是DispatcherServlet 自动实例化，page自动注入到Model中，所以thymeleaf可以直接访问Page对象中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");
        List<DiscussPost> posts = discussPostService.findDiscussPost(0, page.getOffset(),page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if(posts != null){
            for (DiscussPost post : posts) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);
                discussPosts.add(map);
            }
        }
        System.out.println();
        
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }
}
