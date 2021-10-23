package com.xunda.mo.main.login;

import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.hyphenate.easeui.domain.EaseUser;
import com.mcxtzhang.captchalib.SwipeCaptchaView;
import com.xunda.mo.R;
import com.xunda.mo.Receiver.SmsBroadcastReceiver;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.interfaceOrImplement.OnResourceParseCallback;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.MainActivity;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.main.viewmodels.LoginViewModel;
import com.xunda.mo.model.Main_Register_Model;
import com.xunda.mo.model.Olduser_Model;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.CaptchaInputView;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.StaticData;
import com.xunda.mo.staticdata.TimerTextView;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainLogin_Code extends BaseInitActivity {

    private static final int REQUEST_PERMISSION_CODE = 1;
    private Button right_Btn;
    private TimerTextView timer_txt;
    private InputMethodManager inputMethodManager;
    private CaptchaInputView capt_view;
    private String LoginPhoneNume;
    private String TitleName, userNum;
    private SmsBroadcastReceiver mSMSBroadcastReceiver;
    private TextView phone_txt, nonecode_txt;
    String type = "";
    private String equipmentName;
    private String version;
    private String meid;
    private LoginViewModel loginViewModels;

    public static void actionStart(Context context, String titleName, String loginPhone) {
        Intent intent = new Intent(context, MainLogin_Code.class);
        intent.putExtra("TitleName", titleName);
        intent.putExtra("LoginPhoneNume", loginPhone);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, String titleName, String loginPhone, String userNum) {
        Intent intent = new Intent(context, MainLogin_Code.class);
        intent.putExtra("TitleName", titleName);
        intent.putExtra("LoginPhoneNume", loginPhone);
        intent.putExtra("userNum", userNum);
        context.startActivity(intent);
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_login_code);
//
//        StatusBar(this);
//        MIUISetStatusBarLightMode(this.getWindow(), true);
//        FlymeSetStatusBarLightMode(this.getWindow(), true);
//
//        equipmentName = android.os.Build.BRAND + "  " + android.os.Build.MODEL;
//        version = android.os.Build.VERSION.RELEASE;
//        meid = StaticData.getIMEI(this);
//        String smsCode;
//
//
//        LoginPhoneNume = getIntent().getStringExtra("LoginPhoneNume");
//        TitleName = getIntent().getStringExtra("TitleName");
//        userNum = getIntent().getStringExtra("userNum");
//        initTitle();
//        initView();
//        initReceiver();
//    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_login_code;
    }

    @Override
    protected void initSystemFit() {
        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        equipmentName = android.os.Build.BRAND + "  " + android.os.Build.MODEL;
        version = android.os.Build.VERSION.RELEASE;
        meid = StaticData.getIMEI(this);
        String smsCode;

        LoginPhoneNume = intent.getStringExtra("LoginPhoneNume");
        TitleName = intent.getStringExtra("TitleName");
        userNum = intent.getStringExtra("userNum");
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();
        initView();
    }

    @Override
    protected void initData() {
        super.initData();
//        initReceiver();
        initViewModel();
    }


    private void initViewModel() {
        loginViewModels = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModels.getLoginObservable().observe(this, response -> parseResource(response, new OnResourceParseCallback<EaseUser>(true) {
            @Override
            public void onSuccess(EaseUser data) {
                Log.e("login", "login success");
                DemoHelper.getInstance().setAutoLogin(true);
            }

            @Override
            public void onError(int code, String message) {
                super.onError(code, message);
            }
        }));
    }

    private boolean isShow = false;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !isShow) {
            CodeMore(this, phone_txt, 0);
            isShow = true;
        }
    }

    private void initReceiver() {
        //实例化IntentFilter对象
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(1000);
        mSMSBroadcastReceiver = new SmsBroadcastReceiver();
        //注册广播接收
        registerReceiver(mSMSBroadcastReceiver, filter);
        mSMSBroadcastReceiver.setOnReceivedMessageListener(message -> {
            Log.i("tag", "1=" + message);
            capt_view.setText(getDynamicPwd(message));//截取4位验证码
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, REQUEST_PERMISSION_CODE);
        }
    }


    /*高版本手动获取权限*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length != 0) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "您阻止了app读取您的短信，您可以自己手动输入验证码", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("TAG", "获取权限");
            }
        }
    }


    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
//        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.colorFristWhite));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            title_Include.setElevation(2f);//阴影
        }
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText(TitleName);
        right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.VISIBLE);
        right_Btn.setText("问题反馈");
//        StaticData.ViewScale(return_Btn, 100, 100);
//        StaticData.ViewScale(title_Include, 0, 88);

        return_Btn.setOnClickListener(new return_Btn());
        right_Btn.setOnClickListener(new right_BtnOnClickLister());
    }

    private class return_Btn implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            finish();
        }
    }

    private class right_BtnOnClickLister extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Intent intent = new Intent(MainLogin_Code.this, MainLogin_QuestionFeedBack.class);
            startActivity(intent);
        }
    }

    private void initView() {
        inputMethodManager = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
        timer_txt = findViewById(R.id.timer_txt);
        phone_txt = findViewById(R.id.phone_txt);
        nonecode_txt = findViewById(R.id.nonecode_txt);
        capt_view = findViewById(R.id.capt_view);

        showSoftInput(capt_view);
        timer_txt.setOnClickListener(new TimerOnClickLister());
        timer_txt.setEnabled(false);
        capt_view.setTextChangeListener(new capt_viewChangeLister());
        nonecode_txt.setOnClickListener(new nonecode_txtOnClick());

        phone_txt.setText("+86  " + LoginPhoneNume);
    }

    private void Data() {
        if (TitleName.equals("手机号注册")) {
            type = "1";
            codeMethod(saveFile.User_SmsCode_Url, "type", type, type);
        } else if (TitleName.equals("验证码登录")) {
            //登录验证码
            type = "2";
            codeMethod(saveFile.User_SmsCode_Url, "type", type, type);
        } else if (TitleName.equals("忘记密码")) {
            //忘记密码
            nonecode_txt.setVisibility(View.VISIBLE);
            type = "3";
            codeMethod(saveFile.User_checkPhone_Url, "userNum", userNum, type);
        }

    }

    public class nonecode_txtOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            MainLogin_ForgetPsw_Question.actionStart(mContext, LoginPhoneNume);
        }
    }

    private class TimerOnClickLister extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            CodeMore(MainLogin_Code.this, phone_txt, 0);
        }
    }

    public void startTimer() {
        if (this != null && timer_txt != null) {
            timer_txt.setBackground(null);
            timer_txt.setText("获取验证码");
            timer_txt.setTextColor(ContextCompat.getColor(MainLogin_Code.this, R.color.greytwo));
            timer_txt.setEnabled(false);
            timer_txt.startTimer(() -> {
                timer_txt.setEnabled(true);
                timer_txt.setTextColor(ContextCompat.getColor(MainLogin_Code.this, R.color.yellowthree));
                timer_txt.setText("重新获取验证码");
            });
        }
    }

    //发送验证码
    public void codeMethod(String baseUrl, String keyStr, String valueStr, String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("phoneNum", LoginPhoneNume);
        map.put(keyStr, valueStr);
        xUtils3Http.get(MainLogin_Code.this, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                startTimer();
            }

            @Override
            public void failed(String... args) {
            }
        });
    }

    //忘记密码校验
    public void ForgetCodeMethod(String baseUrl, String Code, String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("phoneNum", LoginPhoneNume);
        map.put("code", Code);
        xUtils3Http.get(MainLogin_Code.this, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                if (TitleName.equals("忘记密码")) {
                    Intent intent = new Intent(MainLogin_Code.this, MainLogin_ForgetPsw_SetPsw.class);
                    intent.putExtra("phoneNum",LoginPhoneNume);
                    startActivity(intent);
                }
            }

            @Override
            public void failed(String... args) {
            }
        });

    }


    //手机号注册
    public void RegisterMethod(final String baseUrl, String loginType) {
        Map<String, Object> map = new HashMap<>();
        map.put("equipmentName", equipmentName);
        map.put("version", version);
        map.put("meid", meid);
        map.put("phoneNum", LoginPhoneNume);
        map.put("smsCode", capt_view.getText());
        map.put("osType", "2");
        xUtils3Http.post(MainLogin_Code.this, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Main_Register_Model baseModel = new Gson().fromJson(result, Main_Register_Model.class);
                String name = baseModel.getData().getHxUserName();
                loginViewModels.login(name, name, false);
                DemoHelper.getInstance().setAutoLogin(true);

                saveFile.saveShareData("phoneNum", baseModel.getData().getPhoneNum(), MainLogin_Code.this);
                Intent intent = new Intent(MainLogin_Code.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void failed(String... args) {
            }
        });


    }


    //登录
    public void LoginMethod(Context context, String baseUrl, String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("equipmentName", equipmentName);
        map.put("loginType", "1");
        map.put("meid", meid);
        map.put("version", version);
        map.put("phoneNum", LoginPhoneNume);
        map.put("smsCode", capt_view.getText().toString());
        map.put("osType", "2");
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Olduser_Model baseModel = new Gson().fromJson(result, Olduser_Model.class);
                String name = baseModel.getData().getHxUserName();
                loginViewModels.login(name, name, false);
                DemoHelper.getInstance().setAutoLogin(true);

                saveFile.saveShareData("JSESSIONID", baseModel.getData().getToken(), context);
                saveFile.saveShareData("phoneNum", baseModel.getData().getPhoneNum(), context);
                saveFile.saveShareData("userId", baseModel.getData().getUserId(), context);

                MyInfo myInfo = new MyInfo(context);
                myInfo.setUserInfo(baseModel.getData());
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void failed(String... args) {

            }
        });
    }


    /**
     * 判断软键盘 弹出
     */
    public void showSoftInput(EditText editText) {

        if (inputMethodManager.isActive()) {
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.setEnabled(true);
            editText.requestFocus();
            inputMethodManager.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
        }
        editText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    /**
     * 关闭软键盘 *针对于 有一个EdtxtView * @param input_email
     */
    public void hideSoftInput(EditText input_email) {
        if (inputMethodManager.isActive()) {
            // 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
            inputMethodManager.hideSoftInputFromWindow(input_email.getWindowToken(), 0);
        }
    }

    /**
     * 从字符串中截取连续6位数字组合 ([0-9])截取六位数字 进行前后断言不能出现数字 用于从短信中获取动态密码
     *
     * @param content 短信内容
     * @return 截取得到的6位动态密码
     */
    public String getDynamicPwd(String content) {
//        Pattern pattern = Pattern.compile("(?<![0-9])([0-9])(?![0-9])");
        Pattern pattern = Pattern.compile("(\\d{4})");//连续4个数字的验证码
        Matcher matcher = pattern.matcher(content);
        String dynamicPwd = "";
        while (matcher.find()) {
            dynamicPwd = matcher.group();
            Log.i("TAG", "getDynamicPwd: find pwd=" + dynamicPwd);
        }
        return dynamicPwd;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(mSMSBroadcastReceiver);
//        mSMSBroadcastReceiver = null;
    }

    private class capt_viewChangeLister implements CaptchaInputView.TextChangeListener {
        @Override
        public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
            if (text.toString().length() >= 4) {
                if (TitleName.equals("手机号注册")) {
                    type = "1";
                    RegisterMethod(saveFile.User_Register_Url, type);
                } else if (TitleName.equals("验证码登录")) {
                    String type = "1";
                    String phoneNum = LoginPhoneNume;
                    String smsCode = capt_view.getText().toString();
                    LoginMethod(MainLogin_Code.this, saveFile.User_Login_Url, type);
                } else if (TitleName.equals("忘记密码")) {
                    //忘记密码
                    type = "3";
                    String Code = capt_view.getText().toString();
                    ForgetCodeMethod(saveFile.User_checkChangeCode_Url, Code, type);
                }

            }
        }
    }

    //验证码
    private void CodeMore(final Context mContext, final View view, final int pos) {
        View contentView = View.inflate(mContext, R.layout.popup_code, null);
        PopupWindow MorePopup = new BasePopupWindow(mContext);
        MorePopup.setWidth(RadioGroup.LayoutParams.MATCH_PARENT);
        MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setTouchable(true);
        //透明区域不能点击
        MorePopup.setFocusable(false);
        MorePopup.setOutsideTouchable(false);
        MorePopup.setContentView(contentView);
        MorePopup.showAtLocation(view, Gravity.CENTER, 0, 0);
        SwipeCaptchaView mSwipeCaptchaView = contentView.findViewById(R.id.swipeCaptchaView);
        SeekBar mSeekBar = contentView.findViewById(R.id.dragBar);
        View test_btn = contentView.findViewById(R.id.test_btn);

        test_btn.setOnClickListener(v -> {
            mSwipeCaptchaView.createCaptcha();
            mSeekBar.setEnabled(true);
            mSeekBar.setProgress(0);
        });
        mSwipeCaptchaView.setOnCaptchaMatchCallback(new SwipeCaptchaView.OnCaptchaMatchCallback() {
            @Override
            public void matchSuccess(SwipeCaptchaView swipeCaptchaView) {
//                Toast.makeText(mContext, "验证成功", Toast.LENGTH_SHORT).show();
                mSeekBar.setEnabled(false);
                Data();
                MorePopup.dismiss();
            }

            @Override
            public void matchFailed(SwipeCaptchaView swipeCaptchaView) {
                Toast.makeText(mContext, "验证失败", Toast.LENGTH_SHORT).show();
                swipeCaptchaView.resetCaptcha();
                mSeekBar.setProgress(0);
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSwipeCaptchaView.setCurrentSwipeValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //随便放这里是因为控件
                mSeekBar.setMax(mSwipeCaptchaView.getMaxSwipeValue());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("zxt", "onStopTrackingTouch() called with: seekBar = [" + seekBar + "]");
                mSwipeCaptchaView.matchCaptcha();
            }
        });

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    v.clearFocus();
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }


}
