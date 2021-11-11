package com.mszlu.blog.controller;

import com.mszlu.blog.service.LoginService;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.LoginParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("register")
public class RegisterController {

    @Autowired
    private LoginService loginService;

    @PostMapping //获取客户端请求过来的请求体(loginParam:用户名、密码、昵称)，并进行用户注册服务
    public Result register(@RequestBody LoginParam loginParam){
        //如果后期把登录和注册功能提出去，可以使用sso单点登录(独立提供登录和注册的接口服务)
        return loginService.register(loginParam);
    }
}
