package com.mszlu.blog;


import com.mszlu.blog.dao.mapper.ArticleMapper;
import com.mszlu.blog.service.ArticleService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@MapperScan("com.mszlu.blog.dao.mapper") //mybatis相关的接口都写到这个包下面
public class BlogApp {
    public static void main(String[] args) {
        SpringApplication.run(BlogApp.class, args);
    }
}
