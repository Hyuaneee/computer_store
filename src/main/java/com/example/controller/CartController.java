package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.common.ReturnFront;
import com.example.pojo.Cart;
import com.example.pojo.CartVO;
import com.example.pojo.User;
import com.example.service.CartService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    //添加商品到购物车
    @GetMapping("/insert")
    public ReturnFront insert(Cart cart, HttpSession session) {
        //获取session中uid并查找对应数据
        Long uid = (Long) session.getAttribute("uid");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        User user = userService.getOne(queryWrapper);
        if (user == null) {
            return ReturnFront.error("用户不存在");
        }

        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", user.getUid());
        wrapper.eq("pid", cart.getPid());
        Cart one = cartService.getOne(wrapper);
        if (one != null) {
            return ReturnFront.error("已添加到购物车,请在购物车页面修改或者结算");
        }

        cart.setUid(user.getUid());

        cart.setCreatedUser(user.getUsername());
        cart.setCreatedTime(new Date());
        cart.setModifiedUser(user.getUsername());
        cart.setModifiedTime(new Date());
        boolean flag = cartService.save(cart);
        if (!flag) {
            return ReturnFront.error("添加失败");
        }
        return ReturnFront.success("添加成功");
    }

    //根据商品id（pid）获取购物车信息
    @GetMapping("/getCart")
    public ReturnFront getCart(Long pid) {
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("pid", pid);
        Cart cart = cartService.getOne(wrapper);
        if (cart == null) {
            return ReturnFront.error("未加入购物车");
        }
        return ReturnFront.success("");
    }

    //根据商品id（pid）删除信息
    @GetMapping("/delete/{pid}")
    public ReturnFront delete(@PathVariable Long pid) {
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("pid", pid);
        boolean flag = cartService.remove(wrapper);
        if (!flag) {
            return ReturnFront.error("操作失败，请重试");
        }
        return ReturnFront.success("已从购物车中移除！");
    }

    //购物车页面获取所有的购物车信息
    @PostMapping("/getPageList")
    public ReturnFront getPageList(HttpSession session) {
        Long uid = (Long) session.getAttribute("uid");
        List<CartVO> list = cartService.PageCartVO(uid);
        return ReturnFront.success(list);
    }

    //行删除
    @GetMapping("/delCartItem/{cid}")
    public ReturnFront delCartItem(@PathVariable Long cid) {
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("cid", cid);
        Cart one = cartService.getOne(wrapper);
        if (one == null) {
            throw new RuntimeException("信息有误，请刷新");
        }
        boolean flag = cartService.remove(wrapper);
        if (!flag) {
            return ReturnFront.error("删除失败");
        }
        return ReturnFront.success("删除成功");
    }

    //集合删除
    @PostMapping("/deleteList")
    public ReturnFront deleteList(@RequestBody List<Long> list) {
        for (Long cid : list) {
            QueryWrapper<Cart> wrapper = new QueryWrapper<>();
            wrapper.eq("cid", cid);
            boolean flag = cartService.remove(wrapper);
            if (!flag) {
                return ReturnFront.error("删除失败");
            }
        }

        return ReturnFront.success("删除成功");
    }

    //增加或减少购物车中商品的num
    @PutMapping("/handleChange/{cid}/{num}")
    public ReturnFront handleChange(@PathVariable Long cid, @PathVariable Long num) {
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("cid", cid);
        Cart one = cartService.getOne(wrapper);
        if (one == null) {
            return ReturnFront.error("信息有误，请刷新");
        }
        UpdateWrapper<Cart> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("cid", cid);  //条件
        updateWrapper.set("num", num);   //修改后的值
        boolean flag = cartService.update(updateWrapper);
        if (!flag) {
            return ReturnFront.error("添加失败,请重试");
        }
        return ReturnFront.success("添加成功");
    }

    //根据cids数组获取购物车信息
    @PostMapping("/getListCids")
    public ReturnFront getListCids(@RequestBody List<Long> cids) {
        List<CartVO> list = cartService.getListCids(cids);
        if (list == null) {
            return ReturnFront.error("获取失败，请重试");
        }

        return ReturnFront.success(list);
    }
}
