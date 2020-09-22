package com.hins.service;

import com.hins.pojo.Carousel;
import com.hins.pojo.Users;
import com.hins.pojo.bo.UserBO;

import java.util.List;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2020-09-20
 */
public interface CarouselService {

    public List<Carousel> getIndexCarouselList(Integer isShow);
}
