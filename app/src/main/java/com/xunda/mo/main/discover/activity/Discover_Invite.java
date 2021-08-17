package com.xunda.mo.main.discover.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.me.activity.MeAndGroup_QRCode;
import com.xunda.mo.model.Invite_Bean;
import com.xunda.mo.model.baseModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.HashMap;
import java.util.Map;

public class Discover_Invite extends BaseInitActivity {

    private ImageView invite_become_Img, invite_binding_Img;
    private EditText seek_edit;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Discover_Invite.class);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_discover_invite;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();
        invite_binding_Img = findViewById(R.id.invite_binding_Img);
        invite_binding_Img.setOnClickListener(new invite_binding_ImgClick());
        invite_become_Img = findViewById(R.id.invite_become_Img);
        invite_become_Img.setOnClickListener(new invite_become_ImgClick());
        seek_edit = findViewById(R.id.seek_edit);
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setElevation(2f);//阴影
        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("邀新有奖");
        Button right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.GONE);
        return_Btn.setOnClickListener(new return_Btn());
    }

    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }


    @Override
    protected void initData() {
        super.initData();
        InviteMethod(Discover_Invite.this, saveFile.Inviter_UserNum_Url);
    }


    private class invite_binding_ImgClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            if (TextUtils.isEmpty(seek_edit.getText())){
                Toast.makeText(Discover_Invite.this,"MO ID不能为空",Toast.LENGTH_SHORT).show();
                return;
            }
            BindingMethod(Discover_Invite.this, saveFile.Inviter_Bind_Url,seek_edit.getText().toString());
        }
    }

    private class invite_become_ImgClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            MeAndGroup_QRCode.actionUserStart(Discover_Invite.this);
        }
    }

    @SuppressLint("SetTextI18n")
    public void InviteMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Invite_Bean inviteModel = new Gson().fromJson(result, Invite_Bean.class);
                setIsInviter(inviteModel.getData().getIsInviter(), inviteModel.getData().getUserNum().toString());
            }

            @Override
            public void failed(String... args) {

            }
        });
    }
    @SuppressLint("SetTextI18n")
    public void BindingMethod(Context context, String baseUrl,String userNum) {
        Map<String, Object> map = new HashMap<>();
        map.put("userNum",userNum);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                baseModel inviteModel = new Gson().fromJson(result, baseModel.class);
                setIsInviter(1, userNum);
            }

            @Override
            public void failed(String... args) {

            }
        });
    }

    private void setIsInviter(Integer isInviter, String userNum) {
        invite_binding_Img.setVisibility(View.VISIBLE);
        seek_edit.setVisibility(View.VISIBLE);
        seek_edit.setEnabled(true);
        if (isInviter == 1) {
            invite_binding_Img.setVisibility(View.GONE);
            seek_edit.setVisibility(View.VISIBLE);
            seek_edit.setTextColor(ContextCompat.getColor(Discover_Invite.this, R.color.vipred));
            seek_edit.setEnabled(false);
            seek_edit.setText(userNum);
        }
    }


}