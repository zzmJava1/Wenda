package com.newcode.service;

import com.newcode.Util.JedisAdapter;
import com.newcode.Util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class FollowService {

    @Autowired
    JedisAdapter jedisAdapter;


    //关注服务
    public boolean follow (int userId , int entityType , int entityId ){
        String followerKey = RedisKeyUtil.getFollowerkey(entityType,entityId);
        String followeeKey = RedisKeyUtil.getFolloweekey(userId,entityType);

        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        //开启事务
        Transaction ts = jedisAdapter.multi(jedis);
        //粉丝，记录粉丝id
        ts.zadd(followerKey,date.getTime(),String.valueOf(userId));
        //关注的对象的id，可能是问题，也可能是用户
        ts.zadd(followeeKey,date.getTime(),String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(ts,jedis);
        return ret.size()==2&&(long)ret.get(0)>0&&(long)ret.get(1)>0;



    }

    //取消关注服务
    public boolean unfollow (int userId , int entityType , int entityId ){
        String followerKey = RedisKeyUtil.getFollowerkey(entityType,entityId);
        String followeeKey = RedisKeyUtil.getFolloweekey(userId,entityType);

        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        //开启事务
        Transaction ts = jedisAdapter.multi(jedis);
        //粉丝，记录粉丝id
        ts.zrem(followerKey,String.valueOf(userId));
        //关注的对象的id，可能是问题，也可能是用户
        ts.zrem(followeeKey,String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(ts,jedis);
        return ret.size()==2&&(long)ret.get(0)>0&&(long)ret.get(1)>0;
    }


    private List<Integer> getIdFromSet(Set<String> idSet){
        List<Integer> ids = new ArrayList<>();
        for (String s : idSet) {
            ids.add(Integer.parseInt(s));
        }
        return ids;
    }
    public List<Integer> getfollowers(int entityType,int entityId,int count ){
        String followerKey = RedisKeyUtil.getFollowerkey(entityType,entityId);
        return getIdFromSet(jedisAdapter.zrange(followerKey,0,count));

    }

    //用于分页
    public List<Integer> getfollowers(int entityType,int entityId,int offset,int count ){
        String followerKey = RedisKeyUtil.getFollowerkey(entityType,entityId);
        return getIdFromSet(jedisAdapter.zrange(followerKey,offset,count));

    }

    public List<Integer> getfollowees(int userId, int entityType,int count ){
        String followeeKey = RedisKeyUtil.getFolloweekey(userId,entityType);
        return getIdFromSet(jedisAdapter.zrange(followeeKey,0,count));

    }

    //用于分页
    public List<Integer> getfollowees(int userId, int entityType,int offset,int count ){
        String followeeKey = RedisKeyUtil.getFolloweekey(userId,entityType);
        return getIdFromSet(jedisAdapter.zrange(followeeKey,offset,count));

    }

    public long followerCount(int entityType,int entityId ){
        String followerKey = RedisKeyUtil.getFollowerkey(entityType,entityId);
        return jedisAdapter.zcard(followerKey);
    }

    public long followeeCount(int userId, int entityType ){
        String followeeKey = RedisKeyUtil.getFolloweekey(userId,entityType);
        return jedisAdapter.zcard(followeeKey);
    }

    public boolean isfollower(int userId,int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFollowerkey(entityType,entityId);
        return jedisAdapter.zscore(followerKey,String.valueOf(userId)) != null;
    }
}
