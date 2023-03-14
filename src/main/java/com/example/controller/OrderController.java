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
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private CartService cartService;
    @Autowired
    private Order_itemService order_itemService;

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
        Order order = new Order();
        order.setUid(user.getUid());
        order.setRecv_name(address.getName());
        order.setRecv_phone(address.getPhone());
        order.setRecv_province(address.getProvince_name());
        order.setRecv_city(address.getCity_name());
        order.setRecv_area(address.getArea_name());
        order.setRecv_address(address.getAddress());
        order.setTotal_price(total_price);
        order.setStatus(0); //订单状态
        order.setOrder_time(new Date());

        order.setCreatedUser(user.getUsername());
        order.setCreatedTime(new Date());
        order.setModifiedUser(user.getUsername());
        order.setModifiedTime(new Date());

        //生成订单（添加到表内）
        boolean flag1 = orderService.save(order);
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
            Order_item order_item = new Order_item();
            order_item.setOid(order.getOid());
            order_item.setPid(cartVO.getPid());
            order_item.setTitle(cartVO.getTitle());
            order_item.setImage(cartVO.getImage());
            order_item.setPrice(cartVO.getPrice());
            order_item.setNum(cartVO.getNum());
            order_item.setAfter_sale(0);
            order_item.setItem_status(0);
            order_item.setIs_receive(0);

            order_item.setCreatedUser(user.getUsername());
            order_item.setCreatedTime(new Date());
            order_item.setModifiedUser(user.getUsername());
            order_item.setModifiedTime(new Date());
            boolean flag2 = order_itemService.save(order_item);
            if (!flag2) {
                return ReturnFront.error("创建订单失败");
            }
        }
        return ReturnFront.success("");
    }

    //更新表的status字段和支付时间
    @GetMapping("/updateStatus/{oid}")
    public ReturnFront updateStatus(@PathVariable Long oid) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("oid", oid);
        Order order = orderService.getOne(wrapper);
        if (order == null) {
            return ReturnFront.error("订单不存在");
        }
        if (order.getStatus() == 1) {
            return ReturnFront.error("请不要重复支付");
        }
        if (order.getStatus() == 2 || order.getStatus() == 3 || order.getStatus() == 4) {
            return ReturnFront.error("您的订单已取消或已完成");
        }
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("oid", oid);
        updateWrapper.set("status", 1);
        updateWrapper.set("pay_time", new Date());
        boolean flag = orderService.update(updateWrapper);
        if (!flag) {
            return ReturnFront.error("支付失败");
        }
        return ReturnFront.success("支付成功");
    }

    //根据oid获取订单列表
    @GetMapping("/getListoid")
    public ReturnFront getListoid(Integer status, HttpSession session) {
        Long uid = (Long) session.getAttribute("uid");
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid);
        if (status != null) {
            wrapper.eq("status", status);
        }
        List<Order> orderList = orderService.list(wrapper);
        List<ReturnFront> result = new ArrayList<>();
        for (Order order : orderList) {
            QueryWrapper<Order_item> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("oid", order.getOid());
            List<Order_item> order_itemList = order_itemService.list(queryWrapper);
            if (order_itemList == null) {
                return ReturnFront.error("订单出现异常");
            }
            result.add(new ReturnFront(order, order_itemList));

        }
        return ReturnFront.success(result);
    }


    //用户确认是否收货
    @GetMapping("/updateIs_receive/{id}")
    public ReturnFront updateIs_receive(@PathVariable Long id) {
        Order_item order_item = order_itemService.getById(id);
        //判断订单商品是否存在
        if (order_item == null) {
            return ReturnFront.error("订单商品有误,请刷新");
        }

        //判断是否已收货
        if (order_item.getIs_receive() == 1) {
            return ReturnFront.error("已收货");
        }

        //查询商品所属订单
        QueryWrapper<Order> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("oid", order_item.getOid());
        Order one = orderService.getOne(queryWrapper1);

        //判断订单状态
        if (one.getStatus() == 0) {  //0-未支付，1-已支付，2-已取消，3-已关闭，4-已完成
            return ReturnFront.error("还未支付,请支付");
        }
        if (one.getStatus() == 2 || one.getStatus() == 3) {  //0-未支付，1-已支付，2-已取消，3-已关闭，4-已完成
            return ReturnFront.error("订单已取消或已关闭!");
        }

        //收货操作
        UpdateWrapper<Order_item> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id);
        wrapper.set("is_receive", 1); //修改为收货状态
        boolean flag = order_itemService.update(wrapper);
        if (!flag) {
            return ReturnFront.error("操作失败，请重试");
        }

        //判断全部收货来完成订单
        QueryWrapper<Order_item> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("oid", order_item.getOid()); //判断订单oid是否相等
        //集合遍历所有订单中商品
        List<Order_item> list = order_itemService.list(queryWrapper2);
        int is_receive = 1;
        for (Order_item orderItem : list) {
            //发现有商品还未收货，不进入下面的if
            if (order_item.getIs_receive() == 0) {
                is_receive = 0;
            }
        }

        //集合循环没有修改收货状态，所有商品状态为收货，订单完成
        if (is_receive == 1) {
            UpdateWrapper<Order> queryWrapper3 = new UpdateWrapper<>();
            queryWrapper3.eq("oid", order_item.getOid());
            queryWrapper3.set("status", 4); //订单完成
            boolean update = orderService.update(queryWrapper3);
            if (!update) {
                return ReturnFront.error("订单有误,请刷新");
            }
        }

        return ReturnFront.success("操作成功");
    }

    //用户取消订单
    @GetMapping("/changeStatus/{oid}")
    public ReturnFront changeStatus(@PathVariable Long oid) {
        QueryWrapper<Order> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("oid", oid);
        Order order = orderService.getOne(wrapper1);
        if (order == null) {
            return ReturnFront.error("订单信息不存在,请刷新");
        }
        UpdateWrapper<Order> wrapper2 = new UpdateWrapper<>();
        wrapper2.eq("oid", oid);
        wrapper2.set("status", 2); //修改为取消状态
        boolean flag = orderService.update(wrapper2);
        if (!flag) {
            throw new RuntimeException("取消失败,请重试");
        }
        return ReturnFront.success("操作成功");
    }

}
