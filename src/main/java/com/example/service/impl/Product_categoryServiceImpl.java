package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.Product_categoryMapper;
import com.example.pojo.Product_category;
import com.example.service.Product_categoryService;
import org.springframework.stereotype.Service;

@Service
public class Product_categoryServiceImpl extends ServiceImpl<Product_categoryMapper,Product_category> implements Product_categoryService {
}
