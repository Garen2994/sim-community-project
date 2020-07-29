package com.garen.community.util;
/**
 * @Title : 常量接口
 * @Author : garen_hou
 * @Email : garen2994@hotmail.com
 * @Date :  2020/7/27 4:43 下午
 */
public interface CommunityConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;
    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;
    
    int ACTIVATION_FAILURE = 2;
    
    /**
     * 默认状态的登陆凭证超时时间
     */
    int DEFAULT_EXPIRED_TIME = 3600 * 12;
    /**
     * 记住状态的登陆凭证超时时间
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24;
}
