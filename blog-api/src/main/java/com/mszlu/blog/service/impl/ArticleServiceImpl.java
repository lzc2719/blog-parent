package com.mszlu.blog.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mszlu.blog.dao.dos.Archives;
import com.mszlu.blog.dao.mapper.ArticleBodyMapper;
import com.mszlu.blog.dao.mapper.ArticleMapper;
import com.mszlu.blog.dao.mapper.ArticleTagMapper;
import com.mszlu.blog.dao.polo.Article;
import com.mszlu.blog.dao.polo.ArticleBody;
import com.mszlu.blog.dao.polo.ArticleTag;
import com.mszlu.blog.dao.polo.SysUser;
import com.mszlu.blog.service.*;
import com.mszlu.blog.utils.UserThreadLocal;
import com.mszlu.blog.vo.ArticleBodyVo;
import com.mszlu.blog.vo.ArticleVo;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.TagVo;
import com.mszlu.blog.vo.params.ArticleParam;
import com.mszlu.blog.vo.params.PageParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;


@Service
public class ArticleServiceImpl implements ArticleService {


    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleBodyMapper articleBodyMapper;

    @Autowired
    private TagService tagService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private ArticleTagMapper articleTagMapper;


    //使用sql语句的方式实现数据分类的初始化
    public Result listArticle(PageParams pageParams) {
        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());

        IPage<Article> articleIPage = articleMapper.listArticle(
                page,
                pageParams.getCategoryId(),
                pageParams.getTagId(),
                pageParams.getYear(),
                pageParams.getMonth());
        List<Article> records = articleIPage.getRecords();
        return Result.success(copyList(records,true,true));
    }


    /*//用上面那种方式，使用sql语句筛选数据
    @Override
    public Result listArticle(PageParams pageParams) {
        //1.分页查询我们的数据库表
        //mybatis-plus的Page类
        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        //查询条件
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        if (pageParams.getCategoryId() != null){
            // and category_id=#{categoryId}
            //
            queryWrapper.eq(Article::getCategoryId, pageParams.getCategoryId());
        }
        List<Long> articleIdList = new ArrayList<>();
        if (pageParams.getTagId() != null){
            //加入标签 条件查询
            //article表中 并没有tag字段 一篇文章 有多个标签
            //article_tag  article_id 1 : n tag_id
            LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
            articleTagLambdaQueryWrapper.eq(ArticleTag::getTagId,pageParams.getTagId());
            List<ArticleTag> articleTags = articleTagMapper.selectList(articleTagLambdaQueryWrapper);
            for (ArticleTag articleTag : articleTags) {
                articleIdList.add(articleTag.getArticleId());
            }
            if (articleIdList.size() > 0){
                // and id in(1,2,3)
                queryWrapper.in(Article::getId,articleIdList);
            }
        }
        queryWrapper.orderByDesc(Article::getWeight); //按照文章置顶权重排序
        queryWrapper.orderByDesc(Article::getCreateDate); //按照文章创建时间的倒序排序
        //或者一条语句解决
        //queryWrapper.orderByDesc(Article::getWeight, Article::getCreateDate);
        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
        List<Article> records = articlePage.getRecords(); //从数据库中得到记录数
        //能直接返回吗？很明显不能！
        List<ArticleVo> articleVoList = copyList(records, true, true); //两个true，表明需要标签和作者
        return Result.success(articleVoList);
    }*/


    //文章按时间归类
    @Override
    public Result listArchives() {
        List<Archives> archivesList = articleMapper.listArchives();
        return Result.success(archivesList);
    }


    //做对应的转移工作
    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor){
        List<ArticleVo> articleVoList = new ArrayList<>();
        //循环遍历，将所有Article数据转移为ArticleVo数据
        for(Article record: records){
            //文章详情 和 文章类别 不需要显示在主页面
            articleVoList.add(copy(record, isTag, isAuthor, false, false));
        }
        return articleVoList;
    }


    //Article转移
    private ArticleVo copy(Article article, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory){
        ArticleVo articleVo = new ArticleVo();
        articleVo.setId(String.valueOf(article.getId()));
        BeanUtils.copyProperties(article, articleVo); //spring提供的
        //将long类型的时间转换为正常的时间格式
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        //并不是所有接口都需要标签，作者信息
        if(isTag){
            Long articleId = article.getId(); //得到文章的id
            articleVo.setTags(tagService.findTagsByArticleId(articleId));
        }
        if(isAuthor){
            Long authorId = article.getAuthorId();
            articleVo.setAuthor(sysUserService.findUserById(authorId).getNickname());
        }
        if (isBody){
            Long bodyId = article.getBodyId();
            articleVo.setBody(findArticleBodyById(bodyId));
        }
        if (isCategory){
            Long categoryId = article.getCategoryId();
            //根据文章类别id去数据库中查询文章类别实体类Category
            articleVo.setCategory(categoryService.findCategoryById(categoryId));
        }
        return articleVo;
    }


    //做对应的转移工作
    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory){
        List<ArticleVo> articleVoList = new ArrayList<>();
        //循环遍历，将所有Article数据转移为ArticleVo数据
        for(Article record: records){
            articleVoList.add(copy(record, isTag, isAuthor, isBody, isCategory));
        }
        return articleVoList;
    }




    //根据文章详情id去数据库中查询文章详情ArticleBody
    private ArticleBodyVo findArticleBodyById(Long bodyId) {
        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;
    }



    //最热文章
    @Override
    public Result hotArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCounts); //根据浏览量从大到小排序
        queryWrapper.select(Article::getId, Article::getTitle); //查询文章的id文章的标题
        queryWrapper.last("limit "+limit); //限制数量
        //select id,title from article order by view_counts desc limit 5
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles,false,false));
    }


    //最新文章
    @Override
    public Result newArticles(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateDate); //根据创建时间逆序排序，时间越大文章越新
        queryWrapper.select(Article::getId, Article::getTitle); //查询文章的id和文章标题
        queryWrapper.last("limit "+limit);
        //select id,title from article order by create_date desc desc limit 5
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles,false,false));
    }



    @Override
    public Result findArticleById(Long articleId) {
        /**
         * 1. 根据id查询 文章信息
         * 2. 根据bodyId和categoryid 去做关联查询
         */
        Article article = this.articleMapper.selectById(articleId);
        ArticleVo articleVo = copy(article, true, true,true,true);
        //查看完文章了，新增阅读数，有没有问题呢？
        //查看完文章之后，本应该直接返回数据了，这时候做了一个更新操作，更新时加写锁，
        //阻塞其他的读操作，性能就会比较低
        //更新增加了此次接口的耗时，如果一旦更新出问题，不能影响查看文章的操作
        //线程池：可以把更新操作扔到线程池中去执行，和主线程就不相关了
        threadService.updateArticleViewCount(articleMapper, article);
        return Result.success(articleVo);
    }


    //注意：此接口要加入到登录拦截当中
    @Override
    public Result publish(ArticleParam articleParam) {
        SysUser sysUser = UserThreadLocal.get();
        /**
         * 1. 发布文章 目的 构建Article对象
         * 2. 作者id  当前的登录用户
         * 3. 标签  要将标签加入到关联列表当中
         * 4. body 内容存储 article bodyId
         */
        Article article = new Article();
        article.setAuthorId(sysUser.getId()); //设置作者id
        article.setWeight(Article.Article_Common); //设置置顶
        article.setViewCounts(0);                   //初始浏览量为0
        article.setTitle(articleParam.getTitle());  //设置文章标题
        article.setSummary(articleParam.getSummary());//设置文章摘要
        article.setCommentCounts(0);                //初始文章评论数为0
        article.setCreateDate(System.currentTimeMillis()); //初始文章创建时间
        article.setCategoryId(Long.parseLong(articleParam.getCategory().getId()));
        this.articleMapper.insert(article); //插入之后 会生成一个文章id
        //tag
        List<TagVo> tags = articleParam.getTags(); //文章标签
        if (tags != null){
            for (TagVo tag : tags) {
                Long articleId = article.getId();
                ArticleTag articleTag = new ArticleTag();
                articleTag.setTagId(Long.parseLong(tag.getId()));
                articleTag.setArticleId(articleId);
                articleTagMapper.insert(articleTag);
            }
        }
        //body
        ArticleBody articleBody  = new ArticleBody();
        articleBody.setArticleId(article.getId());
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBodyMapper.insert(articleBody);

        article.setBodyId(articleBody.getId());
        articleMapper.updateById(article);
        Map<String,String> map = new HashMap<>();
        map.put("id",article.getId().toString());
        return Result.success(map);
    }

}
