package com.hailin.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import com.hailin.mall.common.Constant;
import com.hailin.mall.exception.HailinMallException;
import com.hailin.mall.exception.HailinMallExceptionEnum;
import com.hailin.mall.filter.UserFilter;
import com.hailin.mall.model.dao.CartMapper;
import com.hailin.mall.model.dao.OrderItemMapper;
import com.hailin.mall.model.dao.OrderMapper;
import com.hailin.mall.model.dao.ProductMapper;
import com.hailin.mall.model.pojo.Order;
import com.hailin.mall.model.pojo.OrderItem;
import com.hailin.mall.model.pojo.Product;
import com.hailin.mall.model.request.CreateOrderReq;
import com.hailin.mall.model.vo.CartVO;
import com.hailin.mall.model.vo.OrderItemVO;
import com.hailin.mall.model.vo.OrderVO;
import com.hailin.mall.service.CartService;
import com.hailin.mall.service.OrderService;
import com.hailin.mall.service.UserService;
import com.hailin.mall.util.OrderCodeFactory;
import com.hailin.mall.util.QRCodeGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//订单service实现类
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    CartService cartService;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    CartMapper cartMapper;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Value("${file.upload.ip}")
    String ip;
    @Autowired
    UserService userService;
    @Transactional(rollbackFor = Exception.class)//开启事务，遇到任何异常都会回滚
    @Override
    public String create(CreateOrderReq createOrderReq){
        // 拿到用户Id
        Integer userId=UserFilter.currentUser.getId();
        // 从购物车查找已勾选商品
        List<CartVO> cartVOList = cartService.list(userId);
        ArrayList<CartVO> cartVOListTemp=new ArrayList();
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO =  cartVOList.get(i);
            if(cartVO.getSelected().equals(Constant.Cart.CHECKED)){
                cartVOListTemp.add(cartVO);
            }
        }
        cartVOList=cartVOListTemp;
        // 如果购物车已勾选为空，报错
        if (CollectionUtils.isEmpty(cartVOList)){
            throw new HailinMallException(HailinMallExceptionEnum.CART_EMPTY);
        }
        // 判断商品是否存在、上下架状态、库存
        validSaleStatusAndStock(cartVOList);
        // 把购物车对象转化为订单item对象
        List<OrderItem> orderItemList=cartVOListToOrderItemList(cartVOList);
        // 扣库存
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem =  orderItemList.get(i);
            Product product=productMapper.selectByPrimaryKey(orderItem.getProductId());
            int stock=product.getStock()-orderItem.getQuantity();
            if(stock<0){
                throw new HailinMallException(HailinMallExceptionEnum.NOT_ENOUGH);
            }
            product.setStock(stock);
            productMapper.updateByPrimaryKeySelective(product);
        }
        // 把购物车中的已勾选商品删除
        cleanCart(cartVOList);
        // 生成订单
        Order order=new Order();
        // 生成订单号，有独立的规则
        String orderNo=OrderCodeFactory.getOrderCode(Long.valueOf(userId));
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverName(createOrderReq.getReceiverName());
        order.setReceiverMobile(createOrderReq.getReceiverMobile());
        order.setReceiverAddress(createOrderReq.getReceiverAddress());
        order.setOrderStatus(Constant.OrderStatusEnum.NOT_PAY.getCode());
        order.setPostage(0);
        order.setPaymentType(1);
        //插入到order表
        orderMapper.insertSelective(order);
        // 循环保存每个商品到order_item表
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem =  orderItemList.get(i);
            orderItem.setOrderNo(order.getOrderNo());
            orderItemMapper.insertSelective(orderItem);
        }
        // 返回结果
        return orderNo;
    }

    private Integer totalPrice(List<OrderItem> orderItemList) {
        Integer totalPrice=0;
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            totalPrice+=orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    private void cleanCart(List<CartVO> cartVOList) {
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO =  cartVOList.get(i);
            cartMapper.deleteByPrimaryKey(cartVO.getId());
        }
    }

    private List<OrderItem> cartVOListToOrderItemList(List<CartVO> cartVOList) {
        List<OrderItem> orderItemList=new ArrayList();
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            OrderItem orderItem=new OrderItem();
            orderItem.setProductId(cartVO.getProductId());
            //记录商品快照信息
            orderItem.setProductName(cartVO.getProductName());
            orderItem.setProductImg(cartVO.getProductImage());
            orderItem.setUnitPrice(cartVO.getPrice());
            orderItem.setQuantity(cartVO.getQuantity());
            orderItem.setTotalPrice(cartVO.getTotalPrice());
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    private void validSaleStatusAndStock(List<CartVO> cartVOList) {
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            Product product = productMapper.selectByPrimaryKey(cartVO.getProductId());
            //判断商品是否存在，商品是否上架
            if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
                throw new HailinMallException(HailinMallExceptionEnum.NOT_SALE);
            }
            //判断商品库存
            if (cartVO.getQuantity() > product.getStock()) {
                throw new HailinMallException(HailinMallExceptionEnum.NOT_ENOUGH);
            }
        }
    }

    @Override
    public OrderVO detail(String orderNo){
        Order order=orderMapper.selectByOrderNo(orderNo);
        //订单不存在则报错
        if (order == null) {
            throw new HailinMallException(HailinMallExceptionEnum.NO_ORDER);
        }
        //订单存在则判断所属
        Integer userId=UserFilter.currentUser.getId();
        if (!order.getUserId().equals(userId)){
            throw new HailinMallException(HailinMallExceptionEnum.NOT_YOUR_ORDER);
        }
        OrderVO orderVO=getOrderVO(order);
        return orderVO;
    }
    private OrderVO getOrderVO(Order order) {
        OrderVO orderVO=new OrderVO();
        BeanUtils.copyProperties(order,orderVO);
        //获取订单对应的orderItemVOList
        List<OrderItem> orderItemList=orderItemMapper.selectByOrderNo(order.getOrderNo());
        List<OrderItemVO> orderItemVOList=new ArrayList();
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            OrderItemVO orderItemVO=new OrderItemVO();
            BeanUtils.copyProperties(orderItem,orderItemVO);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVOList(orderItemVOList);
        orderVO.setOrderStatusName(Constant.OrderStatusEnum.codeOf(orderVO.getOrderStatus()).getValue());
        return orderVO;
    }

    @Override
    public PageInfo listForCustomer(Integer pageNo, Integer pageSize){
        Integer userId=UserFilter.currentUser.getId();
        PageHelper.startPage(pageNo,pageSize);
        List<Order> orderList=orderMapper.selectForCustomer(userId);
        List<OrderVO> orderVOList=orderListToOrderVOList(orderList);
        PageInfo pageInfo=new PageInfo(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }

    private List<OrderVO> orderListToOrderVOList(List<Order> orderList) {
        List<OrderVO> orderVOList=new ArrayList();
        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            OrderVO orderVO=getOrderVO(order);
            orderVOList.add(orderVO);
        }
        return orderVOList;
    }

    @Override
    public void cancel(String orderNo){
        Order order=orderMapper.selectByOrderNo(orderNo);
        //查不到订单，报错
        if (order == null) {
            throw new HailinMallException(HailinMallExceptionEnum.NO_ORDER);
        }
        //订单存在则判断所属
        Integer userId=UserFilter.currentUser.getId();
        if (!order.getUserId().equals(userId)){
            throw new HailinMallException(HailinMallExceptionEnum.NOT_YOUR_ORDER);
        }
        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAY.getCode())){
            order.setOrderStatus(Constant.OrderStatusEnum.CANCELED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }else {
            throw new HailinMallException(HailinMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    @Override
    public String qrcode(String orderNo){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request=requestAttributes.getRequest();
        /**简单获取本机局域网ip,这样生成的二维码就可以使用连接同一个wifi的手机扫出来,适用于局域网
         * try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }*/
        String address = ip + ":" + request.getLocalPort();
        String payUrl="http://"+address+"/pay?orderNo="+orderNo;
        try {
            QRCodeGenerator.generateQRCodeImage(payUrl,350,350,Constant.FILE_UPLOAD_DIR+orderNo+".png");
        } catch (WriterException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String pngAddress="http://"+address+"/images/"+orderNo+".png";
        return pngAddress;
    }

    @Override
    public void pay(String orderNo){
        Order order=orderMapper.selectByOrderNo(orderNo);
        //查不到订单，报错
        if (order == null) {
            throw new HailinMallException(HailinMallExceptionEnum.NO_ORDER);
        }
        if (order.getOrderStatus()==Constant.OrderStatusEnum.NOT_PAY.getCode()){
            order.setOrderStatus(Constant.OrderStatusEnum.PAID.getCode());
            order.setPayTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }else {
            throw new HailinMallException(HailinMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }
    @Override
    public PageInfo listForAdmin(Integer pageNo, Integer pageSize){
        PageHelper.startPage(pageNo,pageSize);
        List<Order> orderList=orderMapper.selectAllForAdmin();
        List<OrderVO> orderVOList=orderListToOrderVOList(orderList);
        PageInfo pageInfo=new PageInfo(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }
    @Override
    public void delivered(String orderNo){
        Order order=orderMapper.selectByOrderNo(orderNo);
        //查不到订单，报错
        if (order == null) {
            throw new HailinMallException(HailinMallExceptionEnum.NO_ORDER);
        }
        if (order.getOrderStatus()==Constant.OrderStatusEnum.PAID.getCode()){
            order.setOrderStatus(Constant.OrderStatusEnum.DELIVERED.getCode());
            order.setDeliveryTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }else {
            throw new HailinMallException(HailinMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }
    @Override
    public void finish(String orderNo){
        Order order=orderMapper.selectByOrderNo(orderNo);
        //查不到订单，报错
        if (order == null) {
            throw new HailinMallException(HailinMallExceptionEnum.NO_ORDER);
        }
        //如果是普通用户，需要校验订单所属
        if (!userService.checkAdminRole(UserFilter.currentUser)&&order.getUserId().equals(UserFilter.currentUser.getId())) {
            throw new HailinMallException(HailinMallExceptionEnum.NOT_YOUR_ORDER);
        }
        //发货后可以完结订单
        if (order.getOrderStatus()==Constant.OrderStatusEnum.DELIVERED.getCode()){
            order.setOrderStatus(Constant.OrderStatusEnum.FINISHED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }else {
            throw new HailinMallException(HailinMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }
}
