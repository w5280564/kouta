package com.xunda.mo.main.friend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import android.widget.Toast;

import com.xunda.mo.R;
import com.xunda.mo.main.MainLogin_Register;
import com.xunda.mo.model.Friend_Detalis_Model;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.SetStatusBar;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

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
        AddFriendMethod(Friend_Detalis.this, saveFile.BaseUrl + saveFile.Friend_info_Url + "?friendUserId=" + friendUserId);
//        AddFriendMethod(Friend_Detalis.this, saveFile.BaseUrl + saveFile.User_SearchAll_Url + "?search=" + friendUserId + "&type=" + 2 + "&pageNum=" + 1 + "&pageSize=" + 1);
    }

    //
    public void AddFriendMethod(Context context, String baseUrl) {
        RequestParams params = new RequestParams(baseUrl);
        if (saveFile.getShareData("JSESSIONID", context) != null) {
            params.setHeader("Authorization", saveFile.getShareData("JSESSIONID", context));
        }
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
                if (resultString != null) {
                    Friend_Detalis_Model model = new Gson().fromJson(resultString, Friend_Detalis_Model.class);
                    if (model.getCode() == -1 || model.getCode() == 500) {
                        Intent intent = new Intent(context, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (model.getCode() == 200) {

                        Uri uri = Uri.parse(model.getData().getHeadImg());
                        person_img.setImageURI(uri);
                        cententTxt.setTextColor(ContextCompat.getColor(context, R.color.yellowfive));
                        cententTxt.setText(model.getData().getNikeName());
                        name_txt.setText(model.getData().getNikeName());
                        moid_txt.setText("Mo ID:" + model.getData().getUserNum().intValue());
                        lv_txt.setText("LV" + model.getData().getGrade().intValue());

                        if (model.getData().getVipType() == 0) {

                        } else {
                            moid_txt.setTextColor(ContextCompat.getColor(context, R.color.yellowfive));
                        }


                    } else {
                        Toast.makeText(context, model.getMsg(), Toast.LENGTH_SHORT).show();
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