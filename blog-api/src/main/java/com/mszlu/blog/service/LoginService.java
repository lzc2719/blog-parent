package com.mszlu.blog.service;

import com.mszlu.blog.dao.polo.SysUser;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.LoginParam;
import org.springframework.transaction.annotation.Transactional;


@Transactional //事务注解加在接口上，可以保证它的子类都能开启事务操作，更通用些
public interface LoginService {


    //登录功能
    Result login(LoginParam loginParam);

    //检查用户的token是否合法
    SysUser checkToken(String token);


    //退出登录
    Result logout(String token);


    //注册功能
    Result register(LoginParam loginParam);

}
