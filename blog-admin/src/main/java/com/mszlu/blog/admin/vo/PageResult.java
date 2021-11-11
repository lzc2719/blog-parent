package com.mszlu.blog.admin.vo;

import lombok.Data;
import java.util.List;


//分页的结果，传递给前端
@Data
public class PageResult<T> {

    private List<T> list;

    private Long total;
}