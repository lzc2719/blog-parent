package com.mszlu.blog.service;


import com.mszlu.blog.vo.ArticleBodyVo;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.ArticleParam;
import com.mszlu.blog.vo.params.PageParams;



public interface ArticleService {

    //分页查询文章列表
    Result listArticle(PageParams pageParams);

    //查询最热的文章
    public Result hotArticle(int limit);

    //查询最新的文章
    public Result newArticles(int limit);

    //文章归档
    public Result listArchives();

    //查看文章详情
    public Result findArticleById(Long articleId);

    //文章发布服务
    Result publish(ArticleParam articleParam);
}
