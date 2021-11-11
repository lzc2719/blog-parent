package com.mszlu.blog.controller;


import com.mszlu.blog.common.aop.LogAnnotation;
import com.mszlu.blog.common.cache.Cache;
import com.mszlu.blog.service.ArticleService;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.ArticleParam;
import com.mszlu.blog.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;


    //首页 文章列表
    @PostMapping
    @LogAnnotation(module="文章", operator="获取文章列表") //加上此注解 代表要对此接口记录日志
    @Cache(expire = 5*60*1000, name = "listArticle") //从listArticle()方法切入进去，进行环绕通知
    public Result listArticle(@RequestBody PageParams pageParams){
        Result result = articleService.listArticle(pageParams);
        return result;
    }

    //首页 最热文章
    @PostMapping("hot")
    @Cache(expire = 5*60*1000, name = "hot_article") //从hotArtiche()方法切入进去，进行环绕通知
    public Result hotArtiche(){
        int limit = 5;
        return articleService.hotArticle(limit);
    }


    //首页 最新文章
    @PostMapping("new")
    @Cache(expire = 5*60*1000, name = "news_article") //从news_article()方法切入进去，进行环绕通知
    public Result newArtiche(){
        int limit = 5;
        return articleService.newArticles(limit);
    }


    //首页 最新文章
    @PostMapping("listArchives")
    public Result listArchives(){
        return articleService.listArchives();
    }


    @PostMapping("view/{id}")
    public Result findArticleById(@PathVariable("id") Long articleId){
        return articleService.findArticleById(articleId);
    }


    //接口url：/articles/publish
    //请求方式：POST
    @PostMapping("publish")
    public Result publish(@RequestBody ArticleParam articleParam){
        return articleService.publish(articleParam);
    }
}
