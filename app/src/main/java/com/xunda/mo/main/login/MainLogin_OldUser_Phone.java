package com.xunda.mo.main.login;

import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.xunda.mo.R;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.StaticData;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

public class MainLogin_OldUser_Phone extends AppCompatActivity {

    private Button right_Btn;
    private View num_Btn;
    private TextView code_txt;
    private EditText phone_edit;
    private View fork_img;
    private CheckBox agreement_choice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainlogin_olduser_phone);

        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);

//        mam = MyActivityManager.getInstance();
//        mam.pushOneActivity(this);
        initTitle();
        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initTitle();
        initView();
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
        cententTxt.setText("验证码登录");
        right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.VISIBLE);
        right_Btn.setText("问题反馈");
//        StaticData.ViewScale(return_Btn, 100, 100);
//        StaticData.ViewScale(title_Include, 0, 88);

        return_Btn.setOnClickListener(new return_Btn());
        right_Btn.setOnClickListener(new right_BtnOnClickLister());
    }

    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }

    private class right_BtnOnClickLister extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Intent intent = new Intent(MainLogin_OldUser_Phone.this, MainLogin_QuestionFeedBack.class);
            startActivity(intent);
        }
    }


    private void initView() {
//        LoginPhoneNume = getIntent().getStringExtra("LoginPhoneNume");
        num_Btn = findViewById(R.id.num_Btn);
//        psw_txt = findViewById(R.id.psw_txt);
        code_txt = findViewById(R.id.code_txt);
        phone_edit = findViewById(R.id.phone_edit);
        fork_img = findViewById(R.id.fork_img);
        StaticData.changeShapColor(num_Btn, ContextCompat.getColor(this, R.color.grey));
        num_Btn.setOnClickListener(new MainLogin_OldUser_Phone.num_BtnOnClick());
        code_txt.setOnClickListener(new MainLogin_OldUser_Phone.code_txtOnClick());
        fork_img.setOnClickListener(new fork_imgOnClick());
        phone_edit.addTextChangedListener(new textchangerlister());
        agreement_choice = findViewById(R.id.agreement_choice);
        TextView login_txt = findViewById(R.id.login_txt);
        setSe(login_txt);
    }

    private void setSe(TextView login_txt){
        String str = "登录即视为同意《用户协议》和《隐私政策》";
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(str);
        final int start = str.indexOf("《");//第一个出现的位置
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                String url = xUtils3Http.BASE_URL + "service.html";
                MainRegister_Agreement.actionStart(MainLogin_OldUser_Phone.this, url);
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.blue));
                ds.setUnderlineText(false);
            }
        }, start, start + 6, 0);
        int end = str.lastIndexOf("《");
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                String url = xUtils3Http.BASE_URL + "privacy.html";
                MainRegister_Agreement.actionStart(MainLogin_OldUser_Phone.this, url);
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.blue));
                ds.setUnderlineText(false);
            }
        }, end, end + 6, 0);
        login_txt.setMovementMethod(LinkMovementMethod.getInstance());
        login_txt.setText(ssb, TextView.BufferType.SPANNABLE);
    }


    private class num_BtnOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            if (!StaticData.isPhone(phone_edit.getText().toString())){
                Toast.makeText(MainLogin_OldUser_Phone.this, "请输入正确手机号码", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!agreement_choice.isChecked()) {
                Toast.makeText(MainLogin_OldUser_Phone.this, "请阅读并同意用户协议和隐私政策", Toast.LENGTH_SHORT).show();
                return;
            }
            MainLogin_Code.actionStart(MainLogin_OldUser_Phone.this, "验证码登录", phone_edit.getText().toString());
        }
    }


    public class code_txtOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Intent intent = new Intent(MainLogin_OldUser_Phone.this, MainLogin_OldUser_Psd.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        }
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
                StaticData.changeShapColor(num_Btn, ContextCompat.getColor(MainLogin_OldUser_Phone.this, R.color.yellow));
            } else {
//                num_Btn.setEnabled(false);
                StaticData.changeShapColor(num_Btn, ContextCompat.getColor(MainLogin_OldUser_Phone.this, R.color.grey));
            }

        }

    }


    private class fork_imgOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            phone_edit.setText("");
        }
    }
}