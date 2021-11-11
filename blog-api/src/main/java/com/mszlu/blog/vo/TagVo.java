package com.mszlu.blog.vo;

import lombok.Data;

@Data
public class TagVo {

    private String id; //避免redis缓存带来的精度丢失，故设为String类型

    private String tagName;

    private String avatar;
}
