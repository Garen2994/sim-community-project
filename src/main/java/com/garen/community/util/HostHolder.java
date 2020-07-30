package com.garen.community.util;

import com.garen.community.entity.User;
import org.springframework.stereotype.Component;
/**
 * 持有用户信息 用于代替session对象
 */
@Component
public class HostHolder {
    private ThreadLocal<User> userThreads = new ThreadLocal<>();
    
    public void setUser(User user){
        userThreads.set(user);
    }
    public User getUser(){
        return userThreads.get();
    }
    public void clear(){
        userThreads.remove();
    }
}
