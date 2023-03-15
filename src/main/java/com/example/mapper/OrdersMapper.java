package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.pojo.Orders;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

    //新增订单数据
    @Insert("insert into orders values(#{oid},#{uid},#{recvName},#{recvPhone},#{recvProvince},#{recvCity},#{recvArea},#{recvAddress},#{totalPrice},#{status},#{orderTime},#{payTime},#{createdUser},#{createdTime},#{modifiedUser},#{modifiedTime})")
    //使用数据库中自增长的主键，并且可以将主键的值返回给keyProperty中写好的字段
    @Options(useGeneratedKeys = true, keyProperty = "oid", keyColumn = "oid")
    int insert(Orders orders);

}
