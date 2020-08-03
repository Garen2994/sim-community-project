package com.garen.community;

import com.garen.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = SimCommunityApplication.class)
public class SensitiveFilterTest {
    @Autowired
    private SensitiveFilter sensitiveFilter;
    
    @Test
    public void testSensitiveFilter() {
        //这是发的人比较初级的
        String text = "你就是傻逼呀，脑残极了，还赌博和吸毒" + ",敢不敢不用小号";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
        
        text = "你就是☆傻☆逼☆呀，☆脑☆残极了，还赌☆博和吸☆毒" + ",敢不敢不用☆小☆号"+"☆f☆a☆b☆c☆";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }
    
}
