package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.common.ReturnFront;
import com.example.pojo.*;
import com.example.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private CartService cartService;
    @Autowired
    private Orders_itemService orders_itemService;

    //添加订单
    @PostMapping("/insert/{aid}")
    public ReturnFront insert(@RequestBody List<Long> cids, @PathVariable Long aid, HttpSession session) {
        //获取session中uid并查找对应数据
        //获取用户信息
        Long uid = (Long) session.getAttribute("uid");
        QueryWrapper<User> userWrapper = new QueryWrapper<>();
        userWrapper.eq("uid", uid);
        User user = userService.getOne(userWrapper);
        if (user == null) {
            return ReturnFront.error("用户不存在");
        }
        //获取地址信息
        QueryWrapper<Address> addressWrapper = new QueryWrapper<>();
        addressWrapper.eq("aid", aid);
        Address address = addressService.getOne(addressWrapper);
        if (address == null) {
            return ReturnFront.error("创建订单失败，无对应收货地址");
        }

        //获取购物车信息
        List<CartVO> listCids = cartService.getListCids(cids);
        if (listCids == null || listCids.size() == 0) {
            return ReturnFront.error("创建订单失败，无对应的购物车信息");
        }

        //获取总价
        Long total_price = 0L;
        for (CartVO cartVO : listCids) {
            total_price += cartVO.getRealPrice();
        }
        System.out.println("============  no:1 start");
        System.out.println(total_price);
        System.out.println("============  no:1 end");

        //操作订单表
        Orders orders = new Orders();
        orders.setUid(user.getUid());
        orders.setRecvName(address.getName());
        orders.setRecvPhone(address.getPhone());
        orders.setRecvProvince(address.getProvinceName());
        orders.setRecvCity(address.getCityName());
        orders.setRecvArea(address.getAreaName());
        orders.setRecvAddress(address.getAddress());
        orders.setTotalPrice(total_price);
        orders.setStatus(0); //订单状态
        orders.setOrderTime(new Date());

        orders.setCreatedUser(user.getUsername());
        orders.setCreatedTime(new Date());
        orders.setModifiedUser(user.getUsername());
        orders.setModifiedTime(new Date());

        //生成订单（添加到表内）
        boolean flag1 = ordersService.save(orders);
        if (!flag1) {
            return ReturnFront.error("创建订单失败");
        }

        for (CartVO cartVO : listCids) {
            total_price += cartVO.getRealPrice();
        }
        System.out.println("============  no:2 start");
        System.out.println(total_price);
        System.out.println("============  no:2 end");

        //操作订单商品表
        for (CartVO cartVO : listCids) {
            Orders_item orders_item = new Orders_item();
            orders_item.setOid(orders.getOid());
            orders_item.setPid(cartVO.getPid());
            orders_item.setTitle(cartVO.getTitle());
            orders_item.setImage(cartVO.getImage());
            orders_item.setPrice(cartVO.getPrice());
            orders_item.setNum(cartVO.getNum());
            orders_item.setAfterSale(0);
            orders_item.setItemStatus(0);
            orders_item.setIsReceive(0);

            orders_item.setCreatedUser(user.getUsername());
            orders_item.setCreatedTime(new Date());
            orders_item.setModifiedUser(user.getUsername());
            orders_item.setModifiedTime(new Date());
            boolean flag2 = orders_itemService.save(orders_item);
            if (!flag2) {
                return ReturnFront.error("创建订单失败");
            }
        }
        return ReturnFront.success(orders.getOid());
    }

    //根据oid获取订单
    @GetMapping("/getOrderOid/{oid}")
    public ReturnFront getOrderOid(@PathVariable Long oid) {
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.eq("oid", oid);
        Orders orders = ordersService.getOne(wrapper);
        if (orders == null) {
            throw new RuntimeException("订单不存在");
        }

        return ReturnFront.success(orders);
    }

    //更新表的status字段和支付时间
    @GetMapping("/updateStatus/{oid}")
    public ReturnFront updateStatus(@PathVariable Long oid) {
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.eq("oid", oid);
        Orders orders = ordersService.getOne(wrapper);
        if (orders == null) {
            return ReturnFront.error("订单不存在");
        }
        if (orders.getStatus() == 1) {
            return ReturnFront.error("请不要重复支付");
        }
        if (orders.getStatus() == 2 || orders.getStatus() == 3 || orders.getStatus() == 4) {
            return ReturnFront.error("您的订单已取消或已完成");
        }
        UpdateWrapper<Orders> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("oid", oid);
        updateWrapper.set("status", 1);
        updateWrapper.set("pay_time", new Date());
        boolean flag = ordersService.update(updateWrapper);
        if (!flag) {
            return ReturnFront.error("支付失败");
        }
        return ReturnFront.success("支付成功");
    }

    //根据oid获取订单列表
    @GetMapping("/getListoid")
    public ReturnFront getListoid(Integer status, HttpSession session) {
        Long uid = (Long) session.getAttribute("uid");
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid);
        if (status != null) {
            wrapper.eq("status", status);
        }
        List<Orders> ordersList = ordersService.list(wrapper);
        List<ReturnFront> result = new ArrayList<>();
        for (Orders orders : ordersList) {
            QueryWrapper<Orders_item> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("oid", orders.getOid());
            List<Orders_item> orders_itemList = orders_itemService.list(queryWrapper);
            if (orders_itemList == null) {
                return ReturnFront.error("订单出现异常");
            }
            result.add(new ReturnFront(orders, orders_itemList));

        }
        return ReturnFront.success(result);
    }


    //用户确认是否收货
    @GetMapping("/updateIs_receive/{id}")
    public ReturnFront updateIs_receive(@PathVariable Long id) {
        Orders_item orders_item = orders_itemService.getById(id);
        //判断订单商品是否存在
        if (orders_item == null) {
            return ReturnFront.error("订单商品有误,请刷新");
        }

        //判断是否已收货
        if (orders_item.getIsReceive() == 1) {
            return ReturnFront.error("已收货");
        }

        //查询商品所属订单
        QueryWrapper<Orders> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("oid", orders_item.getOid());
        Orders one = ordersService.getOne(queryWrapper1);

        //判断订单状态
        if (one.getStatus() == 0) {  //0-未支付，1-已支付，2-已取消，3-已关闭，4-已完成
            return ReturnFront.error("还未支付,请支付");
        }
        if (one.getStatus() == 2 || one.getStatus() == 3) {  //0-未支付，1-已支付，2-已取消，3-已关闭，4-已完成
            return ReturnFront.error("订单已取消或已关闭!");
        }

        //收货操作
        UpdateWrapper<Orders_item> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id);
        wrapper.set("is_receive", 1); //修改为收货状态
        boolean flag = orders_itemService.update(wrapper);
        if (!flag) {
            return ReturnFront.error("操作失败，请重试");
        }

        //判断全部收货来完成订单
        QueryWrapper<Orders_item> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("oid", orders_item.getOid()); //判断订单oid是否相等
        //集合遍历所有订单中商品
        List<Orders_item> list = orders_itemService.list(queryWrapper2);
        int is_receive = 1;
        for (Orders_item list_item : list) {
            //发现有商品还未收货，不进入下面的if
            if (list_item.getIsReceive() == 0) {
                is_receive = 0;
            }
        }

        //集合循环没有修改收货状态，所有商品状态为收货，订单完成
        if (is_receive == 1) {
            UpdateWrapper<Orders> queryWrapper3 = new UpdateWrapper<>();
            queryWrapper3.eq("oid", orders_item.getOid());
            queryWrapper3.set("status", 4); //订单完成
            boolean update = ordersService.update(queryWrapper3);
            if (!update) {
                return ReturnFront.error("订单有误,请刷新");
            }
        }

        return ReturnFront.success("操作成功");
    }

    //用户取消订单
    @GetMapping("/changeStatus/{oid}")
    public ReturnFront changeStatus(@PathVariable Long oid) {
        QueryWrapper<Orders> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("oid", oid);
        Orders orders = ordersService.getOne(wrapper1);
        if (orders == null) {
            return ReturnFront.error("订单信息不存在,请刷新");
        }
        UpdateWrapper<Orders> wrapper2 = new UpdateWrapper<>();
        wrapper2.eq("oid", oid);
        wrapper2.set("status", 2); //修改为取消状态
        boolean flag = ordersService.update(wrapper2);
        if (!flag) {
            throw new RuntimeException("取消失败,请重试");
        }
        return ReturnFront.success("操作成功");
    }

}
