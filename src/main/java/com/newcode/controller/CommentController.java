package com.newcode.controller;

import com.newcode.Util.WendaUtil;
import com.newcode.async.EventModel;
import com.newcode.async.EventProducer;
import com.newcode.async.EventType;
import com.newcode.model.Comment;
import com.newcode.model.EntityType;
import com.newcode.model.HostHolder;
import com.newcode.service.CommentService;
import com.newcode.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;


@Controller
public class CommentController {

    private static Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    CommentService commentService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    QuestionService questionService;
    @Autowired
    EventProducer eventProducer;

    /*
    向数据库写入用post
     */
    @RequestMapping( path = {"/addComment"},method = RequestMethod.POST)
    public String addComment(@RequestParam("questionId")int questionId,
                             @RequestParam("content") String content){
        try{
            Comment comment = new Comment();
            comment.setContent(content);

            if(hostHolder.getUser()!=null){
                comment.setUserId(hostHolder.getUser().getId());
            }else{
                comment.setUserId(WendaUtil.ANONYMOUS_USERID);

            }
            comment.setCreatedDate(new Date());
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setEntityId(questionId);
            commentService.addComment(comment);
            //在评论中增加评论数量
            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(),count);
            eventProducer.fireEvent(new EventModel(EventType.COMMENT).setActorId(comment.getUserId())
                    .setEntityId(questionId));
        }catch (Exception e){
            logger.error("增加评论失败"+e.getMessage());
        }

        return "redirect:/question/"+questionId;
    }
}
