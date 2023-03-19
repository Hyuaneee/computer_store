package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.ReturnFront;
import com.example.pojo.Product;
import com.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    //热销排行
    @PostMapping("/getBestList/{currentPage}/{pageSize}")
    public ReturnFront getBestList(@PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        IPage<Product> IPage = new Page<>(currentPage, pageSize);
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 0);
        queryWrapper.orderBy(true, true, "title");
        IPage<Product> page = productService.page(IPage, queryWrapper);
        if (page == null) {
            return ReturnFront.error("数据不存在");
        }
        return ReturnFront.success(page);
    }

    //后台商品查询
    @PostMapping("/getList/{currentPage}/{pageSize}")
    public ReturnFront getList(@PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        IPage<Product> IPage = new Page<>(currentPage, pageSize);
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 0);
        queryWrapper.or();
        queryWrapper.eq("status", 1);
        queryWrapper.orderBy(true, true, "title");
        IPage<Product> page = productService.page(IPage, queryWrapper);
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

    //获取搜索详情页信息
    @PostMapping("/getPageList/{currentPage}/{pageSize}")
    public ReturnFront getPageList(@PathVariable Integer currentPage, @PathVariable Integer pageSize, String context) {
        System.out.println("获取到的参数" + context);
        IPage<Product> iPage = new Page<>(currentPage, pageSize);
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        if (context == null || "".equals(context)) {
            wrapper.eq("status", 0);
            wrapper.like("title", context);
            wrapper.or();
            wrapper.like("item_type", context);
        }
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

    //商品状态设置
    @GetMapping("/setStatus/{id}/{status}")
    public ReturnFront setDefault(@PathVariable Long id, @PathVariable Integer status) {
        status = status == 0 ? 1 : 0;
        UpdateWrapper<Product> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id);
        wrapper.set("status", status);
        boolean flag = productService.update(wrapper);
        if (!flag) {
            return ReturnFront.error("设置失败,请重试");
        }
        return ReturnFront.success("设置成功");
    }

    //账号逻辑删除
    @GetMapping("/setDeleted/{id}")
    public ReturnFront setDeleted(@PathVariable Long id) {
        UpdateWrapper<Product> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id);
        wrapper.set("status", 2);
        boolean flag = productService.update(wrapper);
        if (!flag) {
            return ReturnFront.error("设置失败,请重试");
        }
        return ReturnFront.success("设置成功");
    }

    //添加数据
    @PostMapping("/addRow")
    public ReturnFront addRow(@RequestBody Product product, HttpSession session) {
        product.setStatus(0);
        product.setCreatedUser("admin");
        product.setModifiedUser("admin");
        product.setCreatedTime(new Date());
        product.setModifiedTime(new Date());
        boolean flag = productService.save(product);
        if (!flag) {
            return ReturnFront.error("添加失败");
        }
        return ReturnFront.success("添加成功");
    }

    //查询数据
    @GetMapping("/selectId/{id}")
    public ReturnFront selectId(@PathVariable Long id) {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        Product product = productService.getOne(wrapper);
        if (product == null) {
            return ReturnFront.error("商品信息不存在，请刷新");
        }
        return ReturnFront.success(product);
    }

    //修改数据
    @PutMapping("/updateRow")
    public ReturnFront updateRow(@RequestBody Product product) {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("id", product.getId());
        //不添加数据，数据库中自增
        product.setId(null);
        product.setModifiedTime(new Date());
        boolean flag = productService.update(product, wrapper);
        if (!flag) {
            return ReturnFront.error("修改失败");
        }
        return ReturnFront.success("修改成功");
    }
}
