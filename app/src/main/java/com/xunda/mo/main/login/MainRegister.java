package com.xunda.mo.main.login;

import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.xunda.mo.R;
import com.xunda.mo.main.baseView.MyActivityManager;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.StaticData;
import com.xunda.mo.staticdata.viewTouchDelegate;

public class MainRegister extends AppCompatActivity {

    private MyActivityManager mam;
    private Button right_Btn;
    private Button num_Btn;
    private EditText phone_edit;
    private Button oldusers;
    private CheckBox agreement_choice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);

//        mam = MyActivityManager.getInstance();
//        mam.pushOneActivity(this);//把当前activity压入了栈中


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

        return_Btn.setOnClickListener(new return_Btn());
        right_Btn.setOnClickListener(new right_BtnOnClickLister());
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
        phone_edit = findViewById(R.id.phone_edit);
        num_Btn = findViewById(R.id.num_Btn);
        agreement_choice = findViewById(R.id.agreement_choice);
//        num_Btn.setEnabled(false);
        StaticData.changeShapColor(num_Btn, ContextCompat.getColor(MainRegister.this, R.color.grey));
        oldusers = findViewById(R.id.oldusers);

        TextView login_txt = findViewById(R.id.login_txt);


        phone_edit.addTextChangedListener(new textchangerlister());
        num_Btn.setOnClickListener(new num_BtnOnClick());
        login_txt.setOnClickListener(new login_txtOnClick());
//        oldusers.setOnClickListener(new oldusers_BtnOnClick());
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
                StaticData.changeShapColor(num_Btn, ContextCompat.getColor(MainRegister.this, R.color.yellow));
            } else {
//                num_Btn.setEnabled(false);
                StaticData.changeShapColor(num_Btn, ContextCompat.getColor(MainRegister.this, R.color.grey));
            }

        }

    }

    private class num_BtnOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            if (!StaticData.isPhone(phone_edit.getText().toString())) {
                Toast.makeText(MainRegister.this, "请输入正确手机号码", Toast.LENGTH_SHORT).show();
                return;
            } else if (!agreement_choice.isChecked()) {
                Toast.makeText(MainRegister.this, "请阅读并同意用户使用协议", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainRegister.this, MainLogin_Code.class);
            intent.putExtra("TitleName", "手机号注册");
            intent.putExtra("LoginPhoneNume", phone_edit.getText().toString());
            startActivity(intent);

        }
    }

    private class login_txtOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
//            Intent Intent = new Intent(MainRegister.this, MainRegister_Agreement.class);
//            startActivity(Intent);
            String  url =  "file:///android_asset/service.html";
            MainRegister_Agreement.actionStart(MainRegister.this,url);
        }
    }

    private class oldusers_BtnOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Intent intent = new Intent(MainRegister.this, MainLogin_OldUsers.class);
            intent.putExtra("LoginPhoneNume", phone_edit.getText().toString());
//            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        }
    }

    private class right_BtnOnClickLister extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Intent intent = new Intent(MainRegister.this, MainLogin_QuestionFeedBack.class);
            startActivity(intent);
        }
    }
}