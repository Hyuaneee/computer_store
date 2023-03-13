package com.example.pojo;

import lombok.Data;

import java.util.Date;


//实体类基类
@Data
public class BaseEntity {
    private String createdUser;  //日志-创建人
    private Date createdTime;  //日志-创建时间
    private String modifiedUser;  //日志-最后修改执行人
    private Date modifiedTime;  //日志-最后修改时间
}
