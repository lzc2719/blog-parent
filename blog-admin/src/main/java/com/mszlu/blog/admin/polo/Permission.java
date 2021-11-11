package com.mszlu.blog.admin.polo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;



@Data
public class Permission {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name; //权限名

    private String path; //权限路径

    private String description; //权限描述
}