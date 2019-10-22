package com.newcode.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.newcode.Util.JedisAdapter;
import com.newcode.Util.RedisKeyUtil;
import com.newcode.async.EventHandler;
import com.newcode.async.EventModel;
import com.newcode.async.EventType;
import com.newcode.model.EntityType;
import com.newcode.model.Feed;
import com.newcode.model.Question;
import com.newcode.model.User;
import com.newcode.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FeedHandler implements EventHandler {

    @Autowired
    UserService userService;
    @Autowired
    MessageService messageService;
    @Autowired
    QuestionService questionService;
    @Autowired
    FeedService feedService;
    @Autowired
    FollowService followService;
    @Autowired
    JedisAdapter jedisAdapter;

    private String buildFeedData(EventModel eventModel){
        //用map保存核心数据，例如用户头像，id，name等
        Map<String,String> map = new HashMap<>();
        User user = userService.getUser(eventModel.getActorId());
        if(user==null){
            return null;
        }
        map.put("userId",String.valueOf(user.getId()));
        map.put("userHead",user.getHeadUrl());
        map.put("userName",user.getName());
        if(eventModel.getEventType()==EventType.COMMENT
                ||eventModel.getEventType() == EventType.FOLLOW&&eventModel.getEntityType()== EntityType.ENTITY_QUESTION){
            Question question = questionService.selectById(eventModel.getEntityId());
            if(question==null)return null;
            map.put("questionId",String.valueOf(question.getId()));
            map.put("questionTitle",question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
     }

    @Override
    public void doHandler(EventModel model) {


        Feed feed = new Feed();
        feed.setUserId(model.getActorId());
        feed.setCreatedDate(new Date());
        feed.setType(model.getEventType().getValue());
        feed.setData(buildFeedData(model));
        if(feed.getData()==null){
            return;
        }
        feedService.addFeed(feed);

        //给粉丝推
        List<Integer> followers = followService.getfollowers(EntityType.ENTITY_USER, model.getActorId(), Integer.MAX_VALUE);
        followers.add(0);//表示系统
        for (int follower : followers) {
            String timelineKey = RedisKeyUtil.getTimelineKey(follower);
            jedisAdapter.lpush(timelineKey,String.valueOf(feed.getId()));
        }

    }
    @Override
    public List<EventType> getSupportEventTypes() {

        return Arrays.asList(new EventType[]{EventType.COMMENT,EventType.FOLLOW});
    }
}
