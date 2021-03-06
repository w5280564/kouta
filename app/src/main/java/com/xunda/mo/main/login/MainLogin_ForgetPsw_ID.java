package com.xunda.mo.main.login;

import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.xunda.mo.R;
import com.xunda.mo.model.Main_ForgetPsw_Model;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.StaticData;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.HashMap;
import java.util.Map;

public class MainLogin_ForgetPsw_ID extends AppCompatActivity {

    private Button right_Btn;
    private EditText id_edit;
    private View fork_img;
    private View next_Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain_loginforget_psw_id);

        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);

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
        cententTxt.setText("输入ID信息");
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
        next_Btn = findViewById(R.id.next_Btn);
//        StaticData.changeShapColor(num_Btn, ContextCompat.getColor(this, R.color.grey));
        id_edit = findViewById(R.id.id_edit);
        id_edit.addTextChangedListener(new textchangerLister());
        fork_img = findViewById(R.id.fork_img);
        next_Btn.setOnClickListener(new next_BtnOnClick());
        fork_img.setOnClickListener(new fork_imgOnClick());
    }

    private class textchangerLister implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(s.toString())){
                if (s.length() == 7) {
                    next_Btn.setEnabled(true);
                    StaticData.changeShapColor(next_Btn, ContextCompat.getColor(MainLogin_ForgetPsw_ID.this, R.color.yellow));
                } else {
                    next_Btn.setEnabled(false);
                    StaticData.changeShapColor(next_Btn, ContextCompat.getColor(MainLogin_ForgetPsw_ID.this, R.color.grey));
                }
            }

        }
    }


    private class fork_imgOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            id_edit.setText("");
        }
    }

    private class next_BtnOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            String LoginID = id_edit.getText().toString();
            if (LoginID.isEmpty()){
                Toast.makeText(MainLogin_ForgetPsw_ID.this, "请输入ID", Toast.LENGTH_SHORT).show();
                return;
            }

            baseMethod(MainLogin_ForgetPsw_ID.this, saveFile.User_GetPhone_Url,LoginID);

        }
    }

    public void baseMethod(Context context, String baseUrl, String LoginID) {
        Map<String, Object> map = new HashMap<>();
        map.put("userNum", LoginID);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Main_ForgetPsw_Model baseModel = new Gson().fromJson(result, Main_ForgetPsw_Model.class);
                String phoneNumber = baseModel.getData();
                String titleName = "忘记密码";
                String type = "1";
                MainLogin_ForgetPsw_OrQuestion.actionStart(MainLogin_ForgetPsw_ID.this, titleName,phoneNumber,LoginID,type);
            }

            @Override
            public void failed(String... args) {
            }
        });

    }

}