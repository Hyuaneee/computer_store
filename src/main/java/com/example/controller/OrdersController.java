package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.common.Result;
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
    @Autowired
    private ProductService productService;

    //添加订单
    @PostMapping("/insert/{aid}")
    public Result insert(@RequestBody List<Long> cids, @PathVariable Long aid, HttpSession session) {
        //获取用户信息
        Long uid = (Long) session.getAttribute("uid");
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getUid, uid);
        User user = userService.getOne(userWrapper);
        if (user == null) {
            return Result.error("用户不存在");
        }
        //获取地址信息
        LambdaQueryWrapper<Address> addressWrapper = new LambdaQueryWrapper<>();
        addressWrapper.eq(Address::getAid, aid);
        Address address = addressService.getOne(addressWrapper);
        if (address == null) {
            return Result.error("创建订单失败，无对应收货地址");
        }
        //获取购物车信息
        List<CartVO> listCids = cartService.getListCids(cids);
        if (listCids == null || listCids.size() == 0) {
            return Result.error("创建订单失败，无对应的购物车信息");
        }
        //获取总价
        Long total_price = 0L;
        for (CartVO cartVO : listCids) {
            Product product = productService.getById(cartVO.getPid());
            product.setNum(product.getNum() - cartVO.getNum());
            if (product.getNum() < 0) {
                return Result.error(product.getTitle() + "的库存不足");
            }
            if (product.getNum() == 0) {
                product.setStatus(1);
            }
            boolean numFlag = productService.updateById(product);
            if (!numFlag) {
                return Result.error("库存扣除失败");
            }
            total_price += cartVO.getRealPrice();
        }

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
        boolean flag1 = ordersService.save(orders);        //生成订单（添加到表内）
        if (!flag1) {
            return Result.error("创建订单失败");
        }
        for (CartVO cartVO : listCids) {
            total_price += cartVO.getRealPrice();
        }

        //操作订单商品表
        for (CartVO cartVO : listCids) {
            Orders_item orders_item = new Orders_item();
            orders_item.setOid(orders.getOid());
            orders_item.setPid(cartVO.getPid());
            orders_item.setTitle(cartVO.getTitle());
            orders_item.setImage(cartVO.getImage());
            orders_item.setPrice(cartVO.getPrice());
            orders_item.setNum(cartVO.getNum());
            orders_item.setItemStatus(0);
            orders_item.setIsReceive(0);

            boolean flag2 = orders_itemService.save(orders_item);
            if (!flag2) {
                return Result.error("创建订单失败");
            }
        }
        return Result.success(orders.getOid());
    }

    //立即购买
    @PostMapping("/buyNow/{pid}/{num}/{aid}")
    public Result buyNow(@PathVariable Long pid, @PathVariable Long num, @PathVariable Long aid, HttpSession session) {
        //获取用户信息
        Long uid = (Long) session.getAttribute("uid");
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getUid, uid);
        User user = userService.getOne(userWrapper);
        if (user == null) {
            return Result.error("用户不存在");
        }
        //获取地址信息
        LambdaQueryWrapper<Address> addressWrapper = new LambdaQueryWrapper<>();
        addressWrapper.eq(Address::getAid, aid);
        Address address = addressService.getOne(addressWrapper);
        if (address == null) {
            return Result.error("创建订单失败，无对应收货地址");
        }
        Product product = productService.getById(pid);
        if (product == null) {
            return Result.error("商品不存在");
        }
        Long total_price = product.getPrice() * num;
        Long productNum = product.getNum();
        if (productNum < num) {
            return Result.error("商品库存不足");
        }
        product.setNum(productNum - num);
        if (product.getNum() == 0) {
            product.setStatus(1);
        }
        productService.updateById(product);
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

        boolean flag1 = ordersService.save(orders);        //生成订单（添加到表内）
        if (!flag1) {
            return Result.error("创建订单失败");
        }

        Orders_item orders_item = new Orders_item();
        orders_item.setOid(orders.getOid());
        orders_item.setPid(pid);
        orders_item.setTitle(product.getTitle());
        orders_item.setImage(product.getImage());
        orders_item.setPrice(product.getPrice());
        orders_item.setNum(num);
        orders_item.setItemStatus(0);
        orders_item.setIsReceive(0);
        boolean flag2 = orders_itemService.save(orders_item);
        if (!flag2) {
            return Result.error("创建订单失败");
        }
        return Result.success(orders.getOid());
    }

    //更新表的status字段和支付时间
    @GetMapping("/updateStatus/{oid}")
    public Result updateStatus(@PathVariable Long oid) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getOid, oid);
        Orders orders = ordersService.getOne(wrapper);
        if (orders == null) {
            return Result.error("订单不存在");
        }
        if (orders.getStatus() == 1) {
            return Result.error("请不要重复支付");
        }
        if (orders.getStatus() == 2) {
            return Result.error("您的订单已取消");
        }
        LambdaUpdateWrapper<Orders> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Orders::getOid, oid);
        updateWrapper.set(Orders::getStatus, 1);
        updateWrapper.set(Orders::getPayTime, new Date());
        boolean flag = ordersService.update(updateWrapper);
        if (!flag) {
            return Result.error("支付失败");
        }
        return Result.success("支付成功");
    }

    //根据oid获取订单
    @GetMapping("/getOrderOid/{oid}")
    public Result getOrderOid(@PathVariable Long oid, HttpSession session) {
        Long uid = (Long) session.getAttribute("uid");
        LambdaQueryWrapper<Orders> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(Orders::getOid, oid);
        Orders orders = ordersService.getOne(wrapper1);
        if (orders == null) {
            throw new RuntimeException("订单不存在");
        }
        LambdaQueryWrapper<Orders_item> wrapper2 = new LambdaQueryWrapper<>();
        wrapper2.eq(Orders_item::getOid, oid);
        List<Orders_item> orders_itemList = orders_itemService.list(wrapper2);
        if (orders_itemList == null) {
            return Result.error("订单出现异常");
        }
        List<Result> result = new ArrayList<>();
        result.add(new Result(orders, orders_itemList));
        return Result.success(result);
    }

    //根据oid获取订单列表
    @GetMapping("/getListoid")
    public Result getListoid(Integer status, HttpSession session) {
        Long uid = (Long) session.getAttribute("uid");
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUid, uid);
        if (status != null) {
            wrapper.eq(Orders::getStatus, status);
        }
        wrapper.orderByDesc(Orders::getOrderTime);
        List<Orders> ordersList = ordersService.list(wrapper);
        List<Result> result = new ArrayList<>();
        for (Orders orders : ordersList) {
            LambdaQueryWrapper<Orders_item> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Orders_item::getOid, orders.getOid());
            wrapper.orderByDesc(Orders::getPayTime);
            List<Orders_item> orders_itemList = orders_itemService.list(queryWrapper);
            if (orders_itemList == null) {
                return Result.error("订单出现异常");
            }
            result.add(new Result(orders, orders_itemList));

        }
        return Result.success(result);
    }


    //用户确认是否收货
    @GetMapping("/updateIsReceive/{id}")
    public Result updateIsReceive(@PathVariable Long id) {
        Orders_item orders_item = orders_itemService.getById(id);
        //判断订单商品是否存在
        if (orders_item == null) {
            return Result.error("订单商品有误,请刷新");
        }

        //判断是否已收货
        if (orders_item.getIsReceive() == 1) {
            return Result.error("商品已收货");
        }

        //查询商品所属订单
        LambdaQueryWrapper<Orders> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Orders::getOid, orders_item.getOid());
        Orders one = ordersService.getOne(queryWrapper1);

        //判断订单状态
        if (one.getStatus() == 0) {  //0-未支付，1-已支付，2-已取消，3-已完成
            return Result.error("还未支付,请支付");
        }
        if (one.getStatus() == 2) {  //0-未支付，1-已支付，2-已取消，3已完成
            return Result.error("订单已取消!");
        }

        //收货操作
        LambdaUpdateWrapper<Orders_item> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Orders_item::getId, id);
        wrapper.set(Orders_item::getIsReceive, 1); //修改为收货状态
        boolean flag = orders_itemService.update(wrapper);
        if (!flag) {
            return Result.error("操作失败，请重试");
        }

        //判断全部收货来完成订单
        LambdaQueryWrapper<Orders_item> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(Orders_item::getOid, orders_item.getOid()); //判断订单oid是否相等
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
            LambdaUpdateWrapper<Orders> queryWrapper3 = new LambdaUpdateWrapper<>();
            queryWrapper3.eq(Orders::getOid, orders_item.getOid());
            queryWrapper3.set(Orders::getStatus, 3); //订单完成
            boolean update = ordersService.update(queryWrapper3);
            if (!update) {
                return Result.error("订单有误,请刷新");
            }
        }

        return Result.success("操作成功");
    }

    //用户取消订单
    @GetMapping("/changeStatus/{oid}")
    public Result changeStatus(@PathVariable Long oid) {
        LambdaQueryWrapper<Orders> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(Orders::getOid, oid);
        Orders orders = ordersService.getOne(wrapper1);
        if (orders == null) {
            return Result.error("订单信息不存在,请刷新");
        }
        LambdaUpdateWrapper<Orders> wrapper2 = new LambdaUpdateWrapper<>();
        wrapper2.eq(Orders::getOid, oid);
        wrapper2.set(Orders::getStatus, 2); //修改为取消状态
        boolean flag = ordersService.update(wrapper2);
        if (!flag) {
            return Result.error("取消失败,请重试");
        }
        LambdaQueryWrapper<Orders_item> wrapper3 = new LambdaQueryWrapper<>();
        wrapper3.eq(Orders_item::getOid, oid);
        List<Orders_item> list = orders_itemService.list(wrapper3);
        for (Orders_item orders_item : list) {
            LambdaQueryWrapper<Product> wrapper4 = new LambdaQueryWrapper<>();
            wrapper4.eq(Product::getId, orders_item.getPid());
            Product one = productService.getOne(wrapper4);
            if (one == null) {
                return Result.error("商品不存在");
            }
            one.setNum(one.getNum() + orders_item.getNum());
            if (one.getNum() > 0) {
                one.setStatus(0);
            }
            boolean numFlag = productService.updateById(one);
            if (!numFlag) {
                return Result.error("操作失败");
            }
        }
        return Result.success("操作成功");
    }

    //后台获取所有订单
    @GetMapping("/getList")
    public Result getList(String context, Integer status) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        if (!context.equals("")) {
            wrapper.eq(Orders::getOid, context);
        }
        if (status != null) {
            wrapper.eq(Orders::getStatus, status);
        }
        wrapper.orderByDesc(Orders::getPayTime);
        List<Orders> ordersList = ordersService.list(wrapper);
        List<Result> result = new ArrayList<>();
        for (Orders orders : ordersList) {
            LambdaQueryWrapper<Orders_item> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Orders_item::getOid, orders.getOid());
            wrapper.orderByDesc(Orders::getPayTime);
            List<Orders_item> orders_itemList = orders_itemService.list(queryWrapper);
            if (orders_itemList == null) {
                return Result.error("订单出现异常");
            }
            result.add(new Result(orders, orders_itemList));

        }
        return Result.success(result);
    }

    //商品发货
    @GetMapping("/updateItemStatus/{id}")
    public Result updateIs_receive(@PathVariable Long id) {
        Orders_item orders_item = orders_itemService.getById(id);
        //判断订单商品是否存在
        if (orders_item == null) {
            return Result.error("订单商品有误,请刷新");
        }

        //判断是否已收货
        if (orders_item.getItemStatus() == 1) {
            return Result.error("已发货");
        }

        //查询商品所属订单
        LambdaQueryWrapper<Orders> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Orders::getOid, orders_item.getOid());
        Orders one = ordersService.getOne(queryWrapper1);

        //判断订单状态
        if (one.getStatus() == 0) {  //0-未支付，1-已支付，2-已取消，3-已完成
            return Result.error("还未支付,请支付");
        }
        if (one.getStatus() == 2) {  //0-未支付，1-已支付，2-已取消，3-已完成
            return Result.error("订单已取消!");
        }

        //发货操作
        LambdaUpdateWrapper<Orders_item> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Orders_item::getId, id);
        wrapper.set(Orders_item::getItemStatus, 1); //修改为收货状态
        boolean flag = orders_itemService.update(wrapper);
        if (!flag) {
            return Result.error("操作失败，请重试");
        }

        return Result.success("操作成功");
    }
}
