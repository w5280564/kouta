package com.xunda.mo.main.friend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.gson.Gson;
import com.xunda.mo.R;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.chat.activicy.ChatActivity;
import com.xunda.mo.main.me.activity.MeAndGroup_QRCode;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Friend_Group_Detail extends BaseInitActivity {

    private String GroupId, groupHxId;
    String groupName;
    String groupID;
    private ImageView group_Head_Image;
    private TextView group_Nick_Txt, group_Brief_Txt, send_mess_Txt, share_group_Txt, apply_group_Txt,group_MoId_Txt;
    private LinearLayout label_Lin;

    public static void actionStart(Context context, int Identity, GruopInfo_Bean groupModel) {
        Intent intent = new Intent(context, Friend_Group_Detail.class);
        intent.putExtra("Identity", Identity);
        intent.putExtra("groupModel", (Serializable) groupModel);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, String GroupId) {
        Intent intent = new Intent(context, Friend_Group_Detail.class);
        intent.putExtra("GroupId", GroupId);
        context.startActivity(intent);
    }

    public static void actionStartHXID(Context context, String groupHxId) {
        Intent intent = new Intent(context, Friend_Group_Detail.class);
        intent.putExtra("groupHxId", groupHxId);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_friend_groupdetail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();
        group_Head_Image = findViewById(R.id.group_Head_Image);
        group_Nick_Txt = findViewById(R.id.group_Nick_Txt);
        group_MoId_Txt = findViewById(R.id.group_MoId_Txt);
        group_Brief_Txt = findViewById(R.id.group_Brief_Txt);
        label_Lin = findViewById(R.id.label_Lin);
        send_mess_Txt = findViewById(R.id.send_mess_Txt);
        send_mess_Txt.setOnClickListener(new send_mess_TxtClick());
        share_group_Txt = findViewById(R.id.share_group_Txt);
        share_group_Txt.setOnClickListener(new share_group_TxtClick());

        apply_group_Txt = findViewById(R.id.apply_group_Txt);
        apply_group_Txt.setOnClickListener(new apply_group_TxtClick());
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(Friend_Group_Detail.this, R.color.white));
        title_Include.setElevation(2f);//阴影
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("群组资料");
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
        groupHxId = intent.getStringExtra("groupHxId");

    }

    @Override
    protected void initData() {
        super.initData();

        if (TextUtils.isEmpty(GroupId)) {
            groupName = "groupHxId";
            groupID = groupHxId;
        } else {
            groupName = "groupId";
            groupID = GroupId;
        }
        GroupMethod(Friend_Group_Detail.this, saveFile.Group_MyGroupInfo_Url);
    }

    //发送消息
    private class send_mess_TxtClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            ChatActivity.actionStart(mContext, groupModel.getData().getGroupHxId(), DemoConstant.CHATTYPE_GROUP);
        }
    }

    //群二维码
    private class share_group_TxtClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            MeAndGroup_QRCode.actionGroupStart(Friend_Group_Detail.this, groupModel);
        }
    }

    //申请加群
    private class apply_group_TxtClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            String GroupId = groupModel.getData().getGroupId();
            Friend_Group_AdditiveGroup.actionStart(Friend_Group_Detail.this, GroupId,groupModel);
        }
    }

    GruopInfo_Bean groupModel;
    int Identity;

    public void GroupMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put(groupName, groupID);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                groupModel = new Gson().fromJson(result, GruopInfo_Bean.class);
                GruopInfo_Bean.DataDTO dataDTO = groupModel.getData();
                Glide.with(context).load(dataDTO.getGroupHeadImg()).transforms(new CenterCrop(), new RoundedCorners(9)).into(group_Head_Image);
                group_Nick_Txt.setText(dataDTO.getGroupName());
                String content = dataDTO.getGroupIntroduction().isEmpty() ? "群主很懒，还没有群介绍哦~" : dataDTO.getGroupIntroduction();
                group_Brief_Txt.setText(content);
                String myNickname = dataDTO.getMyNickname();
                group_MoId_Txt.setText("群ID: " + dataDTO.getGroupNum());
                tagList(label_Lin,  Friend_Group_Detail.this);

                if (TextUtils.isEmpty(myNickname)) {
                    apply_group_Txt.setVisibility(View.VISIBLE);
                }else {
                    send_mess_Txt.setVisibility(View.VISIBLE);
                    share_group_Txt.setVisibility(View.VISIBLE);
                }

//                myGroupId = dataDTO.getGroupId();
//                Identity = dataDTO.getIdentity();
            }
            @Override
            public void failed(String... args) {

            }
        });
    }

    public void tagList(LinearLayout label_Lin, Context mContext) {
        String tag = groupModel.getData().getTag();
        if (TextUtils.isEmpty(tag)){
            return;
        }
        String[] tagS = tag.split(",");
        for (int i = 0; i < tagS.length; i++) {
            TextView textView = new TextView(mContext);
            textView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
            textView.setTextColor(ContextCompat.getColor(mContext,R.color.white));
            textView.setBackgroundResource(R.drawable.group_label_radius);
            textView.setText(tagS[i]);
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int margins = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, mContext.getResources().getDisplayMetrics());
            itemParams.setMargins(0, 0, margins, 0);
            textView.setLayoutParams(itemParams);
            textView.setTag(i);
            label_Lin.addView(textView);
            textView.setOnClickListener(v -> {
            });
        }
    }


}