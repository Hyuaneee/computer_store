package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pojo.Order;

public interface OrderService extends IService<Order> {
    int insert(Order order);
}
