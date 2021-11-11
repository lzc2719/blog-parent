package com.mszlu.blog.admin.model.params;

import lombok.Data;


@Data
public class PageParam {

    private Integer currentPage; //当前页

    private Integer pageSize; //页面大小

    private String queryString; //查询语句
}