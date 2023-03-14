package com.example.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Address extends BaseEntity{
    @TableId(type = IdType.AUTO)
    private Long aid;  //收货地址(本类)id
    private Long uid;  //归属的用户id
    private String name;  //收货人姓名
    private String province_name;  //省-名称
    private String province_code;  //省-行政代号
    private String city_name;  //市-名称
    private String city_code;  //市-行政代号
    private String area_name;  //区-名称
    private String area_code;  //区-行政代号
    private String zip;  //邮政编码
    private String address;  //详细地址
    private String phone;  //手机
    private String tel;  //固定电话
    private String tag;  //标签
    private Integer is_default;  //是否默认：0-不默认，1-默认

}
