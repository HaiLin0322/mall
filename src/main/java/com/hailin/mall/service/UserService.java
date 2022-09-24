package com.hailin.mall.service;

import com.hailin.mall.exception.HailinMallException;
import com.hailin.mall.model.pojo.User;

public interface UserService {
    User getUser();
    void register(String userName,String password) throws HailinMallException;

    User login(String userName, String password) throws HailinMallException;

    void updateInformation(User user) throws HailinMallException;

    boolean checkAdminRole(User user);
}
