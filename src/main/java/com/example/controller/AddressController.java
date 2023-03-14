package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.ReturnFront;
import com.example.pojo.Address;
import com.example.service.AddressService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    //aid查询
    @GetMapping("/selectId/{aid}")
    public ReturnFront selectId(@PathVariable Long aid) {
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("aid", aid);
        Address address = addressService.getOne(wrapper);
        if (address == null) {
            return ReturnFront.error("地址信息不存在，请刷新");
        }
        return ReturnFront.success(address);
    }

    //分页查询
    @PostMapping("/PageList/{currentPage}/{pageSize}")
    public ReturnFront insert(@PathVariable Integer currentPage, @PathVariable Integer pageSize, HttpSession session) {
        Long uid = (Long) session.getAttribute("uid");
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid);
        IPage<Address> iPage = new Page<Address>(currentPage, pageSize);
        IPage<Address> page = addressService.page(iPage, wrapper);
        return ReturnFront.success(page);
    }

    //添加数据
    @PostMapping("/addRow")
    public ReturnFront addRow(@RequestBody Address address, HttpSession session) {
        Long uid = (Long) session.getAttribute("uid");
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid);
        int count = addressService.count(wrapper);
        if (count > 20) {
            return ReturnFront.error("一个用户最多只能有20个收货地址");
        }

        address.setUid(uid);
        address.setIs_default(0);
        boolean flag = addressService.save(address);
        if (!flag) {
            return ReturnFront.error("添加失败");
        }
        return ReturnFront.success("添加成功");
    }

    //修改数据
    @PutMapping("/updateRow")
    public ReturnFront updateRow(@RequestBody Address address) {
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("aid", address.getAid());
        //不添加数据，数据库中自增
        address.setAid(null);
        boolean flag = addressService.update(address, wrapper);
        if (!flag) {
            return ReturnFront.error("修改失败");
        }
        return ReturnFront.success("修改成功");
    }

    //删除数据(基于aid)(物理删除)
    @DeleteMapping("/deleteRow/{aid}")
    public ReturnFront deleteRow(@PathVariable Long aid) {
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("aid", aid);
        boolean flag = addressService.remove(wrapper);
        if (!flag) {
            return ReturnFront.error("删除失败");
        }
        return ReturnFront.success("删除成功");
    }

    //多行删除
    @PostMapping("/delete")
    public ReturnFront delete(@RequestBody List<Address> list, HttpSession session) {
        for (Address address : list) {
            QueryWrapper<Address> wrapper = new QueryWrapper<>();
            wrapper.eq("aid", address.getAid());
            boolean flag = addressService.remove(wrapper);
            if (!flag) {
                return ReturnFront.error("删除失败");
            }
        }
        return ReturnFront.success("删除成功");
    }


    //设为默认
    @GetMapping("/setDefault/{aid}")
    public ReturnFront setDefault(@PathVariable Long aid, HttpSession session) {
        //获取所有属于uid的发货信息
        Long uid = (Long) session.getAttribute("uid");
        QueryWrapper<Address> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("uid", uid);
        List<Address> list = addressService.list(wrapper1);
        //设置所有属于uid的发货信息为is_default：0
        for (Address address : list) {
            UpdateWrapper<Address> wrapperL = new UpdateWrapper<>();
            wrapperL.eq("aid", address.getAid());
            wrapperL.set("is_default", 0);
            boolean flag = addressService.update(wrapperL);
            if (!flag) {
                return ReturnFront.error("设置失败,请重试");
            }
        }
        UpdateWrapper<Address> wrapper2 = new UpdateWrapper<>();
        wrapper2.eq("aid", aid);
        wrapper2.set("is_default", 1);
        boolean flag = addressService.update(wrapper2);
        if (!flag) {
            return ReturnFront.error("设置失败,请重试");
        }
        return ReturnFront.success("设置成功");
    }

    /*//获取地址集合
    @GetMapping("/getAddressList")
    public ReturnFront getAddressList() {
        List<Address> list = addressService.list();
        if (list == null) {
            return ReturnFront.error("获取地址信息有误");
        }
        if (list.size() == 0) {
            return ReturnFront.error("您还未添加地址信息");
        }
        return ReturnFront.success(list);
    }*/
}
