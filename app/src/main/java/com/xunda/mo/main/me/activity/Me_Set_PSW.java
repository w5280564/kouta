package com.xunda.mo.main.me.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import androidx.core.content.ContextCompat;

import com.hyphenate.EMCallBack;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.main.login.MainLogin_ForgetPsw_ID;
import com.xunda.mo.main.login.MainLogin_Register;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.StaticData;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.HashMap;
import java.util.Map;

public class Me_Set_PSW extends BaseInitActivity {


    private EditText old_Psw, new_Psw,new_PswAgain;
    private Button num_Btn;
    private TextView moID_Txt;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Me_Set_PSW.class);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.me_set_psw;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();
        moID_Txt = findViewById(R.id.moID_Txt);
        old_Psw = findViewById(R.id.old_Psw);
        new_Psw = findViewById(R.id.new_Psw);
        new_PswAgain = findViewById(R.id.new_PswAgain);
        num_Btn = findViewById(R.id.num_Btn);
        num_Btn.setOnClickListener(new num_BtnClick());
        CheckBox choice_check = findViewById(R.id.choice_check);
        viewTouchDelegate.expandViewTouchDelegate(choice_check,50,50,50,50);
        choice_check.setOnCheckedChangeListener(new choice_checkOnChackedLister());
      TextView  psw_txt = findViewById(R.id.psw_txt);
        psw_txt.setOnClickListener(new psw_txtClick());
    }

    @Override
    protected void initData() {
        super.initData();
        MyInfo myInfo = new MyInfo(mContext);
        String moID = String.format("Mo ID：%1$s",myInfo.getUserInfo().getUserNum());
        moID_Txt.setText(moID);
        int oldPsd = myInfo.getUserInfo().getIsHasLoginPassword();
        old_Psw.setVisibility(View.VISIBLE);
        if (oldPsd == 0){
            old_Psw.setVisibility(View.GONE);
        }
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setElevation(2f);//阴影
        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("登录密码");
        Button right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.GONE);
        return_Btn.setOnClickListener(new return_Btn());
    }

    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }

    private class psw_txtClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Intent intent = new Intent(mContext, MainLogin_ForgetPsw_ID.class);
            startActivity(intent);
        }
    }


    private class choice_checkOnChackedLister implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                old_Psw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                new_Psw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                new_PswAgain.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // 隐藏密码
                old_Psw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                new_Psw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                new_PswAgain.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    private class num_BtnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String oldPsw = old_Psw.getText().toString().trim();
            String newPsw = new_Psw.getText().toString().trim();
            String pswAgain = new_PswAgain.getText().toString().trim();
//            if(!StaticData.isPasswordForm(oldPsw)){//旧密码可以为空
//                Toast.makeText(mContext,"密码不合规",Toast.LENGTH_SHORT).show();
//            }
            if(!StaticData.isPasswordForm(newPsw) || !StaticData.isPasswordForm(pswAgain)){
                Toast.makeText(mContext,"密码不合规",Toast.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.equals(pswAgain,newPsw)){
                Toast.makeText(mContext,"新密码不一致",Toast.LENGTH_SHORT).show();
                return;
            }

            ChangeMethod(mContext, saveFile.User_PSW);
        }
    }

    public void ChangeMethod(Context context, final String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("oldPassword", old_Psw.getText().toString().trim());
        map.put("newPassword", new_PswAgain.getText().toString().trim());
        map.put("type", "1");
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Toast.makeText(mContext,"修改成功,请重新登录",Toast.LENGTH_SHORT).show();
                logout();
            }

            @Override
            public void failed(String... args) {

            }
        });
    }

    void logout() {
        final ProgressDialog pd = new ProgressDialog(this);
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        DemoHelper.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    pd.dismiss();
                    // show login screen
                    MyInfo myInfo = new MyInfo(mContext);
                    myInfo.clearInfoData(mContext);
                    saveFile.clearShareData("JSESSIONID", mContext);
                    Intent intent = new Intent(mContext, MainLogin_Register.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(() -> {
                    pd.dismiss();
                    Toast.makeText(mContext, "退出失败", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

}