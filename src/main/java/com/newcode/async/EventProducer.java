package com.newcode.async;

import com.alibaba.fastjson.JSONObject;
import com.newcode.Util.JedisAdapter;
import com.newcode.Util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
将事件加入到队列中

 */
@Service
public class EventProducer {

    @Autowired
    JedisAdapter jedisAdapter;

    //将事件放到队列中
    public boolean fireEvent(EventModel eventModel){
        try {
            //将对象序列化
            String json = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key,json);

            return true;
        }catch (Exception e){
            
            return false;
        }
    }

}
