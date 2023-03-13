package com.example;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mapper.UserMapper;
import com.example.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@SpringBootTest
class ComputerStoreApplicationTests {

    @Autowired
    private UserMapper mapper;

    @Test
    void contextLoads() {
        /*Date date = new Date();
        System.out.println(date);*/
        String username = "admin";
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        User user = mapper.selectOne(queryWrapper);
        System.out.println(user);
    }

}
