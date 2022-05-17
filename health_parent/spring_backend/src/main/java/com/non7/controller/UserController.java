package com.non7.controller;

import com.non7.constant.MessageConstant;
import com.non7.entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
//    获得当前登录用户的用户名
    @RequestMapping("/getUsername")
    public Result getUsername(){
//        当Spring security完成认证后，会将当前用户信息保存到框架提供的上下文对象中
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        User user=(User)user1;
        String username=user.getUsername();
        System.out.println(username);
        if(user!=null){
            return new Result(true, MessageConstant.GET_USERNAME_SUCCESS,username);
        }
        return new Result(false,MessageConstant.GET_USERNAME_FAIL);

    }

}
