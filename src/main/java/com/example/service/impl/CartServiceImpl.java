package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.CartMapper;
import com.example.pojo.Cart;
import com.example.service.CartService;
import org.springframework.stereotype.Service;


@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

}
