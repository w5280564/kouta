package com.example.kouta.model;

import java.util.List;

public class adress_Model {

    private String msg;
    private long code;
    private List<DataDTO> data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public List<DataDTO> getData() {
        return data;
    }

    public void setData(List<DataDTO> data) {
        this.data = data;
    }

    public static class DataDTO {
        private String ciphertext;
        private long fireType;
        private String headImg;
        private String hxUserName;
        private double isCiphertext;
        private double lightStatus;
        private String remarkName;
        private String userId;
        private double userNum;
        private long vipType;

        public String getCiphertext() {
            return ciphertext;
        }

        public void setCiphertext(String ciphertext) {
            this.ciphertext = ciphertext;
        }

        public long getFireType() {
            return fireType;
        }

        public void setFireType(long fireType) {
            this.fireType = fireType;
        }

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }

        public String getHxUserName() {
            return hxUserName;
        }

        public void setHxUserName(String hxUserName) {
            this.hxUserName = hxUserName;
        }

        public double getIsCiphertext() {
            return isCiphertext;
        }

        public void setIsCiphertext(double isCiphertext) {
            this.isCiphertext = isCiphertext;
        }

        public double getLightStatus() {
            return lightStatus;
        }

        public void setLightStatus(double lightStatus) {
            this.lightStatus = lightStatus;
        }

        public String getRemarkName() {
            return remarkName;
        }

        public void setRemarkName(String remarkName) {
            this.remarkName = remarkName;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public double getUserNum() {
            return userNum;
        }

        public void setUserNum(double userNum) {
            this.userNum = userNum;
        }

        public long getVipType() {
            return vipType;
        }

        public void setVipType(long vipType) {
            this.vipType = vipType;
        }
    }
}
