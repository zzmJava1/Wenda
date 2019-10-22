package com.newcode.controller;

import com.newcode.Util.WendaUtil;
import com.newcode.model.HostHolder;
import com.newcode.model.Message;
import com.newcode.model.User;
import com.newcode.model.ViewObject;
import com.newcode.service.MessageService;
import com.newcode.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {


    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    @Autowired
    MessageService messageService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @RequestMapping(path = "/msg/list",method = RequestMethod.GET)
    public String getConversationList(Model model){

        if(hostHolder.getUser()==null){
            return "redirect:/relogin";
        }
        int localUserId = hostHolder.getUser().getId();

        List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
        List<ViewObject> conversations = new ArrayList<>();
        for (Message message : conversationList) {
            ViewObject vo = new ViewObject();
            vo.set("message",message);
          //获取目标的id，我们希望展示的是localUser之外的人发的信息
            int targetId = message.getFromId()==localUserId?message.getToId():message.getFromId();
            vo.set("user",userService.getUser(targetId));
            vo.set("unread",messageService.getConversationUnreadCount(localUserId,message.getConversationId()));
            conversations.add(vo);
        }
        model.addAttribute("conversations",conversations);


        return  "letter";
    }

    @RequestMapping(path = "/msg/detail",method = RequestMethod.GET)
    public String getConversationDetail(Model model,@RequestParam("conversationId") String conversationId){
        try {
            List<Message> list = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> messages = new ArrayList<>();
            for (Message message : list) {
                //已经看过将hasRead置为1
                messageService.updateHasRead(message.getFromId());
                ViewObject vo = new ViewObject();
                vo.set("message",message);
                vo.set("user",userService.getUser(message.getFromId()));
                messages.add(vo);

            }
            model.addAttribute("messages",messages);
        }catch (Exception e){
            logger.error("获取详情失败"+e.getMessage());
        }

        return "letterDetail";
    }

    @RequestMapping(path = "/msg/addMessage",method = RequestMethod.POST)
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content){
        try {
            //若用户未登录
            if(hostHolder.getUser() == null) {
                return WendaUtil.getJSONString(999, "用户未登录");
            }
            User user = userService.selectUserByName(toName);
            if(user==null){
                return WendaUtil.getJSONString(1,"用户不存在");
            }
            Message message = new Message();
            message.setContent(content);
            message.setFromId(hostHolder.getUser().getId());
            message.setToId(user.getId());
            message.setCreatedDate(new Date());
           messageService.addMessage(message);
           return WendaUtil.getJSONString(0);


        }catch (Exception e){
            logger.error("发送消息失败"+e.getMessage());
            return WendaUtil.getJSONString(1,"发信失败");

        }
    }
}
