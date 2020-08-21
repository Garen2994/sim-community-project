package com.garen.community.service;

import com.garen.community.dao.MessageMapper;
import com.garen.community.entity.Message;
import com.garen.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.unbescape.html.HtmlEscape;

import java.util.List;

@Service
public class MessageService {
    
    @Autowired
    private MessageMapper messageMapper;
    
    @Autowired
    private SensitiveFilter sensitiveFilter;
    
    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }
    
    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }
    
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }
    
    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }
    
    public int findLetterUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }
    
    //新建私信
    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }
    //读私信
    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }
    
}

