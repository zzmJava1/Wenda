package com.newcode.service;

import com.newcode.Util.JedisAdapter;
import com.newcode.Util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    JedisAdapter jedisAdapter;

    public long getLikeCount(int entityType,int entityId){
        String likekey = RedisKeyUtil.getLikekey(entityType,entityId);
        return jedisAdapter.scard(likekey);
    }

    public long like(int userId,int entityType,int entityId){
        String likekey = RedisKeyUtil.getLikekey(entityType,entityId);
        jedisAdapter.sadd(likekey,String.valueOf(userId));
        String disLikeKey = RedisKeyUtil.getDislikekey(entityType,entityId);
        jedisAdapter.srem(disLikeKey,String.valueOf(userId));

        return jedisAdapter.scard(likekey);
    }

    public long disLike(int userId,int entityType,int entityId){
        String disLikeKey = RedisKeyUtil.getDislikekey(entityType,entityId);
        jedisAdapter.sadd(disLikeKey,String.valueOf(userId));
        String likekey = RedisKeyUtil.getLikekey(entityType,entityId);
        jedisAdapter.srem(likekey,String.valueOf(userId));

        return jedisAdapter.scard(likekey);
    }

    public int getLikeStatus(int userId,int entityType,int entityId){
        String likeKey = RedisKeyUtil.getLikekey(entityType,entityId);
        if(jedisAdapter.sismember(likeKey,String.valueOf(userId))){
            return 1;
        }
        String disLikeKey = RedisKeyUtil.getDislikekey(entityType,entityId);
        return jedisAdapter.sismember(disLikeKey,String.valueOf(userId))?-1:0;
    }
}
