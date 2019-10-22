package com.newcode.model;

import org.springframework.stereotype.Component;

/**
 *作用：为了在service层或者controller层方便的获取User，只需要@Autowired
 *                                                         hostholder
 *                                                         就可以得到该用户
 */
@Component
public class HostHolder {
    //用ThreadLocal来作为变量
    private static ThreadLocal<User> users = new ThreadLocal<>();

    public  User getUser() {
        return users.get();
    }

    public void setUser(User user) {
        users.set(user);
    }
    public void remove(){
        users.remove();
    }
}
