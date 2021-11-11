package com.mszlu.blog.config;


import com.mszlu.blog.hander.LoginInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



@Configuration
public class WebMVCConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //跨域配置
        //前端8080 访问  访问后端8888 ，属于跨域访问，两个不同的服务器
        registry.addMapping("/**").allowedOrigins("http://localhost:8080"); //允许跨域访问
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //当前拦截test接口。后续实际遇到需要拦截的接口时，再配置为真正需要拦截的接口
        registry.addInterceptor(loginInterceptor).
                addPathPatterns("/test").
                addPathPatterns("/comments/create/change").
                addPathPatterns("/articles/publish");

        /*//拦截所有路径，但排除login和register
        * registry.addInterceptor(loginInterceptor).addPathPatterns("/**").
                excludePathPatterns("/login").
                excludePathPatterns("/register");*/
    }
}
