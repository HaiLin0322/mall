package com.hailin.mall.controller;

import com.hailin.mall.common.ApiRestResponse;
import com.hailin.mall.filter.UserFilter;
import com.hailin.mall.model.vo.CartVO;
import com.hailin.mall.service.CartService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//购物车
@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    CartService cartService;
    @ApiOperation("添加商品到购物车")
    @PostMapping("/add")
    public ApiRestResponse add(@RequestParam Integer productId,@RequestParam Integer count){
        List<CartVO> cartVOList=cartService.add(UserFilter.currentUser.getId(),productId,count);
        return ApiRestResponse.success(cartVOList);
    }
    @ApiOperation("购物车列表")
    @GetMapping("/list")
    public ApiRestResponse list(){
        //内部获取用户Id，防止横向越权
        List<CartVO> cartList = cartService.list(UserFilter.currentUser.getId());
        return ApiRestResponse.success(cartList);
    }
    @ApiOperation("更新购物车")
    @PostMapping("/update")
    public ApiRestResponse update(@RequestParam Integer productId,@RequestParam Integer count){
        List<CartVO> cartVOList=cartService.update(UserFilter.currentUser.getId(),productId,count);
        return ApiRestResponse.success(cartVOList);
    }
    @ApiOperation("删除购物车")
    @PostMapping("/delete")
    public ApiRestResponse delete(@RequestParam Integer productId){
        //不嫩传入userId,cartId，需要自己从登录状态调用，防止可以删除别人的购物车
        List<CartVO> cartVOList=cartService.delete(UserFilter.currentUser.getId(),productId);
        return ApiRestResponse.success(cartVOList);
    }
    @ApiOperation("勾选/取消勾选购物车的某商品")
    @PostMapping("/select")
    public ApiRestResponse select(@RequestParam Integer productId,@RequestParam Integer selected){
        //不嫩传入userId,cartId，需要自己从登录状态调用，防止可以删除别人的购物车
        List<CartVO> cartVOList=cartService.selectOrNot(UserFilter.currentUser.getId(),productId,selected);
        return ApiRestResponse.success(cartVOList);
    }
    @ApiOperation("全选/取消全选购物车的某商品")
    @PostMapping("/selectAll")
    public ApiRestResponse selectAll(@RequestParam Integer selected){
        //不嫩传入userId,cartId，需要自己从登录状态调用，防止可以删除别人的购物车
        List<CartVO> cartVOList=cartService.selectAllOrNot(UserFilter.currentUser.getId(),selected);
        return ApiRestResponse.success(cartVOList);
    }
}
