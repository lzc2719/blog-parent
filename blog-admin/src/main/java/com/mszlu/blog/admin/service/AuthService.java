package com.mszlu.blog.admin.service;

import com.mszlu.blog.admin.mapper.AdminMapper;
import com.mszlu.blog.admin.polo.Admin;
import com.mszlu.blog.admin.polo.Permission;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;


//权限认证
@Service
@Slf4j
public class AuthService {

    @Autowired
    private AdminService adminService;

    //权限认证
    public boolean auth(HttpServletRequest request, Authentication authentication){
        String requestURI = request.getRequestURI(); //获取请求路径
        log.info("request url:{}", requestURI);
        Object principal = authentication.getPrincipal(); //获取当前网页访问者信息
        //true代表放行，false代表拦截
        if (principal == null || "anonymousUser".equals(principal)){
            return false; //未登录
        }
        UserDetails userDetails = (UserDetails) principal; //说明用户已经登录，强转为具体对象
        String username = userDetails.getUsername(); //获取用户名称
        Admin admin = adminService.findAdminByUserName(username); //获取此登录用户的数据库信息
        if (admin == null){
            return false;
        }
        log.info("-------------------"+admin.getId().toString());
      /*  if (admin.getId() == 1){ //认为是超级管理员
            return true;
        }*/
        List<Permission> permissions = adminService.findPermissionsByAdminId(admin.getId());
        requestURI = StringUtils.split(requestURI,'?')[0]; //取到不带参数的
        for (Permission permission : permissions) {
            log.info("-------------------------请求的权限"+requestURI);
            log.info("-------------------------本身的权限"+permission.getPath());
            if (requestURI.equals(permission.getPath())){ //用户请求的url权限与数据库权限相等
                log.info("权限通过");
                return true;
            }
        }
        return false;
    }
}