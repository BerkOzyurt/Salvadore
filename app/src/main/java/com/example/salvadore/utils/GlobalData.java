package com.example.salvadore.utils;

public class GlobalData {

    private static GlobalData instance = new GlobalData();

    public static GlobalData getInstance() {
        return instance;
    }

    public static void setInstance(GlobalData instance) {
        GlobalData.instance = instance;
    }

    private GlobalData() {
    }

    int helpID;

    double HelpLat;
    double HelpLng;

    String HelpAdres;
    String HelpUserName;
    String createdAccessToken;
    String UserToken;

    public String getUserToken() {
        return UserToken;
    }

    public void setUserToken(String userToken) {
        UserToken = userToken;
    }

    public String getCreatedAccessToken() {
        return createdAccessToken;
    }

    public void setCreatedAccessToken(String createdAccessToken) {
        this.createdAccessToken = createdAccessToken;
    }

    public int getHelpID() {
        return helpID;
    }

    public void setHelpID(int helpID) {
        this.helpID = helpID;
    }

    public double getHelpLat() {
        return HelpLat;
    }

    public void setHelpLat(double helpLat) {
        HelpLat = helpLat;
    }

    public double getHelpLng() {
        return HelpLng;
    }

    public void setHelpLng(double helpLng) {
        HelpLng = helpLng;
    }

    public String getHelpAdres() {
        return HelpAdres;
    }

    public void setHelpAdres(String helpAdres) {
        HelpAdres = helpAdres;
    }

    public String getHelpUserName() {
        return HelpUserName;
    }

    public void setHelpUserName(String helpUserName) {
        HelpUserName = helpUserName;
    }
}
