package com.example.salvadore.clouddb;

import android.content.Intent;

public class UserEditFields {

    public static final String EDIT_MODE = "EDIT_MODE";
    public static String USER_ID = "id";
    public static String USER_TOKEN = "token";
    public static String USER_NAME = "name";
    public static String USER_LAST_NAME = "last_name";
    public static String USER_MAIL = "mail";
    public static String USER_PHONE = "phone";
    public static String USER_AGE = "age";
    public static String USER_GENDER = "gender";
    public static String USER_COUNTRY = "country";
    public static String USER_CITY = "city";
    //String EDIT_MODE = "EDIT_MODE";

    /**
     * To mark it's in add mode or in modify mode
     */
    public enum EditMode {
        ADD,
        MODIFY
    }
}
