package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.ReturnFront;
import com.example.mapper.UserMapper;
import com.example.pojo.Address;
import com.example.pojo.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;

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
    public ReturnFront<User> login(HttpServletRequest request, @RequestBody User user) {
        //密码加盐（拼接盐+密码后使用MD5算法加密）
        String passwd = user.getPassword().concat(md5Salt);
        passwd = DigestUtils.md5DigestAsHex(passwd.getBytes());

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());
        User one = userService.getOne(queryWrapper);
        if (one == null || one.getDeleted() == 1) {
            return ReturnFront.error("用户名或密码错误");
        }

        if (!one.getPassword().equals(passwd)) {
            return ReturnFront.error("用户名或密码错误");
        }

        if (one.getStatus() == 1) {
            return ReturnFront.error("用户已被禁用！");
        }

        request.getSession().setAttribute("uid", one.getUid());
        System.out.println("用户:" + one.getUid() + one.getUsername() + "已登录");
        return ReturnFront.success(one);
    }

    /**
     * 用户退出
     *
     * @param request
     * @return
     */
    @GetMapping("/logout")
    public ReturnFront<String> logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().removeAttribute("uid");
        response.sendRedirect("/login.html");
        return ReturnFront.success("用户已退出");
    }

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    @PostMapping("/enroll")
    public ReturnFront<String> insert(@RequestBody User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());
        User one = userService.getOne(queryWrapper);
        if (one != null) {
            return ReturnFront.error("用户已存在");
        }
        //设置密码
        String passwd = user.getPassword().concat(md5Salt);
        user.setPassword(DigestUtils.md5DigestAsHex(passwd.getBytes()));

        user.setStatus(0);  //是否启用：0-启用，1-禁用
        user.setDeleted(0); //是否删除：0-未删除，1-已删除
        user.setCreatedUser(user.getUsername());  //日志-创建人
        user.setModifiedUser(user.getUsername()); //日志-最后修改执行人
        user.setCreatedTime(new Date());    //日志-创建时间
        user.setModifiedTime(new Date());   //日志-最后修改时间

        boolean flag = userService.save(user);
        if (!flag) {
            return ReturnFront.error("用户创建失败");
        }
        return ReturnFront.success("用户创建成功");
    }

    //获取user数据
    @GetMapping("/getUser")
    public ReturnFront<User> getUser(HttpSession session) {
        //获取session中uid并查找对应数据
        Long uid = (Long) session.getAttribute("uid");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        User user = userService.getOne(queryWrapper);
        if (user == null) {
            return ReturnFront.error("用户不存在");
        }
        return ReturnFront.success(user);
    }


    //用户密码修改
    @PutMapping("/update/{password}/{password2}")
    public ReturnFront<String> update(HttpSession session, @PathVariable String password, @PathVariable String password2) {
        //获取session中uid并查找对应数据
        Long uid = (Long) session.getAttribute("uid");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        User user = mapper.selectOne(queryWrapper);

        //密码加盐（拼接盐+密码后使用MD5算法加密）
        String md5Passwd = password.concat(md5Salt);
        md5Passwd = DigestUtils.md5DigestAsHex(md5Passwd.getBytes());
        if (!md5Passwd.equals(user.getPassword())) {
            return ReturnFront.error("原密码不正确");
        }
        //加密新密码并输入数据库
        String newPasswd = password2.concat(md5Salt);
        System.out.println("新密码为:" + newPasswd + "and" + password2);
        newPasswd = DigestUtils.md5DigestAsHex(newPasswd.getBytes());
        User updateUser = new User();
        updateUser.setPassword(newPasswd);

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", user.getUid());
        boolean flag = userService.update(updateUser, wrapper);
        if (!flag) {
            return ReturnFront.error("更新失败");
        }
        return ReturnFront.success("更新成功");
    }

    //修改用户信息
    @PutMapping("/updateUser")
    public ReturnFront<String> updateUser(HttpSession session, @RequestBody User user) {
        //获取session中uid并查找对应数据
        Long uid = (Long) session.getAttribute("uid");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        boolean flag = userService.update(user, queryWrapper);
        if (!flag) {
            return ReturnFront.error("修改失败");
        }
        //更新sesson中的数据
        session.setAttribute("user", user);
        return ReturnFront.success("修改成功");
    }

    //分页查询
    @PostMapping("/PageList/{currentPage}/{pageSize}")
    public ReturnFront insert(@PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        IPage<User> iPage = new Page<>(currentPage, pageSize);
        IPage<User> page = userService.page(iPage, wrapper);
        return ReturnFront.success(page);
    }


    //账号状态设置
    @GetMapping("/setStatus/{uid}/{status}")
    public ReturnFront setDefault(@PathVariable Long uid, @PathVariable Integer status) {
        status = status == 0 ? 1 : 0;
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("uid", uid);
        wrapper.set("status", status);
        boolean flag = userService.update(wrapper);
        if (!flag) {
            return ReturnFront.error("设置失败,请重试");
        }
        return ReturnFront.success("设置成功");
    }

    //账号逻辑删除
    @GetMapping("/setDeleted/{uid}")
    public ReturnFront setDeleted(@PathVariable Long uid) {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("uid", uid);
        wrapper.set("deleted", 1);
        boolean flag = userService.update(wrapper);
        if (!flag) {
            return ReturnFront.error("设置失败,请重试");
        }
        return ReturnFront.success("设置成功");
    }

}
