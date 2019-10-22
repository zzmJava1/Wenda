package com.newcode.controller;

import com.newcode.async.EventModel;
import com.newcode.async.EventProducer;
import com.newcode.async.EventType;
import com.newcode.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class regController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(regController.class);

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/reg/"}, method = {RequestMethod.POST})
    public String reg(Model model, @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value = "next" , required = false) String next,
                      @RequestParam(value="rememberme", defaultValue = "false") boolean rememberme,
                      HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.register(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme) {
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            } else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }

        } catch (Exception e) {
            logger.error("注册异常" + e.getMessage());
            model.addAttribute("msg", "服务器错误");
            return "login";
        }
    }

    @RequestMapping(path = {"/login/"}, method = RequestMethod.POST)
    public String login(Model model,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        //next参数用来保存未登录时的网页url
                        @RequestParam(value = "next" , required = false) String next,
                        @RequestParam(value="rememberme", defaultValue = "false")boolean rememberme,
                        HttpServletResponse response) {
        try{
            Map<String,Object> map = userService.login(username,password);
            if(map.containsKey("ticket")){


                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                /*
                正常的cookie只能在一个应用中共享，即一个cookie只能由创建它的应用获得。
                1.可在同一应用服务器内共享方法：设置cookie.setPath("/");
                 */
                cookie.setPath("/");
                response.addCookie(cookie);

                eventProducer.fireEvent(new EventModel(EventType.LOGIN)
                        .setExt("username",username).setExt("email","1828610573@qq.com")
                        .setActorId((int)map.get("userId")));
                if(StringUtils.isNotBlank(next)){
                    return "redirect:"+next;
                }
                return "redirect:/";

            }else {
                model.addAttribute("msg",map.get("msg"));
                return "login";//登录失败
            }


        }catch (Exception e){
            logger.error("登陆异常"+e.getMessage());
            return "login";
        }



    }
    @RequestMapping(path = {"/reglogin"}, method = RequestMethod.GET)
    public String index(Model model,
                        @RequestParam(value = "next" , required = false) String next) {
        model.addAttribute("next",next);
        return "login";
    }

    @RequestMapping(path = {"/logout"}, method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";//退出返回首页

    }


}
