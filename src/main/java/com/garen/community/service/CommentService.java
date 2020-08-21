package com.garen.community.service;

import com.garen.community.dao.CommentMapper;
import com.garen.community.entity.Comment;
import com.garen.community.util.CommunityConstant;
import com.garen.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private SensitiveFilter sensitiveFilter;
    
    @Autowired
    private DiscussPostService discussPostService;
    
    public List<Comment> findCommentsByEntity(int entityType,int entityId,int offset,int limit){
        return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
    }
    public int findCommentCount(int entityType,int entityId){
        return commentMapper.selectCountByEntity(entityType, entityId);
    }
    
    
}
