package com.newcode.Util;

import com.alibaba.fastjson.JSONObject;
import com.newcode.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/*
redis测试
 */

@Service
public class JedisAdapter implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool pool;


    public static void print(int index,Object obj){
        System.out.println(String.format("%d,%s",index,obj.toString()));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("redis://localhost:6379/10");
    }

    public List<String> lrange(String key,int start,int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrange(key,start,end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public Jedis getJedis(){
        return pool.getResource();

    }

    public Double zscore(String key,String member){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key,member);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long zcard(String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public Set<String> zrange(String key,int start,int end){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrange(key,start,end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long zadd(String key,Double score,String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zadd(key,score,value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public Transaction multi(Jedis jedis) {
        try {
            return jedis.multi();
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        }
        return null;
    }

    public List<Object> exec(Transaction ts,Jedis jedis)  {
        try {
            return ts.exec();
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        }finally {
            if(ts!=null){
                try {
                    ts.close();
                } catch (IOException e) {
                    logger.error("发生异常" + e.getMessage());
                }

            }
            if(jedis!=null){
                jedis.close();
            }
        }
        return null;
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }


    public long lpush(String key, String json) {

        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key,json);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return  0;
    }

    public long sadd(String key,String value){

        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key,value);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return  0;
    }

    public long scard (String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return  0;
    }

    public long srem(String key,String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key,value);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return  0;
    }

    public boolean sismember(String key , String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key,value);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return  false;
    }


    public static void main(String[] args) {
        Jedis jedis = new Jedis("redis://localhost:6379/9");//表示端口为6379，数据库选择9
        jedis.flushDB();//将数据库内容清空

        jedis.set("hello","world");
        print(1,jedis.get("hello"));

        jedis.rename("hello","newHello");
        print(2,jedis.get("newHello"));

        jedis.setex("hello1",15,"heihei");//可以设置过期时间，可以用与一些验证行为，如验证码、登录激活


        jedis.set("pv","100");
        jedis.incr("pv");//使pv的值自增
        jedis.incrBy("pv",5);//表示加5
        jedis.decrBy("pv",3);

        for (int i = 0; i < 10 ; i++) {

            jedis.lpush("list","a"+String.valueOf(i));//类似于栈先进后出

        }
        System.out.println(jedis.lrange("list",0,10));


        jedis.linsert("list", BinaryClient.LIST_POSITION.BEFORE, "a5","bb");//返回的是list的长度
        System.out.println(jedis.lrange("list",0,10));

        /*
        redis存储hash，好处：关系型数据库要添加一个字段会使表中所有数据都加上这个字段，而redis
        则不需要
         */

        jedis.hset("hashKey","id","1");
        jedis.hset("hashKey","name","zzm");
        jedis.hset("hashKey","age","1");
        jedis.hset("hashKey","gender","男");

        System.out.println(jedis.hgetAll("hashKey"));

        //判断是否存在某个key
        jedis.hexists("hashKey","age");

        jedis.hkeys("hashKey");
        jedis.hvals("hashKey");

        //不存在就写入,存在则不写入
        jedis.hsetnx("hashKey","age","15");

        //添加set
        for (int i = 0; i < 10 ; i++) {
            jedis.sadd("set1",String.valueOf(i));
            jedis.sadd("set2",String.valueOf(i*i));

        }
        System.out.println(jedis.smembers("set1"));
        System.out.println(jedis.smembers("set2"));
        System.out.println(jedis.sunion("set1","set2"));//求并集
        System.out.println(jedis.sdiff("set1","set2"));//求第一个集合有第二个集合没有的
        System.out.println(jedis.sinter("set1","set2"));//求二者的交集
        //判断是否存在
        jedis.sismember("set1","2");
        //移动元素
        jedis.smove("set2","set1","25");
        System.out.println(jedis.smembers("set1"));
        System.out.println(jedis.smembers("set2"));
        //集合中的元素的数量
        System.out.println(jedis.scard("set1"));
        //随机抽取
        System.out.println(jedis.srandmember("set1",2));

        /*
        sorted set 排序的集合也被称为优先级队列，底层是堆
        是根据score进行排序
         */
       String rankkey = "rankKey";
       jedis.zadd(rankkey,15,"jim");
       jedis.zadd(rankkey,5,"jm");
       jedis.zadd(rankkey,115,"j");
       jedis.zadd(rankkey,125,"me");

        System.out.println(jedis.zcard(rankkey));
        System.out.println(jedis.zcount(rankkey,60,150));//找出区间在60~150的人数

        //求得分的后三名
        System.out.println(jedis.zrange(rankkey,0,2));
        //求得分的前三名
        System.out.println(jedis.zrevrange(rankkey,0,2));

        //遍历
        Set<Tuple> tuples = jedis.zrangeByScoreWithScores(rankkey, 0, 150);
        for (Tuple tuple : tuples) {
            System.out.println(tuple.getElement()+":"+tuple.getScore());
        }

        //根据字母排序
        String sSet = "sSet";
        jedis.zadd(sSet,1,"a");
        jedis.zadd(sSet,1,"b");
        jedis.zadd(sSet,1,"c");
        jedis.zadd(sSet,1,"d");
        jedis.zadd(sSet,1,"e");
        jedis.zadd(sSet,1,"f");

        System.out.println(jedis.zlexcount(sSet,"-","+"));//相当于从正无穷到负无穷
        System.out.println(jedis.zlexcount(sSet,"[a","[d"));
        System.out.println(jedis.zlexcount(sSet,"(a","[d"));
        //删除
        jedis.zremrangeByLex(sSet,"(c","+");//删除c以上的值不包含c
        System.out.println(jedis.zrange(sSet,0,10));

        //测试连接池,用完要将连接归还否则会独占该链接
        JedisPool jedisPool = new JedisPool();
        Jedis resource = jedisPool.getResource();
        resource.close();

        /*

        通过redis缓存
        例如：缓存一个user

         */
        User user = new User();
        user.setId(1);
        user.setName("zzm");
        user.setPassword("1234");
        user.setSalt("salt");
        user.setHeadUrl("sssss");
        //此时User只存在于内存并没有把它放在数据库中

        //Json会将对象转化为键值对的形式,序列化
        jedis.set("user1", JSONObject.
                toJSONString(user));

        System.out.println(jedis.get("user1"));
        //反序列化取出对象
        String value = jedis.get("user1");
        User user2 = JSONObject.parseObject(value,User.class);



    }


}
