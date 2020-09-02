package com.garen.community.controller;

import com.garen.community.entity.DiscussPost;
import com.garen.community.entity.Page;
import com.garen.community.service.ElasticsearchService;
import com.garen.community.service.LikeService;
import com.garen.community.service.UserService;
import com.garen.community.util.CommunityConstant;
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
public class SearchController implements CommunityConstant {
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    
    @RequestMapping(path = "/search",method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model){
        org.springframework.data.domain.Page<DiscussPost> searchResults = elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (searchResults != null){
            for (DiscussPost post : searchResults) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                //作者
                map.put("user",userService.findUserById(post.getUserId()));
                //获赞数
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId()));
                
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("keyword",keyword);
        
        //分页信息
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResults == null ?0 :(int) searchResults.getTotalElements());
        
        return "site/search.html";
    }
}
