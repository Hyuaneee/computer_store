package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.pojo.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Insert("insert into order values(#{oid},#{uid},#{recv_name},#{recv_phone},#{recv_province},#{recv_city},#{recv_area},#{recv_address}," +
            "#{total_price},#{status},#{order_time},#{pay_time},#{created_user},#{created_time}," +
            "#{modified_user},#{modified_time})")
    @Options(useGeneratedKeys = true,keyProperty = "oid",keyColumn = "oid")
    int insert(Order order);

}
