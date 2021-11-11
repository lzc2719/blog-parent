package com.mszlu.blog.hander;

import com.alibaba.fastjson.JSON;
import com.mszlu.blog.dao.polo.SysUser;
import com.mszlu.blog.service.LoginService;
import com.mszlu.blog.utils.UserThreadLocal;
import com.mszlu.blog.vo.ErrorCode;
import com.mszlu.blog.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



//登录拦截器，进行统一拦截判断。有些页面需要用户登录状态才能进入
//注意要去SpringMVC配置WebMVCConfig类中，注册此拦截器！
@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginService loginService;


    //在执行controller方法(Handler)之前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                                                                        /*
             1. 需要判断 请求的接口路径 是否为 HandlerMethod (controller方法)
             2. 判断 token是否为空，如果为空 未登录
             3. 如果token 不为空，登录验证 loginService checkToken
             4. 如果认证成功 放行即可 */
        if (!(handler instanceof HandlerMethod)){ //如果不是访问的controller类的方法，直接放行
            //handler也可能是RequestResourceHandler，比如在springboot程序访问静态资源时，默认去classpath下的static目录去查询
            return true;
        }
        //从请求头的中获取token值，因为用户的token值都放在请求头里面，当访问服务器时带上token一起访问
        String token = request.getHeader("Authorization");

        log.info("=================request start===========================");
        String requestURI = request.getRequestURI();
        log.info("request uri:{}",requestURI);
        log.info("request method:{}",request.getMethod());
        log.info("token:{}", token);
        log.info("=================request end===========================");

        //token值为空，说明用户处于未登录状态
        if (StringUtils.isBlank(token)){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), "未登录");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        SysUser sysUser = loginService.checkToken(token);
        if (sysUser == null){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), "未登录");
            response.setContentType("application/json;charset=utf-8"); //将网页编码格式设置为utf-8，支持Json数据
            response.getWriter().print(JSON.toJSONString(result)); //将用户未登录的信息返回到页面，提示用户登录
            return false;
        }
        //登录验证成功，放行
        //我希望在controller中 直接获取用户的信息 怎么获取?
        UserThreadLocal.put(sysUser);
        return true;
    }


    //在执行controller方法(Handler)之后执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //如果不删除 ThreadLocal中用完的信息 会有内存泄漏的风险
        UserThreadLocal.remove(); //底层会进行Thread.currentThread获取，删除的是当前线程的本地变量值
    }

}
