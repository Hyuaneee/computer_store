package com.example.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.Result;
import com.example.pojo.ProductCategory;
import com.example.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//ProductCategoryController 控制层
@RestController
@RequestMapping("/productCategory")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    @PostMapping("/addCode")
    public Result addCode(ProductCategory productCategory) {
        productCategory.setStatus(0);
        boolean flag = productCategoryService.save(productCategory);
        if (!flag) {
            return Result.error("添加失败");
        }
        return Result.success("添加成功");
    }

    @GetMapping("/getAllCode")
    public Result getAllCode() {
        LambdaQueryWrapper<ProductCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ProductCategory::getId);
        List<ProductCategory> list = productCategoryService.list(wrapper);
        if (list == null) {
            throw new RuntimeException("数据有误");
        }
        return Result.success(list);
    }

    @GetMapping("/getNameById/{id}")
    public Result getNameById(@PathVariable Integer id) {
        ProductCategory byId = productCategoryService.getById(id);
        if (byId == null) {
            return Result.error("商品种类不存在");
        }
        String name = byId.getName();
        return Result.success(name);
    }
}

