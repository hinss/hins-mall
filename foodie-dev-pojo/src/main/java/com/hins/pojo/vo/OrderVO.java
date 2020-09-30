package com.hins.pojo.vo;

/**
 * @author: hins
 * @created: 2020-09-30 16:08
 * @desc:
 **/
public class OrderVO {

    private String orderId;

    private MerchantOrderVO merchantOrderVO;

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
}
