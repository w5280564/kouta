package com.xunda.mo.main.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.group.activity.GroupDetailSet;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.HashMap;
import java.util.Map;

/**
 * 设置群昵称
 */
public class ChangeGroupNameActivity extends BaseInitActivity {
    private EditText reset_name;
    private String old_name,myGroupId;
    private GruopInfo_Bean.DataDTO mGroupObj;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_nan_changename;
    }


    public static void actionStart(Context context,String name,GruopInfo_Bean mGroup) {
        Intent intent = new Intent(context, ChangeGroupNameActivity.class);
        intent.putExtra("name",name);
        intent.putExtra("mGroup",mGroup);
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
        Button return_Btn = title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView contentText = title_Include.findViewById(R.id.cententtxt);
        contentText.setText("群名称");
        Button right_Btn =  title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.VISIBLE);
        right_Btn.setText("完成");
        return_Btn.setOnClickListener(new ChangeGroupNameActivity.return_Btn());
        right_Btn.setOnClickListener(new ChangeGroupNameActivity.right_Btn());
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
                Toast.makeText(mContext,"群名称不能为空",Toast.LENGTH_SHORT).show();
                return;
            }

            if(new_name.equals(old_name)){
                Toast.makeText(mContext,"请先修改再提交",Toast.LENGTH_SHORT).show();
                return;
            }

            CreateGroupMethod(new_name);
        }
    }


    @Override
    protected void initData() {
        old_name = getIntent().getStringExtra("name");
        reset_name.setText(TextUtils.isEmpty(old_name)?"":old_name);
        GruopInfo_Bean mTempObj = (GruopInfo_Bean) getIntent().getSerializableExtra("mGroup");
        if (mTempObj!=null) {
            mGroupObj = mTempObj.getData();
            myGroupId = mGroupObj.getGroupId();
        }

    }

    /**
     * 修改修改群昵称
     */
    public void CreateGroupMethod(String new_name) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", myGroupId);
        map.put("groupName", new_name);
        xUtils3Http.post(this, saveFile.Group_UpdateInfo_Url, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Toast.makeText(ChangeGroupNameActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                sendMes(new_name);
                finish();
            }

            @Override
            public void failed(String... args) {
            }
        });
    }


    //发送消息 创建群聊发送一条邀请人信息
    private void sendMes(String NameStr) {
        if (mGroupObj==null) {
            return;
        }
        MyInfo myInfo = new MyInfo(mContext);
        String conversationId = mGroupObj.getGroupHxId();
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        EMTextMessageBody txtBody = new EMTextMessageBody("");
        message.addBody(txtBody);
        message.setTo(conversationId);
        message.setChatType(EMMessage.ChatType.GroupChat);

        message.setAttribute(MyConstant.ADMIN_TYPE, mContext.getString(R.string.GROUP_OWNER));
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.UPDATE_GROUP_NAME);
        message.setAttribute(MyConstant.NAME_STR, NameStr);
        message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNickname());
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
        message.setAttribute(MyConstant.GROUP_NAME, NameStr);
        message.setAttribute(MyConstant.GROUP_HEAD, mGroupObj.getGroupHeadImg());
        DemoHelper.getInstance().getChatManager().sendMessage(message);
    }
}