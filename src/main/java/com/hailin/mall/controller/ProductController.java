package com.hailin.mall.controller;

import com.github.pagehelper.PageInfo;
import com.hailin.mall.common.ApiRestResponse;
import com.hailin.mall.model.pojo.Product;
import com.hailin.mall.model.request.ProductListReq;
import com.hailin.mall.service.ProductService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//前台商品Controller
@RestController
public class ProductController {
    @Autowired
    ProductService productService;
    @ApiOperation("商品详情")
    @GetMapping("/product/detail")
    public ApiRestResponse detail(@RequestParam Integer id){
        Product product=productService.detail(id);
        return ApiRestResponse.success(product);
    }

    @ApiOperation("商品列表")
    @GetMapping("/product/list")
    public ApiRestResponse list(ProductListReq productListReq){
        PageInfo pageInfo =productService.list(productListReq);
        return ApiRestResponse.success(pageInfo);
    }
}
