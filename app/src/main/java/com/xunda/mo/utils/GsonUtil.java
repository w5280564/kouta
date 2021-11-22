package com.xunda.mo.utils;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JSON转换工具类 使用前调用getInstance方法
 * 
 * @author M.c
 * 
 * @date 2014-11-13
 * @toJson 将对象转换成json字符串
 * @getValue 在json字符串中，根据key值找到value
 * @json2Map 将json格式转换成map对象
 * @json2Bean 将json转换成bean对象
 * @json2List 将json格式转换成List对象
 * @Obj2Map obj 转为 map
 * 
 */
public class GsonUtil {

	private Gson gson;

	private GsonUtil() {
		gson = new Gson();
	}

	private static class gsonUtilHolder {
		private static GsonUtil instance = new GsonUtil();
	}

	/**
	 * 使用方法前调用getInstance,获得gsonUtil实例
	 *
	 * @return
	 */
	public static GsonUtil getInstance() {
		return gsonUtilHolder.instance;
	}

	/**
	 * 将对象转换成Base64字符串
	 * @param obj
	 * @return
	 */
	public String toBase64Json(Object obj) {
		if (obj == null) {
			return "{}";
		}

		String jsonObj = gson.toJson(obj);
		if(!StringUtil.isBlank(jsonObj)){
			return Base64.encodeToString(jsonObj.getBytes(),Base64.DEFAULT);
		}else{
			return "{}";
		}
	}


	/**将对象转换成字符串
	 * @param obj
	 * @return
	 */
	public String toJson(Object obj) {
		if (obj == null) {
			return "{}";
		}
		return gson.toJson(obj);
	}



	/**
	 * 在json字符串中，根据key值找到value
	 *
	 * @param data
	 * @param key
	 * @return
	 */

	public String getValue(String data, String key) {
			try {
				JSONObject jsonObject = new JSONObject(data);
				return jsonObject.getString(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		return null;
	}



	public Object getObjValue(String data, String key) {
		try {
			JSONObject jsonObject = new JSONObject(data);
			return jsonObject.get(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}


    public void setMethod(Object method, Object value ,Object thisObj)
    {
      Class c;
      try
      {
        c = Class.forName(thisObj.getClass().getName());
        String met = (String) method;
        met = met.trim();
        if (!met.substring(0, 1).equals(met.substring(0, 1).toUpperCase()))
        {
          met = met.substring(0, 1).toUpperCase() + met.substring(1);
        }
        if (!String.valueOf(method).startsWith("set"))
        {
          met = "set" + met;
        }
        Class types[] = new Class[1];
        types[0] = Class.forName("java.lang.String");
        Method m = c.getMethod(met, types);
        m.invoke(thisObj, value);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }


    public void setValue(Map map,Object thisObj)
    {
      Set set = map.keySet();
      Iterator iterator = set.iterator();
      while (iterator.hasNext())
      {
        Object obj = iterator.next();
        Object val = map.get(obj);
        setMethod(obj, val, thisObj);
      }
    }


	/**
	 * 将json格式转换成map对象
	 *
	 * @param json
	 * @return
	 */
	public Map<String, Object> json2Map(String json) {
		Map<String, Object> objMap = null;
		if (gson != null) {
			Type type = new TypeToken<Map<String, Object>>() {
			}.getType();
			objMap = gson.fromJson(json, type);
		}
		if (objMap == null) {
			return null;
		}
		return objMap;
	}

	/**
	 * 将json转换成bean对象
	 *
	 * @param <T>
	 * @param json
	 * @param clazz
	 * @return
	 */
	public <T> T json2Bean(String json, Class<T> clazz) {
		T obj = null;
		if (gson != null) {
			try{
				obj = gson.fromJson(json, clazz);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return obj;
	}


	/**
	 * 将json格式转换成List对象
	 *
	 */

	public <T> T json2List(String json, Type type) {
		try{
			if (gson != null) {
				return gson.fromJson(json, type);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * obj 转为 map
	 *
	 * @param obj
	 *            需要转的对象
	 * @return
	 */
	public Map<String, Object> Obj2Map(Object obj) {
		if (obj != null) {
			return json2Map(toJson(obj));
		}
		return null;
	}


	/**
	 * Google Gson
	 * @param stringValue
	 * @return
	 */
	public  boolean stringIsJsonFormat(String stringValue) {
		try {
			if (gson != null) {
				gson.fromJson(stringValue, Object.class);
				return true;
			}else{
				return false;
			}
		} catch (JsonSyntaxException ex) {
			return false;
		}
	}



	/**
	 * 解析没有key的数组json字符串
	 *
	 * @param json
	 * @return
	 */
	public List<String> parseDataNoKey(String json) {//Gson 解析
		List<String> list = new ArrayList<>();
		try {
			JSONArray data = new JSONArray(json);
			for (int i = 0; i < data.length(); i++) {
				String obj = data.get(i).toString();
				list.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
