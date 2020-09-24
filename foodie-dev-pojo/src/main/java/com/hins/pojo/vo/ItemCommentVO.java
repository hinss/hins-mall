package com.hins.pojo.vo;

/**
 * @author: hins
 * @created: 2020-09-24 09:44
 * @desc: 商品评论vo
 **/
public class ItemCommentVO {

    private String specName;
    private Integer commentLevel;
    private String content;
    private String userFace;
    private String nickname;

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public Integer getCommentLevel() {
        return commentLevel;
    }

    public void setCommentLevel(Integer commentLevel) {
        this.commentLevel = commentLevel;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserFace() {
        return userFace;
    }

    public void setUserFace(String userFace) {
        this.userFace = userFace;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
