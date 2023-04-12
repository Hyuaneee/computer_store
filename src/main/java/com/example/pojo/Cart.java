package com.example.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 购物车类
 */

@Data
public class Cart {
    @TableId(type = IdType.AUTO)
    private Long cid;   //购物车数据id
    private Long uid;   //用户id
    private Long pid;   //商品id
    private Long price;    //加入时商品单价
    private Long num;   //商品数量
}
