package com.newcode.Util;

/*
用来生成redis的key
 */
public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIS_LIKE = "LIKE";
    private static String BIS_DISLIKE = "DISLIKE";
    private static String BIS_EVENTQUEUE = "EVENTQUEUE ";
    //粉丝
    private static String BIS_FOLLOWER = "FOLLOWER";
    //关注对象
    private static String BIS_FOLLOWEE = "FOLLOWEE";
    private static String BIZ_TIMELINE = "TIMELINE";


    public static String getFollowerkey(int entityType,int entityId){
        return BIS_FOLLOWER+SPLIT+String.valueOf(entityType)+SPLIT+String.valueOf(entityId);

    }

    public static String getFolloweekey(int userId, int entityType){
        return BIS_FOLLOWEE+SPLIT+String.valueOf(userId)+SPLIT+String.valueOf(entityType);

    }

    public static String getLikekey(int entityType,int entityId){
        return BIS_LIKE+SPLIT+String.valueOf(entityType)+SPLIT+String.valueOf(entityId);

    }

    public static String getDislikekey(int entityType,int entityId){
        return BIS_DISLIKE+SPLIT+String.valueOf(entityType)+SPLIT+String.valueOf(entityId);

    }
    public static String getEventQueueKey(){
        return BIS_EVENTQUEUE;

    }
    public static String getTimelineKey(int userId) {
        return BIZ_TIMELINE + SPLIT + String.valueOf(userId);
    }

}
