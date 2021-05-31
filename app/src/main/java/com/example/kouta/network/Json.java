package com.moying.energyring.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Json {
    /**
     * 参数说明: 1.webContent 获取的网页封装的json格式数据 2.key 以数组形式组成的json的键名称
     */
    public ArrayList<String> getJSONList(String webContent, String string) {
//		int size = string.length;
        ArrayList<String> dateArr = new ArrayList<String>();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(webContent);
            Iterator<?> it = jsonObject.keys();
            String objvalue = "";
            String objname = "";
            while (it.hasNext()) {////遍历JSONObject
                objname = (String) it.next().toString();
                objvalue = jsonObject.getString(objname);
                if (objvalue.equals("0")) {
                    dateArr.add(objname);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            dateArr = null;
        }
        return dateArr;
    }

    public List<String> getJSONnomnameList(String webContent) {
//		int size = string.length;
        List<String> dateArr = new ArrayList<String>();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(webContent);
            Iterator<?> it = jsonObject.keys();
            String objvalue = "";
            String objname = "";
            while (it.hasNext()) {////遍历JSONObject
                objname = (String) it.next().toString();
                objvalue = jsonObject.getString(objname);
                dateArr.add(objname);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            dateArr = null;
        }
        return dateArr;
    }


    // 解析单一的json封装,并返回字符串数组

    /**
     * 参数说明: 1.webContent 获取的网页封装的json格式数据 2.key 以数组形式组成的json的键名称
     */
    public String[] getJSON(String webContent, String[] key) {
        int size = key.length;
        String[] s = new String[size];
        try {
            JSONObject jsonObject = new JSONObject(webContent);
            for (int j = 0; j < size; j++) {
                if (jsonObject.isNull(key[j]) == false){
                    s[j] = jsonObject.getString(key[j]);
                }else{
                    s[j] = "";
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return s;
    }
    /**
     * 参数说明: 1.webContent 获取的网页封装的json格式数据 2.key 以数组形式组成的json的键名称 3.jsonName
     * 封装json数组数据的json名称
     */

//	private ArrayList<HashMap<String, Object>> list;
    public List<Map<String, Object>> getnotitleJSONArray(List<Map<String, Object>> list, String webContent, String[] key) {
        JSONArray jsonObject;
        try {
            jsonObject = new JSONArray(webContent);
//			list = new ArrayList<HashMap<String, Object>>();
            for (int i = 0; i < jsonObject.length(); i++) {
                JSONObject jsonObject2 = (JSONObject) jsonObject.opt(i);
                HashMap<String, Object> map = new HashMap<String, Object>();
                for (int j = 0; j < key.length; j++) {
                    try {
                        map.put(key[j], jsonObject2.getString(key[j]));
                    } catch (Exception e) {
                        // TODO: handle exception
                        map.put(key[j], "");
                    }
                }
                list.add(map);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

    /**
     * json解析无name值无key列表
     **/
    public List<String> getJSONnonamekeytitle(String webContent) {
        List<String> list = new ArrayList<>();
        JSONArray jsonObject;
        try {
            jsonObject = new JSONArray(webContent);
            for (int i = 0; i < jsonObject.length(); i++) {
                String map =jsonObject.opt(i).toString();
                list.add(map);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

    /**
     * @param response
     * @param tag
     * @return
     */
    public String getReturnValue(String response, String tag) {
        String value = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (!jsonObject.isNull(tag)) {
                value = jsonObject.getString(tag);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (value == null) {
            return "";
        } else {
            return value;
        }
    }


    public List<Map<String, Object>> getJSONArraytitle(List<Map<String, Object>> list, String webContent, String[] key, String jsonName) {
        JSONArray jsonObject;
        try {
            jsonObject = new JSONObject(webContent).getJSONArray(jsonName);
//			list = new ArrayList<HashMap<String, Object>>();
            for (int i = 0; i < jsonObject.length(); i++) {
                JSONObject jsonObject2 = (JSONObject) jsonObject.opt(i);
                HashMap<String, Object> map = new HashMap<String, Object>();
                for (int j = 0; j < key.length; j++) {
                    try {
                        map.put(key[j], jsonObject2.getString(key[j]));
                    } catch (Exception e) {
                        // TODO: handle exception
                        map.put(key[j], "");
                    }
                }
                list.add(map);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
//			list = null;
        }
        return list;
    }

    public List<String> getList(List<String> list, String resultString, String keyname){
        try {
            JSONArray jsonArr = new JSONArray(resultString);
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArr.opt(i);

                list.add(jsonObject.getString(keyname));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }



}