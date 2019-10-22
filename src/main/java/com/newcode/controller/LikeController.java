package com.newcode.controller;

import com.newcode.Util.WendaUtil;
import com.newcode.async.EventModel;
import com.newcode.async.EventProducer;
import com.newcode.async.EventType;
import com.newcode.model.Comment;
import com.newcode.model.EntityType;
import com.newcode.model.HostHolder;
import com.newcode.service.CommentService;
import com.newcode.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    CommentService commentService;

    @RequestMapping(path = "/like", method = {RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId){
        if(hostHolder.getUser()==null){
            return WendaUtil.getJSONString(999);
        }

        Comment comment = commentService.getCommentById(commentId);

        eventProducer.fireEvent(new EventModel(EventType.LIKE)
                .setActorId(hostHolder.getUser().getId()).setEntityId(commentId)
                .setEntityType(EntityType.ENTITY_COMMENT).setEntityOwner(comment.getUserId())
                .setExt("questionId", String.valueOf(comment.getEntityId())));

        long count = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);

        return WendaUtil.getJSONString(0,String.valueOf(count));
    }

    @RequestMapping(path = "/dislike", method = {RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("commentId") int commentId){
        if(hostHolder.getUser()==null){
            return WendaUtil.getJSONString(999);
        }
        long count = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);

        return WendaUtil.getJSONString(0,String.valueOf(count));
    }

}
