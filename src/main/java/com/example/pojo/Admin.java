package com.example.pojo;

import lombok.Data;

@Data
public class Admin {

    private Long uid; //管理员id
    private String username; //管理员账号
    private String password; //管理员密码

}
