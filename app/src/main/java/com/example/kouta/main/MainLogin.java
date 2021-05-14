package com.example.kouta.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kouta.R;
import com.example.kouta.baseview.MyActivityManager;
import com.example.kouta.staticdata.NoDoubleClickListener;
import com.example.kouta.staticdata.StaticData;
import com.example.kouta.staticdata.viewTouchDelegate;

import org.xutils.x;

import static org.xutils.common.util.DensityUtil.dip2px;

public class MainLogin extends AppCompatActivity {

    private MyActivityManager mam;
    private Button right_Btn;
    private Button num_Btn;
    private EditText phone_edit;
    private Button oldusers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        mam = MyActivityManager.getInstance();
        mam.pushOneActivity(this);//把当前activity压入了栈中
        x.view().inject(this);

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
        cententTxt.setText("手机号注册");
        right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.VISIBLE);
        right_Btn.setText("问题反馈");
//        StaticData.ViewScale(return_Btn, 100, 100);
//        StaticData.ViewScale(title_Include, 0, 88);

        return_Btn.setOnClickListener(new return_Btn());
//        right_Btn.setOnClickListener(new right_Btn());
    }

    private class return_Btn implements View.OnClickListener {
        @Override
        public void onClick(View view) {
//            if (!StaticData.isSpace(content_Edit.getText().toString()) || photoPaths.size() > 0) {
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//关闭键盘
//                imm.hideSoftInputFromWindow(content_Edit.getWindowToken(), 0);
//                savePop = new save_Popup(PostingActivity.this, right_Btn);
//            } else {
//                finish();
//            }
            finish();
//            Toast.makeText(MainLogin.this,"该用户不存在",Toast.LENGTH_SHORT).show();

        }
    }


    private void initView() {
//        RelativeLayout edit_Rel = (RelativeLayout) findViewById(R.id.edit_Rel);
        phone_edit = findViewById(R.id.phone_edit);
        num_Btn = findViewById(R.id.num_Btn);
        oldusers = findViewById(R.id.oldusers);

        TextView login_txt = findViewById(R.id.login_txt);

//        if (!StaticData.isPhone(phone_edit.getText().toString())){
//            Toast.makeText(this,"请输入正确手机号码",Toast.LENGTH_SHORT).show();
//            return;
//        }

        phone_edit.addTextChangedListener(new textchangerlister());
        num_Btn.setOnClickListener(new num_BtnOnClick());
        login_txt.setOnClickListener(new login_txtOnClick());
        oldusers.setOnClickListener(new oldusers_BtnOnClick());
    }


    private class textchangerlister implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {


        }

        @Override
        public void afterTextChanged(Editable s) {
            if (StaticData.isPhone(s.toString())) {
                num_Btn.setEnabled(true);
                StaticData.changeShapColor(num_Btn, ContextCompat.getColor(MainLogin.this, R.color.yellow));
            } else {
                num_Btn.setEnabled(false);
                StaticData.changeShapColor(num_Btn, ContextCompat.getColor(MainLogin.this, R.color.grey));
            }

        }

    }

    private class num_BtnOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Intent intent = new Intent(MainLogin.this, MainLogin_Code.class);
            intent.putExtra("LoginPhoneNume", phone_edit.getText().toString());
            startActivity(intent);

        }
    }

    private class login_txtOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent Intent = new Intent(MainLogin.this, MainLogin_Agreement.class);
            startActivity(Intent);
        }
    }

    private class oldusers_BtnOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Intent intent = new Intent(MainLogin.this, MainLogin_OldUsers.class);
            intent.putExtra("LoginPhoneNume", phone_edit.getText().toString());
//            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        }
    }
}