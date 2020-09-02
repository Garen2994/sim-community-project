package com.garen.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
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
    //统计20万个重复数据的独立总数
    @Test
    public void testHyperLogLog(){
        String redisKey = "test:h11:01";
        for (int i = 1; i < 100000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey,i);
        }
        for (int i = 1; i < 100000; i++) {
            int r = (int)(Math.random()*100000+1);
            redisTemplate.opsForHyperLogLog().add(redisKey,r);
        }
        System.out.println(redisTemplate.opsForHyperLogLog().size(redisKey));
    }
    //将3组数据合并，再统计合并后的总数
    @Test
    public void testHyperLogLogUnion() {
        String redisKey2 = "test:h11:02";
        for (int i = 1; i < 10000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey2, i);
        }
        String redisKey3 = "test:h11:03";
        for (int i = 5001; i < 15000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey3, i);
        }
        String redisKey4 = "test:h11:04";
        for (int i = 10001; i < 20000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey4, i);
        }
        String unionKey = "test:h11:union";
        redisTemplate.opsForHyperLogLog().union(unionKey, redisKey2, redisKey3, redisKey4);
        long size = redisTemplate.opsForHyperLogLog().size(unionKey);
        System.out.println(size);
    }
    @Test
    public void testBitMap(){
        //统计一组数据的布尔值
        String redisKey = "test:bm:01";
        redisTemplate.opsForValue().setBit(redisKey,1,true);
        redisTemplate.opsForValue().setBit(redisKey,4,true);
        redisTemplate.opsForValue().setBit(redisKey,7,true);
    
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,2));
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                //可以多个做OR，XOR等位运算
                return redisConnection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);
        
    }
}
