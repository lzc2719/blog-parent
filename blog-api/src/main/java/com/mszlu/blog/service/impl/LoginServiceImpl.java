package com.mszlu.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.mszlu.blog.dao.polo.SysUser;
import com.mszlu.blog.dao.polo.SysUser;
import com.mszlu.blog.service.LoginService;
import com.mszlu.blog.service.SysUserService;
import com.mszlu.blog.utils.JWTUtils;
import com.mszlu.blog.utils.UserThreadLocal;
import com.mszlu.blog.vo.ErrorCode;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.LoginParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;



@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private SysUserService sysUserService;

    //导入一个redis的Template
    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    private static final String slat = "mszlu!@#"; //加密盐


    @Override
    public Result login(LoginParam loginParam) {
        /*
             1. 检查参数是否合法
             2. 根据用户名和密码去user表中查询 是否存在
             3. 如果不存在 登录失败
             4. 如果存在 ，使用jwt 生成token 返回给前端
             5. token放入redis当中，redis  token：user信息 设置过期时间
               (登录认证的时候 先认证token字符串是否合法，去redis认证是否存在)*/
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password)){ //如果用户栏或密码栏为空，返回错误信息
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }
        password = DigestUtils.md5Hex(password + slat); //对密码进行加密，后面追加加密盐，以访数据库的密码泄露
        SysUser sysUser = sysUserService.findUser(account,password); //去数据库查找用户是否存在
        if (sysUser == null){ //用户名或密码错误，在数据库中没找到指定用户，返回null
            return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(),ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }
            String token = JWTUtils.createToken(sysUser.getId()); //利用工具类将用户id转为token
        //TimeUnit.DAYS：过期时间，一天过期
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysUser),1, TimeUnit.DAYS);
        return Result.success(token);
    }

    //检查登录用户的token是否合法
    @Override
    public SysUser checkToken(String token) {
        if (StringUtils.isBlank(token)){ //判断token是否为空
            return null;
        }
        Map<String, Object> stringObjectMap = JWTUtils.checkToken(token); //解析token
        if (stringObjectMap == null){
            return null;
        }
        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token); //去redis缓存中获取token的值
        if (StringUtils.isBlank(userJson)){ //说明redis缓存中的token过期了
            return null;
        }
        SysUser sysUser = JSON.parseObject(userJson, SysUser.class); //将token的值转换为SysUser对象
        return sysUser;
    }


    //退出功能
    @Override
    public Result logout(String token) {
        redisTemplate.delete("TOKEN_"+token); //直接从redis缓存中删除token及token对应的值
        return Result.success(null);
    }


    //注册功能
    @Override
    public Result register(LoginParam loginParam) {
        /*
             1. 判断参数 是否合法
             2. 判断账户是否存在，存在 返回账户已经被注册
             3. 不存在，注册用户
             4. 生成token
             5. 存入redis 并返回
             6. 注意加上事务，一旦中间的任何过程出现问题，注册的用户 需要回滚 */
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        String nickname = loginParam.getNickname();
        //判断输入栏中是否有没输入的
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password) || StringUtils.isBlank(nickname)){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }
        //根据输入的新用户信息，去数据库中找，看是否已经存在该用户
        SysUser sysUser =  sysUserService.findUserByAccount(account);
        if (sysUser != null){
            return Result.fail(ErrorCode.ACCOUNT_EXIST.getCode(),"账户已经被注册了");
        }
        sysUser = new SysUser();
        sysUser.setNickname(nickname);
        sysUser.setAccount(account);
        //对新用户的密码进行加密，并且还加上了加密盐，保证密码足够安全
        sysUser.setPassword(DigestUtils.md5Hex(password+slat));
        sysUser.setCreateDate(System.currentTimeMillis());
        sysUser.setLastLogin(System.currentTimeMillis());
        sysUser.setAvatar("/static/img/logo.b3a48c0.png");
        sysUser.setAdmin(1); //1 为 true
        sysUser.setDeleted(0); //0 为 false
        sysUser.setSalt("");
        sysUser.setStatus("");
        sysUser.setEmail("");
        this.sysUserService.save(sysUser);
        String token = JWTUtils.createToken(sysUser.getId()); //将用户的id生成对应的token值
        //将新用户的token值存放进redis的缓存中
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysUser),1, TimeUnit.DAYS);
        return Result.success(token);
    }



}
