package com.xunda.mo.staticdata;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.network.saveFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("SimpleDateFormat")
public class StaticData {

    public static Object viewTou;

    /**
     * 验证邮箱格式
     *
     * @param email
     * @return true 是格式不正确
     */
    public static boolean isEmail(String email) {
        String pattern_email = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        if (!email.matches(pattern_email)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isPhone(String phone) {
        /*截止到2015年
         * 移动：134、135、136、137、138、139、150、151、152、157(TD)、158、159、182、183、184、187、188、147、188
         * 联通：130、131、132、152、155、156、185、186、145、176 电信：133、153、180、189、（1349卫通）
         * 总结起来就是第一位必定为1，第二位必须为3、4、5、7、8可改为 [0-9]，其他位置的可以为0-9
         */
        String telRegex = "[1][0-9]\\d{9}";// "[1]"代表第1位为数字1，"[34578]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(phone))
            return false;
        else
            return phone.matches(telRegex);
    }

    /**
     * 验证昵称
     *
     * @param context
     * @param str
     * @return
     */
    @SuppressLint("ShowToast")
    public static boolean verifyNickname(Context context, String str) {
        String nickname = str;
        if (nickname == null || nickname.length() == 0) {
            Toast.makeText(context, "不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        int len = 0;
        char[] nickchar = nickname.toCharArray();
        for (int i = 0; i < nickchar.length; i++) {
            if (isChinese(nickchar[i])) {
                len += 2;
            } else {
                len += 1;
            }
        }
        if (len < 4 || len > 15) {
            Toast.makeText(context, "正确的昵称应该为\n1、4-15个字符\n2、2-7个汉字\n3、不能是邮箱和手机号", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //判断是中文
    private static boolean isChinese(char c) {
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
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * String转意URL格式
     *
     * @param paramString
     * @return
     */
    public static String toURLEncoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            return "";
        }
        String str = "";
        try {
            str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        } catch (Exception localException) {
        }

        return str;
    }

    /**
     * 保存图片 每个项目要修改存入文件名
     *
     * @param
     * @param
     * @param
     * @throws IOException
     */
    @SuppressLint("ShowToast")
    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Cpp");
//		File appDir = new File(context.getFilesDir().getAbsoluteFile(), "Cpp");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 其次把文件插入到系统图库
        if (bmp != null) {
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
    }

    /**
     * 将字符串转成二维码图片
     *
     * @param str
     * @return
     * @throws WriterException
     */
//	public static Bitmap Create2DCode(String str) {
//		// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
//		BitMatrix matrix = null;
//		try {
//			matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE,
//					(int) (500 * scale), (int) (500 * scale));
//		} catch (WriterException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		int width = matrix.getWidth();
//		int height = matrix.getHeight();
//		// 二维矩阵转为一维像素数组,也就是一直横着排了
//		int[] pixels = new int[width * height];
//		for (int y = 0; y < height; y++) {
//			for (int x = 0; x < width; x++) {
//				if (matrix.get(x, y)) {
//					pixels[y * width + x] = 0xff000000;// 黑色
//				} else {
//					pixels[y * width + x] = 0xffffffff;// 白色
//				}
//			}
//		}
//		Bitmap bitmap = Bitmap.createBitmap(width, height,
//				Config.ARGB_8888);
//		// 通过像素数组生成bitmap,具体参考api
//		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//		return bitmap;
//	}

    /**
     * 截取数字
     *
     * @param content
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

    /**
     * 截取非数字
     *
     * @param content
     * @return String汉字
     */
    public static String splitNotNumber(String content) {
        Pattern pattern = Pattern.compile("\\D+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    /**
     * 得到本地路径名
     *
     * @param filename
     * @param mBitmap
     * @return
     */
    public static String createDir(String filename, Bitmap mBitmap) {
        File sdcardDir = Environment.getExternalStorageDirectory();
        // 得到一个路径，内容是sdcard的文件夹路径和名字
        String path = sdcardDir.getPath() + "/ME";
        File path1 = new File(path);
        if (!path1.exists())
            // 若不存在，创建目录，可以在应用启动的时候创建
            path1.mkdirs();
        path = path + "/" + filename;
        File picf = new File(path);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(picf);
            mBitmap.compress(CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            return path;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "null";
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "null";
        }
    }

    /**
     * 截取图片宽不变高一半
     *
     * @param bit
     * @return
     */
    public static Bitmap removeBit(Bitmap bit) {
        Bitmap picNewRes = null;
        // 获取需要的图片的宽高
        int picWidth = bit.getWidth();
        int picHeight = bit.getHeight() / 2;
        // 从源图片中截取需要的部分
        picNewRes = Bitmap.createBitmap(bit, 0, picHeight / 2, picWidth, picHeight);
        return picNewRes;
    }

    /**
     * 截取图片
     *
     * @param bitmap     原图
     * @param edgeLength 希望得到的正方形部分的边长
     * @return 缩放截取正中部分后的位图。
     */
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength) {
        if (null == bitmap || edgeLength <= 0) {
            return null;
        }
        Bitmap result = null;
        result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();

        if (widthOrg > edgeLength && heightOrg > edgeLength) {
            // 压缩到一个最小长度是edgeLength的bitmap
            int longerEdge = (int) (edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));

            int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
            int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
            Bitmap scaledBitmap;
            try {
                //src:源图的地址 dstWidth：新图片的宽dstHeight：新图片的高filter：是否过滤
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            } catch (Exception e) {
                return null;
            }
            // 从图中截取正中间的正方形部分。
            int xTopLeft = (scaledWidth - edgeLength) / 2;
            int yTopLeft = (scaledHeight - edgeLength) / 2;
            try {
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
                scaledBitmap.recycle();
                scaledBitmap = null;
            } catch (Exception e) {
                return null;
            }
        }

        return result;
    }

    /**
     * 判断是否有网true有网
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 是否输入的是空格
     *
     * @param
     */
    public static boolean Stringspace(String space) {
        if (space.matches("\\s*")) // "/s"匹配任意的空白符，包括空格，制表符(Tab)，换行符，中文全角空格
            return true;
        else
            return false;
    }


    /**
     * 验证URL正则
     *
     * @param httpstr
     * @return
     */
    public static boolean StringHttp(String httpstr) {
//		Pattern pattern=Pattern.compile("(http://|ftp://|https://|www){0,1}[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr)[^\u4e00-\u9fa5\\s]*");
        Pattern pattern = Pattern.compile("^((http|https)://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$");
        Matcher matcher = pattern.matcher(httpstr);
        if (matcher.find())
            return true;//是网站
        else
            return false;
    }


    /**
     * ArrayList排序
     *
     * @param baseArr
     * @param comtxt
     * @return
     */
    public static ArrayList<HashMap<String, Object>> ComparatorArr(ArrayList<HashMap<String, Object>> baseArr, final String comtxt) {
        Collections.sort(baseArr, new Comparator<HashMap<String, Object>>() {
            @Override
            public int compare(HashMap<String, Object> arg0, HashMap<String, Object> arg1) {
                return (arg1.get(comtxt).toString()).compareTo(arg0.get(comtxt).toString());
            }
        });
        return baseArr;
    }

    /**
     * List按Date时间排序
     *
     * @param baseArr
     * @param str
     * @return
     */
    public static List<HashMap<String, Object>> timeArr(List<HashMap<String, Object>> baseArr, final String str) {
        Collections.sort(baseArr, new Comparator<HashMap<String, Object>>() {
            @Override
            public int compare(HashMap<String, Object> arg0, HashMap<String, Object> arg1) {
                Date a = null;
                Date b = null;
                String retime = "";
                try {
                    String time1 = arg0.get(str).toString().replace("T", " ");
                    String time2 = arg1.get(str).toString().replace("T", " ");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                    a = sdf.parse(time1);
                    b = sdf.parse(time2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return b.compareTo(a);
            }
        });
        return baseArr;
    }


    /**
     * 格式20151023141514为2015.10.23 HH:mm
     *
     * @param datestr
     * @return 2015-10-23T14:15:14
     */
    public static String Datestyle(String datestr) {
        String dateString = null;
        if (datestr != null) {
            StringBuffer sbf = new StringBuffer(datestr);
            sbf.insert(4, ".").insert(7, ".").insert(10, " ").insert(13, ":");
            dateString = sbf.toString();
        }
        return dateString;
    }


    //截取16位
    public static String Datatype(String datestr) {
        String datestring;
        if (datestr.equals("null") || TextUtils.isEmpty(datestr)) {
            datestring = "";
        } else {
            datestring = datestr.substring(0, 16).replace("-", ".").replace("T", " ");
        }
        return datestring;
    }

    //截取10位
    public static String Datatypetwo(String datestr) {
        String datestring = "";
        if (datestr != null) {
            datestring = datestr.substring(0, 10);
        }
        return datestring;
    }


    //是否是空格 true是空
    public static boolean isSpace(String str) {
        boolean space;
        if (str == null || TextUtils.isEmpty(str)) {
            space = true;
        } else {
            space = false;
        }
        return space;
    }


    public static String idstring(ArrayList<String> arrstr) {
        StringBuffer sbf = new StringBuffer();
        for (int i = 0; i < arrstr.size(); i++) {
            sbf.append(arrstr.get(i) + ",");
        }
        return sbf.toString();
    }

    /**
     * 获取软件Name版本号
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("msg", e.getMessage());
        }
        return verName;
    }

    /**
     * 得到现在时间
     *
     * @return 字符串 yyyyMMdd HHmmss
     */
    public static String getStringToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }


    public static void textWidt(String text, int width, TextView view) {
        int textwidth;
        if (text.length() > 4) {
            textwidth = width / Integer.valueOf(text.length());
        } else {
            textwidth = 24;
        }
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, textwidth / 2);
        view.setText(text);
    }

    //保存网络图片
    public static void saveImgUrl(Context context, String imgurl) {
        //获取bitmap保存
        Bitmap bitmap = null;
        try {
            URL pictureUrl = new URL(imgurl);
            InputStream in = pictureUrl.openStream();
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "EnergyM");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 其次把文件插入到系统图库
        if (bitmap != null) {
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
    }


    public static String parseDate(String date) {
        String[] s = date.split(" ");
        String[] d = s[0].split("-");
        if (d[1].substring(0, 1).equals("0")) {
            d[1] = d[1].substring(1);
        }
        if (d[2].substring(0, 1).equals("0")) {
            d[2] = d[2].substring(1);
        }
        return d[0] + "/" + d[1] + "/" + d[2];
    }

    public static String parseUrlCode(String s) {
        s = s.replace(" ", "%20").replace(".", "%2E");
        String str = "";
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (Exception e) {
            return str;
        }
    }

    /**
     * 调起系统发短信功能
     *
     * @param phoneNumber
     * @param message
     */
    public void doSendSMSTo(String phoneNumber, String message) {
        if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
//            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
//            intent.putExtra("sms_body", message);
//            startActivity(intent);
        }
    }

    /**
     * 固定格式
     *
     * @param sformat MMdd
     * @return
     */
    public static String getUserDate(String sformat) {
        String dateString = "2017-03-28";
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(sformat);//先转固定格式
            dateString = new SimpleDateFormat("yyyy-MM-dd").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }


    public static String getDate(String sformat) {
        String dateString = "2017-03-28";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = sdf.parse(sformat);
            dateString = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }


    public static int getMaxDayByYearMonth(int year, int month) {
        int maxDay = 0;
        int day = 1;
        /**
         * 与其他语言环境敏感类一样，Calendar 提供了一个类方法 getInstance，
         * 以获得此类型的一个通用的对象。Calendar 的 getInstance 方法返回一
         * 个 Calendar 对象，其日历字段已由当前日期和时间初始化：
         */
        Calendar calendar = Calendar.getInstance();
        /**
         * 实例化日历各个字段,这里的day为实例化使用
         */
        calendar.set(year, month - 1, day);
        /**
         * Calendar.Date:表示一个月中的某天
         * calendar.getActualMaximum(int field):返回指定日历字段可能拥有的最大值
         */
        maxDay = calendar.getActualMaximum(Calendar.DATE);
        return maxDay;
    }

    /**
     * 将时间戳转为代表"距现在多久之前"的字符串
     *
     * @param timeStr 时间戳
     * @return
     */
    public static String getStandardDate(String timeStr) {
        final long minute = 60 * 1000;// 1分钟
        final long hour = 60 * minute;// 1小时
        final long day = 24 * hour;// 1天
        final long month = 31 * day;// 月
        final long year = 12 * month;// 年

        long diff = new Date().getTime() - getTime(timeStr).getTime();

        long r;
        if (diff > day) {
            return Datatypetwo(timeStr);
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "个小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "刚刚";

    }


    // 将字符串转为时间戳
    public static Date getTime(String user_time) {
        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = null;
        try {
            d = sdf.parse(user_time);
//            long l = d.getTime();
//            String str = String.valueOf(l);
//            re_time = str.substring(0, 10);
        } catch (ParseException e) {
            // TODO Auto-generated catch block e.printStackTrace();
        }
        return d;
    }


    /**
     * 时间戳转换成时间
     * 后端时间戳是毫秒 Android时间戳是秒
     * 时间戳输入例如（1402733340000毫秒）输出（"06/14"）
     */
    public static String stampToDate(long stamp) {
        Long ssTime = Math.round(stamp * 1.0 / 1000);//毫秒转化秒
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");//这个是你要转成后的时间的格式
        Date date = new Date(ssTime * 1000);
        String sd = sdf.format(date);
        return sd;
    }

    public static String toDateMinute(long stamp) {
        Long ssTime = Math.round(stamp * 1.0 / 1000);//毫秒转化秒
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");//这个是你要转成后的时间的格式
        Date date = new Date(ssTime * 1000);
        String sd = sdf.format(date);
        return sd;
    }

    public static String toDateDay(String forMat, long stamp) {
        Long ssTime = Math.round(stamp * 1.0 / 1000);//毫秒转化秒
        SimpleDateFormat sdf = new SimpleDateFormat(forMat);//这个是你要转成后的时间的格式
        Date date = new Date(ssTime * 1000);
        String sd = sdf.format(date);
        return sd;
    }

    /**
     * 将时间戳转为代表"距现在多久之前"的字符串
     *
     * @param timeStr 时间戳
     * @return
     */
    public static String getForbiddenTimeDate(long timeStr) {
        Long ssTime = Math.round(timeStr * 1.0 / 1000);//毫秒转化秒
        Date forbiddenDate = new Date(ssTime * 1000);
        long time = forbiddenDate.getTime() - new Date().getTime();
        long mill = (long) Math.floor(time / 1000);//秒前
        long minute = (long) Math.floor(time / 60 / 1000.0f);// 分钟前
        long hour = (long) Math.floor(time / 60 / 60 / 1000.0f);// 小时
        long day = (long) Math.floor(time / 24 / 60 / 60 / 1000.0f);// 天前
        if (day - 1 > 0) {
            return day + "天";
        }
        if (hour - 1 > 0) {
            return hour + "小时";
        }
        if (minute - 1 > 0) {
            return minute + "分钟";
        }
        return "未设置";
    }

//    public static void addPlace(SimpleDraweeView myDraw, Context context) {
//        //获取GenericDraweeHierarchy对象
//        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(context.getResources())
//                //设置占位图及它的缩放方式
//                .setProgressBarImage(R.drawable.placeholder_icon)
////                .setPlaceholderImage(ContextCompat.getDrawable(context, R.drawable.placeholder_icon), ScalingUtils.ScaleType.FIT_XY)
//                //构建
//                .build();
//
//        //设置GenericDraweeHierarchy
//        myDraw.setHierarchy(hierarchy);
//    }
//
//    public static void addPlaceRound(SimpleDraweeView myDraw, Context context) {
//        //获取GenericDraweeHierarchy对象
//        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(context.getResources())
//                .setRoundingParams(RoundingParams.asCircle())
//                //设置占位图及它的缩放方式
//                .setProgressBarImage(R.drawable.placeholder_icon)
////                .setPlaceholderImage(ContextCompat.getDrawable(context, R.drawable.placeholder_icon), ScalingUtils.ScaleType.FOCUS_CROP)
//                //构建
//                .build();
//
//        //设置GenericDraweeHierarchy
//        myDraw.setHierarchy(hierarchy);
//    }

    /**
     * 实现文本复制功能
     * add by wangqianzhou
     *
     * @param content
     */
    public static void copy(String content, Context context) {
        ClipboardManager manager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", content);
        manager.setPrimaryClip(clipData);
    }

    /*
     * 毫秒转化
     */
    public static String formatTime(long ms) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        String strDay = day < 10 ? "0" + day : "" + day; //天
        String strHour = hour < 10 ? "0" + hour : "" + hour;//小时
        String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
        String strSecond = second < 10 ? "0" + second : "" + second;//秒
        String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;//毫秒
        strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;

        if (hour == 0) {
            if (minute == 0) {
                return second + "秒";
            } else {
                return minute + "分钟";
            }
        } else {
            return hour + "小时";
        }

//        return strMinute + " 分钟 " + strSecond + " 秒";
    }


    /**
     * 截取固定的位置当前屏幕
     *
     * @param activity
     * @param
     * @param
     * @return
     */
    public static Bitmap myfixed(Activity activity, int viewX, int viewY, int widht, int heght) {
        Bitmap bmp = null;
        if (bmp != null) {
            bmp.recycle();
        }
        bmp = null;
        // 获取windows中最顶层的view
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();
        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = activity.getWindowManager().getDefaultDisplay();
        // 获取屏幕宽和高
        int widths = display.getWidth();
        int heights = display.getHeight();
        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
        // 去掉状态栏
        statusBarHeights = statusBarHeights + viewY;
//        heights = heights - statusBarHeights - heghtbelowy;
//        int padd =  (int)(Float.parseFloat(saveFile.getShareData("scale", context))*311);


        bmp = Bitmap.createBitmap(view.getDrawingCache(), viewX, viewY, widht, heght);
        // 销毁缓存信息
        view.destroyDrawingCache();
        return bmp;
    }

    //修改shape背景颜色
    public static void changeShapColor(View v, int color) {
        GradientDrawable da = (GradientDrawable) v.getBackground();
        da.setColor(color);
    }


    /**
     * 检测输入密码是否符合规范
     * 8~16位数字和字母组成{8,16}
     * 不能是纯数字或纯字母
     * {8,}不少于8位
     */
    public static boolean isPasswordForm(String pwd) {
        if (TextUtils.isEmpty(pwd)) return false;
        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,}$";
        return pwd.matches(regex);
    }


    @SuppressLint("MissingPermission")
    public static String getIMEI(Context context) {
        String deviceId = "";

        String uuid = saveFile.getShareData(MyConstant.USER_UUID, context);
        if (TextUtils.equals(uuid, "false")) {
            deviceId = getUUID();
            saveFile.saveShareData(MyConstant.USER_UUID, deviceId, context);
        }else {
            deviceId = uuid;
        }
//        if (isHarmonyOSa()){
//            return deviceId;
//        }

//        try {
//            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//
//                deviceId = DeviceID.getOAID();
//                if (TextUtils.isEmpty(deviceId)) {
//                    deviceId = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//                }
//            } else {
//                // request old storage permission 需要电话权限
//                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                    return null;
//                }
//                deviceId = getDouIMEI(context);
//            }
//
//            if (TextUtils.isEmpty(deviceId)) {
//                return getLocalMacAddress(context);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (TextUtils.isEmpty(deviceId)) {
//                return getLocalMacAddress(context);
//            }
//        }
        return deviceId;
    }


    /**
     * @param context
     * @return 双卡手机
     */
    @SuppressLint("MissingPermission")
    public static String getDouIMEI(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Method method = manager.getClass().getMethod("getImei", int.class);
            String imei1 = (String) method.invoke(manager, 0);
            String imei2 = (String) method.invoke(manager, 1);
            if (TextUtils.isEmpty(imei2)) {
                return imei1;
            }
            if (!TextUtils.isEmpty(imei1)) {
                //因为手机卡插在不同位置，获取到的imei1和imei2值会交换，所以取它们的最小值,保证拿到的imei都是同一个
                String imei = "";
                if (imei1.compareTo(imei2) <= 0) {
                    imei = imei1;
                } else {
                    imei = imei2;
                }
                return imei;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return manager.getDeviceId();
        }
        return "";
    }

    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    private static final String HARMONY_OS = "harmony";


    //判断是否为鸿蒙
    public static boolean isHarmonyOSa() {
        try {
            Class clz = Class.forName("com.huawei.system.BuildEx");
            Method method = clz.getMethod("getOsBrand");
            ClassLoader classLoader = clz.getClassLoader();
            System.out.println("classLoader: " + classLoader);
            //BootClassLoader的parent为null
            if (classLoader != null && classLoader.getParent() == null) {
                //return method.invoke(clz).toString();//返回获取的名称
                //****返回是否是鸿蒙系统 自行注释并将方法返回值改为boolean
                return HARMONY_OS.equals(method.invoke(clz));
            }
        } catch (Exception ignored) {
        }
        return false;
    }


    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


}