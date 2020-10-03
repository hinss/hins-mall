package com.hins.service.center;

import com.hins.pojo.Users;
import com.hins.pojo.bo.center.CenterUserBO;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2020-10-03
 */
public interface CenterUserService {

    public Users queryUserInfo(String userId);

    public Users updateUserInfo(CenterUserBO centerUserBO, String userId);

    public Users updateUserFace(String userId, String userFacePath);
}
