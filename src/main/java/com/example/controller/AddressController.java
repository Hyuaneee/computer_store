package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
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
    public Result selectId(@PathVariable Long aid) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getAid, aid);
        Address address = addressService.getOne(wrapper);
        if (address == null) {
            return Result.error("地址信息不存在，请刷新");
        }
        return Result.success(address);
    }

    //分页查询
    @PostMapping("/PageList/{currentPage}/{pageSize}")
    public Result insert(@PathVariable Integer currentPage, @PathVariable Integer pageSize, HttpSession session) {
        Long uid = (Long) session.getAttribute("uid");
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUid, uid);
        IPage<Address> iPage = new Page<Address>(currentPage, pageSize);
        IPage<Address> page = addressService.page(iPage, wrapper);
        return Result.success(page);
    }

    //添加数据
    @PostMapping("/addRow")
    public Result addRow(@RequestBody Address address, HttpSession session) {
        Long uid = (Long) session.getAttribute("uid");
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUid, uid);
        int count = addressService.count(wrapper);
        if (count > 20) {
            return Result.error("一个用户最多只能有20个收货地址");
        }
        address.setUid(uid);
        address.setIsDefault(0);
        boolean flag = addressService.save(address);
        if (!flag) {
            return Result.error("添加失败");
        }
        return Result.success("添加成功");
    }

    //修改数据
    @PutMapping("/updateRow")
    public Result updateRow(@RequestBody Address address) {
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("aid", address.getAid());
        //不添加数据，数据库中自增
        address.setAid(null);
        boolean flag = addressService.update(address, wrapper);
        if (!flag) {
            return Result.error("修改失败");
        }
        return Result.success("修改成功");
    }

    //删除数据(基于aid)(物理删除)
    @DeleteMapping("/deleteRow/{aid}")
    public Result deleteRow(@PathVariable Long aid) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getAid, aid);
        boolean flag = addressService.remove(wrapper);
        if (!flag) {
            return Result.error("删除失败");
        }
        return Result.success("删除成功");
    }

    //多行删除
    @PostMapping("/delete")
    public Result delete(@RequestBody List<Address> list, HttpSession session) {
        for (Address address : list) {
            LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Address::getAid, address.getAid());
            boolean flag = addressService.remove(wrapper);
            if (!flag) {
                return Result.error("删除失败");
            }
        }
        return Result.success("删除成功");
    }


    //设为默认
    @GetMapping("/setDefault/{aid}")
    public Result setDefault(@PathVariable Long aid, HttpSession session) {
        //获取所有属于uid的发货信息
        Long uid = (Long) session.getAttribute("uid");
        LambdaQueryWrapper<Address> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(Address::getUid, uid);
        List<Address> list = addressService.list(wrapper1);
        //设置所有属于uid的发货信息为is_default：0
        for (Address address : list) {
            LambdaUpdateWrapper<Address> wrapperL = new LambdaUpdateWrapper<>();
            wrapperL.eq(Address::getAid, address.getAid());
            wrapperL.set(Address::getIsDefault, 0);
            boolean flag = addressService.update(wrapperL);
            if (!flag) {
                return Result.error("设置失败,请重试");
            }
        }
        LambdaUpdateWrapper<Address> wrapper2 = new LambdaUpdateWrapper<>();
        wrapper2.eq(Address::getAid, aid);
        wrapper2.set(Address::getIsDefault, 1);
        boolean flag = addressService.update(wrapper2);
        if (!flag) {
            return Result.error("设置失败,请重试");
        }
        return Result.success("设置成功");
    }

    //获取地址集合
    @GetMapping("/getAddressList")
    public Result getAddressList() {
        List<Address> list = addressService.list();
        if (list == null) {
            return Result.error("获取地址信息有误");
        }
        if (list.size() == 0) {
            return Result.error("您还未添加地址信息");
        }
        return Result.success(list);
    }
}
