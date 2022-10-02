package com.hailin.mall.service;

import com.github.pagehelper.PageInfo;
import com.hailin.mall.model.request.CreateOrderReq;
import com.hailin.mall.model.vo.OrderVO;

//订单service
public interface OrderService {
    String create(CreateOrderReq createOrderReq);

    OrderVO detail(String orderNo);

    PageInfo listForCustomer(Integer pageNo, Integer pageSize);

    void cancel(String orderNo);

    String qrcode(String orderNo);

    void pay(String orderNo);

    PageInfo listForAdmin(Integer pageNo, Integer pageSize);

    void delivered(String orderNo);

    void finish(String orderNo);
}
