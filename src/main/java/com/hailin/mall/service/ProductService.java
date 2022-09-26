package com.hailin.mall.service;

import com.github.pagehelper.PageInfo;
import com.hailin.mall.exception.HailinMallException;
import com.hailin.mall.model.pojo.Product;
import com.hailin.mall.model.pojo.User;
import com.hailin.mall.model.request.AddProductReq;
import com.hailin.mall.model.request.ProductListReq;
//商品service

public interface ProductService {
    void add(AddProductReq addProductReq);

    void update(Product updateProduct);

    void delete(Integer id);

    void batchUpdateSellStatus(Integer[] ids, Integer sellStatus);

    PageInfo listForAdmin(Integer pageNum, Integer PageSize);

    Product detail(Integer id);

    PageInfo list(ProductListReq productListReq);
}
