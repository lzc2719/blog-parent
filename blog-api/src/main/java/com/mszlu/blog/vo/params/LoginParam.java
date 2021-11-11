package com.mszlu.blog.vo.params;

import lombok.Data;

//登录参数的封装类
@Data
public class LoginParam {

    private String account;

    private String password;

    private String nickname;
}
