package com.mszlu.blog.controller;


import com.mszlu.blog.service.TagService;
import com.mszlu.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("tags")
public class TagsController {

    @Autowired
    private TagService tagService;


    @GetMapping("hot")
    private Result hot(){
        int limit = 6; //表示展示热度排名前6的标签
        return tagService.hots(limit);
    }


    @GetMapping
    private Result findAll(){
        return tagService.findAll();
    }


    @GetMapping("detail")
    public Result findAllDetail(){
        return tagService.findAllDetail();
    }


    @GetMapping("detail/{id}")
    public Result findDetailById(@PathVariable("id") Long id){
        return tagService.findDetailById(id);
    }

}
