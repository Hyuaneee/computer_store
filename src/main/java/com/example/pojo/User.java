package com.example.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户类
 */

@Data
public class User {
    @TableId(type = IdType.AUTO)
    private Long uid;  //用户id
    private String username;  //用户名
    private String password;  //密码
    private Integer status; //账号状态 0-启用，1-禁用
    private String phone;  //手机号
    private Integer gender;  //性别:0-女，1-男
    private Integer deleted;  //是否删除：0-未删除，1-已删除

}
