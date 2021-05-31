package com.example.kouta.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.example.kouta.R;
import com.example.kouta.network.saveFile;

import org.xutils.x;

import static com.example.kouta.network.saveFile.saveShareData;

public class Main_Launch extends AppCompatActivity {


    private String FIRSTINIT = "firstinit";
    private SharedPreferences preferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //不显示系统的标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activitymain_launch);
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
//        Float scale = (float) dm.widthPixels / 750;
//        Float heghtSclae = (float) dm.heightPixels / 1334;
//        saveShareData("scale", scale + "", this);
//        saveShareData("heghtSclae", heghtSclae + "", this);

//		JPushInterface.resumePush(getApplicationContext());//推送注册
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

//        SimpleDraweeView lunchimg = (SimpleDraweeView) findViewById(R.id.lunchimg);
//        Uri imgurl = Uri.parse("res:// /" + R.drawable.launch_icon);
//        lunchimg.setImageURI(imgurl);

        x.view().inject(this);

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
                    Intent intent = new Intent(Main_Launch.this, MainActivity.class);
                    startActivity(intent);
                    finish();

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

}