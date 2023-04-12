package com.example.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * 订单类
 */
@Data
public class Orders {
    @TableId(type = IdType.AUTO)
    private Long oid;   //订单id
    private Long uid;   //用户id
    private String recvName;   //收货人姓名
    private String recvPhone;   //收货人电话
    private String recvProvince;    //收货人所在省
    private String recvCity;   //收货人所在市
    private String recvArea;    //收货人所在区
    private String recvAddress;   //收货详细地址
    private Long totalPrice;    //总价
    private Integer status;    //状态：0-未支付，1-已支付，2-已取消，3-已关闭，4-已完成
    private Date orderTime;   //下单时间
    private Date payTime;    //支付时间
}
