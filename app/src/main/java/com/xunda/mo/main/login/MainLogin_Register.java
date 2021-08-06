package com.xunda.mo.main.login;

import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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


}