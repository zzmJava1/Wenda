package com.newcode.service;

import com.newcode.Dao.LoginDao;
import com.newcode.Dao.UserDao;
import com.newcode.Util.WendaUtil;
import com.newcode.model.LoginTicket;
import com.newcode.model.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private LoginDao loginDao;



    public User getUser(int id){
        return userDao.selectById(id);
    }

    public User selectUserByName(String name){
        return userDao.selectByName(name);
    }


    public Map<String,Object> register(String username,String password){
        Map<String,Object> map = new HashMap<>();//用来保存错误信息发送到Controller,controller发送信息至前端
        if(StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;

        }
        User user = userDao.selectByName(username);
        if(user!=null){
            map.put("msg","用户名重复");
            return map;
        }
        //添加用户
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));//设置salt加密
        user.setPassword(WendaUtil.MD5(password+user.getSalt()));//利用md5算法加密
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));

        userDao.addUser(user);

        //下发ticket
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isBlank(username)) {
            map.put("msg", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msg", "密码不能为空");
            return map;
        }

        User user = userDao.selectByName(username);

        if (user == null) {
            map.put("msg", "用户名不存在");
            return map;
        }

        if (!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword())) {
            map.put("msg", "密码不正确");
            return map;
        }

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        map.put("userId",user.getId());
        return map;
    }

    private String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date now = new Date();
        now.setTime(3600*24*100+now.getTime());//设置过期时间为100天
        ticket.setExpired(now);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginDao.addTicket(ticket);
        return ticket.getTicket();
    }

    public void logout(String ticket) {
        loginDao.updateStatus(ticket,"1");

    }
}
