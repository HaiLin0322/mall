package com.hailin.mall.service;

import com.hailin.mall.exception.HailinMallException;
import com.hailin.mall.model.pojo.User;
import com.hailin.mall.model.request.AddProductReq;
//商品service

public interface ProductService {
    void add(AddProductReq addProductReq);
}
