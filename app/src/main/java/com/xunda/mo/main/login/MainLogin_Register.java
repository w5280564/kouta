package com.xunda.mo.main.login;

import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.xunda.mo.R;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;

public class MainLogin_Register extends AppCompatActivity {

    private Button login_Btn, regster_Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain_login_register);

        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);

        if (saveFile.getShareData("phoneNum", MainLogin_Register.this).equals("false")) {
//            Intent intent = new Intent(MainRegister.this, MainLogin_Register.class);
//            startActivity(intent);
//            finish();
        } else {
            Intent intent = new Intent(MainLogin_Register.this, MainLogin_OldUsers.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        initView();

        if (getFirstInit()) {
            startDialog();
        }
    }

    private void initView() {
        login_Btn = findViewById(R.id.login_Btn);
        regster_Btn = findViewById(R.id.regster_Btn);


        login_Btn.setOnClickListener(new login_Btnlister());
        regster_Btn.setOnClickListener(new regster_BtnOnClick());
    }


    private class login_Btnlister extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Intent intent = new Intent(MainLogin_Register.this, MainLogin_OldUser_Psd.class);
            startActivity(intent);

        }
    }

    private class regster_BtnOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Intent intent = new Intent(MainLogin_Register.this, MainRegister.class);
            startActivity(intent);
        }
    }
    private void startDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(MainLogin_Register.this).inflate(R.layout.dialog_initmate, null);
        alertDialog.setView(view);
        alertDialog.show();
        alertDialog.setCancelable(false);
        TextView tvContent = view.findViewById(R.id.tv_content);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        TextView tvAgree = view.findViewById(R.id.tv_agree);

        String str = "    用户协议和隐私政策请您务必审慎阅读、充分理解“用户协议”和“隐私政策”各项条款，" +
                "包括但不限于:为了向您提供即时通讯、内容分享等服务,我们需要收集您的设备信息、操作日志等个人信息。" +
                "您可以在“设置”中查看、变更、删除个人信息并管理您的授权。您可阅读《用户协议》和《隐私政策》了解详细信息。如您同意,请点击“同意”开始接受我们的服务。";

        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(str);
        final int start = str.indexOf("《");//第一个出现的位置
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                String  url =  "file:///android_asset/privacy.html";
                MainRegister_Agreement.actionStart(MainLogin_Register.this,url);
//                Toast.makeText(MainLogin_Register.this, "《隐私政策》", Toast.LENGTH_SHORT).show();
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
                String  url =  "file:///android_asset/service.html";
                MainRegister_Agreement.actionStart(MainLogin_Register.this,url);
//                Toast.makeText(MainLogin_Register.this, "《用户协议》", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.blue));
                ds.setUnderlineText(false);
            }
        }, end, end + 6, 0);

        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        tvContent.setText(ssb, TextView.BufferType.SPANNABLE);
        tvCancel.setOnClickListener(v -> {
            alertDialog.cancel();
            finish();
        });

        tvAgree.setOnClickListener(v -> {
            saveFirstInit(false);
            alertDialog.cancel();
        });

    }

    public void saveFirstInit(boolean is) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("launch", is);
        editor.commit();
    }

    public boolean getFirstInit() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean("launch", true);
    }


}