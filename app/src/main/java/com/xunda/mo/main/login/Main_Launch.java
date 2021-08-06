package com.xunda.mo.main.login;

import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.WindowManager;

import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.util.EMLog;
import com.xunda.mo.R;
import com.xunda.mo.hx.common.interfaceOrImplement.OnResourceParseCallback;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.login.viewmodels.SplashViewModel;
import com.xunda.mo.main.MainActivity;

import org.xutils.x;

public class Main_Launch extends BaseInitActivity {
    private SplashViewModel model;

    private String FIRSTINIT = "firstinit";
    private SharedPreferences preferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);
        //不显示系统的标题栏
        getWindow().setFlags(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activitymain_launch);
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();

//		JPushInterface.resumePush(getApplicationContext());//推送注册
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        x.view().inject(this);
        model = new ViewModelProvider(this).get(SplashViewModel.class);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getFirstInit()) {
                    saveFirstInit(false);
//                    Intent i = new Intent(Main_Launch.this, WelcomeNew.class);
//                    startActivity(i);
//                    finish();
                    Intent intent = new Intent(Main_Launch.this, MainLogin_Register.class);
                    startActivity(intent);
                    finish();
                } else {
                    loginSDK();

                }
            }

        }, 500);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activitymain_launch;
    }

    @Override
    protected void initSystemFit() {
        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);
        //不显示系统的标题栏
        getWindow().setFlags(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

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
                if (getFirstInit()) {
                    saveFirstInit(false);
//                    Intent i = new Intent(Main_Launch.this, WelcomeNew.class);
//                    startActivity(i);
//                    finish();
                    Intent intent = new Intent(Main_Launch.this, MainLogin_Register.class);
                    startActivity(intent);
                    finish();
                } else {
                    loginSDK();
                }
            }

        }, 500);
    }

    public void saveFirstInit(boolean is) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(FIRSTINIT, is);
        editor.commit();
    }


    public boolean getFirstInit() {
        return preferences.getBoolean(FIRSTINIT, true);
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
                    Intent intent = new Intent(Main_Launch.this, MainActivity.class);
                    startActivity(intent);
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