package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.common.ReturnFront;
import com.example.pojo.Dict_district;
import com.example.service.Dict_districtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dict_district")
public class Dict_districtController {
    @Autowired
    private Dict_districtService service;

    //获取所有的省/直辖市
    @GetMapping("/getAllProvince")
    public ReturnFront getAllProvince() {
        QueryWrapper<Dict_district> wrapper = new QueryWrapper<>();
        wrapper.eq("parent", "86");   //省和直辖市parent都为86
        wrapper.orderBy(true, true, "id", "parent");

        List<Dict_district> list = service.list(wrapper);
        if (list == null) {
            throw new RuntimeException("数据有误");
        }
        return ReturnFront.success(list);
    }

    //获取符合条件的城市
    @GetMapping("/getListCity/{parent}")
    public ReturnFront getListCity(@PathVariable String parent) {
        System.out.println("获取到的城市parent" + parent);
        QueryWrapper<Dict_district> wrapper = new QueryWrapper<>();
        wrapper.eq("parent", parent);
        wrapper.orderBy(true, true, "id", "parent");

        List<Dict_district> list = service.list(wrapper);
        if (list == null) {
            throw new RuntimeException("数据有误");
        }
        return ReturnFront.success(list);
    }

    //获取符合条件的区/县
    @GetMapping("/getListArea/{parent}")
    public ReturnFront getListArea(@PathVariable String parent) {
        System.out.println("获取到的区/县parent" + parent);
        QueryWrapper<Dict_district> wrapper = new QueryWrapper<>();
        wrapper.eq("parent", parent);
        wrapper.orderBy(true, true, "id", "parent");

        List<Dict_district> list = service.list(wrapper);
        if (list == null) {
            throw new RuntimeException("数据有误");
        }
        return ReturnFront.success(list);
    }
}
