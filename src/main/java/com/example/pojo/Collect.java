package com.example.pojo;

import lombok.Data;

/**
 * 收藏类
 */
@Data
public class Collect extends BaseEntity {
    private Long id;   //用户收藏数据id
    private Long uid;  //用户id
    private Long pid;  //商品id
    private String title;   //商品标题
    private Long price;   //商品单价
    private String image;   //商品图片
}
