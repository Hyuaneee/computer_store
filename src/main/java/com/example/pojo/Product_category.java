package com.example.pojo;

import lombok.Data;

/**
 * 商品分类实体类
 */
@Data
public class Product_category extends BaseEntity {
    private Long id;   //商品分类id
    private Long parentId;   //父分类id
    private String name;  //分类名称
    private Integer status;  //状态   1：正常   0：删除
    private Integer sortOrder;  //排序号
    private Integer isParent;   //是否是父分类   1：是  0：否
}
