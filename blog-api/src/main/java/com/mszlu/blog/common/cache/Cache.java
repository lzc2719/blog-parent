package com.mszlu.blog.common.cache;


import java.lang.annotation.*;



//自定义缓存注解，为了作统一缓存管理。将此注解加在哪个地方，哪个地方就是切点
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {

    long expire() default 1 * 60 * 1000; //默认数据过期时间

    String name() default ""; //默认缓存标识key

}
