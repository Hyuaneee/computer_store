package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.pojo.Collect;
import com.example.pojo.Product;
import com.example.pojo.User;
import com.example.service.CollectService;
import com.example.service.ProductService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/collect")
public class CollectController {
    @Autowired
    private CollectService collectService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;

    //添加商品到收藏
    @GetMapping("/insertCollect/{pid}")
    public Result insert(@PathVariable Long pid, HttpServletRequest request) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getId, pid);
        Product product = productService.getOne(wrapper);
        if (product == null) {
            return Result.error("商品信息有误，请稍后再试");
        }

        //获取session中uid并查找对应数据
        HttpSession session = request.getSession();
        Long uid = (Long) session.getAttribute("uid");
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUid, uid);
        User user = userService.getOne(queryWrapper);
        if (user == null) {
            return Result.error("用户不存在");
        }

        Collect collect = new Collect();
        collect.setUid(user.getUid());
        collect.setPid(pid);
        collect.setTitle(product.getTitle());
        collect.setPrice(product.getPrice());
        collect.setImage(product.getImage());
        boolean flag = collectService.save(collect);
        if (!flag) {
            return Result.error("收藏失败，请稍后再试");
        }
        return Result.success("收藏成功");
    }

    //删除
    @GetMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        boolean flag = collectService.removeById(id);
        if (!flag) {
            return Result.error("删除失败,请刷新");
        }

        return Result.success("删除成功");
    }

    //查询
    @GetMapping("/getCollect/{pid}")
    public Result getCollect(@PathVariable Long pid, HttpSession session) {
        LambdaQueryWrapper<Collect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Collect::getPid, pid);
        Long uid = (Long) session.getAttribute("uid");
        wrapper.eq(Collect::getUid, uid);
        Collect collect = collectService.getOne(wrapper);
        if (collect == null) {
            return Result.error("查询失败");
        }

        return Result.success(collect);
    }

    //获取收藏列表
    @PostMapping("/getPageList/{currentPage}/{pageSize}")
    public Result getPageList(@PathVariable Integer currentPage, @PathVariable Integer pageSize, HttpSession session) {
        Long uid = (Long) session.getAttribute("uid");
        IPage<Collect> iPage = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Collect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Collect::getUid, uid);
        wrapper.orderBy(true, false, Collect::getTitle);
        IPage<Collect> page = collectService.page(iPage, wrapper);
        if (page == null) {
            return Result.error("数据有误,请刷新");
        }
        List<List<Collect>> resultList = new ArrayList<>();
        for (int index = 0; index < page.getRecords().size(); index = index + 4) {
            List<Collect> temp = new ArrayList<>();
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
}
