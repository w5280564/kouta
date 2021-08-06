package com.xunda.mo.main.friend;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.xunda.mo.R;
import com.xunda.mo.model.Friend_Details_Bean;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.SetStatusBar;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.HashMap;
import java.util.Map;

public class Friend_Detalis extends AppCompatActivity {

    private Button right_Btn;
    private String friendUserId;
    private SimpleDraweeView person_img;
    private TextView name_txt, moid_txt, lv_txt;
    private TextView cententTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_detalis);


        SetStatusBar.StatusBar(this);
        SetStatusBar.MIUISetStatusBarLightMode(this.getWindow(), true);
        SetStatusBar.FlymeSetStatusBarLightMode(this.getWindow(), true);

//        friendUserId
        Intent intent = getIntent();
        friendUserId = intent.getStringExtra("friendUserId");
        initTitle();
        initView();
        initData();
    }


    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            title_Include.setElevation(2f);//阴影
        }
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("名字");
        right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.VISIBLE);
        right_Btn.setBackgroundResource(R.mipmap.adress_head_more);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        right_Btn.setLayoutParams(layoutParams);
//        right_Btn.setText("问题反馈");


        return_Btn.setOnClickListener(new return_Btn());
        right_Btn.setOnClickListener(new right_BtnOnClickLister());
    }

    private class return_Btn implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            finish();
        }
    }

    private class right_BtnOnClickLister extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
//            Intent intent = new Intent(Friend_Detalis.this, MainLogin_QuestionFeedBack.class);
//            startActivity(intent);
        }
    }

    private void initView() {
        person_img = findViewById(R.id.person_img);
        name_txt = findViewById(R.id.name_txt);
        moid_txt = findViewById(R.id.moid_txt);
        lv_txt = findViewById(R.id.lv_txt);
    }

    private void initData() {
        AddFriendMethod(Friend_Detalis.this, saveFile.Friend_info_Url);
    }

    //
    public void AddFriendMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("friendUserId", friendUserId);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Friend_Details_Bean model = new Gson().fromJson(result, Friend_Details_Bean.class);
                Uri uri = Uri.parse(model.getData().getHeadImg());
                person_img.setImageURI(uri);
                cententTxt.setTextColor(ContextCompat.getColor(context, R.color.yellowfive));
                cententTxt.setText(model.getData().getNickname());
                name_txt.setText(model.getData().getNickname());
                moid_txt.setText("Mo ID:" + model.getData().getUserNum().intValue());
                lv_txt.setText("LV" + model.getData().getGrade().intValue());
                if (model.getData().getVipType() == 0) {

                } else {
                    moid_txt.setTextColor(ContextCompat.getColor(context, R.color.yellowfive));
                }
            }
            @Override
            public void failed(String... args) {

            }
        });
    }

}