/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 * Generated by the CloudDB ObjectType compiler.  DO NOT EDIT!
 */
package com.example.salvadore.clouddb;

import com.huawei.agconnect.cloud.database.CloudDBZoneObject;
import com.huawei.agconnect.cloud.database.annotations.DefaultValue;
import com.huawei.agconnect.cloud.database.Text;
import com.huawei.agconnect.cloud.database.annotations.NotNull;
import com.huawei.agconnect.cloud.database.annotations.IsIndex;
import com.huawei.agconnect.cloud.database.annotations.PrimaryKey;

import java.util.Date;

public class TableUser extends CloudDBZoneObject {
    @PrimaryKey
    private Integer user_id;

    private String user_token;

    private String user_name;

    private String user_mail;

    private String user_phone;

    private String user_age;

    private String user_gender;

    private String user_country;

    private String user_city;

    private String auth_id;

    public TableUser() {
        super();

    }
    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_token(String user_token) {
        this.user_token = user_token;
    }

    public String getUser_token() {
        return user_token;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_mail(String user_mail) {
        this.user_mail = user_mail;
    }

    public String getUser_mail() {
        return user_mail;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_age(String user_age) {
        this.user_age = user_age;
    }

    public String getUser_age() {
        return user_age;
    }

    public void setUser_gender(String user_gender) {
        this.user_gender = user_gender;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public void setUser_country(String user_country) {
        this.user_country = user_country;
    }

    public String getUser_country() {
        return user_country;
    }

    public void setUser_city(String user_city) {
        this.user_city = user_city;
    }

    public String getUser_city() {
        return user_city;
    }

    public void setAuth_id(String auth_id) {
        this.auth_id = auth_id;
    }

    public String getAuth_id() {
        return auth_id;
    }

}
