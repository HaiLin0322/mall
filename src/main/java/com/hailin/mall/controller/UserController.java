package com.hailin.mall.controller;

import com.hailin.mall.common.ApiRestResponse;
import com.hailin.mall.common.Constant;
import com.hailin.mall.exception.HailinMallException;
import com.hailin.mall.exception.HailinMallExceptionEnum;
import com.hailin.mall.model.pojo.User;
import com.hailin.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {
    @Autowired
    UserService userService;
    @GetMapping("/test")
    @ResponseBody
    public User personalPage(){
        return userService.getUser();
    }
    @PostMapping("/register")
    @ResponseBody
    public ApiRestResponse register(@RequestParam("userName") String userName, @RequestParam("password") String password) throws HailinMallException {
        if(!StringUtils.hasLength(userName)){
            return ApiRestResponse.error(HailinMallExceptionEnum.NEED_USER_NAME);
        }
        if(!StringUtils.hasLength(password)){
            return ApiRestResponse.error(HailinMallExceptionEnum.NEED_PASSWORD);
        }
        if (password.length()<8) {
            return ApiRestResponse.error(HailinMallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        userService.register(userName,password);
        return ApiRestResponse.success();
    }
    @PostMapping("/login")
    @ResponseBody
    public ApiRestResponse login(@RequestParam("userName") String userName, @RequestParam("password") String password, HttpSession session) throws HailinMallException {
        if(!StringUtils.hasLength(userName)){
            return ApiRestResponse.error(HailinMallExceptionEnum.NEED_USER_NAME);
        }
        if(!StringUtils.hasLength(password)){
            return ApiRestResponse.error(HailinMallExceptionEnum.NEED_PASSWORD);
        }
        User user=userService.login(userName, password);
        user.setPassword(null);//返回用户信息时，不返回密码
        session.setAttribute(Constant.HAILIN_MALL_USER,user);
        return ApiRestResponse.success(user);
    }
    @PostMapping("/user/update")
    @ResponseBody
    public ApiRestResponse updateUserInfo(HttpSession session,@RequestParam String signature) throws HailinMallException {
        User user=(User) session.getAttribute(Constant.HAILIN_MALL_USER);
        if(user==null){
            return ApiRestResponse.error(HailinMallExceptionEnum.NEED_LOGIN);
        }
        User user1=new User();
        user1.setId(user.getId());
        user1.setPersonalizedSignature(signature);
        userService.updateInformation(user1);
        return ApiRestResponse.success();
    }
    @PostMapping("/user/logout")
    @ResponseBody
    public ApiRestResponse logout(HttpSession session){
        session.removeAttribute(Constant.HAILIN_MALL_USER);
        return ApiRestResponse.success();
    }
    //管理员登录
    @PostMapping("/adminLogin")
    @ResponseBody
    public ApiRestResponse adminLogin(@RequestParam("userName") String userName, @RequestParam("password") String password, HttpSession session) throws HailinMallException {
        if(!StringUtils.hasLength(userName)){
            return ApiRestResponse.error(HailinMallExceptionEnum.NEED_USER_NAME);
        }
        if(!StringUtils.hasLength(password)){
            return ApiRestResponse.error(HailinMallExceptionEnum.NEED_PASSWORD);
        }
        User user=userService.login(userName, password);
        //校验是否是管理员
        if (userService.checkAdminRole(user)) {
            //校验成功，执行操作
            user.setPassword(null);//返回用户信息时，不返回密码
            session.setAttribute(Constant.HAILIN_MALL_USER,user);
            return ApiRestResponse.success(user);
        }else {
            return ApiRestResponse.error(HailinMallExceptionEnum.NEED_ADMIN);
        }
    }
}
