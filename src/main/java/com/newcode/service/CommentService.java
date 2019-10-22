package com.newcode.service;

import com.newcode.Dao.CommentDao;
import com.newcode.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    CommentDao commentDao;


    @Autowired
    SensitiveService sensitiveService;

    public List<Comment> getCommentByEntity(int entityId,int entityType){
        return commentDao.selectCommentByEntity(entityId,entityType);
    }



    public int getCommentCount(int entityId,int entityType){
        return commentDao.getCommentCount(entityId,entityType);

    }
    /*
       返回值代表了是否添加成功，若成功则返回0，失败则返回评论的id
    */
    public int addComment(Comment comment){
        //敏感词过滤
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDao.addComment(comment)>0?comment.getId():0;

    }

    public boolean deleteComment(int id  ){

        return commentDao.updateStatus(id,1)>0;
    }

    public Comment getCommentById(int id){
        return commentDao.getCommentById(id);
    }

    public int getUserCommentCount(int userId){
        return commentDao.getUserCommentCount(userId);
    }

}
