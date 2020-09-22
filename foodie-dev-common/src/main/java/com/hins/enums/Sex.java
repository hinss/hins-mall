package com.hins.enums;

public enum Sex {

    MAN(0, "男"),
    WOMAN(1, "女"),
    SECRET(2, "保密");

    public final Integer type;
    public final String code;

    Sex(Integer type, String code) {
        this.type = type;
        this.code = code;
    }
}
