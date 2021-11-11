package com.mszlu.blog.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mszlu.blog.dao.mapper.ArticleMapper;
import com.mszlu.blog.dao.polo.Article;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;



@Component
public class ThreadService {

    //把初始化好的线程池注册到这里来用。@Async注解修饰的这个方法是从线程池中获取的线程运行的，不影响主线程操作
    @Async("taskExecutor")
    public void updateArticleViewCount(ArticleMapper articleMapper, Article article) {

      /*try { //不用线程池的话，当查看文章详情时，会等待5秒后，文章详情内容才会加载出来
            Thread.sleep(5000);
            System.out.println("更新完成了....");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        Article articleUpdate = new Article();
        int viewCounts = article.getViewCounts(); //获取当前文章的浏览量
        articleUpdate.setViewCounts(viewCounts +1); //将文章浏览量+1后再封装成新文章实体类Article
        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId, article.getId());
        //设置一个 为了在多线程的环境下线程安全
        updateWrapper.eq(Article::getViewCounts, viewCounts);
        // update article set view_count=100 where view_count=99 and id=11
        articleMapper.update(articleUpdate, updateWrapper);

    }
}
