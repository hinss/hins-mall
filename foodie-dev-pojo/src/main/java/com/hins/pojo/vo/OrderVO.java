package com.hins.pojo.vo;

import com.hins.pojo.bo.ShopcartBO;

import java.util.List;

/**
 * @author: hins
 * @created: 2020-09-30 16:08
 * @desc:
 **/
public class OrderVO {

    private String orderId;

    private MerchantOrderVO merchantOrderVO;

    private List<ShopcartBO> toBeRemoveShopCartBoList;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public MerchantOrderVO getMerchantOrderVO() {
        return merchantOrderVO;
    }

    public void setMerchantOrderVO(MerchantOrderVO merchantOrderVO) {
        this.merchantOrderVO = merchantOrderVO;
    }

    public List<ShopcartBO> getToBeRemoveShopCartBoList() {
        return toBeRemoveShopCartBoList;
    }

    public void setToBeRemoveShopCartBoList(List<ShopcartBO> toBeRemoveShopCartBoList) {
        this.toBeRemoveShopCartBoList = toBeRemoveShopCartBoList;
    }
}
