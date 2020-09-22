package com.hins.service.impl;

import com.hins.enums.Sex;
import com.hins.mapper.UsersMapper;
import com.hins.pojo.Users;
import com.hins.pojo.bo.UserBO;
import com.hins.service.UserService;
import com.hins.utils.DateUtil;
import com.hins.utils.MD5Utils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2020-09-20
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    private static final String USER_FACE = "http://122.152.205.72:88/group1/M00/00/05/CpoxxFw_8_qAIlFXAAAcIhVPdSg994.png";

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {

        Example userExample = new Example(Users.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("username", username);

        Users users = usersMapper.selectOneByExample(userExample);

        return users == null ? false : true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users createUser(UserBO userBO) {

        Users users = new Users();

        // 这里的主键 数据库中没有使用自增 考虑到分布式拓展
        // 所以使用sid组件的唯一id实现
        String userId = sid.nextShort();
        users.setId(userId);
        // 设置用户名
        users.setUsername(userBO.getUsername());
        // 设置密码
        try {
            users.setPassword(MD5Utils.getMD5Str(userBO.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 设置默认昵称和用户名一致
        users.setNickname(userBO.getUsername());
        // 设置默认生日
        users.setBirthday(DateUtil.stringToDate("1900-01-01"));
        // 设置用户默认头像
        users.setFace(USER_FACE);
        // 设置默认性别
        users.setSex(Sex.SECRET.type);

        users.setCreatedTime(new Date());
        users.setUpdatedTime(new Date());

        usersMapper.insert(users);

        return users;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users userLogin(String username, String password) {

        try {
            Thread.sleep(3500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Example userExample = new Example(Users.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("username", username);
        userCriteria.andEqualTo("password", password);

        Users users = usersMapper.selectOneByExample(userExample);

        return users;
    }


}
