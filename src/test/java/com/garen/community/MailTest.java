package com.garen.community;

import com.garen.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = SimCommunityApplication.class)
public class MailTest {
    
    @Autowired
    private MailClient mailClient;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    @Test
    public void testMail(){
        mailClient.sendMail("1721864383@qq.com", "Test", "Welcome to my world! Garen");
    }
    
    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username", "Garen");
        String content = templateEngine.process("mail/demo", context);
        mailClient.sendMail("1721864383@qq.com","HTML",content);
    }
}
