package com.newcode.async;

import com.alibaba.fastjson.JSON;
import com.newcode.Util.JedisAdapter;
import com.newcode.Util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
用来关联handler和事件，看每个事件与哪个handler有关
 */
@Service
public class EventConsumer implements InitializingBean,ApplicationContextAware{

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType,List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;//Spring上下文Spring核心接口

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans != null) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();

                for (EventType type : eventTypes) {
                    if (!config.containsKey(type)) {
                        config.put(type, new ArrayList<EventHandler>());
                    }
                    config.get(type).add(entry.getValue());
                }
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    String key = RedisKeyUtil.getEventQueueKey();
                    List<String> events = jedisAdapter.brpop(0, key);

                    for (String message : events) {
                        if (message.equals(key)) {
                            continue;
                        }

                        EventModel eventModel = JSON.parseObject(message, EventModel.class);
                        if (!config.containsKey(eventModel.getEventType())) {
                            logger.error("不能识别的事件");
                            System.out.println(eventModel.getEventType());
                            continue;
                        }

                        for (EventHandler handler : config.get(eventModel.getEventType())) {
                            handler.doHandler(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
