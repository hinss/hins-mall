package com.hins.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @author: hins
 * @created: 2020-11-16 15:28
 * @desc: ES test pojo
 **/
@Document(indexName = "shop", type = "_doc")
public class Shop {

    @Id
    private Long id;

    @Field(store = true)
    private String username;

    @Field(store = true)
    private String desc;

    @Field(store = true)
    private Integer age;

    @Field(store = true)
    private String nickname;

    @Field(store = true)
    private byte sex;

    @Field(store = true, index = false)
    private String face;

    @Field(store = true)
    private Float money;

    @Field(store = true)
    private Date birthday;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public byte getSex() {
        return sex;
    }

    public void setSex(byte sex) {
        this.sex = sex;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public Float getMoney() {
        return money;
    }

    public void setMoney(Float money) {
        this.money = money;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "Shop{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", desc='" + desc + '\'' +
                ", age=" + age +
                ", nickname='" + nickname + '\'' +
                ", sex=" + sex +
                ", face='" + face + '\'' +
                ", money=" + money +
                ", birthday=" + birthday +
                '}';
    }
}
