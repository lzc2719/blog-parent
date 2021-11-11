package com.mszlu.blog.admin.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mszlu.blog.admin.polo.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;



@Component
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    List<Permission> findPermissionsByAdminId(Long adminId);
}