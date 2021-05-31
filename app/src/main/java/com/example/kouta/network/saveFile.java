package com.example.kouta.network;

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
    //        public static String BaseUrl = "http://www.ec.dev.com/";
//    public static String BaseUrl = "http://172.16.0.222/";//本地
//    public static String BaseUrl = "http://www.myjy.biz/";//域名
    //    public static String BaseUrl = "http://120.26.218.68:1111/";
        public static String BaseUrl = "http://123.60.0.152:8088/";
    public static String User_SmsCode_Url = "user/smsCode";
    public static String User_Register_Url = "user/register";
    public static String User_Login_Url = "user/login";
    public static String User_UserQuestionList_Url = "securityQuestion/getUserQuestionList";
    public static String User_ForgetQuestionList_Url = "public/getUserQuestionList";
    public static String User_addQuestionBack_Url = "public/addQuestionBack";
    public static String User_GetPhone_Url = "public/getPhone";
    public static String User_checkPhone_Url = "public/checkPhone";
    public static String User_checkChangeCode_Url = "public/checkChangeCode";
    public static String User_forgetPassword_Url = "public/forgetPassword";
    public static String User_Friendlist_Url = "friend/list";



    private static SharedPreferences mShared = null;
    /**
     * 程序中可以同时存在多个SharedPreferences数据， 根据SharedPreferences的名称就可以拿到对象
     **/
    public final static String SHARED_MAIN = "main";
    /**
     * SharedPreferences中储存数据的Key名称
     **/
    public final static String KEY_NAME = "name";
    public final static String KEY_NUMBER = "number";

    /**
     * SharedPreferences中储存数据的路径
     **/
    public final static String DATA_URL = "/data/test/";
    public final static String SHARED_MAIN_XML = "test.xml";

    public static void saveShareData(String keyname, String key, Context context) {
        mShared = context.getSharedPreferences(SHARED_MAIN, Context.MODE_PRIVATE);
        Editor editor = mShared.edit();
        editor.putString(keyname, key);
        /**put完毕必需要commit()否则无法保存**/
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

    //存List<String>
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

    //取List<String>
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

    //删除一条订阅
    public static void removeUserOne(Context context, String KEY_NAME, String valen_Name) {
        SharedPreferences sp = context.getSharedPreferences(KEY_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        int size = sp.getInt("size", 0);
        List<String> baseArr = getUserList(context, "listselect");//
        for (int i = 0; i < size; i++) {//删除
            if (baseArr.get(i).equals(valen_Name)) {
                baseArr.remove(i);
//                size = size-1;//删除一个数据 size-1
                break;
            }
        }
        saveUserList(context, baseArr, "listselect");//保存
        editor.commit();
    }

    //添加一条订阅
    public static void addUserone(Context context, String KEY_NAME, String valen_Name) {
        SharedPreferences sp = context.getSharedPreferences(KEY_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        List<String> baseArr = getUserList(context, "listselect");
        baseArr.add(valen_Name);
        saveUserList(context, baseArr, "listselect");//保存
        editor.commit();
    }


    //保存map
    public static String SceneList2String(HashMap<Integer, Boolean> hashmap)
            throws IOException {
        // 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 然后将得到的字符数据装载到ObjectOutputStream
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                byteArrayOutputStream);
        // writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
        objectOutputStream.writeObject(hashmap);
        // 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
        String SceneListString = new String(Base64.encode(
                byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
        // 关闭objectOutputStream
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
     * true在今天之前flase在之后
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
                flag = true;//日期相同
            } else {
                flag = d.before(nowdate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    //trues今天  其他flase
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
                flag = false;//日期相同
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 根据用户传入的时间表示格式，返回当前时间的格式 如果是MMdd，注意字母y不能大写。
     *
     * @param sformat MMdd
     * @return
     */
    public static String getUserDate(String sformat) {
        String dateString = "2017-03-28";
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(sformat);//先转固定格式
            dateString = new SimpleDateFormat("MM/dd").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }

    /**
     * 保存实体类
     *
     * @param
     */
//    public static void putClass(Context context, String KEY_NAME, List<ProjectModel> model) {//需要实体类继承一个基类
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
     * 获取实体类
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

    //删除一条实体类
//    public static void removeGsonOne(Context context, String KEY_NAME, int valen_Id) {
//        SharedPreferences sp = context.getSharedPreferences(KEY_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        List<ProjectModel> listModel = getGosnClass(context, KEY_NAME, ProjectModel.class);
//        int size = sp.getInt("size", 0);
//        for (int i = 0; i < size; i++) {
//            if (listModel.get(i).getProjectId() == valen_Id) {
//                listModel.remove(i);//删除
//                break;
//            }
//        }
//        putClass(context, "moreModel", listModel);//保存
//        editor.commit();
//    }
//    //保存一条实体类
//    public static void saveGsonOne(Context context, String model_NAME, ProjectModel model) {
//        SharedPreferences sp = context.getSharedPreferences(model_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        List<ProjectModel> listModel = getGosnClass(context, model_NAME, ProjectModel.class);
//
//        listModel.add(model);
////        int size = sp.getInt("size", 0);
////        for (int i = 0; i < size; i++) {
////            if (listModel.get(i).getProjectId() == valen_Id) {
////                listModel.remove(i);//删除
////                break;
////            }
////        }
//        putClass(context, model_NAME, listModel);//保存
//        editor.commit();
//    }

    public static void clearGson(Context context, String KEY_NAME) {
        SharedPreferences sp = context.getSharedPreferences(KEY_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }


}
