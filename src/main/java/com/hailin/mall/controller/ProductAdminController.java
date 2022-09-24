package com.hailin.mall.controller;

import com.hailin.mall.common.ApiRestResponse;
import com.hailin.mall.model.request.AddProductReq;
import com.hailin.mall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

//后台商品管理
@Controller
public class ProductAdminController {
    @Autowired
    ProductService productService;
    @PostMapping("admin/product/add")
    public ApiRestResponse addProduct(@Valid @RequestBody AddProductReq addProductReq){
        productService.add(addProductReq);
        return ApiRestResponse.success();
    }
}
