package com.xunda.mo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author ouyang
 * @isBlank 判断字符串是否为空
 * @isChinese 判断是否为中文
 * @countStringLength 判断字符串是否超出长度
 * @isNum 判断是否为纯数字
 * @DoubleToAmountString double转为字符串, 保留小数位
 * @removeAllChar 去掉字符串中某一字符
 * @getInitialAlphaEn 获取英文首字母 并大写显示,不为英文字母时,返回"#"
 * @getEditText 获取非空edittext
 * @getMd5Value 字符串MD5加密
 */
public class StringUtil {




    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    /**
     * 根据type值获取单位名称
     * @param unit_type 单位（1-双 2-件 3-个 4-斤 5-套）
     * @return
     */
    public static String getStringUnit(String unit_type) {
        String value = "个";
        if (!isBlank(unit_type)) {
            if ("1".equals(unit_type)) {
                return  value = "双";
            } else if ("2".equals(unit_type)) {
                value = "件";
            } else if ("3".equals(unit_type)) {
                value = "个";
            } else if ("4".equals(unit_type)) {
                value = "斤";
            } else if ("5".equals(unit_type)) {
                value = "套";
            }
        } else {
            value = "个";
        }
        return value;
    }


    /**
     * 26英文字母字符串
     */
    public static String[] ENGLIST_LETTER = {"A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
            "T", "U", "V", "W", "X", "Y", "Z"};

    /**
     * 判断字符串是否为空或者空字符串 如果字符串是空或空字符串则返回true，否则返回false。也可以使用Android自带的TextUtil
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        if (str == null || "".equals(str)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断string是否包含字母和数字
     * @param str
     * @return
     */

    public static boolean isLetterDigit(String str){
        boolean isDigit = false;//定义一个boolean值，用来表示是否包含数字
        boolean isLetter = false;//定义一个boolean值，用来表示是否包含字母
        for(int i=0 ; i<str.length() ; i++) {
            if (Character.isDigit(str.charAt(i))) { //用char包装类中的判断数字的方法判断每一个字符
                isDigit = true;
            }
            if (Character.isLetter(str.charAt(i))) { //用char包装类中的判断字母的方法判断每一个字符
                isLetter = true;
            }
        }
        String regex = "^[a-zA-Z0-9]+$";
        boolean isRight = isDigit && isLetter&&str.matches(regex);
        return isRight;
    }

    /**
     * 获取字符串字段的值，已做非空判断
     * @param value
     * @return
     */
    public static String getStringValue(String value) {
        if (isBlank(value)) {
            return "";
        } else {
            return value;
        }
    }


    /**
     * 获取非空edittext
     *
     * @param
     * @return
     */
    public static String getEditText(EditText et_text) {
        if (null == et_text || et_text.getText().toString().trim().equals("")) {
            return "";
        }
        return et_text.getText().toString().trim();
    }


    /**
     * 获取非空TextView
     *
     * @param
     * @return
     */
    public static String getTextViewText(TextView tv_text) {
        if (null == tv_text || tv_text.getText().toString().trim().equals("")) {
            return "";
        }
        return tv_text.getText().toString().trim();
    }




    public static String deleteSomeString(String parent, String son) {//s是需要删除某个子串的字符串s1是需要删除的子串
        int postion = parent.indexOf(son);
        int sonLength = son.length();
        int parentLength = parent.length();
        String newString = parent.substring(0, postion) + parent.substring(postion + sonLength, parentLength);
        return newString;//返回已经删除好的字符串
    }


    /**
     * 输入框小写转大写
     *
     * @return
     */
    public static void smallToBig(final EditText editText) {// 小写转大写
        editText.addTextChangedListener(new TextWatcher() {

            int index = 0;

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editText.removeTextChangedListener(this);// 解除文字改变事件
                index = editText.getSelectionStart();// 获取光标位置
                editText.setText(s.toString().toUpperCase());// 转换
                editText.setSelection(index);// 重新设置光标位置
                editText.addTextChangedListener(this);// 重新绑定事件
            }
        });
    }




    /**
     * 判断是否是中文
     *
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }


    /**
     * 邮箱验证_新
     *
     * author zhangliangliang
     *
     * @param strEmail
     * @return
     */
    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        if (TextUtils.isEmpty(strPattern)) {
            return false;
        } else {
            return strEmail.matches(strPattern);
        }
    }

    //验证邮政编码
    public static boolean checkPost(String post) {
        if (post.matches("[1-9]\\d{5}(?!\\d)")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检测String是否全是中文
     *
     * @param name
     * @return
     */

    public static boolean checkNameChese(String name) {

        boolean res = true;

        char[] cTemp = name.toCharArray();

        for (int i = 0; i < name.length(); i++) {

            if (!isChinese(cTemp[i])) {

                res = false;

                break;

            }

        }

        return res;

    }

    /**
     * 对中文进行URL编码
     *
     * @return
     */
    public static String getURLEncoderString(String string) {
        String stringUTF8 = "";
        try {
            stringUTF8 = URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return stringUTF8;
    }


    /**
     * 对URL进行中文解码
     *
     * @return
     */
    public static String getURLDecoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 截取字符串里面的数字
     *
     * @return
     */
    public static String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    // 截取非数字
    public static String splitNotNumber(String content) {
        Pattern pattern = Pattern.compile("\\D+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    /**
     * 检测String是否包含中文
     *
     * @return
     */
    public static boolean containsChinese(String s) {
        if (null == s || "".equals(s.trim()))
            return false;
        for (int i = 0; i < s.length(); i++) {
            if (isChinese(s.charAt(i)))
                return true;
        }
        return false;
    }

    /**
     * 过滤中文
     *
     * @param name
     * @return
     */
    public static String filterChinese(String name) {
        String reg = "[\u4e00-\u9fa5]";
        Pattern pat = Pattern.compile(reg);
        Matcher mat = pat.matcher(name);
        String repickStr = mat.replaceAll("");
        return repickStr;

    }


    /**
     * 截取中文
     *
     * @param name
     * @return
     */
    public static String subStringChinese(String name) {
        String reg = "[^\u4e00-\u9fa5]";
        name = name.replaceAll(reg, " ").trim();
        return name;
    }





    /**
     * 将double转换为字符串，保留小数点位数
     *
     * @param doubleNum 需要解析的double
     * @return
     */
    public static String DoubleToAmountString(Double doubleNum) {
        DecimalFormat df = new DecimalFormat("0.00");
        String str = df.format(doubleNum);
        return str;
    }




    public static String getUserNickName(String extInfo,String nickname) {
        try {
            if (!StringUtil.isBlank(extInfo)) {
                JSONObject jsonObject = new JSONObject(extInfo);//用户资料扩展属性
                String remarkName = jsonObject.getString("remarkName");
                String name = TextUtils.isEmpty(remarkName) ? nickname : remarkName;
                return name;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return nickname;
    }



    /**
     * 提取英文的首字母，非英文字母用#代替
     *
     * @param str
     * @return
     */
    public static String getInitialAlphaEn(String str) {
        if (str == null) {
            return "#";
        }

        if (str.trim().length() == 0) {
            return "#";
        }

        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式，判断首字母是否是26字母
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase(Locale.getDefault()); // 大写输出
        } else {
            return "#";
        }
    }

    /**
     * 去除String中的某一个字符
     *
     * @param orgStr
     * @param splitStr 要去除的字符串
     * @return
     */
    public static String removeAllChar(String orgStr, String splitStr) {
        String[] strArray = orgStr.split(splitStr);
        String res = "";
        for (String tmp : strArray) {
            res += tmp;
        }
        return res;
    }


    /**
     * 手机号正则表达式
     * "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
     *
     * @param phonenumber
     * @return
     */
    public static boolean isPhoneNumber(String phonenumber) {
        String phoneRegex = "[1][23456789]\\d{9}";
        if (StringUtil.isBlank(phonenumber))
            return false;
        else
            return phonenumber.matches(phoneRegex);
    }


    /**
     * 判断是否是为身份证号
     */
    public static boolean isIdcardNumber(String idcardNumber) {

        String isIdcardRegex = "^(\\d{6})(18|19|20)?(\\d{2})([01]\\d)([0123]\\d)(\\d{3})(\\d|X|x)?$";
        if (TextUtils.isEmpty(idcardNumber))
            return false;
        else
            return idcardNumber.matches(isIdcardRegex);
    }



    /**
     * MD5加密 32位小写
     *
     * @param sSecret
     * @return
     */
    public static String getMd5Value(String sSecret) {
        try {
            MessageDigest bmd5 = MessageDigest.getInstance("MD5");
            bmd5.update(sSecret.getBytes());
            int i;
            StringBuffer buf = new StringBuffer();
            byte[] b = bmd5.digest();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static Bitmap stringToBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = android.util.Base64.decode(string, android.util.Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * 人名币符号+大写数字
     * @param times 倍数（数字字体的大小/人民币符号字体的大小）
     * @param tv_price 文本控件
     */
    public static void setPriceTextShow(String temp_price,float times,TextView tv_price) {
        if (!StringUtil.isBlank(temp_price)) {
            String price_value = String.format("￥%s", StringUtil.DoubleToAmountString(Double.parseDouble(temp_price)));
            tv_price.setText(price_value);
            Spannable span = new SpannableString(price_value);
            span.setSpan(new RelativeSizeSpan(times), 1, price_value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_price.setText(span);//把字体放大size
        }
    }




    /**
     * 数组转化成逗号分隔的字符串
     * @param stringList
     * @return
     */
    public static String listToString(List<String> stringList) {
        if (ListUtils.isEmpty(stringList)) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String string : stringList) {
            if (flag) {
                result.append(",");
            } else {
                flag = true;
            }
            result.append(string);
        }
        return result.toString();
    }


    /**
     * 数组转化成加号分隔的字符串
     * @param stringList
     * @return
     */
    public static String listToString2(List<String> stringList) {
        if (ListUtils.isEmpty(stringList)) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String string : stringList) {
            if (flag) {
                result.append("+");
            } else {
                flag = true;
            }
            result.append(string);
        }
        return result.toString();
    }

    /**
     * 数组转化成加号分隔的字符串
     * @param stringList
     * @return
     */
    public static String listToString3(List<String> stringList) {
        if (ListUtils.isEmpty(stringList)) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String string : stringList) {
            if (flag) {
                result.append(" ");
            } else {
                flag = true;
            }
            result.append(string);
        }
        return result.toString();
    }


    /**
     * 逗号分隔的字符串转化成数组
     * @param string_list
     * @return
     */
    public static List<String>  stringToList(String string_list) {
        List<String> bottom_image_List = new ArrayList<>();
        if (string_list.contains(",")) {
            List<String> tempList = Arrays.asList(string_list.split(","));
            if (!ListUtils.isEmpty(tempList)) {
                bottom_image_List.addAll(tempList);
            }
        }else{
            bottom_image_List.add(string_list);
        }
        return bottom_image_List;
    }



    public static String listToStringByLine(List<String> stringList) {
        if (ListUtils.isEmpty(stringList)) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String string : stringList) {
            if (flag) {
                result.append("/");
            } else {
                flag = true;
            }
            result.append(string);
        }
        return result.toString();
    }


    /**
     * 功能：判断字符串是否为数字
     *
     * @param str
     * @return
     */
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 将文件转成base64字符串(用于发送语音文件)
     *
     * @param path
     * @return
     */
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return android.util.Base64.encodeToString(buffer, android.util.Base64.DEFAULT);
    }


    /**
     * 将base64编码后的字符串转成文件
     *
     * @param base64Code
     */
    public static String decoderBase64File(String base64Code) {
        String savePath = "";
        String saveDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Yun/Sounds/";
        File dir = new File(saveDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        savePath = saveDir + getRandomFileName();
        byte[] buffer = android.util.Base64.decode(base64Code, android.util.Base64.DEFAULT);
        try {
            FileOutputStream out = new FileOutputStream(savePath);
            out.write(buffer);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return savePath;
    }

    public static String getRandomFileName() {
        String rel = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());
        rel = formatter.format(curDate);
        rel = rel + new Random().nextInt(1000);
        return rel + ".amr";
    }


    public static void loadStringInWebView(WebView mWebView, String content) {
        mWebView.loadData(getHtmlData(content), "text/html; charset=UTF-8", null);
    }

    public static void loadStringInWebView2(WebView mWebView, String content) {//展会服务用到的富文本
        mWebView.getSettings().setUseWideViewPort(true);//关键点
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.loadData(getHtmlData2(content), "text/html; charset=UTF-8", null);
    }

    private static String getHtmlData2(String bodyHTML) {//展会服务用到的富文本
        String body = "<p style=\"white-space: normal; text-align: center;\"><img src=\"http://59.37.4.70:881/upload/2020/04/20/a65fa7a134a63005672930048096596f.png\" title=\"icon_jqqd.png\" alt=\"icon_jqqd.png\" width=\"180\" height=\"140\"/><br/><br/><span style=\"font-size:16px; color:#666\">敬请期待</span></p>";
        String head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                "</head>";
        return "<html>" +head+ "<body>" + bodyHTML + "</body></html>";
    }

    private static String getHtmlData(String bodyHTML) {
        String head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                "<style>img{max-width: 100%; width:auto; height:auto;}</style>" +
                "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }


}
