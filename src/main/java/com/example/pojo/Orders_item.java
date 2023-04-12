package com.example.pojo;

import lombok.Data;

/**
 * 订单中存放商品类
 */
@Data
public class Orders_item {
    private Long id;   //订单中的商品记录的id
    private Long oid;   //所归属的订单的id
    private Long pid;   //商品的id(购物车中的商品id)
    private String title;   //商品标题
    private String image;   //商品图片
    private Long price;    //商品价格
    private Long num;    //购买数量
    private Integer itemStatus;  //货品状态：0未发货，1已发货，
    private Integer isReceive;  //用户接受状态，0未接受，1已接收
}
