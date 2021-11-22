package com.xunda.mo.main.login;

import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.easeui.domain.EaseUser;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.interfaceOrImplement.OnResourceParseCallback;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.me.activity.Me_Set_SecurityQuestion;
import com.xunda.mo.main.viewmodels.LoginViewModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.CaptchaInputView;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.TimerTextView;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.HashMap;
import java.util.Map;

public class Main_Novalidation_Code extends BaseInitActivity {
    private Button right_Btn;
    private TimerTextView timer_txt;
    private InputMethodManager inputMethodManager;
    private CaptchaInputView capt_view;
    private String LoginPhoneNume;
    private String TitleName, userNum;
    private TextView phone_txt, nonecode_txt;
    String type = "";
    private LoginViewModel loginViewModels;

//    public static void actionStart(Context context, String titleName, String loginPhone) {
//        Intent intent = new Intent(context, Main_Novalidation_Code.class);
//        intent.putExtra("TitleName", titleName);
//        intent.putExtra("LoginPhoneNume", loginPhone);
//        context.startActivity(intent);
//    }

    public static void actionStart(Context context, String titleName, String loginPhone, String userNum) {
        Intent intent = new Intent(context, Main_Novalidation_Code.class);
        intent.putExtra("TitleName", titleName);
        intent.putExtra("LoginPhoneNume", loginPhone);
        intent.putExtra("userNum", userNum);
        context.startActivity(intent);
    }

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
        startTimer();
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


    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setElevation(2f);//阴影
        Button return_Btn = title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText(TitleName);
        right_Btn = title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.GONE);
        return_Btn.setOnClickListener(new return_Btn());
    }

    private class return_Btn implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            finish();
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
        if (TitleName.equals("手机号验证")) {
            //密保手机号验证
            type = "4";
            codeMethod(saveFile.User_checkPhone_Url, "userNum", userNum, type);
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
            Intent intent = new Intent(Main_Novalidation_Code.this, MainLogin_ForgetPsw_Question.class);
            intent.putExtra("phoneNum", LoginPhoneNume);
            startActivity(intent);
        }
    }

    private class TimerOnClickLister extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            Data();
        }
    }

    //填写验证码
    private class capt_viewChangeLister implements CaptchaInputView.TextChangeListener {
        @Override
        public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
            if (text.toString().length() >= 4) {
                if (TitleName.equals("手机号验证")) {
                    //忘记密码
                    type = "4";
                    String Code = capt_view.getText().toString();
                    ForgetCodeMethod(saveFile.User_checkChangeCode_Url, Code, type);
                } else if (TitleName.equals("忘记密码")) {
                    //忘记密码
                    type = "3";
                    String Code = capt_view.getText().toString();
                    ForgetCodeMethod(saveFile.User_checkChangeCode_Url, Code, type);
                }

            }
        }
    }


    public void startTimer() {
        if (this != null && timer_txt != null) {
            timer_txt.setBackground(null);
            timer_txt.setText("获取验证码");
            timer_txt.setTextColor(ContextCompat.getColor(Main_Novalidation_Code.this, R.color.greytwo));
            timer_txt.setEnabled(false);
            timer_txt.startTimer(() -> {
                timer_txt.setEnabled(true);
                timer_txt.setTextColor(ContextCompat.getColor(Main_Novalidation_Code.this, R.color.yellowthree));
                timer_txt.setText("重新获取验证码");
            });
        }
    }

    //发送验证码
    public void codeMethod(String baseUrl, String keyStr, String valueStr, String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("phoneNum", LoginPhoneNume);
        map.put(keyStr, valueStr);
        xUtils3Http.get(Main_Novalidation_Code.this, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                startTimer();
            }

            @Override
            public void failed(String... args) {
            }
        });
    }

    //第一次设置密码问题校验与忘记密码校验
    public void ForgetCodeMethod(String baseUrl, String Code, String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("phoneNum", LoginPhoneNume);
        map.put("code", Code);
        xUtils3Http.get(Main_Novalidation_Code.this, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                if (TitleName.equals("手机号验证")) {
                    Me_Set_SecurityQuestion.actionStart(mContext,LoginPhoneNume);

                } else if (TitleName.equals("忘记密码")) {
                    Intent intent = new Intent(Main_Novalidation_Code.this, MainLogin_ForgetPsw_Question.class);
                    intent.putExtra("phoneNum", LoginPhoneNume);
                    startActivity(intent);
                }
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
