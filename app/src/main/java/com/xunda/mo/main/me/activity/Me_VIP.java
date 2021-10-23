package com.xunda.mo.main.me.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hyphenate.easeui.widget.EaseImageView;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xunda.mo.R;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.main.baseView.FlowLayout;
import com.xunda.mo.main.pay.alipay.AuthResult;
import com.xunda.mo.main.pay.alipay.PayResult;
import com.xunda.mo.model.CardFragment_Bean;
import com.xunda.mo.model.Me_Vip_Count;
import com.xunda.mo.model.Order_Bean;
import com.xunda.mo.model.UserDetail_Bean;
import com.xunda.mo.model.baseDataModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.StaticData;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Me_VIP extends BaseInitActivity {
    private Button on_Btn,return_Btn;
    private ConstraintLayout vip_Con;
    private TextView zun_Txt, couponCount_Txt, attemptCount_Txt, expCount_Txt, vip_Time, name_Txt, vip_Txt, id_Txt;
    Double pay = 0.0;
    int month = 1;
    String cardId;
    List<Integer> vipData = new ArrayList<>();


    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    private EaseImageView head_Image;
    private FlowLayout vipType_Flow;


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Me_VIP.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_me_vip;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setFitSystemForTheme(true, R.color.viptop);
        setStatusBarTextColor(false);

        vip_Con = findViewById(R.id.vip_Con);
        return_Btn = findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn,50,50,50,50);
        return_Btn.setOnClickListener(new return_BtnClick());
        on_Btn = findViewById(R.id.on_Btn);
        on_Btn.setOnClickListener(new on_BtnClick());
        zun_Txt = findViewById(R.id.zun_Txt);
        couponCount_Txt = findViewById(R.id.couponCount_Txt);
        attemptCount_Txt = findViewById(R.id.attemptCount_Txt);
        expCount_Txt = findViewById(R.id.expCount_Txt);
        vip_Time = findViewById(R.id.vip_Time);
        name_Txt = findViewById(R.id.name_Txt);
        head_Image = findViewById(R.id.head_Image);
        vip_Txt = findViewById(R.id.vip_Txt);
        id_Txt = findViewById(R.id.id_Txt);
        vipType_Flow = findViewById(R.id.vipType_Flow);
    }


    private class return_BtnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }
    private class on_BtnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            showVip(Me_VIP.this, zun_Txt, null);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        regToWx();//注册微信

        vipData.add(68);
        vipData.add(188);
        vipData.add(348);
        vipData.add(698);
        LiveDataBus.get().with("discountCard", CardFragment_Bean.DataDTO.class).observe(this, dataDTO -> {
            if (dataDTO != null) {
                cardId = dataDTO.getUserCardId();
                showVip(Me_VIP.this, zun_Txt, dataDTO);
            }
        });
    }

    private void setMyData(UserDetail_Bean userModel) {
//        MyInfo myInfo = new MyInfo(mContext);
        String headUrl = userModel.getData().getHeadImg();
        Glide.with(mContext).load(headUrl).into(head_Image);
        String name = userModel.getData().getNickname();
        name_Txt.setText(name);
        int vipType = userModel.getData().getVipType();
        if (vipType == 1) {
            vip_Txt.setVisibility(View.VISIBLE);
            on_Btn.setText("续费特权");
        }
        int idStr = userModel.getData().getUserNum();
        id_Txt.setText("Mo ID:" + idStr);
        vipTypeList(vipType_Flow, mContext, vipType);
    }

    @Override
    protected void onResume() {
        super.onResume();
        vipData(mContext, saveFile.UserVipConfig_UserVipInfo);
        UserMethod(mContext, saveFile.User_GetUserInfo_Url);
//        setMyData();
    }

    public void vipTypeList(FlowLayout label_Flow, Context mContext, int vipType) {
        String[] vipData = new String[]{"专属铭牌", "双向撤回", "超大群聊", "升级加速", "隐藏在线状态", "超长语音", "Mo消息", "好友上限"};

        int[] vipImgData = new int[]{R.mipmap.vip_crown_yellow, R.mipmap.vip_recall_yellow, R.mipmap.vip_group_yellow, R.mipmap.vip_grade_yellow, R.mipmap.vip_clock_yellow, R.mipmap.vip_voice_yellow, R.mipmap.vip_fire_yellow, R.mipmap.vip_friend_yellow};
        int[] unVipImgData = new int[]{R.mipmap.vip_crown_gray, R.mipmap.vip_recall_gray, R.mipmap.vip_group_gray, R.mipmap.vip_grade_gray, R.mipmap.vip_clock_gray, R.mipmap.vip_voice_gray, R.mipmap.vip_fire_gray, R.mipmap.vip_friend_gray};
        if (label_Flow != null) {
            label_Flow.removeAllViews();
        }
        for (int i = 0; i < vipData.length; i++) {
            View contentView = View.inflate(mContext, R.layout.vip_type_popup, null);
            ConstraintLayout vip_Con = contentView.findViewById(R.id.vip_Con);
            ImageView vip_Img = contentView.findViewById(R.id.vip_Img);
            if (vipType == 0) {
                vip_Img.setImageResource(unVipImgData[i]);
            } else {
                vip_Img.setImageResource(vipImgData[i]);
            }
            TextView vipType_Txt = contentView.findViewById(R.id.vipType_Txt);
            vipType_Txt.setText(vipData[i]);
            vip_Con.setTag(i);
            label_Flow.addView(contentView);
            vip_Con.setOnClickListener(v -> {
                int tag = (int) v.getTag();
            });
        }
    }


    TextView coupon_type, coupon_Txt, payPrice_Txt, discount_Txt;
    private void showVip(final Context mContext, final View view, CardFragment_Bean.DataDTO cardData) {
        View contentView = View.inflate(mContext, R.layout.vip_top_popup, null);
        PopupWindow MorePopup = new BasePopupWindow(mContext);
        if (MorePopup.isShowing()) {
            return;
        }
        MorePopup.setWidth(RadioGroup.LayoutParams.MATCH_PARENT);
        MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setTouchable(true);
        MorePopup.setContentView(contentView);
        MorePopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        ConstraintLayout coupon_Con = contentView.findViewById(R.id.coupon_Con);
        LinearLayout choice_Lin = contentView.findViewById(R.id.choice_Lin);
        ImageView vip_Fork = contentView.findViewById(R.id.vip_Fork);
        coupon_type = contentView.findViewById(R.id.coupon_type);
        coupon_Txt = contentView.findViewById(R.id.coupon_Txt);
        payPrice_Txt = contentView.findViewById(R.id.payPrice_Txt);
        discount_Txt = contentView.findViewById(R.id.discount_Txt);
        TextView payRenew_Txt = contentView.findViewById(R.id.payRenew_Txt);

        tagList(choice_Lin, mContext, cardData);
        coupon_Con.setOnClickListener(v -> {
            Me_CouponCard.actionStart(mContext, "折扣券", "true");
            MorePopup.dismiss();
        });
        vip_Fork.setOnClickListener(v -> MorePopup.dismiss());
        payRenew_Txt.setOnClickListener(v -> {
            orderData(mContext, saveFile.Order_Create);
            payMore(mContext, view, pay);
            MorePopup.dismiss();
        });
    }

    public void tagList(LinearLayout label_Lin, Context mContext, CardFragment_Bean.DataDTO cardData) {
        List<ConstraintLayout> myLin = new ArrayList<>();
        List<String> monthData = new ArrayList<>();
        monthData.add("1个月");
        monthData.add("3个月");
        monthData.add("6个月");
        monthData.add("1年");
        if (label_Lin != null) {
            label_Lin.removeAllViews();
        }
        Double rules = 1.0;
        int price = vipData.get(0);//原价
        if (cardData == null) {
            coupon_type.setText("暂未选择折扣券");
        } else {
            rules = cardData.getRules();
            String dis = String.format("VIP%1$s折券", (int)(rules*10));
            coupon_type.setText(dis);
        }
        for (int i = 0; i < vipData.size(); i++) {
            View contentView = View.inflate(mContext, R.layout.vip_price_pop, null);
            ConstraintLayout price_Con = contentView.findViewById(R.id.price_Con);
            TextView price_Txt = contentView.findViewById(R.id.price_Txt);
            TextView originalCost_Txt = contentView.findViewById(R.id.originalCost_Txt);
            TextView month_Txt = contentView.findViewById(R.id.month_Txt);
            ImageView vip_lab = contentView.findViewById(R.id.vip_lab);
            if (i==0){
                vip_lab.setVisibility(View.VISIBLE);
                vip_lab.setImageResource(R.mipmap.vip_tui_icon);
            }else   if (i==3){
                vip_lab.setVisibility(View.VISIBLE);
                vip_lab.setImageResource(R.mipmap.vip_top_icon);
            }
            originalCost_Txt.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            int priceLoop = vipData.get(i);
            int originalCostLoop = (int) (priceLoop * rules);
            price_Txt.setText("￥" + priceLoop);
            originalCost_Txt.setText("￥" + priceLoop);
            if (rules < 1) {
                originalCost_Txt.setVisibility(View.VISIBLE);
                price_Txt.setText("￥" + originalCostLoop);
            }
            month_Txt.setText(monthData.get(i));

            price_Con.setTag(i);
            myLin.add(price_Con);
            label_Lin.addView(contentView);
            Double finalRules = rules;
            price_Con.setOnClickListener(v -> {
                int tag = (int) v.getTag();
                for (int k = 0; k < vipData.size(); k++) {
                    myLin.get(k).setSelected(false);
                }
                myLin.get(tag).setSelected(true);
                int choicePrice = vipData.get(tag);
                setDisCountPrice(finalRules, choicePrice, tag);
            });
        }
        myLin.get(0).setSelected(true);
        setDisCountPrice(rules, price, 0);
    }

    /**
     * @param finalRules  折扣
     * @param choicePrice 原价
     */
    private void setDisCountPrice(Double finalRules, int choicePrice, int tag) {
        pay = (double) choicePrice;
        Double price = (double) choicePrice;//原价
        DecimalFormat df = new DecimalFormat("#.##");
        Double originalCost = Double.parseDouble(df.format(price * finalRules)) ;; //折后价
        Double priceSpread = Double.parseDouble(df.format(price - originalCost));//差价
        payPrice_Txt.setText("￥" + price);
        if (finalRules < 1) {
            coupon_Txt.setText("-￥" + priceSpread);
            coupon_Txt.setVisibility(View.VISIBLE);
            discount_Txt.setText(String.format("VIP折扣券立减%1$s元", priceSpread + ""));
            discount_Txt.setVisibility(View.VISIBLE);
            payPrice_Txt.setText("￥" + originalCost);
            pay = originalCost;
        }
        if (tag == 0) {
            month = 1;
        } else if (tag == 1) {
            month = 3;
        } else if (tag == 2) {
            month = 6;
        } else if (tag == 3) {
            month = 12;
        }
    }

    private void payMore(final Context mContext, final View view, Double pay) {
        View contentView = View.inflate(mContext, R.layout.choicepay, null);
        PopupWindow MorePopup = new BasePopupWindow(mContext);
        MorePopup.setWidth(RadioGroup.LayoutParams.MATCH_PARENT);
        MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setTouchable(true);
        MorePopup.setContentView(contentView);
        MorePopup.showAtLocation(view, Gravity.CENTER, 0, 0);
        ImageView vip_Fork = contentView.findViewById(R.id.vip_Fork);
        TextView pay_price = contentView.findViewById(R.id.pay_price);
        Button wechat_Btn = contentView.findViewById(R.id.wechat_Btn);
        Button ali_Btn = contentView.findViewById(R.id.ali_Btn);
        Button payOn_Btn = contentView.findViewById(R.id.payOn_Btn);
        pay_price.setText("￥" + pay);
        vip_Fork.setOnClickListener(v -> {
            MorePopup.dismiss();
        });
        wechat_Btn.setOnClickListener(v -> {
            wechat_Btn.setSelected(true);
            ali_Btn.setSelected(false);
        });

        ali_Btn.setOnClickListener(v -> {
            wechat_Btn.setSelected(false);
            ali_Btn.setSelected(true);
        });
        payOn_Btn.setOnClickListener(v -> {
            if (wechat_Btn.isSelected()) {
                if (orderModel != null) {
                    String orderId = orderModel.getData().getOrderId();
                    payData(mContext, saveFile.WxPay_AppPay, orderId, "wxPay");
                    MorePopup.dismiss();
                }
            }
            if (ali_Btn.isSelected()) {
                if (orderModel != null) {
                    String orderId = orderModel.getData().getOrderId();
                    payData(mContext, saveFile.Alipay_AppPay, orderId, "aliPay");
                    MorePopup.dismiss();
                }
            }

        });
        wechat_Btn.setSelected(true);
    }

    //是否是VIP
    @SuppressLint("SetTextI18n")
    public void UserMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                UserDetail_Bean  userModel = new Gson().fromJson(result, UserDetail_Bean.class);
                UserDetail_Bean.DataDTO dataDTO = userModel.getData();
                setMyData(userModel);
            }
            @Override
            public void failed(String... args) {

            }
        });
    }


    @SuppressLint("SetTextI18n")
    public void vipData(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("sysVersion", "2");
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Me_Vip_Count vipModel = new Gson().fromJson(result, Me_Vip_Count.class);
                couponCount_Txt.setText("" + vipModel.getData().getZkCount());
                attemptCount_Txt.setText("" + vipModel.getData().getTyCount());
                expCount_Txt.setText("" + vipModel.getData().getJyCount());

                String time = StaticData.toDateDay("yyyy-MM-dd", vipModel.getData().getVipEndTime());
                String timeStr = String.format("VIP特权将在%1$s过期", time);
                vip_Time.setText(timeStr);
            }
            @Override
            public void failed(String... args) {
            }
        });
    }


    Order_Bean orderModel;
    @SuppressLint("SetTextI18n")
    public void orderData(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("cardId", cardId);
        map.put("orderType", "1");
        map.put("validMonth", month);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                orderModel = new Gson().fromJson(result, Order_Bean.class);

            }

            @Override
            public void failed(String... args) {
            }
        });
    }


    @SuppressLint("SetTextI18n")
    public void payData(Context context, String baseUrl, String orderId, String aliPayOrWechatPay) {
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                baseDataModel baseModel = new Gson().fromJson(result, baseDataModel.class);
                String orderInfo = baseModel.getData();
                if (TextUtils.equals(aliPayOrWechatPay, "wxPay")) {
                    wechatPay(orderInfo);
                } else if (TextUtils.equals(aliPayOrWechatPay, "aliPay")) {
                    aliPay(orderInfo);
                }
            }

            @Override
            public void failed(String... args) {
            }
        });
    }


    /**
     * 支付宝支付业务示例
     */
    public void aliPay(String orderInfo) {
//        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
//            showAlert(this, getString(R.string.error_missing_appid_rsa_private));
//            return;
//        }

        /*
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo 的获取必须来自服务端；
         */
//        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
//        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
//        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

//        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
//        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
//        final String orderInfo = orderParam + "&" + sign;

        final Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(Me_VIP.this);
//                String order = "alipay_root_cert_sn=687b59193f3f462dd5336e5abf83c5d8_02941eef3187dddf3d3b83462e1dfcf6&alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_cert_sn=935fa777fff420101a401beb06f81df1&app_id=2021002147642867&biz_content=%7B%22body%22%3A%22123%22%2C%22out_trade_no%22%3A%2220210817010101004%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22456%22%2C%22timeout_express%22%3A%2290m%22%2C%22total_amount%22%3A%220.01%22%7D&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=%2FaliPay%2FpayNotify&sign=Eamuc%2FhH0jWHj1DRchSxbKIjh%2BRl3StZHdeoFjiLQLFiobePakEbBPb%2BhIM8F347arDwPo4gXT0Gi0k6E0tLmiUiDbjV1wGFMAEmFH4uJTdNKl2LrOVkfd5ELNclSOt1l8PL0fWZ7Y9w48tQZZyZxhEFXQSNNMBbnYB0GwJE7MD%2FJ%2BLoxf20X73mxSmIJKEcPU7%2F%2F3hXlyN%2Fz59K0%2BSCyMqRPS9QPJh%2FvIvCU8hflIjUFPEZEUWxPjlLBD0dAgN%2Fb9ihO2JBfSVpl4iwb0e0TdIZ5neg2VhvGqVSxHhL2fDzHpHxvf%2FtU56wkTca68m5cM45IUkSJLGXbY2mSfIkUg%3D%3D&sign_type=RSA2&timestamp=2021-09-16+17%3A56%3A01&version=1.0";
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                        showAlert(Me_VIP.this, getString(R.string.pay_success) + payResult);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                        showAlert(Me_VIP.this, getString(R.string.pay_failed) + payResult);
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
//                        showAlert(Me_VIP.this, getString(R.string.auth_success) + authResult);
                    } else {
                        // 其他状态值则为授权失败
//                        showAlert(Me_VIP.this, getString(R.string.auth_failed) + authResult);
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };


    private static void showAlert(Context ctx, String info) {
        showAlert(ctx, info, null);
    }

    private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
        new AlertDialog.Builder(ctx)
                .setMessage(info)
                .setPositiveButton(R.string.confirm, null)
                .setOnDismissListener(onDismiss)
                .show();
    }


    // APP_ID 替换为你的应用从官方网站申请到的合法appID
    private static final String APP_ID = "wxdd3384bb6c79b2ca";

    // IWXAPI 是第三方app和微信通信的openApi接口
    private IWXAPI api;

    private void regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);

        // 将应用的appId注册到微信
        api.registerApp(APP_ID);

        //建议动态监听微信启动广播进行注册到微信
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 将该app注册到微信
                api.registerApp(APP_ID);
            }
        }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));

//        api.handleIntent(getIntent(), this);
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setIntent(intent);
//        api.handleIntent(intent, this);
//    }

    private void wechatPay(String payOrder) {
        try {
            if (!TextUtils.isEmpty(payOrder)) {
                Log.e("get server pay params:", payOrder);
                JSONObject json = new JSONObject(payOrder);
                if (null != json && !json.has("retcode")) {
//                if (null != json ) {
                    PayReq req = new PayReq();
                    //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                    req.appId = json.getString("appid");
                    req.partnerId = json.getString("partnerid");
                    req.prepayId = json.getString("prepayid");
                    req.nonceStr = json.getString("noncestr");
                    req.timeStamp = json.getString("timestamp");
                    req.packageValue = json.getString("package");
                    req.sign = json.getString("sign");
//                    req.extData			= "app data"; // optional
//                    Toast.makeText(Me_VIP.this, "正常调起支付", Toast.LENGTH_SHORT).show();
                    // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                    api.sendReq(req);
                } else {
                    Log.d("PAY_GET", "返回错误" + json.getString("retmsg"));
                    Toast.makeText(Me_VIP.this, "返回错误" + json.getString("retmsg"), Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("PAY_GET", "服务器请求错误");
                Toast.makeText(Me_VIP.this, "服务器请求错误", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("PAY_GET", "异常：" + e.getMessage());
            Toast.makeText(Me_VIP.this, "异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

//    // 微信发送请求到第三方应用时，会回调到该方法
//    @Override
//    public void onReq(BaseReq req) {
//        Toast.makeText(this, "openid = " + req.openId, Toast.LENGTH_SHORT).show();
//
//        switch (req.getType()) {
//            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
//                goToGetMsg();
//                break;
//            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
//                goToShowMsg((ShowMessageFromWX.Req) req);
//                break;
//            case ConstantsAPI.COMMAND_LAUNCH_BY_WX:
//                Toast.makeText(this, R.string.launch_from_wx, Toast.LENGTH_SHORT).show();
//                break;
//            default:
//                break;
//        }
//    }
//
//    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
//    @Override
//    public void onResp(BaseResp resp) {
//        Toast.makeText(this, "openid = " + resp.openId, Toast.LENGTH_SHORT).show();
//
//        if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
//            Toast.makeText(this, "code = " + ((SendAuth.Resp) resp).code, Toast.LENGTH_SHORT).show();
//        }
//
//        int result = 0;
//
//        switch (resp.errCode) {
//            case BaseResp.ErrCode.ERR_OK:
//                result = R.string.errcode_success;
//                break;
//            case BaseResp.ErrCode.ERR_USER_CANCEL:
//                result = R.string.errcode_cancel;
//                break;
//            case BaseResp.ErrCode.ERR_AUTH_DENIED:
//                result = R.string.errcode_deny;
//                break;
//            default:
//                result = R.string.errcode_unknown;
//                break;
//        }
//
//        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
//    }
//
//    private void goToGetMsg() {
////        Intent intent = new Intent(this, GetFromWXActivity.class);
////        intent.putExtras(getIntent());
////        startActivity(intent);
////        finish();
//    }
//
//    private void goToShowMsg(ShowMessageFromWX.Req showReq) {
//        WXMediaMessage wxMsg = showReq.message;
//        WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;
//
//        StringBuffer msg = new StringBuffer(); // 组织一个待显示的消息内容
//        msg.append("description: ");
//        msg.append(wxMsg.description);
//        msg.append("\n");
//        msg.append("extInfo: ");
//        msg.append(obj.extInfo);
//        msg.append("\n");
//        msg.append("filePath: ");
//        msg.append(obj.filePath);
//
////        Intent intent = new Intent(this, ShowFromWXActivity.class);
////        intent.putExtra(Constants.ShowMsgActivity.STitle, wxMsg.title);
////        intent.putExtra(Constants.ShowMsgActivity.SMessage, msg.toString());
////        intent.putExtra(Constants.ShowMsgActivity.BAThumbData, wxMsg.thumbData);
////        startActivity(intent);
//        finish();
//    }
//
//    @Override
//    public void onPointerCaptureChanged(boolean hasCapture) {
//
//    }


}