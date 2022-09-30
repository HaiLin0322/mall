package com.hailin.mall.service;

import com.hailin.mall.model.request.CreateOrderReq;

//订单service
public interface OrderService {
    String create(CreateOrderReq createOrderReq);
}
