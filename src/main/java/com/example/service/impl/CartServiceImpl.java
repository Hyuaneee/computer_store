package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.CartMapper;
import com.example.pojo.Cart;
import com.example.pojo.CartVO;
import com.example.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Override
    public List<CartVO> PageCartVO(Long uid) {
        return cartMapper.PageCartVO(uid);
    }

    @Override
    public List<CartVO> getListCids(List<Long> list) {
        return cartMapper.getListCids(list);
    }
}
