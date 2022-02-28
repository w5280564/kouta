package com.xunda.mo.main.login;

import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.hyphenate.easeui.utils.StringUtil;
import com.mcxtzhang.captchalib.SwipeCaptchaView;
import com.xunda.mo.R;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.StaticData;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.HashMap;
import java.util.Map;

public class MainLogin_ForgetPsw_OrQuestion extends AppCompatActivity {
    private Button right_Btn;
    private View num_Btn;
    private EditText phone_edit;
    private View fork_img;
    private String LoginID, titleName, phoneNumber, type;
    private TextView id_txt;


    /**
     * @param context
     * @param titleName
     * @param phoneNumber
     * @param LoginID
     * @param type        1是忘记密码ID验证  2是个人设置密码问题
     */
    public static void actionStart(Context context, String titleName, String phoneNumber, String LoginID, String type) {
        Intent intent = new Intent(context, MainLogin_ForgetPsw_OrQuestion.class);
        intent.putExtra("titleName", titleName);
        intent.putExtra("phoneNumber", phoneNumber);
        intent.putExtra("LoginID", LoginID);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainlogin_forgetpsw);
        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);

        LoginID = getIntent().getStringExtra("LoginID");
        titleName = getIntent().getStringExtra("titleName");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        type = getIntent().getStringExtra("type");
        initTitle();
        initView();
        initData();
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setElevation(2f);//阴影
        Button return_Btn = title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView contentTxt = title_Include.findViewById(R.id.cententtxt);
        contentTxt.setText(StringUtil.getStringValue(titleName));
        if (TextUtils.equals(type, "1")) {
            contentTxt.setText("输入手机号");
        }
        right_Btn = title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.GONE);
        return_Btn.setOnClickListener(new return_Btn());
    }

    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }

    private void initView() {
        num_Btn = findViewById(R.id.num_Btn);
        StaticData.changeShapColor(num_Btn, ContextCompat.getColor(this, R.color.grey));
        id_txt = findViewById(R.id.id_txt);
        phone_edit = findViewById(R.id.phone_edit);
        fork_img = findViewById(R.id.fork_img);
        num_Btn.setOnClickListener(new num_BtnOnClick());
        phone_edit.addTextChangedListener(new textchangerlister());
        fork_img.setOnClickListener(new fork_imgOnClick());
    }

    private void initData() {
        id_txt.setText("为保护您的账号安全，请您输入完整的手机号码：\n" + phoneNumber);
    }

    private class num_BtnOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            hideSoftInput(phone_edit);
            if (TextUtils.equals(type, "1")) {
                String edit_PhoneNumber = phone_edit.getText().toString().trim();
                checkPhoneNoSms(edit_PhoneNumber);
            } else if (TextUtils.equals(type, "2")) {
                CodeMore(MainLogin_ForgetPsw_OrQuestion.this, num_Btn, 0);
            }
        }
    }


    //校验手机号不用发短信
    public void checkPhoneNoSms(String edit_PhoneNumber) {
        Map<String, Object> map = new HashMap<>();
        map.put("phoneNum", edit_PhoneNumber);
        map.put("userNum", LoginID);
        xUtils3Http.get(MainLogin_ForgetPsw_OrQuestion.this, saveFile.User_checkPhoneNoSms_Url, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                MainLogin_Code.actionStart(MainLogin_ForgetPsw_OrQuestion.this, "手机号验证", edit_PhoneNumber, LoginID);
            }

            @Override
            public void failed(String... args) {
            }
        });
    }


    private class textchangerlister implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (StaticData.isPhone(s.toString())) {
                num_Btn.setEnabled(true);
                StaticData.changeShapColor(num_Btn, ContextCompat.getColor(MainLogin_ForgetPsw_OrQuestion.this, R.color.yellow));
            } else {
                num_Btn.setEnabled(false);
                StaticData.changeShapColor(num_Btn, ContextCompat.getColor(MainLogin_ForgetPsw_OrQuestion.this, R.color.grey));
            }
        }
    }


    private class fork_imgOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            phone_edit.setText("");
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
                MorePopup.dismiss();

                codeMethod(saveFile.User_checkPhone_Url, "userNum", LoginID);
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

    //发送验证码
    public void codeMethod(String baseUrl, String keyStr, String valueStr) {
        Map<String, Object> map = new HashMap<>();
        map.put("phoneNum", phone_edit.getText().toString().trim());
        map.put(keyStr, valueStr);
        xUtils3Http.get(MainLogin_ForgetPsw_OrQuestion.this, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                String phoneNumber = phone_edit.getText().toString().trim();
                Main_Novalidation_Code.actionStart(MainLogin_ForgetPsw_OrQuestion.this, titleName, phoneNumber, LoginID);
            }

            @Override
            public void failed(String... args) {
            }
        });
    }

    /**
     * 关闭软键盘 *针对于 有一个EdtxtView * @param input_email
     */
    public void hideSoftInput(EditText input_email) {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            // 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
            inputMethodManager.hideSoftInputFromWindow(input_email.getWindowToken(), 0);
        }
    }


}