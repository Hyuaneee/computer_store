package com.example.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Admin {
    @TableId(type = IdType.AUTO)
    private Long uid; //管理员id
    private String username; //管理员账号
    private String password; //管理员密码

}
