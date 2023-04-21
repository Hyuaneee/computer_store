package com.example.pojo;


import lombok.Data;


// 商品分类实体类
@Data
public class ProductCategory {
    //主键
    private Integer id;
    //名称
    private String name;
    //状态   1：正常   0：删除
    private Integer status;

}

