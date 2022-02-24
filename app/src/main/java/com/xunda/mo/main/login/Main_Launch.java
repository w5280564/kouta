package com.xunda.mo.main.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;

import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.util.EMLog;
import com.xunda.mo.R;
import com.xunda.mo.hx.common.interfaceOrImplement.OnResourceParseCallback;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.login.viewmodels.SplashViewModel;
import com.xunda.mo.main.MainActivity;
import com.xunda.mo.main.info.MyInfo;

import org.xutils.x;

public class Main_Launch extends BaseInitActivity {
    private SplashViewModel model;
    private SharedPreferences preferences = null;

    @Override
    protected int getLayoutId() {
        return R.layout.activitymain_launch;
    }

//    @Override
//    protected void initSystemFit() {
//        StatusBar(this);
//        MIUISetStatusBarLightMode(this.getWindow(), true);
//        FlymeSetStatusBarLightMode(this.getWindow(), true);
//        //不显示系统的标题栏
////        getWindow().setFlags(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setFitSystemForTheme(true, R.color.launch_black);
        setStatusBarTextColor(true);
    }

    @Override
    protected void initData() {
        super.initData();
        x.view().inject(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        model = new ViewModelProvider(this).get(SplashViewModel.class);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isLogin()) {
                    Intent intent = new Intent(Main_Launch.this, MainLogin_Register.class);
                    startActivity(intent);
                    finish();
                } else {
                    loginSDK();
                }
            }

        }, 3000);
    }



    public boolean isLogin() {
        MyInfo myInfo = new MyInfo(this);

        if (myInfo.getUserInfo()== null) {
            return false;
        }

        return true;
    }

    //屏蔽返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return false;
    }

    private void loginSDK() {
        model.getLoginData().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>(true) {
                @Override
                public void onSuccess(Boolean data) {
                    MainActivity.startAction(Main_Launch.this);
                    finish();
                }
                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                    EMLog.i("TAG", "error message = " + response.getMessage());
                    Intent intent = new Intent(Main_Launch.this, MainLogin_Register.class);
                    startActivity(intent);
                    finish();
                }
            });

        });
    }




}