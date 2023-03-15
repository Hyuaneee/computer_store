package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.ReturnFront;
import com.example.pojo.Product;
import com.example.pojo.Product_category;
import com.example.service.ProductService;
import com.example.service.Product_categoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private Product_categoryService product_categoryService;

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

    //获取分类
    @GetMapping("/getListProductType")
    public ReturnFront getListProductType() {
        QueryWrapper<Product_category> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", 0);
        List<Product_category> list = product_categoryService.list(wrapper);
        if (list == null) {
            throw new RuntimeException("哔哔");
        }
        return ReturnFront.success(list);
    }

    /* 和收藏collect一样没有完全搞懂 */
    //获取搜索详情页信息
    @PostMapping("/getPageList/{currentPage}/{pageSize}")
    public ReturnFront getPageList(@PathVariable Integer currentPage, @PathVariable Integer pageSize, String context) {
        System.out.println("获取到的参数" + context);
        IPage<Product> iPage = new Page<>(currentPage, pageSize);
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        //if (context == null || "".equals(context)) {
        wrapper.like("title", context);
        wrapper.or();
        wrapper.like("item_type", context);
        //}
        wrapper.orderByDesc("modified_time", "title");
        IPage<Product> page = productService.page(iPage, wrapper);
        if (page == null) {
            return ReturnFront.error("数据有误,请刷新");
        }
        List<List<Product>> resultList = new ArrayList<>();


        for (int index = 0; index < page.getRecords().size(); index = index + 4) {
            List<Product> temp = new ArrayList<>();
            for (int i = index; i < page.getRecords().size(); i++) {
                if (page.getRecords().get(i) != null && i < index + 4) {
                    temp.add(page.getRecords().get(i));
                }
            }
            resultList.add(temp);
        }
        page.setRecords(null);
        return new ReturnFront(page, resultList);
    }

}
