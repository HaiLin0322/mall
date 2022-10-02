package com.hailin.mall.controller;

import com.github.pagehelper.PageInfo;
import com.hailin.mall.common.ApiRestResponse;
import com.hailin.mall.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//订单后台管理Controller
@RestController
public class OrderAdminController {
    @Autowired
    OrderService orderService;
    @ApiOperation("管理员订单列表")
    @GetMapping("/admin/order/list")
    public ApiRestResponse listForAdmin(@RequestParam Integer pageNo,@RequestParam Integer pageSize){
        PageInfo pageInfo=orderService.listForAdmin(pageNo, pageSize);
        return ApiRestResponse.success(pageInfo);
    }
    //发货。订单状态流程：0，已取消；10，未付款；20，已付款；30，已发货；40，交易完成
    @ApiOperation("管理员发货")
    @PostMapping("/admin/order/delivered")
    public ApiRestResponse delivered(@RequestParam String orderNo){
        orderService.delivered(orderNo);
        return ApiRestResponse.success();
    }
    //管理员和用户都能调用。订单状态流程：0，已取消；10，未付款；20，已付款；30，已发货；40，交易完成
    @ApiOperation("完结订单")
    @PostMapping("/order/finish")
    public ApiRestResponse finish(@RequestParam String orderNo){
        orderService.finish(orderNo);
        return ApiRestResponse.success();
    }
}
