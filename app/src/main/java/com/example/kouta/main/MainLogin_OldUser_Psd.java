package com.example.kouta.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kouta.R;
import com.example.kouta.staticdata.NoDoubleClickListener;
import com.example.kouta.staticdata.StaticData;
import com.example.kouta.staticdata.viewTouchDelegate;

public class MainLogin_OldUser_Psd extends AppCompatActivity {

    private Button right_Btn;
    private View code_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainlogin_olduserpsd);

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
        cententTxt.setText("登录");
        right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.VISIBLE);
        right_Btn.setText("问题反馈");
//        StaticData.ViewScale(return_Btn, 100, 100);
//        StaticData.ViewScale(title_Include, 0, 88);

        return_Btn.setOnClickListener(new return_Btn());
//        right_Btn.setOnClickListener(new right_Btn());
    }

    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }


    private void initView() {
        code_txt = findViewById(R.id.code_txt);
        code_txt.setOnClickListener(new code_txtOnClick());

    }

    private class code_txtOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Intent intent = new Intent(MainLogin_OldUser_Psd.this, MainLogin_OldUser_Phone.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        }
    }

}