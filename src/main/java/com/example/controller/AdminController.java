package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.Result;
import com.example.pojo.Admin;
import com.example.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Value("${salt.passwd}")
    private String md5Salt;

    @Autowired
    private AdminService adminService;

    /**
     * 管理员退出
     *
     * @param request
     * @param admin
     * @return
     */

    @PostMapping("/login")
    public Result<Admin> login(HttpServletRequest request, @RequestBody Admin admin) {
        //密码加盐（拼接盐+密码后使用MD5算法加密）
        String passwd = admin.getPassword().concat(md5Salt);
        passwd = DigestUtils.md5DigestAsHex(passwd.getBytes());

        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername, admin.getUsername());
        Admin one = adminService.getOne(queryWrapper);
        if (one == null) {
            return Result.error("管理员账户或密码错误");
        }

        if (!one.getPassword().equals(passwd)) {
            return Result.error("管理员账户或密码错误");
        }

        request.getSession().setAttribute("uid", one.getUid());
        System.out.println("管理员账户:" + one.getUid() + one.getUsername() + "已登录");
        return Result.success(one);

    }

    /**
     * 管理员退出
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */

    @GetMapping("/logout")
    public Result<String> logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().removeAttribute("uid");
        response.sendRedirect("/adminLogin.html");
        System.out.println("管理员已退出");
        return Result.success("管理员已退出");
    }
}
