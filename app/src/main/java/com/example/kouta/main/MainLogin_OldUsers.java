package com.example.kouta.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kouta.R;
import com.example.kouta.staticdata.NoDoubleClickListener;
import com.example.kouta.staticdata.StaticData;

public class MainLogin_OldUsers extends AppCompatActivity {

    private Button num_Btn;
    private TextView psw_txt;
    private String LoginPhoneNume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login_old_users);


        initView();

    }

    //栈复用页面
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initView();
    }

    private void initView() {
         LoginPhoneNume = getIntent().getStringExtra("LoginPhoneNume");
        num_Btn = findViewById(R.id.num_Btn);
        psw_txt = findViewById(R.id.psw_txt);
        StaticData.changeShapColor(num_Btn, ContextCompat.getColor(MainLogin_OldUsers.this, R.color.yellow));
        num_Btn.setOnClickListener(new num_BtnOnClick());
        psw_txt.setOnClickListener(new psw_txtOnClick());
    }

    private class num_BtnOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Intent intent = new Intent(MainLogin_OldUsers.this, MainLogin_Code.class);
            intent.putExtra("LoginPhoneNume", LoginPhoneNume);
            startActivity(intent);

        }
    }

    private class psw_txtOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Intent intent = new Intent(MainLogin_OldUsers.this, MainLogin_OldUser_Psd.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        }
    }
}