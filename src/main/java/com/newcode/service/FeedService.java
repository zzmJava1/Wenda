package com.newcode.service;

import com.newcode.Dao.FeedDao;
import com.newcode.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {

    @Autowired
    FeedDao feedDao ;
    public List<Feed> getUserFeeds(int maxId,List<Integer> userIds,int count){
        return feedDao.selectUserFeeds(maxId,userIds,count);
    }

    public boolean addFeed(Feed feed){
        feedDao.addFeed(feed);
        return feed.getId()>0;
    }
    public Feed getFeedById(int id){
        return feedDao.getFeedById(id);
    }

}
