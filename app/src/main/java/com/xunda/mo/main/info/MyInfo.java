package com.xunda.mo.main.info;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.xunda.mo.model.Olduser_Model;
import com.xunda.mo.model.UserDetail_Bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 个人资料
 */

public class MyInfo {
    SharedPreferences share = null; //本地存储类

    public MyInfo(Context context) {
        share = context.getSharedPreferences("myInfo", Context.MODE_PRIVATE);
    }

    //读取本地数据
    public Olduser_Model.DataDTO getUserInfo() {
        Olduser_Model.DataDTO myInfo = new Olduser_Model.DataDTO();
//        myInfo.setUserNum( Double.longBitsToDouble(share.getLong("userNum",0)));
        myInfo.setAge(share.getInt("age", 0));
        myInfo.setAreaCode(share.getString("areaCode", ""));
        myInfo.setAreaName(share.getString("areaName", ""));
        myInfo.setBirthday(share.getString("birthday", ""));
        myInfo.setCreateTime(share.getLong("createTime", 0));
        myInfo.setDeletePassword(share.getString("deletePassword", ""));
        myInfo.setDeleteStatus(share.getInt("deleteStatus", 0));
        myInfo.setGrade(share.getInt("grade", 0));
        myInfo.setHeadImg(share.getString("HeadImg", ""));
        myInfo.setHxUserName(share.getString("HxUserName", ""));
        myInfo.setInPassword(share.getString("inPassword", ""));
        myInfo.setIsNeedUnlock(share.getInt("isNeedUnlock", 0));
        myInfo.setLightStatus(share.getInt("lightStatus", 0));
        myInfo.setMailbox(share.getString("mailbox", ""));
        myInfo.setNickname(share.getString("nickname", ""));
        myInfo.setPhoneNum(share.getString("phoneNum", ""));
        myInfo.setSex(share.getInt("sex", 0));
        myInfo.setSignature(share.getString("signature", ""));
        myInfo.setTag(share.getString("tag", ""));
        myInfo.setToken(share.getString("token", ""));
        myInfo.setUserId(share.getString("userId", ""));
        myInfo.setUserNum(share.getInt("userNum", 0));
        myInfo.setVipType(share.getInt("VipType", 0));
        myInfo.setIsQuestion(share.getInt("isQuestion", 0));
        myInfo.setIsHasLoginPassword(share.getInt("isHasLoginPassword", 0));
        return myInfo;
    }

    //存储本地数据
    public void setUserInfo(Olduser_Model.DataDTO model) {
        SharedPreferences.Editor editor = share.edit();
//        editor.putFloat("userNum",Double.doubleToRawLongBits(model.getUserNum()));
        editor.putInt("age", model.getAge());
        editor.putString("areaCode", model.getAreaCode());
        editor.putString("areaName", model.getAreaName());
        editor.putString("birthday", model.getBirthday());
        editor.putLong("createTime", model.getCreateTime());
        editor.putString("deletePassword", model.getDeletePassword());
        editor.putInt("deleteStatus", model.getDeleteStatus());
        editor.putInt("grade", model.getGrade());
        editor.putString("HeadImg", model.getHeadImg());
        editor.putString("HxUserName", model.getHxUserName());
        editor.putString("inPassword", model.getInPassword());
        editor.putInt("isNeedUnlock", model.getIsNeedUnlock());
        editor.putInt("lightStatus", model.getLightStatus());
        editor.putString("mailbox", model.getMailbox());
        editor.putString("nickname", model.getNickname());
        editor.putString("phoneNum", model.getPhoneNum());
        editor.putInt("sex", model.getSex());
        editor.putString("signature", model.getSignature());
        editor.putString("tag", model.getTag());
        editor.putString("token", model.getToken());
        editor.putString("userId", model.getUserId());
        editor.putInt("userNum", model.getUserNum());
        editor.putInt("VipType", model.getVipType());
        editor.putInt("isQuestion", model.getIsQuestion());
        editor.putInt("isHasLoginPassword", model.getIsHasLoginPassword());
        editor.apply();
    }

    //更新个人信息 没有token与问题isQuestion
    public void  updateData(UserDetail_Bean.DataDTO userModel){
        SharedPreferences.Editor editor = share.edit();
        editor.putInt("age", userModel.getAge());
        editor.putString("areaCode", userModel.getAreaCode());
        editor.putString("areaName", userModel.getAreaName());
        editor.putString("birthday", userModel.getBirthday());
        editor.putLong("createTime", userModel.getCreateTime());
        editor.putString("deletePassword", userModel.getDeletePassword());
        editor.putInt("deleteStatus", userModel.getDeleteStatus());
        editor.putInt("grade", userModel.getGrade());
        editor.putString("HeadImg", userModel.getHeadImg());
        editor.putString("HxUserName", userModel.getHxUserName());
        editor.putString("inPassword", userModel.getInPassword());
        editor.putInt("isNeedUnlock", userModel.getIsNeedUnlock());
        editor.putInt("lightStatus", userModel.getLightStatus());
        editor.putString("mailbox", userModel.getMailbox());
        editor.putString("nickname", userModel.getNickname());
        editor.putString("phoneNum", userModel.getPhoneNum());
        editor.putInt("sex", userModel.getSex());
        editor.putString("signature", userModel.getSignature());
        editor.putString("tag", userModel.getTag());
        editor.putString("userId", userModel.getUserId());
        editor.putInt("userNum", userModel.getUserNum());
        editor.putInt("VipType", userModel.getVipType());
        editor.putInt("isHasLoginPassword", userModel.getIsHasLoginPassword());
        editor.apply();
    }

    //修改存储的一条数据
    public void setOneData(String keyStr, String valueStr) {
        SharedPreferences.Editor editor = share.edit();
        editor.putString(keyStr, valueStr);
        editor.apply();
    }

    public void setOneIntData(String keyStr, int valueStr) {
        SharedPreferences.Editor editor = share.edit();
        editor.putInt(keyStr, valueStr);
        editor.apply();
    }


    //删除本地个人数据
    public void clearInfoData( Context context) {
        share = context.getSharedPreferences("myInfo", Context.MODE_PRIVATE);
        share.edit().clear().apply();
    }


    /**
     * 存放实体类以及任意类型
     *
     * @param context 上下文对象
     * @param key
     * @param obj
     */
    public void putBean(Context context, String key, Object obj) {
        if (obj instanceof Serializable) {// obj必须实现Serializable接口，否则会出问题
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(obj);
                String string64 = new String(Base64.encode(baos.toByteArray(), 0));
//                SharedPreferences.Editor editor = getSharedPreferences(context).edit();
                SharedPreferences.Editor editor = share.edit();
                editor.putString(key, string64).apply();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            throw new IllegalArgumentException("the obj must implement Serializble");
        }

    }

    public Object getBean(Context context, String key) {
        Object obj = null;
        try {
//            String base64 = getSharedPreferences(context).getString(key, "");
            String base64 = share.getString(key, "");
            if (base64.equals("")) {
                return null;
            }
            byte[] base64Bytes = Base64.decode(base64.getBytes(), 1);
            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            obj = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }


}