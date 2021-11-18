package com.xunda.mo.main.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.group.activity.GroupDetailSet;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.HashMap;
import java.util.Map;

/**
 * 设置昵称
 */
public class ChangeNameActivity extends BaseInitActivity {
    private EditText reset_name;
    private String old_name,myGroupId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_nan_changename;
    }


    public static void actionStart(Context context,String name,String myGroupId) {
        Intent intent = new Intent(context, ChangeNameActivity.class);
        intent.putExtra("name",name);
        intent.putExtra("myGroupId",myGroupId);
        context.startActivity(intent);
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        reset_name = findViewById(R.id.reset_name);
        initTitle();
    }


    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setElevation(2f);//阴影
        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView contentText = (TextView) title_Include.findViewById(R.id.cententtxt);
        contentText.setText("我的群昵称");
        Button right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.VISIBLE);
        right_Btn.setText("完成");
        return_Btn.setOnClickListener(new ChangeNameActivity.return_Btn());
        right_Btn.setOnClickListener(new ChangeNameActivity.right_Btn());
    }

    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }


    private class right_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            String new_name = reset_name.getText().toString();

            if(TextUtils.isEmpty(new_name)){
                Toast.makeText(mContext,"昵称不能为空",Toast.LENGTH_SHORT).show();
                return;
            }

            if(new_name.equals(old_name)){
                Toast.makeText(mContext,"请先修改再提交",Toast.LENGTH_SHORT).show();
                return;
            }

            changeGroupNameMethod(new_name);
        }
    }


    @Override
    protected void initData() {
        old_name = getIntent().getStringExtra("name");
        reset_name.setText(TextUtils.isEmpty(old_name)?"":old_name);
        myGroupId = getIntent().getStringExtra("myGroupId");
    }

    public void changeGroupNameMethod(String new_name) {
        MyInfo myInfo = new MyInfo(mContext);
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", myGroupId);
        map.put("nickname", new_name);
        map.put("userId", myInfo.getUserInfo().getUserId());
        xUtils3Http.post(mContext, saveFile.Group_UpdateNickName_Url, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void failed(String... args) {
            }
        });
    }
}