package com.hailin.mall.service.impl;

import com.hailin.mall.common.Constant;
import com.hailin.mall.exception.HailinMallException;
import com.hailin.mall.exception.HailinMallExceptionEnum;
import com.hailin.mall.model.dao.CartMapper;
import com.hailin.mall.model.dao.ProductMapper;
import com.hailin.mall.model.pojo.Cart;
import com.hailin.mall.model.pojo.Product;
import com.hailin.mall.model.vo.CartVO;
import com.hailin.mall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//购物车service
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;
    @Override
    public List<CartVO> add(Integer userId, Integer productId, Integer count){
        validProduct(productId,count);
        Cart cart=cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //这个商品之前不在购物车内，需要新增记录
            cart=new Cart();
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setQuantity(count);
            cart.setSelected(Constant.Cart.CHECKED);
            cartMapper.insert(cart);
        }else {
            //这个商品之前已加入购物车，数量增加
            count=cart.getQuantity()+count;
            Cart cartNew=new Cart();
            cartNew.setQuantity(count);
            cartNew.setId(cart.getId());
            cartNew.setUserId(cart.getUserId());
            cartNew.setProductId(cart.getProductId());
            cartNew.setSelected(Constant.Cart.CHECKED);
            cartMapper.updateByPrimaryKeySelective(cartNew);
        }
        return this.list(userId);
    }

    private void validProduct(Integer productId, Integer count) {
        Product product=productMapper.selectByPrimaryKey(productId);
        //判断商品是否存在，商品是否上架
        if (product == null||product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
            throw new HailinMallException(HailinMallExceptionEnum.NOT_SALE);
        }
        //判断商品库存
        if(count>product.getStock()){
            throw new HailinMallException(HailinMallExceptionEnum.NOT_ENOUGH);
        }
    }
    @Override
    public List<CartVO> list(Integer userId){
        List<CartVO> cartVOS = cartMapper.selectList(userId);
        for (int i = 0; i < cartVOS.size(); i++) {
            CartVO cartVO =  cartVOS.get(i);
            cartVO.setTotalPrice(cartVO.getPrice()*cartVO.getQuantity());
        }
        return cartVOS;
    }
    @Override
    public List<CartVO> update(Integer userId, Integer productId, Integer count){
        validProduct(productId,count);
        Cart cart=cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //这个商品之前不在购物车内，无法更新
            throw new HailinMallException(HailinMallExceptionEnum.UPDATE_FAILED);
        }else {
            //这个商品之前已加入购物车，更新数量
            Cart cartNew=new Cart();
            cartNew.setQuantity(count);
            cartNew.setId(cart.getId());
            cartNew.setUserId(cart.getUserId());
            cartNew.setProductId(cart.getProductId());
            cartNew.setSelected(Constant.Cart.CHECKED);
            cartMapper.updateByPrimaryKeySelective(cartNew);
        }
        return this.list(userId);
    }
    @Override
    public List<CartVO> delete(Integer userId, Integer productId){
        Cart cart=cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //这个商品之前不在购物车内，无法删除
            throw new HailinMallException(HailinMallExceptionEnum.DELETE_FAILED);
        }else {
            //这个商品之前已加入购物车，可以删除
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
        return this.list(userId);
    }
    @Override
    public List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected){
        Cart cart=cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //这个商品之前不在购物车内，无法选中/取消
            throw new HailinMallException(HailinMallExceptionEnum.UPDATE_FAILED);
        }else {
            cartMapper.selectOrNot(userId, productId, selected);
        }
        return this.list(userId);
    }
    @Override
    public List<CartVO> selectAllOrNot(Integer userId, Integer selected){
        //改变选中状态
        cartMapper.selectOrNot(userId, null, selected);
        return this.list(userId);
    }
}
