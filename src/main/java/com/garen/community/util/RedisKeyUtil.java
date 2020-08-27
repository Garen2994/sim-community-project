package com.garen.community.util;

/*生成Redis的Key工具*/
public class RedisKeyUtil {
    private static final String SPLIT = ":"; //key拼接
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    
    // 某个实体(帖子和评论)的赞
    // like:entity:entityType:entityId -> set(userId)  可以看到谁点了赞
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }
    
}
