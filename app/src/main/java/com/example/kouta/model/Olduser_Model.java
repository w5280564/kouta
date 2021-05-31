package com.example.kouta.model;

public class Olduser_Model {

    private String msg;
    private long code;
    private DataDTO data;

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

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO {
        private long age;
        private double areaId;
        private String birthday;
        private long createTime;
        private double deleteStatus;
        private long experience;
        private double grade;
        private String headImg;
        private String hxUserName;
        private long lightStatus;
        private String nikeName;
        private String phoneNum;
        private double sex;
        private String signature;
        private String token;
        private String userId;
        private long userNum;
        private long vipType;

        public long getAge() {
            return age;
        }

        public void setAge(long age) {
            this.age = age;
        }

        public double getAreaId() {
            return areaId;
        }

        public void setAreaId(double areaId) {
            this.areaId = areaId;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public double getDeleteStatus() {
            return deleteStatus;
        }

        public void setDeleteStatus(double deleteStatus) {
            this.deleteStatus = deleteStatus;
        }

        public long getExperience() {
            return experience;
        }

        public void setExperience(long experience) {
            this.experience = experience;
        }

        public double getGrade() {
            return grade;
        }

        public void setGrade(double grade) {
            this.grade = grade;
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

        public long getLightStatus() {
            return lightStatus;
        }

        public void setLightStatus(long lightStatus) {
            this.lightStatus = lightStatus;
        }

        public String getNikeName() {
            return nikeName;
        }

        public void setNikeName(String nikeName) {
            this.nikeName = nikeName;
        }

        public String getPhoneNum() {
            return phoneNum;
        }

        public void setPhoneNum(String phoneNum) {
            this.phoneNum = phoneNum;
        }

        public double getSex() {
            return sex;
        }

        public void setSex(double sex) {
            this.sex = sex;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public long getUserNum() {
            return userNum;
        }

        public void setUserNum(long userNum) {
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
