package com.mszlu.blog.dao.polo;

import lombok.Data;


//封装文章详情的类
@Data
public class ArticleBody {

    private Long id;
    private String content;
    private String contentHtml;
    private Long articleId;
}
