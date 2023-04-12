package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.mapper.UserMapper;
import com.example.pojo.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Value("${salt.passwd}")
    private String md5Salt;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     *
     * @param request
     * @param user
     * @return
     */

    @PostMapping("/login")
    public Result<User> login(HttpServletRequest request, @RequestBody User user) {
        //密码加盐（拼接盐+密码后使用MD5算法加密）
        String passwd = user.getPassword().concat(md5Salt);
        passwd = DigestUtils.md5DigestAsHex(passwd.getBytes());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());
        User one = userService.getOne(queryWrapper);
        if (one == null || one.getDeleted() == 1) {
            return Result.error("用户名或密码错误");
        }
        if (!one.getPassword().equals(passwd)) {
            return Result.error("用户名或密码错误");
        }
        if (one.getStatus() == 1) {
            return Result.error("用户已被禁用！");
        }
        request.getSession().setAttribute("uid", one.getUid());
        return Result.success(one);
    }

    /**
     * 用户退出
     *
     * @param request
     * @return
     */
    @GetMapping("/logout")
    public Result<String> logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().removeAttribute("uid");
        response.sendRedirect("/login.html");
        return Result.success("用户已退出");
    }

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    @PostMapping("/enroll")
    public Result<String> insert(@RequestBody User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());
        User one = userService.getOne(queryWrapper);
        if (one != null) {
            return Result.error("用户已存在");
        }
        //设置密码
        String passwd = user.getPassword().concat(md5Salt);
        user.setPassword(DigestUtils.md5DigestAsHex(passwd.getBytes()));
        user.setStatus(0);  //是否启用：0-启用，1-禁用
        user.setDeleted(0); //是否删除：0-未删除，1-已删除
        boolean flag = userService.save(user);
        if (!flag) {
            return Result.error("用户创建失败");
        }
        return Result.success("用户创建成功");
    }

    //获取user数据
    @GetMapping("/getUser")
    public Result<User> getUser(HttpSession session) {
        //获取session中uid并查找对应数据
        Long uid = (Long) session.getAttribute("uid");
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUid, uid);
        User user = userService.getOne(queryWrapper);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }


    //用户密码修改
    @PutMapping("/update/{password}/{password2}")
    public Result<String> update(HttpSession session, @PathVariable String password, @PathVariable String password2) {
        //获取session中uid并查找对应数据
        Long uid = (Long) session.getAttribute("uid");
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUid, uid);
        User user = mapper.selectOne(queryWrapper);

        //密码加盐（拼接盐+密码后使用MD5算法加密）
        String md5Passwd = password.concat(md5Salt);
        md5Passwd = DigestUtils.md5DigestAsHex(md5Passwd.getBytes());
        if (!md5Passwd.equals(user.getPassword())) {
            return Result.error("原密码不正确");
        }
        //加密新密码并输入数据库
        String newPasswd = password2.concat(md5Salt);
        newPasswd = DigestUtils.md5DigestAsHex(newPasswd.getBytes());
        User updateUser = new User();
        updateUser.setPassword(newPasswd);

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUid, user.getUid());
        boolean flag = userService.update(updateUser, wrapper);
        if (!flag) {
            return Result.error("更新失败");
        }
        return Result.success("更新成功");
    }

    //修改用户信息
    @PutMapping("/updateUser")
    public Result<String> updateUser(HttpSession session, @RequestBody User user) {
        //获取session中uid并查找对应数据
        Long uid = (Long) session.getAttribute("uid");
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUid, uid);
        boolean flag = userService.update(user, queryWrapper);
        if (!flag) {
            return Result.error("修改失败");
        }
        //更新sesson中的数据
        session.setAttribute("uid", user.getUid());
        return Result.success("修改成功");
    }

    //分页查询
    @PostMapping("/PageList/{currentPage}/{pageSize}")
    public Result insert(@PathVariable Integer currentPage, @PathVariable Integer pageSize, String context, String status) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (!context.equals("")) {
            wrapper.eq(User::getUsername, context);
        }
        if (!status.equals("")) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.eq(User::getDeleted, 0);
        wrapper.orderByDesc(User::getUsername);
        IPage<User> iPage = new Page<>(currentPage, pageSize);
        IPage<User> page = userService.page(iPage, wrapper);
        return Result.success(page);
    }


    //账号状态设置
    @GetMapping("/setStatus/{uid}/{status}")
    public Result setDefault(@PathVariable Long uid, @PathVariable Integer status) {
        status = status == 0 ? 1 : 0;
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getUid, uid);
        wrapper.set(User::getStatus, status);
        boolean flag = userService.update(wrapper);
        if (!flag) {
            return Result.error("设置失败,请重试");
        }
        return Result.success("设置成功");
    }

    //账号逻辑删除
    @GetMapping("/setDeleted/{uid}")
    public Result setDeleted(@PathVariable Long uid) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getUid, uid);
        wrapper.set(User::getDeleted, 1);
        boolean flag = userService.update(wrapper);
        if (!flag) {
            return Result.error("设置失败,请重试");
        }
        return Result.success("设置成功");
    }


}
