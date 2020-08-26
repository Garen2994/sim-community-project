package com.garen.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = SimCommunityApplication.class)
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;
    
    @Test
    public void testStrings(){
        String rediskey = "test:count";
        redisTemplate.opsForValue().set(rediskey, 1);
        System.out.println(redisTemplate.opsForValue().get(rediskey));
        System.out.println(redisTemplate.opsForValue().increment(rediskey));
        System.out.println(redisTemplate.opsForValue().decrement(rediskey));
    }
    @Test
    public void testHash(){
        String redisKey = "test:user";
        
        redisTemplate.opsForHash().put(redisKey, "id", 1);
        redisTemplate.opsForHash().put(redisKey, "username", "Garen");
    
        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey,"username"));
    }
    
    @Test
    public void testList(){
        String redisKey = "test:ids";
        redisTemplate.opsForList().leftPush(redisKey,101);
        redisTemplate.opsForList().leftPush(redisKey,102);
        redisTemplate.opsForList().leftPush(redisKey,103);
        redisTemplate.opsForList().leftPush(redisKey,104);
        redisTemplate.opsForList().leftPush(redisKey,105);
    
        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().size(redisKey));
        
        List range = redisTemplate.opsForList().range(redisKey, 0, 2);
        
    }
    @Test
    public void testSets(){
        String redisKey = "test:student";
        
        redisTemplate.opsForSet().add(redisKey,"aaa","bbb","ccc","ddd");
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey)); //randomly pop
        Iterator iterator = redisTemplate.opsForSet().members(redisKey).iterator();
        while(iterator.hasNext()){
            String name = (String)iterator.next();
            System.out.println(name);
        }
    }
    
    @Test
    public void testZSets(){
        String redisKey = "test:teacher";
        redisTemplate.opsForZSet().add(redisKey, "aaa", 10);
        redisTemplate.opsForZSet().add(redisKey, "bbb", 20);
        redisTemplate.opsForZSet().add(redisKey, "ccc", 80);
        redisTemplate.opsForZSet().add(redisKey, "garen", 100);
        redisTemplate.opsForZSet().add(redisKey, "八戒",90);
    
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "ccc"));
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "ccc")); //reverseRank 从大到小
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0,2)); //从大到小前3名
    }
    @Test
    public void testKeys(){
        redisTemplate.delete("test:student");
        redisTemplate.hasKey("test:student");
        
        redisTemplate.expire("test:teacher", 10, TimeUnit.SECONDS);
    }
    
    //多次访问同一个key
    @Test
    public void testBoundOPerations(){
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }
    
    //编程式事务
    @Test
    public void testTransaction(){
        Object obj = redisTemplate.execute(new SessionCallback(){
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:tx";
                operations.multi(); //启用事务
                
                operations.opsForSet().add(redisKey, "kiki");
                operations.opsForSet().add(redisKey, "zizi");
                operations.opsForSet().add(redisKey, "lili");
                operations.opsForSet().add(redisKey, "chichi");
    
                System.out.println(operations.opsForSet().members(redisKey)); // 事务未提交时 命令在队列里没执行
                return operations.exec();
            }
        });
        System.out.println(obj);
    }
}
