package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pojo.Orders;

public interface OrdersService extends IService<Orders> {

    //调用mapper中添加方法
    int insert(Orders orders);

}
