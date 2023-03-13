package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.ReturnFront;
import com.example.pojo.Collect;
import com.example.pojo.Product;
import com.example.pojo.User;
import com.example.service.CollectService;
import com.example.service.ProductService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/collect")
public class CollectController extends BaseController {
    @Autowired
    private CollectService collectService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;

    //添加商品到收藏
    @GetMapping("/insertCollect/{pid}")
    public ReturnFront insert(@PathVariable Long pid, HttpServletRequest request) {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("id", pid);
        Product product = productService.getOne(wrapper);
        if (product == null) {
            return ReturnFront.error("商品信息有误，请稍后再试");
        }

        //获取session中uid并查找对应数据
        HttpSession session = request.getSession();
        Long uid = (Long) session.getAttribute("uid");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        User user = userService.getOne(queryWrapper);
        if (user == null) {
            return ReturnFront.error("用户不存在");
        }

        Collect collect = new Collect();
        collect.setUid(user.getUid());
        collect.setPid(pid);
        collect.setTitle(product.getTitle());
        collect.setPrice(product.getPrice());
        collect.setImage(product.getImage());
        collect.setCreatedUser(user.getUsername());
        collect.setCreatedTime(new Date());
        boolean flag = collectService.save(collect);
        if (!flag) {
            return ReturnFront.error("收藏失败，请稍后再试");
        }
        return ReturnFront.success("收藏成功");
    }

    //删除
    @GetMapping("/delete/{id}")
    public ReturnFront delete(@PathVariable Long id) {
        QueryWrapper<Collect> wrapper = new QueryWrapper<>();
        boolean flag = collectService.removeById(id);
        if (!flag) {
            return ReturnFront.error("删除失败,请刷新");
        }

        return ReturnFront.success("删除成功");
    }

    //查询
    @GetMapping("/getCollect/{pid}")
    public ReturnFront getCollect(@PathVariable Long pid, HttpSession session) {
        QueryWrapper<Collect> wrapper = new QueryWrapper<>();
        wrapper.eq("pid", pid);
        Long uid = (Long) session.getAttribute("uid");
        wrapper.eq("uid", uid);
        Collect collect = collectService.getOne(wrapper);
        if (collect == null) {
            return ReturnFront.error("查询失败");
        }

        return ReturnFront.success(collect);
    }


    //获取收藏列表
    @PostMapping("/getPageList/{currentPage}/{pageSize}")
    public ReturnFront getPageList(@PathVariable Integer currentPage, @PathVariable Integer pageSize, HttpSession session) {
        //获取id
        Long uid = (Long) session.getAttribute("uid");
        QueryWrapper<Collect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        //分页操作
        IPage<Collect> iPage = new Page<>(currentPage, pageSize);
        QueryWrapper<Collect> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid);
        wrapper.orderBy(true, false, "created_time", "title");
        IPage<Collect> page = collectService.page(iPage, wrapper);
        if (page == null) {
            return ReturnFront.error("数据有误!");
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
        return new ReturnFront().add(page, resultList);
    }
}
