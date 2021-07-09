package com.xunda.mo.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xunda.mo.R;
import com.xunda.mo.model.Main_ForgetPsw_Model;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.StaticData;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

public class MainLogin_ForgetPsw extends AppCompatActivity {

    private Button right_Btn;
    private View num_Btn;
    private View nonecode_txt;
    private EditText phone_edit;
    private View fork_img;
    private String LoginID;
    private TextView id_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainlogin_forgetpsw);

        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);

        LoginID = getIntent().getStringExtra("LoginID");
        initTitle();
        initView();
        initData();
//        String phonestr = "18721666525";
//        String replace = phonestr.substring(4, phonestr.length());
//        String newStr = phonestr.replace(replace, "****");
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
        cententTxt.setText("忘记密码");
        right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.GONE);


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
//        LoginPhoneNume = getIntent().getStringExtra("LoginPhoneNume");
        num_Btn = findViewById(R.id.num_Btn);
        StaticData.changeShapColor(num_Btn, ContextCompat.getColor(this, R.color.grey));
//        psw_txt = findViewById(R.id.psw_txt);
        id_txt = findViewById(R.id.id_txt);
        phone_edit = findViewById(R.id.phone_edit);
        fork_img = findViewById(R.id.fork_img);
        num_Btn.setOnClickListener(new num_BtnOnClick());
        phone_edit.addTextChangedListener(new textchangerlister());
        fork_img.setOnClickListener(new fork_imgOnClick());
//        psw_txt.setOnClickListener(new MainLogin_OldUsers.psw_txtOnClick());
    }

    private void initData() {
        baseMethod(MainLogin_ForgetPsw.this, saveFile.BaseUrl + saveFile.User_GetPhone_Url + "?userNum=" + LoginID, "0");
    }

    private class num_BtnOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            if (!phone_edit.getText().toString().equals(baseModel.getData())){
//            if (!StaticData.isPhone(phone_edit.getText().toString())) {
                Toast.makeText(MainLogin_ForgetPsw.this, "请输入ID对应的手机号", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainLogin_ForgetPsw.this, MainLogin_Code.class);
            intent.putExtra("TitleName", "忘记密码");
            intent.putExtra("LoginPhoneNume", phone_edit.getText().toString());
            intent.putExtra("userNum", LoginID);
            startActivity(intent);
        }
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
                StaticData.changeShapColor(num_Btn, ContextCompat.getColor(MainLogin_ForgetPsw.this, R.color.yellow));
            } else {
//                num_Btn.setEnabled(false);
                StaticData.changeShapColor(num_Btn, ContextCompat.getColor(MainLogin_ForgetPsw.this, R.color.grey));
            }

        }

    }


    private class fork_imgOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            phone_edit.setText("");
        }
    }

    //
    Main_ForgetPsw_Model baseModel;
    public void baseMethod(Context context, String baseUrl, String type) {
        RequestParams params = new RequestParams(baseUrl);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
                if (resultString != null) {
                    // {"msg":"操作成功","code":200}
                     baseModel = new Gson().fromJson(resultString, Main_ForgetPsw_Model.class);
                    if (baseModel.getCode() == 200) {
//                        startTimer();
                        id_txt.setText("为保护您的账号安全，请您输入完整的手机号码：\n"+baseModel.getData());
                    } else {
                        Toast.makeText(context, baseModel.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT).show();
                }
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