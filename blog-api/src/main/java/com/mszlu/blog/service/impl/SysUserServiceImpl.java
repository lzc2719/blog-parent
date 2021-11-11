package com.mszlu.blog.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mszlu.blog.dao.mapper.SysUserMapper;
import com.mszlu.blog.dao.polo.SysUser;
import com.mszlu.blog.service.LoginService;
import com.mszlu.blog.service.SysUserService;
import com.mszlu.blog.vo.ErrorCode;
import com.mszlu.blog.vo.LoginUserVo;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;




@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private LoginService loginService;


    @Override
    public UserVo findUserVoById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if (sysUser == null){
            sysUser = new SysUser();
            sysUser.setId(1L);
            sysUser.setAvatar("/static/img/logo.b3a48c0.png");
            sysUser.setNickname("码神之路");
        }
        UserVo userVo  = new UserVo();
        BeanUtils.copyProperties(sysUser, userVo); //相同属性copy过来
        userVo.setId(String.valueOf(sysUser.getId()));
        //userVo.setId(sysUser.getId());
        return userVo;
    }



    @Override
    public SysUser findUserById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if (sysUser == null){
            sysUser = new SysUser();
            sysUser.setNickname("码神之路");
        }
        return sysUser;
    }


    //根据用户名和密码，去数据库中找寻是否存在该用户
    @Override
    public SysUser findUser(String account, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount, account); //去数据库中查找是否有此用户名
        queryWrapper.eq(SysUser::getPassword, password); //去数据库中查找是否有此用户对应的密码
        //将查询到的用户信息封装到SysUser类中
        queryWrapper.select(SysUser::getAccount,SysUser::getId,SysUser::getAvatar,SysUser::getNickname);
        queryWrapper.last("limit 1"); //保证查询效率，以访继续搜索下面的记录
        return sysUserMapper.selectOne(queryWrapper);
    }



    //根据请求头中的token数据，去数据库中寻找用户信息
    @Override
    public Result findUserByToken(String token) {
        /*
             1. token合法性校验（是否为空，解析是否成功 redis是否存在）
             2. 如果校验失败 返回错误
             3. 如果成功，返回对应的结果
             LoginUserVo类来封装用户信息*/
        SysUser sysUser = loginService.checkToken(token);
        if (sysUser == null){
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg());
        }
        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setId(String.valueOf(sysUser.getId()));
        loginUserVo.setNickname(sysUser.getNickname());
        loginUserVo.setAvatar(sysUser.getAvatar());
        loginUserVo.setAccount(sysUser.getAccount());
        return Result.success(loginUserVo);
    }


    //找寻数据库中是否存在该用户
    @Override
    public SysUser findUserByAccount(String account) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount, account); //根据用户名查询数据库中是否已经存在该用户名
        queryWrapper.last("limit 1"); //避免找到记录后，数据库继续搜索
        return this.sysUserMapper.selectOne(queryWrapper);
    }


    //保存新用户信息到数据库
    @Override
    public void save(SysUser sysUser) {
        /*
        保存用户这 id会自动生成，生成的id是分布式的id，非自增，默认采用雪花算法
            为什么用雪花算法？
                用户数多了以后，要进行分表操作，id就需要分布式id了
        */
        this.sysUserMapper.insert(sysUser); //mybatis-plus自带的
    }

}
