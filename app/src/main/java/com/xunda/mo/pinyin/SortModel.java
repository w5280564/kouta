package com.xunda.mo.pinyin;

public class SortModel {

    private String name;
    private String letters;//显示拼音的首字母
    private String headImg;
    private double lightStatus;
    private double userNum;
    private long vipType;
    private String HxUserName;

    public String getHxUserName() {
        return HxUserName;
    }

    public void setHxUserName(String hxUserName) {
        HxUserName = hxUserName;
    }


    public long getVipType() {
        return vipType;
    }

    public void setVipType(long vipType) {
        this.vipType = vipType;
    }


    public double getUserNum() {
        return userNum;
    }

    public void setUserNum(double userNum) {
        this.userNum = userNum;
    }



    public double getLightStatus() {
        return lightStatus;
    }

    public void setLightStatus(double lightStatus) {
        this.lightStatus = lightStatus;
    }



    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }
}
