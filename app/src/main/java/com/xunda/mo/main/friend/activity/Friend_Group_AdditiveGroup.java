package com.xunda.mo.main.friend.activity;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.gson.Gson;
import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.model.baseModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.HashMap;
import java.util.Map;

public class Friend_Group_AdditiveGroup extends BaseInitActivity {
    private GruopInfo_Bean groupModel;
    private EditText apply_Edit;

    public static void actionStart(Context context, String GroupId, GruopInfo_Bean groupModel) {
        Intent intent = new Intent(context, Friend_Group_AdditiveGroup.class);
        intent.putExtra("GroupId", GroupId);
        intent.putExtra("groupModel", groupModel);
        context.startActivity(intent);
    }

    private String GroupId;
    private ImageView group_Head_Image;
    private TextView group_Nick_Txt, group_Brief_Txt, apply_group_Txt, group_MoId_Txt;


    @Override
    protected int getLayoutId() {
        return R.layout.friend_group_additivegroup;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();
        group_Head_Image = findViewById(R.id.group_Head_Image);
        group_Nick_Txt = findViewById(R.id.group_Nick_Txt);
        group_MoId_Txt = findViewById(R.id.group_MoId_Txt);
        group_Brief_Txt = findViewById(R.id.group_Brief_Txt);
        apply_Edit = findViewById(R.id.apply_Edit);

        apply_group_Txt = findViewById(R.id.apply_group_Txt);
        apply_group_Txt.setOnClickListener(new apply_group_TxtClick());
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(Friend_Group_AdditiveGroup.this, R.color.white));
        title_Include.setElevation(2f);//阴影
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("申请加群");
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
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        GroupId = intent.getStringExtra("GroupId");
        groupModel = (GruopInfo_Bean) intent.getSerializableExtra("groupModel");
    }

    @Override
    protected void initData() {
        super.initData();
        if (groupModel != null) {
            setView();
        }
    }



    private void setView() {
        GruopInfo_Bean.DataDTO dataDTO = groupModel.getData();
        Glide.with(Friend_Group_AdditiveGroup.this).load(dataDTO.getGroupHeadImg()).transforms(new CenterCrop(), new RoundedCorners(9)).into(group_Head_Image);
        group_Nick_Txt.setText(dataDTO.getGroupName());
        String content = dataDTO.getGroupIntroduction().isEmpty() ? "群主很懒，还没有群介绍哦~" : dataDTO.getGroupIntroduction();
        group_Brief_Txt.setText(content);
        group_MoId_Txt.setText("群ID: " + dataDTO.getGroupNum());
    }

    //申请
    private class apply_group_TxtClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            if (TextUtils.isEmpty(apply_Edit.getText().toString())) {
                Toast.makeText(Friend_Group_AdditiveGroup.this, "请填写验证信息", Toast.LENGTH_SHORT).show();
                return;
            }

            ApplyGroupMethod(Friend_Group_AdditiveGroup.this,  saveFile.Group_ApplyInto_Url);
        }
    }


    //申请加群
    public void ApplyGroupMethod(Context context, String baseUrl) {
        Map<String,Object> map = new HashMap<>();
        map.put("remark", apply_Edit.getText().toString());
        map.put("groupId", groupModel.getData().getGroupId());
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                baseModel baseBean = new Gson().fromJson(result, baseModel.class);
                Toast.makeText(context, baseBean.getMsg(), Toast.LENGTH_SHORT).show();
                finish();
            }
            @Override
            public void failed(String... args) {
            }
        });
    }

}