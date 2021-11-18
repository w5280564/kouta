package com.xunda.mo.staticdata.Gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

import javax.xml.transform.Result;

public class GsonHelper {
    private static GsonHelper instance = null;
    private static final Object mLock = new Object();
    private static Gson mGson = null;

    public static GsonHelper getInstance() {
        synchronized (mLock) {
            if (instance == null) {
                instance = new GsonHelper();
                mGson = new GsonBuilder().serializeNulls().create();
            }
            return instance;
        }
    }

    private GsonHelper() {
    }

//    BaseEntity<ListEntity<GoodsItem>> entity = mGsonHelper.fromJsonArray(response,GoodsItem.class);

    // 处理 data 为 null 的情况
    public Result fromJsonNull(String jsonString, Class<BaseEntity> clazz) {
        return mGson.fromJson(jsonString, (Type) clazz);
    }

    // 处理 data 为 object 的情况
    public <T> BaseEntity<T> fromJsonObject(String jsonStr, Class<T> clazz) {
        Type type = new ParameterizedTypeImpl(BaseEntity.class, new Class[]{clazz});
        return mGson.fromJson(jsonStr, type);
    }

    // 处理 data 为 array 的情况
    public <T> BaseEntity<ListEntity<T>> fromJsonArray(String jsonStr, Class<T> clazz) {
        // 生成ListEntity<T> 中的 Type
        Type listType = new ParameterizedTypeImpl(ListEntity.class, new Class[]{clazz});
        // 根据ListEntity<T>生成的，再生出完整的BaseEntity<ListEntity<T>>
        Type type = new ParameterizedTypeImpl(BaseEntity.class, new Type[]{listType});
        return mGson.fromJson(jsonStr, type);
    }
}
