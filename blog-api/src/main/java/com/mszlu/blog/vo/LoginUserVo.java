package com.mszlu.blog.vo;

import lombok.Data;


//用来封装正在登录的用户的信息
@Data
public class LoginUserVo {

    private String id; //避免redis缓存带来的精度丢失，故设为String类型

    private String account;

    private String nickname;

    private String avatar;
}
