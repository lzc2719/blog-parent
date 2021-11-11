package com.mszlu.blog.common.cache;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mszlu.blog.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AliasFor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.time.Duration;



//定义一个切面 aop
//切面定义了切点和通知的关系
@Aspect
@Component
@Slf4j
public class CacheAspect {


    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    //定义切点。@Cache加在哪，哪里就是切点。
    @Pointcut("@annotation(com.mszlu.blog.common.cache.Cache)")
    public void pt(){}


    //环绕通知
    @Around("pt()") //以pt()方法作环绕通知
    public Object around(ProceedingJoinPoint pjp){
        try {
            Signature signature = pjp.getSignature();
            String className = pjp.getTarget().getClass().getSimpleName(); //获取类名
            String methodName = signature.getName(); //获取调用的方法名


            Object[] args = pjp.getArgs(); //获取方法里的参数
            Class[] parameterTypes = new Class[pjp.getArgs().length]; //参数类型
            String params = ""; //参数值
            for(int i=0; i<args.length; i++) {
                if(args[i] != null) {
                    params += JSON.toJSONString(args[i]); //将每个参数值进行追加在一起
                    parameterTypes[i] = args[i].getClass(); //将每个参数的类型存放进数组
                }else {
                    parameterTypes[i] = null;
                }
            }
            if (StringUtils.isNotEmpty(params)) {
                //加密，以防出现key过长以及字符转义获取不到的情况
                params = DigestUtils.md5Hex(params);
            }
            Method method = pjp.getSignature().getDeclaringType().getMethod(methodName, parameterTypes);
            Cache annotation = method.getAnnotation(Cache.class); //获取当前方法的@Cache注解
            long expire = annotation.expire(); //缓存过期时间
            String name = annotation.name(); //缓存名称

            //先从redis获取
            String redisKey = name + "::" + className+"::"+methodName+"::"+params;
            //通过redis的key获取redis对应的值
            String redisValue = redisTemplate.opsForValue().get(redisKey);
            if (StringUtils.isNotEmpty(redisValue)){ //判断获取redis的值是否为空
                log.info("走了缓存~~~,{},{}",className,methodName);
                //从redis缓存中读出数据，并返回。避免从磁盘中读取
                Result result = JSON.parseObject(redisValue, Result.class);
                return result;
            }
            Object proceed = pjp.proceed();
            redisTemplate.opsForValue().set(redisKey,JSON.toJSONString(proceed), Duration.ofMillis(expire));
            log.info("存入缓存~~~ {},{}",className,methodName);
            return proceed;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return Result.fail(-999,"系统错误");
    }

}
