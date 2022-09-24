package com.hailin.mall.service.impl;

import com.hailin.mall.exception.HailinMallException;
import com.hailin.mall.exception.HailinMallExceptionEnum;
import com.hailin.mall.model.dao.UserMapper;
import com.hailin.mall.model.pojo.User;
import com.hailin.mall.service.UserService;
import com.hailin.mall.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

//UserService实现类
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Override
    public User getUser() {
        return userMapper.selectByPrimaryKey(1);//通过主键查询
    }

    @Override
    public void register(String userName, String password) throws HailinMallException {
        //查询用户名是否存在，禁止重名
        User result= userMapper.selectByName(userName);
        if(result!=null){
            throw new HailinMallException(HailinMallExceptionEnum.NAME_EXISTED);
        }
        //不重名，完成注册
        User user = new User();
        user.setUsername(userName);
        //user.setPassword(password);
        try {
            user.setPassword(MD5Utils.getMD5String(password));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        int count = userMapper.insertSelective(user);//insertSelective和insert不同的是它会先判断传入元素属性是否为空，只写入不为空的，而insert需要全都有值
        if(count==0){
            throw new HailinMallException(HailinMallExceptionEnum.INSERT_FAILED);
        }
    }
    @Override
    public User login(String userName, String password) throws HailinMallException {
        String md5Password=null;
        try {
            md5Password=MD5Utils.getMD5String(password);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        User user=userMapper.selectLogin(userName, md5Password);
        if (user==null){
            throw new HailinMallException(HailinMallExceptionEnum.WRONG_PASSWORD);
        }
        return user;
    }
    @Override
    public void updateInformation(User user) throws HailinMallException {
        int updateCount=userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>1){
            throw new HailinMallException(HailinMallExceptionEnum.UPDATE_FAILED);
        }
    }
    @Override
    public boolean checkAdminRole(User user){
        return user.getRole().equals(2);
    }
}
