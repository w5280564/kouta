package com.xunda.mo.main.group.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.dialog.DemoDialogFragment;
import com.xunda.mo.hx.section.dialog.EditTextDialogFragment;
import com.xunda.mo.hx.section.dialog.GroupTopDialogFragment;
import com.xunda.mo.hx.section.dialog.SimpleDialogFragment;
import com.xunda.mo.hx.section.group.viewmodels.GroupDetailViewModel;
import com.xunda.mo.hx.section.search.SearchGroupChatActivity;
import com.xunda.mo.main.MainLogin_Register;
import com.xunda.mo.main.baseView.FlowLayout;
import com.xunda.mo.main.baseView.MyArrowItemView;
import com.xunda.mo.main.baseView.MySwitchItemView;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.model.GroupMember_Bean;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.model.baseDataModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;

import org.jetbrains.annotations.NotNull;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class GroupDetailSet extends BaseInitActivity {

    private String groupId;
    private SimpleDraweeView person_img;
    private TextView group_Name, group_content, clearHistory_Txt, remove_Txt;
    private MyArrowItemView group_Name_ArrowItemView, group_Code_ArrowItemView, group_Nick_ArrowItemView, group_member_ArrowItemView,
            clear_ArrowItemView, group_Top_ArrowItemView;
    private MySwitchItemView member_quit_Switch,group_chatTop_Switch,disturb_Switch;
    private Group Muggle_Group;
    private FlowLayout group_Flow;
    private ImageView groupmember_add, groupmember_remove;
    private GroupDetailViewModel viewModel;
    private EMConversation conversation;

    public static void actionStart(Context context, String groupId) {
        Intent intent = new Intent(context, GroupDetailSet.class);
        intent.putExtra("groupId", groupId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_detailset;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();

        ScrollView my_Scroll = findViewById(R.id.my_Scroll);
        OverScrollDecoratorHelper.setUpOverScroll(my_Scroll);
        ConstraintLayout head_Constraint = findViewById(R.id.head_Constraint);
        head_Constraint.setOnClickListener(new head_ConstraintClick());
        person_img = findViewById(R.id.person_img);
        group_Name = findViewById(R.id.group_Name);
        group_content = findViewById(R.id.group_content);
        group_Name_ArrowItemView = findViewById(R.id.group_Name_ArrowItemView);
        group_Name_ArrowItemView.setOnClickListener(new group_Name_ArrowItemViewClick());
        clear_ArrowItemView = findViewById(R.id.clear_ArrowItemView);
        clear_ArrowItemView.setOnClickListener(new clear_ArrowItemViewClick());
        group_Flow = findViewById(R.id.group_Flow);
        group_Code_ArrowItemView = findViewById(R.id.group_Code_ArrowItemView);
        group_Nick_ArrowItemView = findViewById(R.id.group_Nick_ArrowItemView);
        group_Nick_ArrowItemView.setOnClickListener(new group_Nick_ArrowItemViewClick());
        group_Top_ArrowItemView = findViewById(R.id.group_Top_ArrowItemView);
        group_Top_ArrowItemView.setOnClickListener(new group_Top_ArrowItemViewClick());
        group_member_ArrowItemView = findViewById(R.id.group_member_ArrowItemView);
        group_chatTop_Switch = findViewById(R.id.group_chatTop_Switch);
        group_chatTop_Switch.setOnCheckedChangeListener(new group_chatTop_SwitchClick());
        disturb_Switch = findViewById(R.id.disturb_Switch);
        disturb_Switch.setOnCheckedChangeListener(new disturb_SwitchCheckClick());
//        groupmember_add = findViewById(R.id.groupmember_add);
//        groupmember_remove = findViewById(R.id.groupmember_remove);
        clearHistory_Txt = findViewById(R.id.clear_Txt);
        remove_Txt = findViewById(R.id.remove_Txt);

        member_quit_Switch = findViewById(R.id.member_quit_Switch);
        Muggle_Group = findViewById(R.id.Muggle_Group);
        initGroupView();
    }
    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(GroupDetailSet.this, R.color.white));
        title_Include.setElevation(2f);//阴影
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("群聊详情");
        Button right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.VISIBLE);
        right_Btn.setBackgroundResource(R.mipmap.adress_head_more);
        viewTouchDelegate.expandViewTouchDelegate(right_Btn, 50, 50, 50, 50);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        int margisright = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
        layoutParams.setMargins(0, 0, margisright, 0);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        right_Btn.setLayoutParams(layoutParams);

        return_Btn.setOnClickListener(new return_Btn());
        right_Btn.setOnClickListener(new right_Btn());
    }

    private void initGroupView() {
        conversation = DemoHelper.getInstance().getConversation(groupId, EMConversation.EMConversationType.GroupChat, true);
        String extField = conversation.getExtField();
        group_chatTop_Switch.getSwitch().setChecked(!TextUtils.isEmpty(extField) && EaseCommonUtils.isTimestamp(extField));

        List<String> disabledIds = DemoHelper.getInstance().getPushManager().getNoPushGroups();
        disturb_Switch.getSwitch().setChecked(disabledIds != null && disabledIds.contains(groupId));
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
//            showMore(ChatDetailSet.this, right_Btn, 0);
        }
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupId = intent.getStringExtra("groupId");
    }

    @Override
    protected void initData() {
        super.initData();

        viewModel = new ViewModelProvider(this).get(GroupDetailViewModel.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GroupMethod(GroupDetailSet.this, saveFile.BaseUrl + saveFile.Group_MyGroupInfo_Url + "?groupHxId=" + groupId);
    }

    private class head_ConstraintClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            GroupDetail_Edit.actionStart(mContext, groupModel.getData().getIdentity().intValue(), groupModel);
        }
    }

    //清除记录
    private class clear_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            SearchGroupChatActivity.actionStart(mContext, groupId);
        }
    }

    //群名称
    private class group_Name_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            changeGroupName();
        }
    }

    //设置群名称
    private void changeGroupName() {
        new EditTextDialogFragment.Builder(mContext)
                .setContent(group_Name_ArrowItemView.getTvContent().getText().toString())
                .setConfirmClickListener(new EditTextDialogFragment.ConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view, String content) {
                        if (!TextUtils.isEmpty(content)) {
//                            itemGroupName.getTvContent().setText(content);
                            String changType = "4";
                            String keyStr = "groupName";
                            CreateGroupMethod(GroupDetailSet.this, saveFile.BaseUrl + saveFile.Group_UpdateInfo_Url, changType, keyStr, content, "", "");
                        }
                    }
                })
                .setTitle("设置群名称")
                .show();
    }

    //群昵称
    private class group_Nick_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            changeNick();
        }
    }

    private void changeNick() {
        new EditTextDialogFragment.Builder(mContext)
                .setContent(group_Nick_ArrowItemView.getTvContent().getText().toString())
                .setConfirmClickListener(new EditTextDialogFragment.ConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view, String content) {
                        if (!TextUtils.isEmpty(content)) {
//                            itemGroupName.getTvContent().setText(content);
                            String changType = "3";
                            changeGroupNameMethod(GroupDetailSet.this, saveFile.BaseUrl + saveFile.Group_UpdateNikeName_Url, content);
                        }
                    }
                })
                .setTitle("设置群昵称")
                .show();
    }


    //群置顶消息
    private class group_Top_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            showTopDialog();
        }
    }

    //消息置顶
    private class group_chatTop_SwitchClick implements MySwitchItemView.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(MySwitchItemView buttonView, boolean isChecked) {
            if(isChecked) {
                conversation.setExtField(System.currentTimeMillis()+"");
            }else {
                conversation.setExtField("");
            }
            LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP));
        }
    }

    //置顶消息弹窗
    private void showTopDialog() {
        String content = TextUtils.equals(group_Top_ArrowItemView.getTvContent().getText().toString().trim(), "未设置")
                ? "" :group_Top_ArrowItemView.getTvContent().getText().toString().trim();
          GroupTopDialogFragment.showDialog(mContext,
                  "群置顶消息",
                  content,
                  "请输入",
                  (view, content1) -> {
                      //群置顶消息
                      if (!TextUtils.isEmpty(content1)) {
//                            signature_ArrowItemView.getTvContent().setText(content);
                          String changType = "4";
                          String keyStr = "groupNotice";
                          CreateGroupMethod(GroupDetailSet.this, saveFile.BaseUrl + saveFile.Group_UpdateInfo_Url, changType, keyStr, content1, "", "");
                      }
                  }, new GroupTopDialogFragment.OnEmptyClickLister() {
                      @Override
                      public void onEmptyClcik(View view, Dialog fragment) {
                          isChangeTop(fragment);
                      }
                  }
          );

    }

    //清空置顶消息
    private void isChangeTop(Dialog fragment) {
        new SimpleDialogFragment.Builder(mContext)
                .setTitle("提示")
                .showContent(true)
                .setContent("是否删除该置顶消息")
                .setOnConfirmClickListener(new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        fragment.dismiss();
                        String changType = "4";
                        String keyStr = "groupNotice";
                        String empty = "";
                        CreateGroupMethod(GroupDetailSet.this, saveFile.BaseUrl + saveFile.Group_UpdateInfo_Url, changType, keyStr, empty, "", "");
                    }
                })
                .showCancelButton(true)
                .show();
    }

    //群消息免打扰 接入推送
    private class disturb_SwitchCheckClick implements MySwitchItemView.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(MySwitchItemView buttonView, boolean isChecked) {
//            viewModel.updatePushServiceForGroup(groupId, isChecked);
        }
    }

    GruopInfo_Bean groupModel;
    public void GroupMethod(Context context, String baseUrl) {
        RequestParams params = new RequestParams(baseUrl);
        if (saveFile.getShareData("JSESSIONID", context) != null) {
            params.setHeader("Authorization", saveFile.getShareData("JSESSIONID", context));
        }
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
                if (resultString != null) {
                    groupModel = new Gson().fromJson(resultString, GruopInfo_Bean.class);
                    if (groupModel.getCode() == -1 || groupModel.getCode() == 500) {
                        Intent intent = new Intent(context, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (groupModel.getCode() == 200) {
                        GruopInfo_Bean.DataDTO dataDTO = groupModel.getData();
                        Uri uri = Uri.parse(dataDTO.getGroupHeadImg());
                        person_img.setImageURI(uri);
                        group_Name.setText(dataDTO.getGroupName());
                        String content = dataDTO.getGroupIntroduction().isEmpty() ? "群主很懒，还没有群介绍哦~" : dataDTO.getGroupIntroduction();
                        group_content.setText(content);
                        group_Name_ArrowItemView.getTvContent().setText(dataDTO.getGroupName());
                        group_Code_ArrowItemView.getTvContent().setText(dataDTO.getGroupNum().toString());
                        group_Nick_ArrowItemView.getTvContent().setText(dataDTO.getMyNikeName());


                        String topStr = TextUtils.isEmpty(dataDTO.getGroupNotice()) ? "未设置" : dataDTO.getGroupNotice();
                        group_Top_ArrowItemView.getTvContent().setText(topStr);

                        String groupId = dataDTO.getGroupId();
                        Double Identity = dataDTO.getIdentity();

                        GroupMemberListMethod(GroupDetailSet.this, saveFile.BaseUrl + saveFile.Group_UserList_Url + "?groupId=" + groupId, Identity);
                        setGroupManagement(Identity);
                    } else {
                        Toast.makeText(context, groupModel.getMsg(), Toast.LENGTH_SHORT).show();
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

    /**
     * 群主管理员 与普通成员
     *
     * @param Identity 1群主2管理员3普通成员
     */
    private void setGroupManagement(Double Identity) {
        if (Identity == 3) {
            Muggle_Group.setVisibility(View.GONE);
            group_Name_ArrowItemView.setEnabled(false);
            group_Name_ArrowItemView.getArrow().setVisibility(View.GONE);
            remove_Txt.setText("删除并退出");
            groupmember_remove.setVisibility(View.GONE);
        } else {
            Muggle_Group.setVisibility(View.VISIBLE);
            remove_Txt.setText("解散本群");
            groupmember_remove.setVisibility(View.VISIBLE);
        }
    }

    //群成员列表
    public void imgFlow(FlowLayout myFlow, @NotNull List<GroupMember_Bean.DataDTO> imgList, Double Identity) {
        if (myFlow != null) {
            myFlow.removeAllViews();
        }
        int size = imgList.size();
        //群主、管理员只展示2行10人 最后两个是添加与删除
        if (size > 8) {
            size = 8;
            if (Identity == 3) {
                size = 9;
            }
        }
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getResources().getDisplayMetrics());
        ConstraintLayout.LayoutParams itemParams = new ConstraintLayout.LayoutParams(width, height);
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        int marginBo = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
        itemParams.setMargins(margin, 0, 0, marginBo);
        for (int i = 0; i < size; i++) {
            View myView = LayoutInflater.from(GroupDetailSet.this).inflate(R.layout.group_members, null);
            ConstraintLayout.LayoutParams Params = new ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            Params.setMargins(margin, 0, 0, marginBo);
            myView.setLayoutParams(Params);
            SimpleDraweeView mySimple = myView.findViewById(R.id.head_simple);
//            mySimple.setLayoutParams(itemParams);
            TextView name = myView.findViewById(R.id.name);
            ImageView group_man = myView.findViewById(R.id.group_man);

            group_man.setVisibility(View.GONE);
            if (imgList.get(i).getIdentity().intValue() == 1) {
                group_man.setVisibility(View.VISIBLE);
                group_man.setImageResource(R.mipmap.group_man);
            } else if (imgList.get(i).getIdentity().intValue() == 2) {
                group_man.setVisibility(View.VISIBLE);
                group_man.setImageResource(R.mipmap.group_owen);
            }
            name.setText(imgList.get(i).getNikeName());
            Uri headUri = Uri.parse(imgList.get(i).getHeadImg());
            mySimple.setImageURI(headUri);
            myView.setTag(i);
//            int position = myFlow.getChildCount() ;//下标
            myFlow.addView(myView);
//            mySimple.setOnClickListener(v -> {
//                mySimple.setFocusable(false);
//                int tag = (Integer) v.getTag();
//                List<GroupMember_Bean.DataDTO> myArr = new ArrayList<>();
//                for (int i1 = 0; i1 < imgList.size(); i1++) {
//                    myArr.add(imgList.get(i1));
//                }
////                    Intent i = new Intent(this, ImagePagerActivity.class);
//////                    i.putExtra("imgArr", (Parcelable) myBean);
////                    i.putExtra("imgArr", (Serializable) myArr);
////                    i.putExtra("tag", tag);
////                    startActivity(i);
////                    mySimple.setFocusable(true);
//            });
        }

        groupmember_add = new ImageView(this);
        groupmember_add.setBackgroundResource(R.mipmap.groupmember_add);
        groupmember_add.setLayoutParams(itemParams);
        groupmember_remove = new ImageView(this);
        groupmember_remove.setBackgroundResource(R.mipmap.groupmember_remove);
        groupmember_remove.setLayoutParams(itemParams);
        myFlow.addView(groupmember_add);

        if (Identity == 3) {
            groupmember_remove.setVisibility(View.GONE);
        } else {
            myFlow.addView(groupmember_remove);
        }

    }


    public void GroupMemberListMethod(Context context, String baseUrl, Double Identity) {
        RequestParams params = new RequestParams(baseUrl);
        if (saveFile.getShareData("JSESSIONID", context) != null) {
            params.setHeader("Authorization", saveFile.getShareData("JSESSIONID", context));
        }
//        MyInfo myInfo = new MyInfo(context);
//        params.addHeader("Authorization", myInfo.getUserInfo().getToken());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
                if (resultString != null) {
                    GroupMember_Bean groupListModel = new Gson().fromJson(resultString, GroupMember_Bean.class);
                    if (groupListModel.getCode() == -1 || groupListModel.getCode() == 500) {
                        Intent intent = new Intent(context, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (groupListModel.getCode() == 200) {
//                        GroupMember_Bean.DataDTO dataDTO = (GroupMember_Bean.DataDTO) groupListModel.getData();
                        String memberCount = String.format("查看%s名群成员", groupListModel.getData().size());
                        group_member_ArrowItemView.getTvContent().setText(memberCount);
                        imgFlow(group_Flow, groupListModel.getData(), Identity);

                    } else {
                        Toast.makeText(context, groupListModel.getMsg(), Toast.LENGTH_SHORT).show();
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

    //升序列表
    public List<GroupMember_Bean.DataDTO> getLastPriceDescList(List<GroupMember_Bean.DataDTO> list) {
        Collections.sort(list, new Comparator<GroupMember_Bean.DataDTO>() {
            @Override
            public int compare(GroupMember_Bean.DataDTO o1, GroupMember_Bean.DataDTO o2) {
                return o1.getIdentity().compareTo(o2.getIdentity());
            }
        });
        return list;
    }

    /**
     * 修改用户信息
     *
     * @param context
     * @param baseUrl
     * @param changType 修改类型（1.头像 2.地址 3.修改群昵称 4.群置顶消息 5. 6. 7.）
     * @param keyStr
     * @param valueStr
     */
    public void CreateGroupMethod(Context context, String baseUrl, String changType, String keyStr, String valueStr, String keyCity, String valueCityStr) {
        RequestParams params = new RequestParams(baseUrl);
        params.addBodyParameter("groupId", groupModel.getData().getGroupId());
        params.addBodyParameter(keyStr, valueStr);
        if (saveFile.getShareData("JSESSIONID", context) != null) {
            params.setHeader("Authorization", saveFile.getShareData("JSESSIONID", context));
        }
        params.setAsJsonContent(true);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
                if (resultString != null) {
                    baseDataModel baseModel = new Gson().fromJson(resultString, baseDataModel.class);
                    if (baseModel.getCode() == 200) {
                        Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                        if (TextUtils.equals(changType, "3")) {
//                            String adressStr = valueStr.isEmpty() ? "未设置" : valueStr;
                            group_Name_ArrowItemView.getTvContent().setText(valueStr);
                            group_Name.setText(valueStr);
                            sendMes(groupModel, valueStr);
                        }else if (TextUtils.equals(changType, "4")){
                            group_Top_ArrowItemView.getTvContent().setText(valueStr);
                        }
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

    //发送消息 创建群聊发送一条邀请人信息
    private void sendMes(GruopInfo_Bean Model, String NameStr) {
//        try {
        MyInfo myInfo = new MyInfo(mContext);
        String conversationId = Model.getData().getGroupHxId();
//        EMMessage message = EMMessage.createTxtSendMessage(mContext.getString(R.string.CREATE_GROUP_CONTENT), conversationId);
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        EMTextMessageBody txtBody = new EMTextMessageBody("");
        message.addBody(txtBody);
        message.setTo(conversationId);
        message.setChatType(EMMessage.ChatType.GroupChat);

//            JSONObject obj = new JSONObject();
//            obj.put(MyConstant.ADMIN_TYPE, mContext.getString(R.string.GROUP_OWNER));
//            obj.put(MyConstant.MESSAGE_TYPE, MyConstant.UPDATE_GROUP_NAME);
//            obj.put(MyConstant.NAME_STR, NameStr);
//            obj.put(MyConstant.SEND_NAME, myInfo.getUserInfo().getNikeName());
//            obj.put(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
//            obj.put(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
//            obj.put(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
//            obj.put(MyConstant.GROUP_NAME, NameStr);
//            obj.put(MyConstant.GROUP_HEAD, Model.getData().getGroupHeadImg());

        message.setAttribute(MyConstant.ADMIN_TYPE, mContext.getString(R.string.GROUP_OWNER));
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.UPDATE_GROUP_NAME);
        message.setAttribute(MyConstant.NAME_STR, NameStr);
        message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNikeName());
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
        message.setAttribute(MyConstant.GROUP_NAME, NameStr);
        message.setAttribute(MyConstant.GROUP_HEAD, Model.getData().getGroupHeadImg());

//            message.setAttribute(MyConstant.EXT, obj);
//            message.setMsgId(UUID.randomUUID().toString());
//            message.setStatus(EMMessage.Status.SUCCESS);
//            EMClient.getInstance().chatManager().saveMessage(message);
        DemoHelper.getInstance().getChatManager().sendMessage(message);

//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }


    public void changeGroupNameMethod(Context context, String baseUrl, String changeNick) {
        RequestParams params = new RequestParams(baseUrl);
        params.addBodyParameter("groupId", groupModel.getData().getGroupId());
        params.addBodyParameter("nikeName", changeNick);
        MyInfo myInfo = new MyInfo(context);
        params.addBodyParameter("userId", myInfo.getUserInfo().getUserId());

        if (saveFile.getShareData("JSESSIONID", context) != null) {
            params.setHeader("Authorization", saveFile.getShareData("JSESSIONID", context));
        }
        params.setAsJsonContent(true);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
                if (resultString != null) {
                    baseDataModel baseModel = new Gson().fromJson(resultString, baseDataModel.class);
                    if (baseModel.getCode() == 200) {
                        Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                        group_Nick_ArrowItemView.getTvContent().setText(changeNick);
//                        if (TextUtils.equals(changType, "3")) {
//                            String adressStr = valueStr.isEmpty() ? "未设置" : valueStr;
//                            group_Name_ArrowItemView.getTvContent().setText(valueStr);
//                            group_Name.setText(valueStr);
//                            sendMes(groupModel, valueStr);
//                        }
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