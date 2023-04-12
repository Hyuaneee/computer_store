package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.ProductCategoryMapper;
import com.example.pojo.ProductCategory;
import com.example.service.ProductCategoryService;
import org.springframework.stereotype.Service;

//ProductCategoryServiceImpl 应用层实现类
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

}

