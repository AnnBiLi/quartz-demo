package com.wyl.quartz.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("detail")
public class Detail {
    @TableId(type= IdType.AUTO)
    private Integer id;

    private String type;

    private String username;

    private String content;

    private Integer taskInterval;

    private String test;

}
