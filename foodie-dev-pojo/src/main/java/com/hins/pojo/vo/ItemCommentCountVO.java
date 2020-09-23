package com.hins.pojo.vo;

/**
 * @Description: 商品评论数量vo
 * @Author:Wyman
 * @Date: 2020-09-23
 */
public class ItemCommentCountVO {

    // 总评价数量
    private Integer totalCounts;
    // 好评数量
    private Integer goodCounts;
    // 中评数量
    private Integer normalCounts;
    // 差评数量
    private Integer badCounts;

    public Integer getTotalCounts() {
        return totalCounts;
    }

    public void setTotalCounts(Integer totalCounts) {
        this.totalCounts = totalCounts;
    }

    public Integer getGoodCounts() {
        return goodCounts;
    }

    public void setGoodCounts(Integer goodCounts) {
        this.goodCounts = goodCounts;
    }

    public Integer getNormalCounts() {
        return normalCounts;
    }

    public void setNormalCounts(Integer normalCounts) {
        this.normalCounts = normalCounts;
    }

    public Integer getBadCounts() {
        return badCounts;
    }

    public void setBadCounts(Integer badCounts) {
        this.badCounts = badCounts;
    }
}
