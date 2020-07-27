package com.garen.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @Title : Util:generate random strings
 * @Author : garen_hou
 * @Email : garen2994@hotmail.com
 * @Date :  2020/7/26 10:02 下午
 */
public class CommunityUtil {
    /**
     * @description UUID生成随机字符串
     * @param
     * @return java.lang.String
     */
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");//不要"-"
    }
    /**
     * @description MD5加密 :
     *              hello -> asdkjashfnsn123as
     *              hello + sasfk -> asdkjashfnsn123as12u3h
     * @param key
     * @return java.lang.String
     */
    public static String md5(String key){ //salt字段
        if(StringUtils.isBlank(key)){ //空值、空串、空格均判null
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    
    }}
