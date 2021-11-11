package com.mszlu.blog.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mszlu.blog.dao.polo.SysUser;
import lombok.Data;

import java.util.List;

@Data
public class CommentVo  {

    //@JsonSerialize(using = ToStringSerializer.class) //防止前端 精度损失 把id转为string
    private String id; //避免redis缓存带来的精度丢失，故设为String类型

    private UserVo author;

    private String content;

    private List<CommentVo> childrens;

    private String createDate;

    private Integer level;

    private UserVo toUser;
}
