package com.xunda.mo.main.info;

import android.content.Context;
import android.content.SharedPreferences;

import com.xunda.mo.model.Olduser_Model;

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
        myInfo.setHxUserName(share.getString("HxUserName","false"));
        myInfo.setNikeName(share.getString("NikeName","false"));
        myInfo.setHeadImg(share.getString("HeadImg",""));
        myInfo.setLightStatus(Double.parseDouble(share.getString("LightStatus","0")));
        myInfo.setVipType(share.getLong("VipType",0));
        return myInfo;
    }

    //存储本地数据
    public void setUserInfo(Olduser_Model.DataDTO model) {
        SharedPreferences.Editor editor = share.edit();
        editor.putString("HxUserName",model.getHxUserName());
        editor.putString("NikeName",model.getNikeName());
        editor.putString("HeadImg",model.getHeadImg());
        editor.putString("LightStatus",model.getLightStatus().toString());
        editor.putLong("VipType",model.getVipType());
        editor.commit();
    }

    //修改存储的一条数据
    public void setOneData(String keyStr,String valueStr){
        SharedPreferences.Editor editor = share.edit();
        editor.putString(keyStr,valueStr);
        editor.commit();
        getUserInfo().getHeadImg();
    }


}