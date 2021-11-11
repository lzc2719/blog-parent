package com.mszlu.blog.admin.controller;

import com.mszlu.blog.admin.model.params.PageParam;
import com.mszlu.blog.admin.polo.Permission;
import com.mszlu.blog.admin.service.PermissionService;
import com.mszlu.blog.admin.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private PermissionService permissionService;


    //获取所有的用户权限记录
    /*
    前端传递过来的请求体为pagination封装的json串
      pagination: {//分页相关模型数据
    					  currentPage: 1,//当前页码
    					  pageSize:10,//每页显示的记录数
    					  total:0,//总记录数
    					  queryString:null//查询条件
    				}
    将currentPage、pageSize、queryString封装成PageParam  */
    @PostMapping("permission/permissionList")
    public Result permissionList(@RequestBody PageParam pageParam){
        return permissionService.listPermission(pageParam);
    }


    //增加用户权限
    //前端传过来的参数：name(描述)、path(路径)、description(描述)，这里封装成了Permission对象
    @PostMapping("permission/add")
    public Result add(@RequestBody Permission permission){
        return permissionService.add(permission);
    }


    //修改用户权限
    //前端传过来的参数：name(描述)、path(路径)、description(描述)，这里封装成了Permission对象
    @PostMapping("permission/update")
    public Result update(@RequestBody Permission permission){
        return permissionService.update(permission);
    }


    //删除用户权限
    //前端传递过来的参数：id(权限编号)
    @GetMapping("permission/delete/{id}")
    public Result delete(@PathVariable("id") Long id){
        return permissionService.delete(id);
    }

}