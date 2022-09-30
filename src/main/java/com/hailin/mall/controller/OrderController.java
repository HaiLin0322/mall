package com.hailin.mall.controller;

import com.hailin.mall.common.ApiRestResponse;
import com.hailin.mall.model.request.CreateOrderReq;
import com.hailin.mall.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

//订单控制器
@RestController
public class OrderController {
    @Autowired
    OrderService orderService;

    @ApiOperation("创建订单")
    @PostMapping("/order/create")
    public ApiRestResponse create(@RequestBody CreateOrderReq createOrderReq){
        String orderNo=orderService.create(createOrderReq);
        return ApiRestResponse.success(orderNo);
    }


}
