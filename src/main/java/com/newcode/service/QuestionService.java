package com.newcode.service;

import com.newcode.Dao.QuestionDao;
import com.newcode.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Created by newcode on 2016/7/15.
 */
@Service
public class QuestionService {
    @Autowired
    QuestionDao questionDAO;
    @Autowired
    SensitiveService sensitiveService;

    public Question selectById(int id){
        return questionDAO.selectById(id);
    }

    public int addQuestion(Question question){

        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        //敏感词过滤
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));


        return questionDAO.addQuestion(question)>0?question.getId():0;
    }

    public List<Question> getLatestQuestions(int userId, int offset, int limit) {
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }
    public int updateCommentCount(int id, int count) {
        return questionDAO.updateCommentCount(id, count);
    }
}