package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.common.Result;
import com.example.pojo.Cart;
import com.example.pojo.CartVO;
import com.example.pojo.Product;
import com.example.pojo.User;
import com.example.service.CartService;
import com.example.service.ProductService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    //添加商品到购物车
    @GetMapping("/insert")
    public Result insert(Cart cart, HttpSession session) {
        //获取session中uid并查找对应数据
        Long uid = (Long) session.getAttribute("uid");
        LambdaQueryWrapper<User> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(User::getUid, uid);
        User user = userService.getOne(wrapper1);
        if (user == null) {
            return Result.error("用户不存在");
        }
        LambdaQueryWrapper<Cart> wrapper2 = new LambdaQueryWrapper<>();
        wrapper2.eq(Cart::getUid, user.getUid()).eq(Cart::getPid, cart.getPid());
        Cart one = cartService.getOne(wrapper2);
        if (one != null) {
            return Result.error("已添加到购物车,请在购物车页面修改或者结算");
        }
        cart.setUid(user.getUid());
        boolean flag = cartService.save(cart);
        if (!flag) {
            return Result.error("添加失败");
        }
        return Result.success("添加成功");
    }

    //根据商品id（pid）获取购物车信息
    @GetMapping("/getCart")
    public Result getCart(Long pid) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getPid, pid);
        Cart cart = cartService.getOne(wrapper);
        if (cart == null) {
            return Result.error("未加入购物车");
        }
        return Result.success("");
    }

    //根据商品id（pid）删除信息
    @GetMapping("/delete/{pid}")
    public Result delete(@PathVariable Long pid) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getPid, pid);
        boolean flag = cartService.remove(wrapper);
        if (!flag) {
            return Result.error("操作失败，请重试");
        }
        return Result.success("已从购物车中移除！");
    }

    //购物车页面获取所有的购物车信息
    @PostMapping("/getPageList")
    public Result getPageList(HttpSession session) {
        Long uid = (Long) session.getAttribute("uid");
        List<CartVO> list = cartService.PageCartVO(uid);
        return Result.success(list);
    }

    //行删除
    @GetMapping("/delCartItem/{cid}")
    public Result delCartItem(@PathVariable Long cid) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getCid, cid);
        Cart one = cartService.getOne(wrapper);
        if (one == null) {
            throw new RuntimeException("信息有误，请刷新");
        }
        boolean flag = cartService.remove(wrapper);
        if (!flag) {
            return Result.error("删除失败");
        }
        return Result.success("删除成功");
    }

    //集合删除
    @PostMapping("/deleteList")
    public Result deleteList(@RequestBody List<Long> list) {
        for (Long cid : list) {
            LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Cart::getCid, cid);
            boolean flag = cartService.remove(wrapper);
            if (!flag) {
                return Result.error("删除失败");
            }
        }

        return Result.success("删除成功");
    }

    //增加或减少购物车中商品的num
    @PutMapping("/handleChange/{cid}/{num}")
    public Result handleChange(@PathVariable Long cid, @PathVariable Long num) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getCid, cid);
        Cart one = cartService.getOne(wrapper);
        if (one == null) {
            return Result.error("信息有误，请刷新");
        }
        Product product = productService.getById(one.getPid());

        if (num > product.getNum()) {
            num = product.getNum();
        }
        LambdaUpdateWrapper<Cart> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Cart::getCid, cid);  //条件
        updateWrapper.set(Cart::getNum, num);   //修改后的值
        boolean flag = cartService.update(updateWrapper);
        if (!flag) {
            return Result.error("添加失败,请重试");
        }
        return Result.success("添加成功");
    }

    //根据cids数组获取购物车信息
    @PostMapping("/getListCids")
    public Result getListCids(@RequestBody List<Long> cids) {
        List<CartVO> list = cartService.getListCids(cids);
        if (list == null) {
            return Result.error("获取失败，请重试");
        }
        return Result.success(list);
    }
}
