package com.xunda.mo.staticdata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BeanUtils1 {
    public static String checkFieldValueNull(Object bean) {
        StringBuffer sb = new StringBuffer();
        sb.append("");
        if (bean == null) {
            return sb.toString();
        }
        sb.append(bean.getClass().getName() + "  以下属性为空 ");
        Class<?> cls = bean.getClass();
        Method[] methods = cls.getDeclaredMethods();
        Field[] fields = cls.getDeclaredFields();

        for (Field field : fields) {
            try {
                String fieldGetName = parGetName(field.getName());
                if (!checkGetMet(methods, fieldGetName)) {
                    continue;
                }
                Method fieldGetMet = cls.getMethod(fieldGetName, new Class[]{});
                Object fieldVal = fieldGetMet.invoke(bean, new Object[]{});
                if (fieldVal != null) {
                    if ("".equals(fieldVal)) {
                        sb.append(fieldGetMet.getName().toString() + ",");
                    }
                } else {
                    sb.append(fieldGetMet.getName().toString() + ",");
                }
            } catch (Exception e) {
                continue;
            }
        }
        return sb.toString();
    }


    /**
     * 拼接某属性的 get方法
     *
     * @param fieldName
     * @return String
     */
    public static String parGetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == '_') {
            startIndex = 1;
        }
        return "get"
                + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
                + fieldName.substring(startIndex + 1);
    }

    /**
     * 判断是否存在某属性的 get方法
     *
     * @param methods
     * @param fieldGetMet
     * @return boolean
     */
    public static boolean checkGetMet(Method[] methods, String fieldGetMet) {
        for (Method met : methods) {
            if (fieldGetMet.equals(met.getName())) {
                return true;
            }
        }
        return false;
    }


    public static boolean isEmpty(final Object obj) {
        if (obj == null) {
            return true;
        }
        //见以下各种判断
        return false;
    }


}
