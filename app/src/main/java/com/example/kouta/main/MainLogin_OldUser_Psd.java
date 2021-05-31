package com.example.kouta.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.autofill.Transformation;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kouta.R;
import com.example.kouta.model.Olduser_Model;
import com.example.kouta.model.baseModel;
import com.example.kouta.network.saveFile;
import com.example.kouta.staticdata.NoDoubleClickListener;
import com.example.kouta.staticdata.StaticData;
import com.example.kouta.staticdata.viewTouchDelegate;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class MainLogin_OldUser_Psd extends AppCompatActivity {

    private Button right_Btn, num_Btn;
    private View code_txt;
    private View psw_txt;
    private ImageView fork_img;
    private EditText phone_edit, psw_edit;
    private CheckBox display_choice;

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

        return_Btn.setOnClickListener(new return_Btn());
        right_Btn.setOnClickListener(new right_BtnOnClickLister());
    }

    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }

    private class right_BtnOnClickLister extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Intent intent = new Intent(MainLogin_OldUser_Psd.this, MainLogin_QuestionFeedBack.class);
            startActivity(intent);
        }
    }


    private void initView() {
        code_txt = findViewById(R.id.code_txt);
        psw_txt = findViewById(R.id.psw_txt);
        phone_edit = findViewById(R.id.phone_edit);
        fork_img = findViewById(R.id.fork_img);
        display_choice = findViewById(R.id.display_choice);
        display_choice.setChecked(false);
        psw_edit = findViewById(R.id.psw_edit);
        num_Btn = findViewById(R.id.num_Btn);
        code_txt.setOnClickListener(new code_txtOnClick());
        psw_txt.setOnClickListener(new psw_txtOnClick());
        fork_img.setOnClickListener(new fork_imgOnClick());
        display_choice.setOnCheckedChangeListener(new display_choiceOnChecked());
        num_Btn.setOnClickListener(new num_BtnOnClick());
    }

    private class code_txtOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Intent intent = new Intent(MainLogin_OldUser_Psd.this, MainLogin_OldUser_Phone.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        }
    }

    private class psw_txtOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Intent intent = new Intent(MainLogin_OldUser_Psd.this, MainLogin_ForgetPsw_ID.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        }
    }


    private class fork_imgOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            phone_edit.setText("");
        }
    }

    private class display_choiceOnChecked implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                psw_edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                psw_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    private class num_BtnOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            String leId = phone_edit.getText().toString().trim();
            String psw = psw_edit.getText().toString().trim();
            if (leId.isEmpty() && psw.isEmpty()) {
                Toast.makeText(MainLogin_OldUser_Psd.this, "用户名与密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            initData(MainLogin_OldUser_Psd.this);
        }
    }

    private void initData(Context context) {
        String type = "2";
        String  equipmentName = android.os.Build.BRAND +"  "+ android.os.Build.MODEL;
        String meid = StaticData.getIMEI(this);
        String vision = android.os.Build.VERSION.RELEASE;
        String leId = phone_edit.getText().toString().trim();
        String psw = psw_edit.getText().toString().trim();
        LoginMethod(context, saveFile.BaseUrl + saveFile.User_Login_Url + "?equipmentName=" + equipmentName +
                "&loginType=" + type + "&meid=" + meid + "&vision=" + vision + "&userNum=" + leId + "&password=" + psw, type);

    }

    //登录
    public void LoginMethod(Context context, String baseUrl, String type) {
        RequestParams params = new RequestParams(baseUrl);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
                if (resultString != null) {
                    // {"msg":"操作成功","code":200}
                    Olduser_Model baseModel = new Gson().fromJson(resultString, Olduser_Model.class);
                    if (baseModel.getCode() == 200) {
//                        startTimer();

                        saveFile.saveShareData("JSESSIONID", baseModel.getData().getToken(), context);
                        saveFile.saveShareData("phoneNum",baseModel.getData().getPhoneNum(),context);
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
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