package com.mszlu.blog.hander;


import com.mszlu.blog.vo.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;


//统一异常处理类
@RestControllerAdvice //对加了@Controller注解的方法进行拦截   AOP的实现
public class AllExceptionHander {

    //进行异常处理，处理Exception.class的异常
    @ExceptionHandler(Exception.class)
    @ResponseBody //返回json数据
    public Result doException(Exception ex){
        ex.printStackTrace();
        return Result.fail(-999, "系统异常！");
    }
}
