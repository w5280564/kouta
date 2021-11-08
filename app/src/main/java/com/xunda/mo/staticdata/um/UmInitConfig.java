package com.xunda.mo.staticdata.um;

import android.content.Context;
import android.os.Handler;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;


public class UmInitConfig {

//    private static final String TAG = App.class.getName();
    public static final String UPDATE_STATUS_ACTION = "com.umeng.message.example.action.UPDATE_STATUS";
    private Handler handler;

    public  void  UMinit(Context context){

        //初始化组件化基础库, 统计SDK/推送SDK/分享SDK都必须调用此初始化接口
        UMConfigure.init(context, "611215983451547e6843863f", "Umeng", UMConfigure.DEVICE_TYPE_PHONE,
                "669c30a9584623e70e8cd01b0381dcb4");

        String FileProvider = "com.umeng.soexample.fileprovider";
        PlatformConfig.setWeixin("wxdd3384bb6c79b2ca", "cd43e7da31f8e0ab8beaaf56c80d49f9");
        PlatformConfig.setWXFileProvider(FileProvider);
        //企业微信设置
//        PlatformConfig.setWXWork("wwac6ffb259ff6f66a", "EU1LRsWC5uWn6KUuYOiWUpkoH45eOA0yH-ngL8579zs", "1000002", "wwauthac6ffb259ff6f66a000002");
//        PlatformConfig.setWXWorkFileProvider(FileProvider);

//        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad", "http://sns.whalecloud.com");
//        PlatformConfig.setSinaFileProvider(FileProvider);
//        PlatformConfig.setYixin("yxc0614e80c9304c11b0391514d09f13bf");
        PlatformConfig.setQQZone("101968658", "fbda294d9116fed80ec1d2e290a014ef");
        PlatformConfig.setQQFileProvider(FileProvider);
//        PlatformConfig.setTwitter("3aIN7fuF685MuZ7jtXkQxalyi", "MK6FEYG63eWcpDFgRYw4w9puJhzDl0tyuqWjZ3M7XJuuG7mMbO");
//        PlatformConfig.setAlipay("2015111700822536");
//        PlatformConfig.setLaiwang("laiwangd497e70d4", "d497e70d4c3e4efeab1381476bac4c5e");
//        PlatformConfig.setPinterest("1439206");
//        PlatformConfig.setKakao("e4f60e065048eb031e235c806b31c70f");
//        PlatformConfig.setDing("dingoalmlnohc0wggfedpk");
//        PlatformConfig.setDingFileProvider(FileProvider);

//        PlatformConfig.setVKontakte("5764965", "5My6SNliAaLxEm3Lyd9J");
//        PlatformConfig.setDropbox("oz8v5apet3arcdy", "h7p2pjbzkkxt02a");

        //集成umeng-crash-vx.x.x.aar，则需要关闭原有统计SDK异常捕获功能
        MobclickAgent.setCatchUncaughtExceptions(false);
        //PushSDK初始化(如使用推送SDK，必须调用此方法)

        //统计SDK是否支持采集在子进程中打点的自定义事件，默认不支持
        UMConfigure.setProcessEvent(true);//支持多进程打点

        // 页面数据采集模式
        // setPageCollectionMode接口参数说明：
        // 1. MobclickAgent.PageMode.AUTO: 建议大多数用户使用本采集模式，SDK在此模式下自动采集Activity
        // 页面访问路径，开发者不需要针对每一个Activity在onResume/onPause函数中进行手动埋点。在此模式下，
        // 开发者如需针对Fragment、CustomView等自定义页面进行页面统计，直接调用MobclickAgent.onPageStart/
        // MobclickAgent.onPageEnd手动埋点即可。此采集模式简化埋点工作，唯一缺点是在Android 4.0以下设备中
        // 统计不到Activity页面数据和各类基础指标(提示：目前Android 4.0以下设备市场占比已经极小)。

        // 2. MobclickAgent.PageMode.MANUAL：对于要求在Android 4.0以下设备中也能正常采集数据的App,可以使用
        // 本模式，开发者需要在每一个Activity的onResume函数中手动调用MobclickAgent.onResume接口，在Activity的
        // onPause函数中手动调用MobclickAgent.onPause接口。在此模式下，开发者如需针对Fragment、CustomView等
        // 自定义页面进行页面统计，直接调用MobclickAgent.onPageStart/MobclickAgent.onPageEnd手动埋点即可。

        // 如下两种LEGACY模式不建议首次集成友盟统计SDK的新用户选用。
        // 如果您是友盟统计SDK的老用户，App需要从老版本统计SDK升级到8.0.0版本统计SDK，
        // 并且：您的App之前MobclickAgent.onResume/onPause接口埋点分散在所有Activity
        // 中，逐个删除修改工作量很大且易出错。
        // 若您的App符合以上特征，可以选用如下两种LEGACY模式，否则不建议继续使用LEGACY模式。
        // 简单来说，升级SDK的老用户，如果不需要手动统计页面路径，选用LEGACY_AUTO模式。
        // 如果需要手动统计页面路径，选用LEGACY_MANUAL模式。
        // 3. MobclickAgent.PageMode.LEGACY_AUTO: 本模式适合不需要对Fragment、CustomView
        // 等自定义页面进行页面访问统计的开发者，SDK仅对App中所有Activity进行页面统计，开发者需要在
        // 每一个Activity的onResume函数中手动调用MobclickAgent.onResume接口，在Activity的
        // onPause函数中手动调用MobclickAgent.onPause接口。此模式下MobclickAgent.onPageStart
        // ,MobclickAgent.onPageEnd这两个接口无效。

        // 4. MobclickAgent.PageMode.LEGACY_MANUAL: 本模式适合需要对Fragment、CustomView
        // 等自定义页面进行手动页面统计的开发者，开发者如需针对Fragment、CustomView等
        // 自定义页面进行页面统计，直接调用MobclickAgent.onPageStart/MobclickAgent.onPageEnd
        // 手动埋点即可。开发者还需要在每一个Activity的onResume函数中手动调用MobclickAgent.onResume接口，
        // 在Activity的onPause函数中手动调用MobclickAgent.onPause接口。
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

    }




}
