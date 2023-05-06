package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.pojo.CartVO;
import com.example.pojo.Product;
import com.example.pojo.ProductCategory;
import com.example.service.ProductCategoryService;
import com.example.service.ProductService;
import com.example.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductCategoryService productCategoryService;

    //热销排行
    @PostMapping("/getBestList/{currentPage}/{pageSize}")
    public Result getBestList(@PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        IPage<Product> IPage = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 0);
        IPage<Product> page = productService.page(IPage, wrapper);
        List<Product> records = page.getRecords();
        for (Product product : records) {
            String name = productCategoryService.getById(product.getCategoryId()).getName();
            product.setCategoryId(name);
        }
        if (page == null) {
            return Result.error("数据不存在");
        }
        return Result.success(page);
    }

    //后台商品查询
    @PostMapping("/getList/{currentPage}/{pageSize}")
    public Result getList(@PathVariable Integer currentPage, @PathVariable Integer pageSize, String context, String typeData) {
        IPage<Product> IPage = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (!context.equals("")) {
            wrapper.and(queryWrapper -> queryWrapper.like(Product::getTitle, context).or().like(Product::getItemType, context));
        }
        if (!typeData.equals("")) {
            wrapper.eq(Product::getCategoryId, typeData);
        }
        List<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(1);
        wrapper.in(Product::getStatus, list);
        wrapper.orderByAsc(Product::getId);
        IPage<Product> page = productService.page(IPage, wrapper);
        List<Product> records = page.getRecords();
        for (Product product : records) {
            String categoryId = product.getCategoryId();
            if (categoryId == null || categoryId.equals(" ")) {
                continue;
            }
            ProductCategory one = productCategoryService.getById(categoryId);
            product.setCategoryId(one.getName());

        }
        if (page == null) {
            return Result.error("数据不存在");
        }
        return Result.success(page);
    }

    //详情页数据获取
    @GetMapping("/details")
    public Result details(Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }

    //详情页数据获取
    @GetMapping("/buyNow")
    public Result buyNow(Long id, Long num, HttpSession session) {
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        List<CartVO> list = new ArrayList<>();
        CartVO cartVO = new CartVO();
        cartVO.setCid(1L);
        cartVO.setUid((Long) session.getAttribute("uid"));
        cartVO.setPid(id);
        cartVO.setPrice(product.getPrice());
        cartVO.setNum(num);
        cartVO.setNumCount(product.getNum());
        cartVO.setTitle(product.getTitle());
        cartVO.setRealPrice(num * product.getPrice());
        cartVO.setImage(product.getImage());
        list.add(cartVO);
        return Result.success(list);
    }

    //获取搜索详情页信息
    @PostMapping("/getPageList/{currentPage}/{pageSize}")
    public Result getPageList(@PathVariable Integer currentPage,
                              @PathVariable Integer pageSize, String context, String typeData) {
        if (typeData == null) {
            typeData = "";
        }
        if (context == null) {
            context = "";
        }
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (!context.equals("")) {
            wrapper.like(Product::getTitle, context).or().like(Product::getItemType, context);
        }
        if (!typeData.equals("")) {
            wrapper.eq(Product::getCategoryId, typeData);
        }
        wrapper.orderByAsc(Product::getId);
        IPage<Product> iPage = new Page<>(currentPage, pageSize);
        IPage<Product> page = productService.page(iPage, wrapper);
        if (page == null) {
            return Result.error("数据有误,请刷新");
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
        return new Result(page, resultList);
    }

    //商品上下架设置
    @GetMapping("/setStatus/{id}/{status}")
    public Result setDefault(@PathVariable Long id, @PathVariable Integer status) {
        status = status == 0 ? 1 : 0;
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Product::getId, id);
        wrapper.set(Product::getStatus, status);
        boolean flag = productService.update(wrapper);
        if (!flag) {
            return Result.error("设置失败,请重试");
        }
        return Result.success("设置成功");
    }

    //商品逻辑删除
    @GetMapping("/setDeleted/{id}")
    public Result setDeleted(@PathVariable Long id) {
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Product::getId, id);
        wrapper.set(Product::getStatus, 2);
        boolean flag = productService.update(wrapper);
        if (!flag) {
            return Result.error("删除失败,请重试");
        }
        return Result.success("删除成功");
    }

    //添加数据
    @PostMapping("/addRow")
    public Result addRow(@RequestBody Product product) {
        product.setStatus(0);
        boolean flag = productService.save(product);
        if (!flag) {
            return Result.error("添加失败");
        }
        return Result.success("添加成功");
    }

    //查询数据
    @GetMapping("/selectId/{id}")
    public Result selectId(@PathVariable Long id) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getId, id);
        Product product = productService.getOne(wrapper);
        if (product == null) {
            return Result.error("商品信息不存在，请刷新");
        }
        return Result.success(product);
    }

    //修改数据
    @PutMapping("/updateRow")
    public Result updateRow(@RequestBody Product product) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getId, product.getId());
        //不添加数据，数据库中自增
        product.setId(null);
        boolean flag = productService.update(product, wrapper);
        if (!flag) {
            return Result.error("修改失败");
        }
        return Result.success("修改成功");
    }
}
