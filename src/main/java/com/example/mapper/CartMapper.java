package com.example.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.pojo.Cart;
import com.example.pojo.CartVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    //查询购物车数据，使用CartVO返回额外数据
    List<CartVO> PageCartVO(Long uid);

    //动态sql,查询购物车数据，获取购物车结果集
    List<CartVO> getListCids(List<Long> cids);

}
