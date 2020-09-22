package com.hins.service;

import com.hins.pojo.Users;
import com.hins.pojo.bo.UserBO;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2020-09-20
 */
public interface UserService {

    /**
     * 判断用户名是否存在
     */
    public boolean queryUsernameIsExist(String username);

    /**
     * 创建用户
     */
    public Users createUser(UserBO userBO);

    /**
     * 用户登录
     * @param username
     * @param password
     */
    public Users userLogin(String username, String password);
}
