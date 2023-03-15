package com.example.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.Order_itemMapper;
import com.example.pojo.Orders_item;
import com.example.service.Orders_itemService;
import org.springframework.stereotype.Service;

@Service
public class Orders_itemServiceImpl extends ServiceImpl<Order_itemMapper, Orders_item> implements Orders_itemService {

}
