package com.xunda.mo.main.login;

import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

import com.google.gson.Gson;
import com.xunda.mo.R;
import com.xunda.mo.model.Main_Register_Model;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.StaticData;
import com.xunda.mo.staticdata.viewTouchDelegate;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

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
        choice_check = findViewById(R.id.choice_check);
        choice_check.setChecked(false);
        pswagin_edit = findViewById(R.id.pswagin_edit);
        StaticData.changeShapColor(num_Btn, ContextCompat.getColor(this, R.color.yellow));
        choice_check.setOnCheckedChangeListener(new choice_checkOnChackedLister());
        num_Btn.setOnClickListener(new num_BtnOnClickLister());
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
            String psw = psw_edit.getText().toString().trim();
            String pswAgin = pswagin_edit.getText().toString().trim();
            if (StaticData.isPasswordForm(psw) && StaticData.isPasswordForm(pswAgin) && pswAgin.equals(psw)) {


            } else {
                Toast.makeText(MainLogin_ForgetPsw_SetPsw.this, "密码必须包含字母和数字，长度至少8位", Toast.LENGTH_SHORT).show();
                return;
            }

        }
    }

    //
    public void ChangeMethod(Context context, final String baseUrl) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("phoneNum", phoneNum);
            obj.put("password", pswagin_edit.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(baseUrl);
        params.setAsJsonContent(true);
        params.setBodyContent(obj.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
                if (resultString != null) {
                    Main_Register_Model baseModel = new Gson().fromJson(resultString, Main_Register_Model.class);
                    if (baseModel.getCode() == 200) {
                        Intent intent = new Intent(MainLogin_ForgetPsw_SetPsw.this, MainLogin_OldUser_Psd.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(context, baseModel.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT).show();
                }
//                    JPushInterface.resumePush(Man_Login.this);//注册
//                    JPushInterface.setAliasAndTags(Man_Login.this,JsonGet.getReturnValue(resultString, "userid"),null);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {

            }
        });
    }


}