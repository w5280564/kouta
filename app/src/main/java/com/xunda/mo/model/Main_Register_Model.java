package com.xunda.mo.model;

public class Main_Register_Model {


    private String msg;
    private int code;
    private DataDTO data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO {
        private int age;
        private String areaCode;
        private String birthday;
        private long createTime;
        private String deletePassword;
        private int deleteStatus;
        private int experience;
        private int grade;
        private String headImg;
        private String hxUserName;
        private String inPassword;
        private int isNeedUnlock;
        private int lightStatus;
        private String nickname;
        private String phoneNum;
        private int sex;
        private String signature;
        private String token;
        private int userNum;
        private int vipType;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getAreaCode() {
            return areaCode;
        }

        public void setAreaCode(String areaCode) {
            this.areaCode = areaCode;
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

        public String getDeletePassword() {
            return deletePassword;
        }

        public void setDeletePassword(String deletePassword) {
            this.deletePassword = deletePassword;
        }

        public int getDeleteStatus() {
            return deleteStatus;
        }

        public void setDeleteStatus(int deleteStatus) {
            this.deleteStatus = deleteStatus;
        }

        public int getExperience() {
            return experience;
        }

        public void setExperience(int experience) {
            this.experience = experience;
        }

        public int getGrade() {
            return grade;
        }

        public void setGrade(int grade) {
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

        public String getInPassword() {
            return inPassword;
        }

        public void setInPassword(String inPassword) {
            this.inPassword = inPassword;
        }

        public int getIsNeedUnlock() {
            return isNeedUnlock;
        }

        public void setIsNeedUnlock(int isNeedUnlock) {
            this.isNeedUnlock = isNeedUnlock;
        }

        public int getLightStatus() {
            return lightStatus;
        }

        public void setLightStatus(int lightStatus) {
            this.lightStatus = lightStatus;
        }

        public String getNickName() {
            return nickname;
        }

        public void setNickName(String NickName) {
            this.nickname = NickName;
        }

        public String getPhoneNum() {
            return phoneNum;
        }

        public void setPhoneNum(String phoneNum) {
            this.phoneNum = phoneNum;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
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

        public int getUserNum() {
            return userNum;
        }

        public void setUserNum(int userNum) {
            this.userNum = userNum;
        }

        public int getVipType() {
            return vipType;
        }

        public void setVipType(int vipType) {
            this.vipType = vipType;
        }
    }
}
