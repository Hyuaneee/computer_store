package com.example.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.pojo.Cart;
import com.example.pojo.CartVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    //查询购物车数据，使用CartVO返回额外数据
    @Select("SELECT cid,uid,pid,cart.price,cart.num,product.num AS numCount,product.title,product.price*cart.num AS realPrice,product.image FROM cart LEFT JOIN product ON cart.pid=product.id where cart.uid= #{uid} ORDER BY cart.created_time DESC")
    List<CartVO> PageCartVO(Long uid);

    //动态sql,查询购物车数据，获取购物车结果集
    @Select("<script>" +
            "SELECT cid,uid,pid,cart.price,cart.num,product.num AS numCount,product.title,product.price*cart.num AS realPrice,product.image \n" +
            "FROM cart LEFT JOIN product ON cart.pid=product.id" +
            "<where>" +
            "cart.cid in" +
            "<foreach item='cid' collection='list' open='(' separator=',' close=')'>" +
            "#{cid}" +
            "</foreach>" +
            "</where>" +
            "ORDER BY cart.created_time DESC;" +
            "</script>")
    List<CartVO> getListCids(List<Long> cids);

}
