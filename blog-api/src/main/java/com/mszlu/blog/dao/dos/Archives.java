package com.mszlu.blog.dao.dos;

import lombok.Data;


//这个类并不需要持久化
@Data
public class Archives {

    private Integer year;

    private Integer month;

    private Long count;
}
