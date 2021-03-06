package com.xunda.mo.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@SuppressLint({"WorldReadableFiles", "WorldWriteableFiles"})
public class saveFile {
    public static String User_SmsCode_Url = "user/smsCode";
    public static String User_Register_Url = "user/register";
    public static String User_Login_Url = "user/login";
    public static String User_SecurityQuestionList_Url = "public/getSecurityQuestionList";
    public static String User_GetPhone_Url = "public/getPhone";
    public static String User_checkPhone_Url = "public/checkPhone";
    public static String User_checkPhoneNoSms_Url = "public/checkPhoneNoSms";
    public static String User_checkChangeCode_Url = "public/checkChangeCode";
    public static String User_forgetPassword_Url = "public/forgetPassword";
    public static String User_Friendlist_Url = "friend/list";
    public static String User_SearchAll_Url = "user/search";
    public static String User_SearchByType_Url = "user/searchByType";
    public static String User_FriendAdd_Url = "friend/add";
    public static String Group_SearchGroup_Url = "group/searchGroup";
    public static String Friend_info_Url = "friend/info";
    public static String Group_MyGroupInfo_Url = "group/myGroupInfo";
    public static String Report_CreatReportLog_Url = "report/createReportLog";
    public static String Group_MyGroupList_Url = "group/myGroupList";
    public static String Friend_ApplyList_Url = "friend/applyList";
    public static String Friend_Agree_Url = "friend/agree";
    public static String Friend_RejectAll_Url = "friend/rejectAll";
    public static String Friend_ClearApplyList_Url = "friend/clearApplyList";
    public static String Friend_Delete_Url = "friend/delete";
    public static String Friend_SetBlack_Url = "friend/setBlack";
    public static String Friend_Silence_Url = "friend/silence";
    public static String User_GetUserInfo_Url = "user/getUserInfo";
    public static String User_CheckUpdateUser_Url = "user/checkUpdateUser";
    public static String User_Update_Url = "user/update";
    public static String Friend_UpdateRemarkName_Url = "friend/updateRemarkName";
    public static String Group_Create_Url = "group/create";
    public static String Group_UserList_Url = "group/userList";
    public static String Group_UpdateInfo_Url = "group/updateInfo";
    public static String Group_UpdateNickName_Url = "group/updateNickname";
    public static String Group_Out_Url = "group/out";
    public static String Friend_InviteToGroup_Url = "friend/inviteToGroup";
    public static String group_MangerUser_Url = "group/mangerUser";
    public static String group_AddBatchManger_Url = "group/addBatchManger";
    public static String group_MangerSet_Url = "group/mangerSet";
    public static String Group_BlackUsers_Url = "group/blackUsers";
    public static String Group_SetBlack_Url = "group/setBlack";
    public static String Group_MuteUsers_Url = "group/muteUsers";
    public static String Group_UserMute_Url = "group/userMute";
    public static String Group_GroupApplyList_Url = "group/groupApplyList";
    public static String Group_AgreeApply_Url = "group/agreeApply";
    public static String Group_UpdateApplyListByIds_Url = "group/updateApplyListByIds";
    public static String Group_DeleteApplyListByIds_Url = "group/deleteApplyListByIds";
    public static String Group_AnonymousOrMute_Url = "group/anonymousOrMute";
    public static String Group_Way_Url = "group/way";
    public static String Group_TransferMaster_Url = "group/transferMaster";
    public static String Group_UserInfo_Url = "group/userInfo";
    public static String Inviter_UserNum_Url = "inviter/userNum";
    public static String Inviter_Bind_Url = "inviter/bind";
    public static String Group_ApplyInto_Url = "group/applyInto";
    public static String Group_Notify_Url = "group/notify";
    public static String Group_Disturb_Url = "group/disturb";
    public static String Friend_Fire_Url = "friend/fire";
    public static String Receptionist_MyMoRoom = "receptionist/myMoRoom";
    public static String Receptionist_Answer = "receptionist/answer";
    public static String Receptionist_Complaint = "receptionist/complaint";
    public static String Sign_Log = "sign/log";
    public static String Sign_Do = "sign/do";
    public static String ExchangeLog_AllExchangeCard = "exchangeLog/getAllExchangeCard";
    public static String ExchangeLog_ExchangeCard = "exchangeLog/exchangeCard";
    public static String UserCard_CardList = "userCard/getCardList";
    public static String UserCard_Use = "userCard/use";
    public static String Order_Create = "order/create";
    public static String Alipay_AppPay = "aliPay/appPay";
    public static String WxPay_AppPay = "wxPay/appPay";
    public static String UserVipConfig_UserVipInfo = "userVipConfig/userVipInfo";
    public static String Friend_Blacks = "friend/setMeBlacks";
    public static String User_Delete = "user/delete";
    public static String Question_NoLogin = "public/questionBackByNoLogin";
    public static String Question_Login = "public/questionBackByLogin";
    public static String User_PSW = "user/setPassword";
    public static String Question_AddOrUpdate = "securityQuestion/addOrUpdate";
    public static String SecurityQuestion_Check = "securityQuestion/securityQuestionCheck";
    public static String Group_Horn = "group/horn";
    public static String Friend_Blacks_List = "friend/blacks";
    public static String Group_myGroupNicknameByHx = "group/myGroupNicknameByHxIdOrGroupId";
    public static String versionUpdate = "public/isForceUpdate";
    public static String SensitiveWord = "public/sensitiveWord";




    private static SharedPreferences mShared = null;
    /**
     * ?????????????????????????????????SharedPreferences????????? ??????SharedPreferences??????????????????????????????
     **/
    public final static String SHARED_MAIN = "main";
    /**
     * SharedPreferences??????????????????Key??????
     **/
    public final static String KEY_NAME = "name";
    public final static String KEY_NUMBER = "number";

    /**
     * SharedPreferences????????????????????????
     **/
    public final static String DATA_URL = "/data/test/";
    public final static String SHARED_MAIN_XML = "test.xml";

    public static void saveShareData(String keyname, String key, Context context) {
        mShared = context.getSharedPreferences(SHARED_MAIN, Context.MODE_PRIVATE);
        Editor editor = mShared.edit();
        editor.putString(keyname, key);
        /**put???????????????commit()??????????????????**/
        editor.commit();
    }

    public static String getShareData(String keyname, Context context) {
        mShared = context.getSharedPreferences(SHARED_MAIN, Context.MODE_PRIVATE);
        String name = mShared.getString(keyname, "false");
        return name;
    }

    public static void clearShareData(String keyname, Context context) {
        mShared = context.getSharedPreferences(SHARED_MAIN, Context.MODE_PRIVATE);
        Editor editor = mShared.edit();
        editor.remove(keyname);
        editor.commit();
    }

    //???List<String>
    public static void saveUserList(Context context, List<String> baseArr, String KEY_NAME) {
//        baseArr = new ArrayList<>(new HashSet<>(baseArr));
        Editor editor = context.getSharedPreferences(KEY_NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.putInt("size", baseArr.size());
        for (int i = 0; i < baseArr.size(); i++) {
            editor.remove("seekname" + i);
            editor.putString("seekname" + i, baseArr.get(i));
        }
        editor.commit();
    }

    //???List<String>
    public static List<String> getUserList(Context context, String KEY_NAME) {
        List<String> baseArr = null;
        if (baseArr != null) {
            baseArr.clear();
        }
        baseArr = new ArrayList<>();
        SharedPreferences sp = context.getSharedPreferences(KEY_NAME, Context.MODE_PRIVATE);
        int size = sp.getInt("size", 0);
        for (int i = 0; i < size; i++) {
            baseArr.add(sp.getString("seekname" + i, null));
        }
//        baseArr = new ArrayList<String>(new HashSet<String>(baseArr));
        return baseArr;
    }

    //??????????????????
    public static void removeUserOne(Context context, String KEY_NAME, String valen_Name) {
        SharedPreferences sp = context.getSharedPreferences(KEY_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        int size = sp.getInt("size", 0);
        List<String> baseArr = getUserList(context, "listselect");//
        for (int i = 0; i < size; i++) {//??????
            if (baseArr.get(i).equals(valen_Name)) {
                baseArr.remove(i);
//                size = size-1;//?????????????????? size-1
                break;
            }
        }
        saveUserList(context, baseArr, "listselect");//??????
        editor.commit();
    }

    //??????????????????
    public static void addUserone(Context context, String KEY_NAME, String valen_Name) {
        SharedPreferences sp = context.getSharedPreferences(KEY_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        List<String> baseArr = getUserList(context, "listselect");
        baseArr.add(valen_Name);
        saveUserList(context, baseArr, "listselect");//??????
        editor.commit();
    }


    //??????map
    public static String SceneList2String(HashMap<Integer, Boolean> hashmap)
            throws IOException {
        // ???????????????ByteArrayOutputStream????????????????????????????????????????????????
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // ???????????????????????????????????????ObjectOutputStream
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                byteArrayOutputStream);
        // writeObject ??????????????????????????????????????????????????????????????? readObject ?????????????????????
        objectOutputStream.writeObject(hashmap);
        // ????????????Base64.encode????????????????????????Base64???????????????String???
        String SceneListString = new String(Base64.encode(
                byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
        // ??????objectOutputStream
        objectOutputStream.close();
        return SceneListString;
    }

    @SuppressWarnings("unchecked")
    public static HashMap<Integer, Boolean> String2SceneList(
            String SceneListString) throws StreamCorruptedException,
            IOException, ClassNotFoundException {
        byte[] mobileBytes = Base64.decode(SceneListString.getBytes(),
                Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                mobileBytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(
                byteArrayInputStream);
        HashMap<Integer, Boolean> SceneList = (HashMap<Integer, Boolean>) objectInputStream
                .readObject();
        objectInputStream.close();
        return SceneList;
    }


    public static boolean putHashMap(HashMap<Integer, Boolean> hashmap, String key, Context context) {
        SharedPreferences settings = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        Editor editor = settings.edit();
        try {
            String liststr = SceneList2String(hashmap);
            editor.putString(key, liststr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return editor.commit();
    }


    public static HashMap<Integer, Boolean> getHashMap(String key, Context context) {
        SharedPreferences settings = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        String liststr = settings.getString(key, "");
        try {
            return String2SceneList(liststr);
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * true???????????????flase?????????
     *
     * @param str
     * @return
     */
    public static Boolean getBig(String str) {
        Boolean flag = null;
        try {
            Date nowdate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
            Date d = sdf.parse(str);
            if (d == nowdate) {
                flag = true;//????????????
            } else {
                flag = d.before(nowdate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    //trues??????  ??????flase
    public static Boolean getTimeBig(String str) {
        Boolean flag = null;
        try {
            Date nowdate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Date d = sdf.parse(str);

            String time = sdf.format(d);
            String timenow = sdf.format(nowdate);
            if (time.equals(timenow)) {
                flag = true;
            } else {
                flag = false;//????????????
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * ????????????????????????????????????????????????????????????????????? ?????????MMdd???????????????y???????????????
     *
     * @param sformat MMdd
     * @return
     */
    public static String getUserDate(String sformat) {
        String dateString = "2017-03-28";
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(sformat);//??????????????????
            dateString = new SimpleDateFormat("MM/dd").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }

    /**
     * ???????????????
     *
     * @param
     */
//    public static void putClass(Context context, String KEY_NAME, List<ProjectModel> model) {//?????????????????????????????????
//        SharedPreferences.Editor editor = context.getSharedPreferences(KEY_NAME, Context.MODE_PRIVATE).edit();
//        editor.putInt("size", model.size());
//        for (int i = 0; i < model.size(); i++) {
////            String key = model.getClass().getName() + i;
//            String key = KEY_NAME + i;
//            String value = new Gson().toJson(model.get(i));
//            editor.putString(key, value);
//        }
//        editor.commit();
//    }
//

    /**
     * ???????????????
     *
     * @param
     * @param
     * @return
     */
    public static <T> List<T> getGosnClass(Context context, String KEY_NAME, Class<T> model) {
        List<T> listModel = new ArrayList<>();
        SharedPreferences sp = context.getSharedPreferences(KEY_NAME, Context.MODE_PRIVATE);
        int size = sp.getInt("size", 0);
        for (int i = 0; i < size; i++) {
//            String key = model.getName() + i;
            String key = KEY_NAME + i;
            String value = sp.getString(key, "");
            T t = new Gson().fromJson(value, model);
            listModel.add(t);
        }
        return listModel;
    }

    //?????????????????????
//    public static void removeGsonOne(Context context, String KEY_NAME, int valen_Id) {
//        SharedPreferences sp = context.getSharedPreferences(KEY_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        List<ProjectModel> listModel = getGosnClass(context, KEY_NAME, ProjectModel.class);
//        int size = sp.getInt("size", 0);
//        for (int i = 0; i < size; i++) {
//            if (listModel.get(i).getProjectId() == valen_Id) {
//                listModel.remove(i);//??????
//                break;
//            }
//        }
//        putClass(context, "moreModel", listModel);//??????
//        editor.commit();
//    }
//    //?????????????????????
//    public static void saveGsonOne(Context context, String model_NAME, ProjectModel model) {
//        SharedPreferences sp = context.getSharedPreferences(model_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        List<ProjectModel> listModel = getGosnClass(context, model_NAME, ProjectModel.class);
//
//        listModel.add(model);
////        int size = sp.getInt("size", 0);
////        for (int i = 0; i < size; i++) {
////            if (listModel.get(i).getProjectId() == valen_Id) {
////                listModel.remove(i);//??????
////                break;
////            }
////        }
//        putClass(context, model_NAME, listModel);//??????
//        editor.commit();
//    }

    public static void clearGson(Context context, String KEY_NAME) {
        SharedPreferences sp = context.getSharedPreferences(KEY_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }


}
