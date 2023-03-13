package com.example.controller;


import com.example.common.ReturnFront;
import com.example.pojo.User;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpSession;

/** 控制器类的基类 */
public class BaseController {

    //** @ExceptionHandler用于统一处理方法抛出的异常 */
    @ExceptionHandler(RuntimeException.class)
    public ReturnFront<String> handleException(Throwable e){
        return ReturnFront.error(e.getMessage());
    }
   /* //获取session中的uid
    public Long getSessionUId(HttpSession session){
        User user =(User)session.getAttribute("user");
        return user.getUid();
    }
    //获取session中的username
    public String getSessionUsername(HttpSession session){
        User user =(User)session.getAttribute("user");
        return user.getUsername();
    }
    //获取session中的User
    public User getSessionUser(HttpSession session){
        User user =(User)session.getAttribute("user");
        return user;
    }*/
}
