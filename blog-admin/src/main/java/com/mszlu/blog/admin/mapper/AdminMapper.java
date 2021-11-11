package com.mszlu.blog.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mszlu.blog.admin.polo.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;


@Component
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
}