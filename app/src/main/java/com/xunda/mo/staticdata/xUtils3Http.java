package com.xunda.mo.staticdata;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.main.login.MainLogin_Register;
import com.xunda.mo.model.baseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.List;
import java.util.Map;

public class xUtils3Http {
    private static final String BASE_URL = "http://119.3.23.16:8088/";
//    public static String BASE_URL = "https://xd.ahxunda.com/";

    public static void get(Context mContext, String url, Map<String, Object> parms, final GetDataCallback callback) {
        RequestParams params = new RequestParams(BASE_URL + url);
        if (parms != null) {
            for (String key : parms.keySet()) {
                params.addParameter(key, parms.get(key));
            }
        }
        MyInfo myInfo = new MyInfo(mContext);
        if (!TextUtils.isEmpty(myInfo.getUserInfo().getToken())) {
            params.setHeader("Authorization", myInfo.getUserInfo().getToken());
        }
        params.setAsJsonContent(true);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    baseModel model = new Gson().fromJson(result, baseModel.class);
                    if (model.getCode() == 201) {
                        Intent intent = new Intent(mContext, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    } else if (model.getCode() == 200) {
                        if (callback != null) {
                            callback.success(result);
                        }
                    } else {
                        Toast.makeText(mContext, model.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (callback != null) {
                    callback.failed();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    public static void post(Context mContext, String url, Map<String, Object> parms, final GetDataCallback callback) {
        RequestParams params = new RequestParams(BASE_URL + url);
        JSONObject obj = new JSONObject();
        if (parms != null) {
            try {
                for (String key : parms.keySet()) {
                    obj.put(key, parms.get(key));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
//        String aesKey = AESEncrypt.getAesKey();
//        String contentStr = AESEncrypt.encrypt(obj.toString(), aesKey);
//        String rsaKey = RsaEncodeMethod.rsaEncode(aesKey);

        String rsaCode = RsaEncodeMethod.rsaEncode(obj.toString());
        MyInfo myInfo = new MyInfo(mContext);
        if (!TextUtils.isEmpty(myInfo.getUserInfo().getToken())) {
            params.setHeader("Authorization", myInfo.getUserInfo().getToken());
        }
        params.setBodyContent(rsaCode);
//        params.addBodyParameter("key", rsaKey);
//        params.addBodyParameter("content", contentStr);
        params.setAsJsonContent(true);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    baseModel model = new Gson().fromJson(result, baseModel.class);
                    if (model.getCode() == 201) {
                        Intent intent = new Intent(mContext, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    } else if (model.getCode() == 200) {
                        if (callback != null) {
                            callback.success(result);
                        }
                    } else {
                        callback.failed();
                        Toast.makeText(mContext, model.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (callback != null) {
                    callback.failed();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    public static void postRsa(Context mContext, String url, Map<String, Object> parms, final GetDataCallback callback) {
        RequestParams params = new RequestParams(BASE_URL + url);
        JSONObject obj = new JSONObject();
        if (parms != null) {
            try {
                for (String key : parms.keySet()) {
                    obj.put(key, parms.get(key));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String rsaCode = RsaEncodeMethod.rsaEncode(obj.toString());
        MyInfo myInfo = new MyInfo(mContext);
        params.setHeader("Authorization", myInfo.getUserInfo().getToken());
        params.setAsJsonContent(true);
        params.setBodyContent(rsaCode);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    baseModel model = new Gson().fromJson(result, baseModel.class);
                    if (model.getCode() == 201) {
                        Intent intent = new Intent(mContext, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    } else if (model.getCode() == 200) {
                        if (callback != null) {
                            callback.success(result);
                        }
                    } else {
                        callback.failed();
                        Toast.makeText(mContext, model.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (callback != null) {
                    callback.failed();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }


    public static void uploadFile(Context mContext, List<String> path, Map<String, Object> map, final GetDataCallback callback) {
        RequestParams params = new RequestParams(BASE_URL + "upload");
        params.setMultipart(true);
        for (String key : map.keySet()) {
            params.addBodyParameter(key, map.get(key).toString());
        }
        for (int i = 0; i < path.size(); i++) {
            params.addBodyParameter("uploadFile" + i, new File(path.get(i)));
        }
        MyInfo myInfo = new MyInfo(mContext);
        params.setHeader("Authorization", myInfo.getUserInfo().getToken());
        params.setAsJsonContent(true);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    baseModel model = new Gson().fromJson(result, baseModel.class);
                    if (model.getCode() == 201) {
                        Intent intent = new Intent(mContext, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    } else if (model.getCode() == 200) {
                        if (callback != null) {
                            callback.success(result);
                        }
                    } else {
                        Toast.makeText(mContext, model.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (callback != null) {
                    callback.failed();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }


    public interface GetDataCallback {
        void success(String result);

        void failed(String... args);
    }
}
