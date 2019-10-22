package com.newcode.controller;

import com.newcode.Util.JedisAdapter;
import com.newcode.Util.RedisKeyUtil;
import com.newcode.model.EntityType;
import com.newcode.model.Feed;
import com.newcode.model.HostHolder;
import com.newcode.service.FeedService;
import com.newcode.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
public class FeedController {

    @Autowired
    HostHolder hostHolder;
    @Autowired
    FeedService feedService;
    @Autowired
    FollowService followService;
    @Autowired
    JedisAdapter jedisAdapter;

    //拉模式
    @RequestMapping( path = {"/pullfeeds"},method = RequestMethod.GET)
    public String getPullFeeds(Model model){
        int localUserId = hostHolder.getUser() ==null? 0:hostHolder.getUser().getId();
        List<Integer> followees = new ArrayList<>();
        if(localUserId!=0){
            followees = followService.getfollowees(localUserId, EntityType.ENTITY_USER,Integer.MAX_VALUE);

        }
        //将关注的人的动态取出10
        List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE, followees, 10);
        model.addAttribute("feeds",feeds);


        //放在feed.html渲染
        return "feeds";


    }
    //推，直接在jedis中获取feed
    @RequestMapping( path = {"/pushfeeds"},method = RequestMethod.GET)
    public String getPushFeeds(Model model){
        int localUserId = hostHolder.getUser() ==null? 0:hostHolder.getUser().getId();
        List<String> feedIds = jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localUserId), 0, 10);
        List<Feed> feeds = new ArrayList<>();
        for (String feedId : feedIds) {
            Feed feed = feedService.getFeedById(Integer.parseInt(feedId));
            if(feed==null) continue;
            feeds.add(feed);

        }
        model.addAttribute("feeds",feeds);
        return "feeds";
    }
}
