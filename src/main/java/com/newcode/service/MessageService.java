package com.newcode.service;

import com.newcode.Dao.MessageDao;
import com.newcode.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    MessageDao messageDao;

    @Autowired
    SensitiveService sensitiveService;

    public int addMessage(Message message){
        //敏感词过滤
        message.setContent(sensitiveService.filter(message.getContent()));
        return messageDao.addMessage(message)>0?message.getId():0;
    }

    public List<Message> getConversationDetail( String conversationId,int offset,int limit){

        return messageDao.getConversationDetail(conversationId,offset,limit);
    }

    public List<Message> getConversationList(int userId, int offset, int limit){
        return messageDao.getConversationList(userId,offset,limit);
    }
    public int getConversationUnreadCount(int userId, String conversationId){
        return messageDao.getConversationUnreadCount(userId,conversationId);
    }
    public int updateHasRead(int userId){
        return messageDao.updateHasRead(userId);
    }
}
