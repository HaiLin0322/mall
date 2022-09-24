package com.hailin.mall.service.impl;

import com.hailin.mall.exception.HailinMallException;
import com.hailin.mall.exception.HailinMallExceptionEnum;
import com.hailin.mall.model.dao.ProductMapper;
import com.hailin.mall.model.pojo.Product;
import com.hailin.mall.model.request.AddProductReq;
import com.hailin.mall.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductMapper productMapper;
    @Override
    public void add(AddProductReq addProductReq){
        Product product=new Product();
        BeanUtils.copyProperties(addProductReq,product);
        Product productOld = productMapper.selectByName(addProductReq.getName());
        if (productOld!=null){
            throw new HailinMallException(HailinMallExceptionEnum.NAME_EXISTED);
        }
        int count=productMapper.insertSelective(product);
        if (count == 0) {
            throw new HailinMallException(HailinMallExceptionEnum.CREATE_FAILED);
        }
    }
}
