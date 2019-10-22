package com.newcode.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

//@Controller
public class Demo01 {

    @RequestMapping(path = {"/","/index"})
    @ResponseBody
    public String index(HttpSession session){
        return "hello myProject "+session.getAttribute("redirect");
    }


    @RequestMapping("/profile/{userId}")
    @ResponseBody
    public String profile(@PathVariable("userId") int userId,
                          @RequestParam(value = "key",defaultValue = "zz") String key){
        return String.format("profile of page %d k:%s",userId,key);
    }
    @RequestMapping(path = {"/vm"} ,method = RequestMethod.GET)
   public String template(Model model){
        model.addAttribute("value1","asdasd");
        return "home";
    }
    @RequestMapping(path = {"/redirect/{code}"} ,method = RequestMethod.GET)
    public String redirect(@PathVariable("code") String code, HttpSession session){
        session.setAttribute("redirect","already redirect");
        return "redirect:/index";
    }
}
