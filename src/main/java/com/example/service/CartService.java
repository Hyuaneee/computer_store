package com.example.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pojo.Cart;
import com.example.pojo.CartVO;

import java.util.List;


public interface CartService extends IService<Cart> {

    List<CartVO> PageCartVO(Long uid);

    List<CartVO> getListCids(List<Long> list);
}
