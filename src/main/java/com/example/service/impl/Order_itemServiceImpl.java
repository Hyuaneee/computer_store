package com.example.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.Order_itemMapper;
import com.example.pojo.Order_item;
import com.example.service.Order_itemService;
import org.springframework.stereotype.Service;

@Service
public class Order_itemServiceImpl extends ServiceImpl<Order_itemMapper, Order_item> implements Order_itemService {

}
