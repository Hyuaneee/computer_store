package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.ReturnFront;
import com.example.mapper.ProductMapper;
import com.example.pojo.Product;
import com.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {

    @Autowired
    private ProductService productService;

    //热销排行
    @PostMapping("/getBestList/{currentPage}/{pageSize}")
    public ReturnFront getBestList(@PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        IPage<Product> IPage = new Page<>(currentPage, pageSize);
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        queryWrapper.orderBy(true, true, "priority", "title");
        IPage<Product> page = productService.page(IPage, queryWrapper);
        if (page == null) {
            return ReturnFront.error("数据不存在");
        }
        return ReturnFront.success(page);
    }


    //推荐栏目
    @PostMapping("/getRecommendList/{currentPage}/{pageSize}")
    public ReturnFront getRecommendList(@PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        IPage<Product> iPage = new Page<>(currentPage, pageSize);
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.orderBy(true, true, "modified_time", "title");  //优先级和标题
        IPage<Product> page = productService.page(iPage, wrapper);
        if (page == null) {
            return ReturnFront.error("数据不存在");
        }
        return ReturnFront.success(page);
    }

    //详情页数据获取
    @GetMapping("/details")
    public ReturnFront details(Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            return ReturnFront.error("商品不存在");
        }
        return ReturnFront.success(product);
    }


}
