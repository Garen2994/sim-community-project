package com.garen.community;

import com.garen.community.dao.DiscussPostMapper;
import com.garen.community.dao.LoginTicketMapper;
import com.garen.community.dao.MessageMapper;
import com.garen.community.dao.UserMapper;
import com.garen.community.entity.DiscussPost;
import com.garen.community.entity.LoginTicket;
import com.garen.community.entity.Message;
import com.garen.community.entity.User;
import com.garen.community.service.DiscussPostService;
import com.garen.community.service.MessageService;
import com.garen.community.util.CommunityConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = SimCommunityApplication.class)
public class MapperTest  implements CommunityConstant{
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    
    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user.toString());
        
        userMapper.selectByName("Liubei");
        System.out.println(user);
        
        userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }
    
    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@163.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());
        
        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }
    
    @Test
    public void testUpdateUser() {
        int row = userMapper.updateStatus(150, 1);
        System.out.println(row);
        userMapper.updateHeader(150, "http://www.nowcoder.com/102.png");
        userMapper.updatePassword(150, "152994");
    }
    
    @Test
    public void testSelectPost() {
        List<DiscussPost> posts = discussPostMapper.selectDiscussPosts(149, 0, 10,0);
        for (DiscussPost post : posts) {
            System.out.println(post);
        }
    }
    @Test
    public void testAddPost() {
        DiscussPost post = new DiscussPost();
         post.setTitle("aaa");
         post.setContent("nihaonihao,Offer");
         post.setUserId(280);
         post.setStatus(1);
         post.setType(0);
         post.setCreateTime(new Date());
         post.setScore(2);
        discussPostService.addDiscussPost(post);
        
    }
    
    
    @Test
    public void testSelectPostRows() {
        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);
    }
    
    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(190);
        loginTicket.setTicket("garen ticket");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        
        loginTicketMapper.insertLoginTicket(loginTicket);
    }
    
    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("garen ticket");
        System.out.println(loginTicket);
        
        loginTicketMapper.updateStatus("garen ticket", 1);
        loginTicket = loginTicketMapper.selectByTicket("garen ticket");
        System.out.println(loginTicket);
        
    }
    @Autowired
    private MessageService messageService;
//    @Test
//    public void testSelectLetters(){
//        List<Message> list = messageMapper.selectConversations(111,0,20);
//        for (Message message : list) {
//            System.out.println(message);
//        }
//    }
    ;@Test
    public void testSelectNotice(){
        messageService.findNoticeCount(159, TOPIC_COMMENT);
        messageService.findNoticeUnreadCount(159, TOPIC_FOLLOW);
        messageService.findLatestNotice(159, TOPIC_LIKE);
        System.out.println(messageService.findNotices(159, TOPIC_FOLLOW, 0, 20).toArray());
        
    }
}
