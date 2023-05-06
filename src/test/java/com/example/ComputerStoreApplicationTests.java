package com.example;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mapper.UserMapper;
import com.example.pojo.Address;
import com.example.pojo.Orders;
import com.example.pojo.User;
import com.example.service.AddressService;
import com.example.service.OrdersService;
import com.example.service.Orders_itemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ComputerStoreApplicationTests {

    @Autowired
    private UserMapper mapper;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private Orders_itemService orders_itemService;

    @Autowired
    private AddressService addressService;

    @Test
    void contextLoads() {
        /*Date date = new Date();
        System.out.println(date);*/
        String username = "front";
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = mapper.selectOne(queryWrapper);
        System.out.println(user);
    }

    @Test
    void getListOid() {
        Long uid = 3L;
        Integer status = 1;
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid);
        if (status != null) {
            wrapper.eq("status", status);
        }
        List<Orders> orderList = ordersService.list(wrapper);
        for (Orders order : orderList) {
            System.out.println(order);
        }
      /*  List<Result> result = new ArrayList<>();
        for (Orders order : orderList) {
            QueryWrapper<Orders_item> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("oid", order.getOid());
            List<Orders_item> order_itemList = orders_itemService.list(queryWrapper);
            if (order_itemList == null) {
                System.out.println("订单出现异常");
            }
            result.add(new Result(order, order_itemList));
        }
        for (Result returnFront : result) {
            System.out.println(returnFront);
        }*/
    }

    @Test
    void getAddressList() {
        List<Address> list = addressService.list();
        for (Address address : list) {
            System.out.println(address);
        }
    }


}
