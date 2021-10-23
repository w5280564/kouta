package com.xunda.mo.main.group.activity;

import android.annotation.SuppressLint;
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

import com.google.gson.Gson;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.model.baseModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.HashMap;
import java.util.Map;

public class Group_Horn extends BaseInitActivity {

    private EditText horn_txt;
    private TextView horn_Count;
    private int limitNum;
    private GruopInfo_Bean groupModel;

    public static void actionStart(Context context, GruopInfo_Bean groupModel) {
        Intent intent = new Intent(context, Group_Horn.class);
        intent.putExtra("groupModel", groupModel);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_horn;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupModel = (GruopInfo_Bean) intent.getSerializableExtra("groupModel");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();
        horn_txt = findViewById(R.id.horn_txt);
        horn_Count = findViewById(R.id.horn_Count);
        //记录字数上限
        limitNum = 500;
        horn_txt.addTextChangedListener(new horn_txtLister());
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(Group_Horn.this, R.color.white));
        title_Include.setElevation(2f);//阴影
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("群喇叭");
        Button right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        viewTouchDelegate.expandViewTouchDelegate(right_Btn, 50, 50, 50, 50);
        right_Btn.setVisibility(View.VISIBLE);
        right_Btn.setTextColor(getResources().getColor(R.color.blacktitlettwo));
        right_Btn.setText("提交");

        return_Btn.setOnClickListener(new return_Btn());
        right_Btn.setOnClickListener(new right_BtnClick());
    }

    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }

    private class right_BtnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(horn_txt.getText().toString())){
                Toast.makeText(mContext,"群喇叭消息不能为空",Toast.LENGTH_SHORT).show();
                return;
            }
            hornMethod(Group_Horn.this, saveFile.Group_Horn);
        }
    }


    private class horn_txtLister implements TextWatcher {
        private CharSequence enterWords;
        private int selectionStart;
        private int selectionEnd;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //实时记录输入的字数
            enterWords = s;
        }

        @Override
        public void afterTextChanged(Editable s) {
            //已输入字数
//            int enteredWords = limitNum - s.length();
            int enteredWords = s.length();
            //TextView显示剩余字数
            horn_Count.setText(enteredWords + "/500字");
            selectionStart = horn_txt.getSelectionStart();
            selectionEnd = horn_txt.getSelectionEnd();
            if (enterWords.length() > limitNum) {
                Toast.makeText(mContext, "内容超出上限", Toast.LENGTH_SHORT).show();
                //删除多余输入的字（不会显示出来）
                s.delete(selectionStart - 1, selectionEnd);
                int tempSelection = selectionEnd;
                horn_txt.setText(s);
                //设置光标在最后
                horn_txt.setSelection(tempSelection);
            }

        }
    }

    @SuppressLint("SetTextI18n")
    public void hornMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                baseModel userModel = new Gson().fromJson(result, baseModel.class);

                sendMes(groupModel,horn_txt.getText().toString());
                finish();
            }

            @Override
            public void failed(String... args) {

            }
        });
    }


    //发送消息 群喇叭消息
    private void sendMes(GruopInfo_Bean Model,String hornTxt) {
        MyInfo myInfo = new MyInfo(mContext);
        String conversationId = Model.getData().getGroupHxId();
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        EMTextMessageBody txtBody = new EMTextMessageBody(hornTxt);
        message.addBody(txtBody);
        message.setTo(conversationId);
        message.setChatType(EMMessage.ChatType.GroupChat);
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MESS_TYPE_GROUP_HORN);
        message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNickname());
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
        message.setAttribute(MyConstant.GROUP_NAME, Model.getData().getGroupName());
        message.setAttribute(MyConstant.GROUP_HEAD, Model.getData().getGroupHeadImg());
        DemoHelper.getInstance().getChatManager().sendMessage(message);
        LiveDataBus.get().with(MyConstant.MESS_TYPE_GROUP_HORN).postValue(message);
    }


}