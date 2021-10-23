package com.xunda.mo.main.login;

import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.xunda.mo.R;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.StaticData;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.HashMap;
import java.util.Map;

public class MainLogin_ForgetPsw_SetPsw extends AppCompatActivity {

    private View num_Btn;
    private EditText psw_edit, pswagin_edit;
    private CheckBox choice_check;
    private String phoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainlogin_forgetpsw_setpsw);
        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);


        phoneNum = getIntent().getStringExtra("phoneNum");
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
        cententTxt.setText("登录密码");
//        right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
//        right_Btn.setVisibility(View.GONE);


        return_Btn.setOnClickListener(new MainLogin_ForgetPsw_SetPsw.return_Btn());
//        right_Btn.setOnClickListener(new right_Btn());
    }

    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }

    private void initView() {
        num_Btn = findViewById(R.id.num_Btn);
        psw_edit = findViewById(R.id.psw_edit);
        pswagin_edit = findViewById(R.id.pswagin_edit);
        StaticData.changeShapColor(num_Btn, ContextCompat.getColor(this, R.color.yellow));
        num_Btn.setOnClickListener(new num_BtnOnClickLister());
        choice_check = findViewById(R.id.choice_check);
        viewTouchDelegate.expandViewTouchDelegate(choice_check,50,50,50,50);
        choice_check.setChecked(false);
        choice_check.setOnCheckedChangeListener(new choice_checkOnChackedLister());
    }


    private class choice_checkOnChackedLister implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                pswagin_edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                psw_edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // 隐藏密码
                psw_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                pswagin_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }


    private class num_BtnOnClickLister extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            String newPsw = psw_edit.getText().toString().trim();
            String pswAgain = pswagin_edit.getText().toString().trim();
            if(!StaticData.isPasswordForm(newPsw) || !StaticData.isPasswordForm(pswAgain)){
                Toast.makeText(MainLogin_ForgetPsw_SetPsw.this,"密码不合规",Toast.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.equals(pswAgain,newPsw)){
                Toast.makeText(MainLogin_ForgetPsw_SetPsw.this,"新密码不一致",Toast.LENGTH_SHORT).show();
                return;
            }

            ChangeMethod(MainLogin_ForgetPsw_SetPsw.this, saveFile.User_forgetPassword_Url);
        }
    }

    public void ChangeMethod(Context context, final String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("phoneNum", phoneNum);
        map.put("password", pswagin_edit.getText().toString().trim());
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Toast.makeText(context,"设置成功请重新登录",Toast.LENGTH_SHORT).show();
                //回到账号登录页面
                Intent intent = new Intent(MainLogin_ForgetPsw_SetPsw.this, MainLogin_OldUser_Psd.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            public void failed(String... args) {

            }
        });
    }


}