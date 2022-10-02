package com.hailin.mall.controller;

import com.github.pagehelper.PageInfo;
import com.hailin.mall.common.ApiRestResponse;
import com.hailin.mall.model.request.CreateOrderReq;
import com.hailin.mall.model.vo.OrderVO;
import com.hailin.mall.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation("前台订单详情")
    @GetMapping("/order/detail")
    public ApiRestResponse detail(@RequestParam String orderNo){
        OrderVO orderVO =orderService.detail(orderNo);
        return ApiRestResponse.success(orderVO);
    }
    @ApiOperation("前台订单列表")
    @GetMapping("/order/list")
    public ApiRestResponse list(@RequestParam Integer pageNo,@RequestParam Integer pageSize){
        PageInfo pageInfo =orderService.listForCustomer(pageNo, pageSize);
        return ApiRestResponse.success(pageInfo);
    }
    @ApiOperation("前台取消订单")
    @PostMapping ("/order/cancel")
    public ApiRestResponse cancel(@RequestParam String orderNo){
        orderService.cancel(orderNo);
        return ApiRestResponse.success();
    }
    @ApiOperation("生成支付二维码")
    @PostMapping ("/order/qrcode")
    public ApiRestResponse qrcode(@RequestParam String orderNo){
        String pngAddress=orderService.qrcode(orderNo);
        return ApiRestResponse.success(pngAddress);
    }
    @ApiOperation("支付接口")
    @GetMapping ("/pay")
    public ApiRestResponse pay(@RequestParam String orderNo){
        orderService.pay(orderNo);
        return ApiRestResponse.success();
    }
}
