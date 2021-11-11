package com.mszlu.blog.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;


import java.util.List;

@Data
public class ArticleVo {

    //@JsonSerialize(using = ToStringSerializer.class)
    private String id; //避免redis缓存带来的精度丢失，故设为String类型

    private String title;

    private String summary;

    private Integer commentCounts;

    private Integer viewCounts;

    private Integer weight;

    private String createDate; //创建时间

    private String author;

    private ArticleBodyVo body; //文章详情

    private List<TagVo> tags; //标签列表

    private CategoryVo category;

}
