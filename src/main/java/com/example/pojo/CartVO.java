package com.example.pojo;

import lombok.Data;

/**
 * 购物车实体类再加工，添加额外字段
 */

@Data
public class CartVO {
    private Long cid;   //购物车数据id
    private Long uid;   //用户id
    private Long pid;   //商品id
    private Long price;    //加入时商品单价
    private Long num;   //商品数量
    private Long numCount;  //库存数量
    private String title;  //商品标题
    private Long realPrice;   //此商品总价格
    private String image;  //图片路径
}
