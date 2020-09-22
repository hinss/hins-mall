package com.hins.pojo.vo;

import java.util.List;

/**
 * @Description: 二级分类
 * @Author:Wyman
 * @Date: 2020-09-22
 */
public class CategoryVO {

    private Integer id;
    private String name;
    private Integer type;
    private Integer fatherId;

    List<SubCatVO> subCatList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getFatherId() {
        return fatherId;
    }

    public void setFatherId(Integer fatherId) {
        this.fatherId = fatherId;
    }

    public List<SubCatVO> getSubCatList() {
        return subCatList;
    }

    public void setSubCatList(List<SubCatVO> subCatList) {
        this.subCatList = subCatList;
    }
}
