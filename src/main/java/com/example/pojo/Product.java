package com.example.pojo;

import lombok.Data;

/**
 * 商品类
 */
@Data
public class Product  {
    private Long id;  //商品id
    private String categoryId; //商品分类
    private String itemType;  //商品简称
    private String title;  //商品全程
    private String sellPoint;  //商品卖点
    private Long price;  //商品单价
    private Long num;  //库存数量
    private String image;  //图片路径
    private Integer status;  //商品状态  1：上架   2：下架   3：删除
}
