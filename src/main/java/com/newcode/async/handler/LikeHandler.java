package com.newcode.async.handler;

import com.newcode.Util.WendaUtil;
import com.newcode.async.EventHandler;
import com.newcode.async.EventModel;
import com.newcode.async.EventType;
import com.newcode.model.Message;
import com.newcode.model.User;
import com.newcode.service.MessageService;
import com.newcode.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHandler {

    @Autowired
    UserService userService;
    @Autowired
    MessageService messageService;
    @Override
    public void doHandler(EventModel model) {
        Message message = new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(model.getEntityOwner());
        message.setCreatedDate(new Date());
        User user = userService.getUser(model.getActorId());
        message.setContent("用户"+user.getName()+"赞了你的评论，http://127.0.0.1:8080/question/"+
        model.getExt("questionId"));
        messageService.addMessage(message);

    }

    @Override
    public List<EventType> getSupportEventTypes() {

        return Arrays.asList(EventType.LIKE);
    }
}
