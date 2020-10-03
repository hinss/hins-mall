package com.hins.service.impl.center;

import com.hins.mapper.UsersMapper;
import com.hins.pojo.Users;
import com.hins.pojo.bo.center.CenterUserBO;
import com.hins.service.center.CenterUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2020-10-03
 */
@Service
public class CenterUserServiceImpl implements CenterUserService {

    @Autowired
    private UsersMapper usersMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfo(String userId) {
        Users users = usersMapper.selectByPrimaryKey(userId);
        users.setPassword(null);
        return users;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserInfo(CenterUserBO centerUserBO, String userId) {

        Users users = new Users();
        BeanUtils.copyProperties(centerUserBO, users);
        users.setId(userId);
        users.setUpdatedTime(new Date());

        usersMapper.updateByPrimaryKeySelective(users);

        Users userInfo = this.queryUserInfo(userId);

        return userInfo;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserFace(String userId, String userFacePath) {

        Users users = new Users();
        users.setId(userId);
        users.setFace(userFacePath);

        usersMapper.updateByPrimaryKeySelective(users);

        Users userInfo = this.queryUserInfo(userId);
        return userInfo;
    }
}
