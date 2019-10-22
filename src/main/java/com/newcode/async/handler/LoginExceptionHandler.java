package com.newcode.async.handler;

import com.newcode.Util.MailSender;
import com.newcode.async.EventHandler;
import com.newcode.async.EventModel;
import com.newcode.async.EventType;
import com.newcode.service.MessageService;
import com.newcode.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class LoginExceptionHandler implements EventHandler {

    @Autowired
    MailSender mailSender;

    @Autowired
    UserService userService;
    @Autowired
    MessageService messageService;
    @Override
    public void doHandler(EventModel model) {

        Map<String,Object> map = new HashMap<>();
        map.put("username",model.getExt("username"));
        mailSender.sendWithHTMLTemplate(model.getExt("email")," 登录ip异常", "mails/login_exception.html",map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {

        return Arrays.asList(EventType.LOGIN);
    }
}
