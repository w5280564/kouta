package com.xunda.mo.main.baseView;


import android.app.Application;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.github.gzuliyujiang.oaid.DeviceID;
import com.hyphenate.util.EMLog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.interfaceOrImplement.UserActivityLifecycleCallbacks;
import com.xunda.mo.hx.common.utils.PreferenceManager;

import org.xutils.x;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class MyApplication extends Application implements Thread.UncaughtExceptionHandler{
    private static final String TAG = "JPush";
    public static Context mycontext;
    private UserActivityLifecycleCallbacks mLifecycleCallbacks = new UserActivityLifecycleCallbacks();
    private static MyApplication instance;


    @Override
    public void onCreate() {
        super.onCreate();
        mycontext = this;
        instance =this;
        initThrowableHandler();

        initHx();
        registerActivityLifecycleCallbacks();
        closeAndroidPDialog();

        //xUtils初始化
        x.Ext.init(this);
        x.Ext.setDebug(true); // 是否输出debug日志
//
////
////		//Jush初始化
//		Log.d(TAG, "[ExampleApplication] onCreate");
//		JPushInterface.setDebugMode(true);
//		JPushInterface.init(this);
////
        //Fresco初始化
        Fresco.initialize(this);


        DeviceID.register(this);


//        EMOptions options = new EMOptions();
//        // 默认添加好友时，是不需要验证的，改成需要验证
//        options.setAcceptInvitationAlways(false);
//        // 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
//        options.setAutoTransferMessageAttachments(true);
//        // 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
//        options.setAutoDownloadThumbnail(true);
//        //初始化
//        EMClient.getInstance().init(mycontext, options);
//        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
//        if (EaseIM.getInstance().init(mycontext, options)) {
//            //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
//            EMClient.getInstance().setDebugMode(true);
//            //EaseIM初始化成功之后再去调用注册消息监听的代码 ...
//        }


    }

    private void initThrowableHandler() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    private void initHx() {
        // 初始化PreferenceManager
        PreferenceManager.init(this);
        // init hx sdk
        if(DemoHelper.getInstance().getAutoLogin()) {
            EMLog.i("DemoApplication", "application initHx");
            DemoHelper.getInstance().init(this);
        }

    }

    private void registerActivityLifecycleCallbacks() {
        this.registerActivityLifecycleCallbacks(mLifecycleCallbacks);
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public UserActivityLifecycleCallbacks getLifecycleCallbacks() {
        return mLifecycleCallbacks;
    }

    //项目超过64k分包
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate);
            }
        });
    }

    /**
     * 为了兼容5.0以下使用vector图标
     */
    static {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        EMLog.e("demoApp", e.getMessage());

    }

    /**
     * 解决androidP 第一次打开程序出现莫名弹窗
     * 弹窗内容“detected problems with api ”
     */
    private void closeAndroidPDialog(){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
            try {
                Class aClass = Class.forName("android.content.pm.PackageParser$Package");
                Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
                declaredConstructor.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Class cls = Class.forName("android.app.ActivityThread");
                Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
                declaredMethod.setAccessible(true);
                Object activityThread = declaredMethod.invoke(null);
                Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
                mHiddenApiWarningShown.setAccessible(true);
                mHiddenApiWarningShown.setBoolean(activityThread, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
