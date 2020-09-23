package com.hins.enums;

public enum ItemsCommentLevelEnum {

    GOOD(1, "好评"),
    NORMAL(2, "中评"),
    BAD(3, "差评");

    public final Integer type;
    public final String desc;

    ItemsCommentLevelEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
