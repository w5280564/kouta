package com.xunda.mo.main.baseView;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.github.gzuliyujiang.oaid.DeviceID;
import com.google.android.gms.common.util.CollectionUtils;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.EMLog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.interfaceOrImplement.UserActivityLifecycleCallbacks;
import com.xunda.mo.hx.common.utils.PreferenceManager;
import com.xunda.mo.main.constant.MyConstant;

import org.xutils.x;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import lombok.SneakyThrows;


public class MyApplication extends Application implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "JPush";
    public static Context mycontext;
    private UserActivityLifecycleCallbacks mLifecycleCallbacks = new UserActivityLifecycleCallbacks();
    private static MyApplication instance;
    public static EMMessageListener msgListener;

    @Override
    public void onCreate() {
        super.onCreate();
        mycontext = this;
        instance = this;

//        UMConfigure.preInit(getApplicationContext(),"611215983451547e6843863f","Umeng");
        //友盟正式初始化
//        UmInitConfig umInitConfig = new UmInitConfig();
//        umInitConfig.UMinit(this);

        initThrowableHandler();
        initHx();

        if (DemoHelper.getInstance().getAutoLogin()) {
//            msgListener = new EMMessageMethod();
//            DemoHelper.getInstance().getChatManager().addMessageListener(msgListener);
//            DemoHelper.getInstance().getChatManager().addMessageListener(new EMMessageMethod());
        }
        registerActivityLifecycleCallbacks();
        closeAndroidPDialog();
        //xUtils初始化
        x.Ext.init(this);
//        x.Ext.setDebug(true); // 是否输出debug日志
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
        if (DemoHelper.getInstance().getAutoLogin()) {
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
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);//指定为经典Header，默认是 贝塞尔雷达Header
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            //指定为经典Footer，默认是 BallPulseFooter
            return new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate);
        });
    }

    /**
     * 为了兼容5.0以下使用vector图标
     */
    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
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
    @SuppressLint("SoonBlockedPrivateApi")
    private void closeAndroidPDialog() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
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


    public class EMMessageMethod implements EMMessageListener {
        @SneakyThrows
        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //接收消息的时候获取到扩展属性
            //获取自定义的属性，第2个参数为没有此定义的属性时返回的默认值
//            if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            if (!CollectionUtils.isEmpty(messages)) {
                for (EMMessage msg : messages) {
                    // 消息所属会话
                    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(msg.getFrom(), EMConversation.EMConversationType.Chat, false);
                    if (conversation == null) {
                        return;
                    }
                    if (conversation.isGroup()) {
                    } else {
                        String isDouble_Recall = msg.getStringAttribute(MyConstant.MESSAGE_TYPE, "");
                        if (TextUtils.equals(isDouble_Recall, MyConstant.MESSAGE_TYPE_DOUBLE_RECALL)) {
//                            EaseEvent event = EaseEvent.create(MyConstant.MESSAGE_TYPE_DOUBLE_RECALL, EaseEvent.TYPE.MESSAGE);
//                            LiveDataBus.get().with(MyConstant.MESSAGE_TYPE_DOUBLE_RECALL).postValue(event);
                            // 删除消息
                            conversation.clearAllMessages();
                            saveMes(conversation.conversationId());
                        }
                    }
                }
            }
//            } else if (chatType == EaseConstant.CHATTYPE_GROUP) {
//                if (!messages.isEmpty()) {
//                    String isAnonymousOn = messages.get(0).getStringAttribute("messageType", "");
//                    if (TextUtils.equals(isAnonymousOn, MyConstant.MESSAGE_TYPE_ANONYMOUS_ON)) {
//                        groupModel.getData().setIsAnonymous(1);
//                        sendAnonymousName(1);
//                    } else if (TextUtils.equals(isAnonymousOn, MyConstant.MESSAGE_TYPE_ANONYMOUS_OFF)) {
//                        groupModel.getData().setIsAnonymous(0);
//                        sendAnonymousName(0);
//                    }
//                }
//            }

            Log.i("message", "接收消息");
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
            Log.i("message", "透传");
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
            Log.i("message", "回执");
//            for (EMMessage message : messages) {
//                String fireType = message.getStringAttribute(MyConstant.FIRE_TYPE, "");
//                if (TextUtils.equals(fireType, "1")) {
//                    // 消息所属会话
//                    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getUserName(), EMConversation.EMConversationType.Chat, true);
//                    conversation.removeMessage(message.getMsgId());
//                }
//            }
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
            Log.i("message", "送达");
        }

        @Override
        public void onMessageRecalled(List<EMMessage> messages) {
            //消息被撤回
            Log.i("message", "撤回");
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
            Log.i("message", "消息变动");
        }
    }


    private void saveMes(String conversationId) {
//        String conversationId = Model.getData().getHxUserName();
//        EMConversation conversation = DemoHelper.getInstance().getChatManager().getConversation(conversationId);
//        EMMessage conMsg = conversation.getLastMessage();
//        EMMessage conMsg =  chatLayout.getChatMessageListLayout().getCurrentConversation().getLastMessage();

        DemoHelper.getInstance().getConversation(conversationId, EMConversation.EMConversationType.Chat, false);
        EMMessage msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        EMTextMessageBody txtBody = new EMTextMessageBody("对方撤回了所有消息");
        msgNotification.addBody(txtBody);
        msgNotification.setFrom(conversationId);
//        msgNotification.setTo(conMsg.getTo());
        msgNotification.setTo(conversationId);
        msgNotification.setUnread(false);
//        msgNotification.setMsgTime(conMsg.getMsgTime());
//        msgNotification.setLocalTime(conMsg.getMsgTime());
        msgNotification.setChatType(EMMessage.ChatType.Chat);
        msgNotification.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TYPE_DOUBLE_RECALL);
        msgNotification.setStatus(EMMessage.Status.SUCCESS);
        EMClient.getInstance().chatManager().saveMessage(msgNotification);
    }

}
