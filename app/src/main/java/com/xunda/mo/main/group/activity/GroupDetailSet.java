package com.xunda.mo.main.group.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import com.hyphenate.easeui.utils.EaseCompat;
import com.hyphenate.easeui.utils.EaseFileUtils;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.dialog.EditTextDialogFragment;
import com.xunda.mo.hx.section.dialog.GroupTopDialogFragment;
import com.xunda.mo.hx.section.dialog.SimpleDialogFragment;
import com.xunda.mo.hx.section.group.viewmodels.GroupDetailViewModel;
import com.xunda.mo.hx.section.search.SearchGroupChatActivity;
import com.xunda.mo.main.MainActivity;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.main.baseView.FlowLayout;
import com.xunda.mo.main.baseView.MyArrowItemView;
import com.xunda.mo.main.baseView.MySwitchItemView;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.main.me.activity.MeAndGroup_QRCode;
import com.xunda.mo.model.GroupMember_Bean;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class GroupDetailSet extends BaseInitActivity {

    private String HXgroupId;
    private SimpleDraweeView person_img;
    private TextView group_Name, group_content, clearHistory_Txt, remove_Txt;
    private MyArrowItemView group_Name_ArrowItemView, group_Code_ArrowItemView, group_Nick_ArrowItemView, group_member_ArrowItemView,
            clear_ArrowItemView, group_Top_ArrowItemView, chatBg_ArrowItemView, group_Management_ArrowItemView;
    private MySwitchItemView member_quit_Switch, group_chatTop_Switch, disturb_Switch;
    private Group Muggle_Group;
    private FlowLayout group_Flow;
    private ImageView groupmember_add, groupmember_remove;
    private GroupDetailViewModel viewModel;
    private EMConversation conversation;
    private String myGroupId;
    private Button right_Btn;
    private List<String> members = new ArrayList<>();

    public static void actionStart(Context context, String HXgroupId) {
        Intent intent = new Intent(context, GroupDetailSet.class);
        intent.putExtra("HXgroupId", HXgroupId);
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
        group_Code_ArrowItemView.setOnClickListener(new group_Code_ArrowItemViewClick());
        group_Nick_ArrowItemView = findViewById(R.id.group_Nick_ArrowItemView);
        group_Nick_ArrowItemView.setOnClickListener(new group_Nick_ArrowItemViewClick());
        group_Top_ArrowItemView = findViewById(R.id.group_Top_ArrowItemView);
        group_Top_ArrowItemView.setOnClickListener(new group_Top_ArrowItemViewClick());
        group_Management_ArrowItemView = findViewById(R.id.group_Management_ArrowItemView);
        group_Management_ArrowItemView.setOnClickListener(new group_Management_ArrowItemViewClick());
        group_member_ArrowItemView = findViewById(R.id.group_member_ArrowItemView);
        group_member_ArrowItemView.setOnClickListener(new group_member_ArrowItemViewClick());
        group_chatTop_Switch = findViewById(R.id.group_chatTop_Switch);
        group_chatTop_Switch.setOnCheckedChangeListener(new group_chatTop_SwitchClick());
        disturb_Switch = findViewById(R.id.disturb_Switch);
        disturb_Switch.getSwitch().setOnClickListener(new disturb_SwitchClick());
//        groupmember_add = findViewById(R.id.groupmember_add);
//        groupmember_remove = findViewById(R.id.groupmember_remove);
        clearHistory_Txt = findViewById(R.id.clear_Txt);
        clearHistory_Txt.setOnClickListener(new clearHistory_TxtClick());
        chatBg_ArrowItemView = findViewById(R.id.chatBg_ArrowItemView);
        chatBg_ArrowItemView.setOnClickListener(new chatBg_ArrowItemViewClick());
        remove_Txt = findViewById(R.id.remove_Txt);
        remove_Txt.setOnClickListener(new remove_TxtClick());

        member_quit_Switch = findViewById(R.id.member_quit_Switch);
        member_quit_Switch.getSwitch().setOnClickListener(new member_quit_SwitchClick());
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
        right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
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
        conversation = DemoHelper.getInstance().getConversation(HXgroupId, EMConversation.EMConversationType.GroupChat, true);
        String extField = conversation.getExtField();
        group_chatTop_Switch.getSwitch().setChecked(!TextUtils.isEmpty(extField) && EaseCommonUtils.isTimestamp(extField));

        List<String> disabledIds = DemoHelper.getInstance().getPushManager().getNoPushGroups();
        disturb_Switch.getSwitch().setChecked(disabledIds != null && disabledIds.contains(HXgroupId));
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
            showMore(GroupDetailSet.this, right_Btn, 0);
        }
    }

    private void showMore(final Context mContext, final View view, final int pos) {
        View contentView = View.inflate(mContext, R.layout.chatdetailset_feedback, null);
        PopupWindow MorePopup = new BasePopupWindow(mContext);
        MorePopup.setWidth(RadioGroup.LayoutParams.MATCH_PARENT);
        MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setTouchable(true);
        MorePopup.setContentView(contentView);
        MorePopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        TextView change_txt = contentView.findViewById(R.id.change_txt);
        change_txt.setText("分享群聊");
        TextView newregistr_txt = contentView.findViewById(R.id.newregistr_txt);
        newregistr_txt.setText("举报");
        TextView cancel_txt = contentView.findViewById(R.id.cancel_txt);
        String group = "group";
        change_txt.setOnClickListener(v -> {
            MeAndGroup_QRCode.actionGroupStart(mContext, groupModel);
            MorePopup.dismiss();
        });
        newregistr_txt.setOnClickListener(v -> {
            String type = "4";
            GroupDetail_Report.actionStart(mContext, myGroupId, group, type);
            MorePopup.dismiss();
        });
        cancel_txt.setOnClickListener(v -> {
            MorePopup.dismiss();
        });
    }


    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        HXgroupId = intent.getStringExtra("HXgroupId");
    }

    @Override
    protected void initData() {
        super.initData();

        viewModel = new ViewModelProvider(this).get(GroupDetailViewModel.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GroupMethod(GroupDetailSet.this, saveFile.Group_MyGroupInfo_Url);
    }

    private class head_ConstraintClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            GroupDetail_Edit.actionStart(mContext, Identity, groupModel);
        }
    }

    //清除记录
    private class clear_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            SearchGroupChatActivity.actionStart(mContext, HXgroupId);
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
                .setConfirmClickListener((view, content) -> {
                    if (!TextUtils.isEmpty(content)) {
//                            itemGroupName.getTvContent().setText(content);
                        String changType = "4";
                        String keyStr = "groupName";
                        CreateGroupMethod(GroupDetailSet.this, saveFile.Group_UpdateInfo_Url, changType, keyStr, content, "", "");
                    }
                })
                .setTitle("设置群名称")
                .show();
    }

    //群二维码
    private class group_Code_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            MeAndGroup_QRCode.actionGroupStart(GroupDetailSet.this, groupModel);
        }
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
                .setConfirmClickListener((view, content) -> {
                    if (!TextUtils.isEmpty(content)) {
//                            itemGroupName.getTvContent().setText(content);
                        String changType = "3";
                        changeGroupNameMethod(GroupDetailSet.this, saveFile.Group_UpdateNickName_Url, content);
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

    //管理群
    private class group_Management_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            GroupAllMembers_Manage.actionStart(mContext, HXgroupId, Identity, members);
        }
    }

    //消息置顶
    private class group_chatTop_SwitchClick implements MySwitchItemView.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(MySwitchItemView buttonView, boolean isChecked) {
            if (isChecked) {
                conversation.setExtField(System.currentTimeMillis() + "");
            } else {
                conversation.setExtField("");
            }
            LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP));
        }
    }

    //置顶消息弹窗
    private void showTopDialog() {
        String content = TextUtils.equals(group_Top_ArrowItemView.getTvContent().getText().toString().trim(), "未设置")
                ? "" : group_Top_ArrowItemView.getTvContent().getText().toString().trim();
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
                        CreateGroupMethod(GroupDetailSet.this, saveFile.Group_UpdateInfo_Url, changType, keyStr, content1, "", "");
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
                .setOnConfirmClickListener(view -> {
                    fragment.dismiss();
                    String changType = "4";
                    String keyStr = "groupNotice";
                    String empty = "";
                    CreateGroupMethod(GroupDetailSet.this, saveFile.Group_UpdateInfo_Url, changType, keyStr, empty, "", "");
                })
                .showCancelButton(true)
                .show();
    }

    //群消息免打扰 接入推送
    private class disturb_SwitchClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            DisturbMethod(GroupDetailSet.this, saveFile.Group_Disturb_Url, disturb_Switch.getSwitch().isChecked(), disturb_Switch.getSwitch());
        }
    }

    private void changePush() {
        List<String> onPushList = new ArrayList<>();
        String hxUserName = groupModel.getData().getGroupHxId();
        onPushList.add(hxUserName);
        boolean isPush = disturb_Switch.getSwitch().isChecked();
        viewModel.updatePushServiceForGroup(hxUserName, isPush);
    }

    //修改背景图
    private class chatBg_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            selectPicFromLocal();
        }
    }

    //清空聊天记录
    private class clearHistory_TxtClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            showClearConfirmDialog();
        }
    }

    private void showClearConfirmDialog() {
        new SimpleDialogFragment.Builder(mContext)
                .setTitle(R.string.em_chat_group_detail_clear_history_warning)
                .setOnConfirmClickListener(view -> viewModel.clearHistory(HXgroupId))
                .showCancelButton(true)
                .show();
    }


    //退出 或解散群
    private class remove_TxtClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            isRemove();
        }
    }

    private class member_quit_SwitchClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            String baseUrl = saveFile.Group_AnonymousOrMute_Url;
            String Type = "4";
            groupManageMethod(mContext, baseUrl, Type, member_quit_Switch.getSwitch().isChecked(), member_quit_Switch.getSwitch());
        }
    }


    //退出 或解散群
    private void isRemove() {
        int Identity = groupModel.getData().getIdentity();
        String content = "";
        if (Identity == 1) {
            content = "解散群后您将失去和群友的联系，确定要解散嘛？";
        } else {
            content = "您将退出群聊,同时删除该群的聊天记录？";
        }
        new SimpleDialogFragment.Builder(mContext)
                .setTitle("提示")
                .showContent(true)
                .setContent(content)
                .setOnConfirmClickListener(view -> {
//                        String changType = "4";
                    dissmissGroupMethod(GroupDetailSet.this, saveFile.Group_Out_Url);
                })
                .showCancelButton(true)
                .show();
    }


    GruopInfo_Bean groupModel;
    int Identity;

    public void GroupMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupHxId", HXgroupId);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                groupModel = new Gson().fromJson(result, GruopInfo_Bean.class);
                GruopInfo_Bean.DataDTO dataDTO = groupModel.getData();
                Uri uri = Uri.parse(dataDTO.getGroupHeadImg());
                person_img.setImageURI(uri);
                group_Name.setText(dataDTO.getGroupName());
                String content = dataDTO.getGroupIntroduction().isEmpty() ? "群主很懒，还没有群介绍哦~" : dataDTO.getGroupIntroduction();
                group_content.setText(content);
                group_Name_ArrowItemView.getTvContent().setText(dataDTO.getGroupName());
                group_Code_ArrowItemView.getTvContent().setText(dataDTO.getGroupNum().toString());
                group_Nick_ArrowItemView.getTvContent().setText(dataDTO.getMyNickname());
                String topStr = TextUtils.isEmpty(dataDTO.getGroupNotice()) ? "未设置" : dataDTO.getGroupNotice();
                group_Top_ArrowItemView.getTvContent().setText(topStr);
                if (groupModel.getData().getIsPush() == 1) {
                    member_quit_Switch.getSwitch().setChecked(true);
                }
                myGroupId = dataDTO.getGroupId();
                Identity = dataDTO.getIdentity();
                isSilenceMethod(groupModel.getData().getIsDisturb());

                GroupMemberListMethod(GroupDetailSet.this, saveFile.Group_UserList_Url, Identity);

                setGroupManagement(Identity);

            }

            @Override
            public void failed(String... args) {

            }
        });

    }

    /**
     * 消息免打扰
     *
     * @param isSilence
     */
    private void isSilenceMethod(int isSilence) {
        if (isSilence == 0) {
            disturb_Switch.getSwitch().setChecked(true);
        } else {
            disturb_Switch.getSwitch().setChecked(false);
        }
    }

    /**
     * 群主管理员 与普通成员
     *
     * @param Identity 1群主2管理员3普通成员
     */
    private void setGroupManagement(int Identity) {
        if (Identity == 3) {
            Muggle_Group.setVisibility(View.GONE);
            group_Name_ArrowItemView.setEnabled(false);
            group_Name_ArrowItemView.getArrow().setVisibility(View.GONE);
//            groupmember_remove.setVisibility(View.GONE);
            remove_Txt.setText("删除并退出");
        } else if (Identity == 2) {
            Muggle_Group.setVisibility(View.VISIBLE);
//            groupmember_remove.setVisibility(View.VISIBLE);
            remove_Txt.setText("删除并退出");
        } else if (Identity == 1) {
            Muggle_Group.setVisibility(View.VISIBLE);
//            groupmember_remove.setVisibility(View.VISIBLE);
            remove_Txt.setText("解散本群");
        }
    }

    //群成员列表
    public void imgFlow(FlowLayout myFlow, @NotNull List<GroupMember_Bean.DataDTO> imgList, int Identity) {
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
            name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

            ImageView group_man = myView.findViewById(R.id.group_man);

            group_man.setVisibility(View.GONE);
            if (imgList.get(i).getIdentity() == 1) {
                group_man.setVisibility(View.VISIBLE);
                group_man.setImageResource(R.mipmap.group_man);
            } else if (imgList.get(i).getIdentity() == 2) {
                group_man.setVisibility(View.VISIBLE);
                group_man.setImageResource(R.mipmap.group_owen);
            }
            name.setText(imgList.get(i).getNickname());
            Uri headUri = Uri.parse(imgList.get(i).getHeadImg());
            mySimple.setImageURI(headUri);
            myView.setTag(i);
//            int position = myFlow.getChildCount() ;//下标
            myFlow.addView(myView);

            myView.setOnClickListener(v -> {
                int isAnonymous = groupModel.getData().getIsAnonymous();
                int issProtect = groupModel.getData().getIsProtect();
                if (isAnonymous == 1 || issProtect == 1) {
                    Toast.makeText(mContext, "成员保护中", Toast.LENGTH_SHORT).show();
                    return;
                }
                int tag = (int) v.getTag();
                String userID = imgList.get(tag).getUserId();
                String hxUserName = imgList.get(tag).getHxUserName();
                GroupFriend_Detail.actionStart(mContext, userID, hxUserName, groupModel);
            });
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

        groupmember_add.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                GroupAllMembers_Add.actionStart(GroupDetailSet.this, myGroupId);
            }
        });

        groupmember_remove.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                GroupAllMembers_Remove.actionStart(GroupDetailSet.this, myGroupId);
            }
        });

    }

    //查看全部群成员
    private class group_member_ArrowItemViewClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            GroupAllMembers.actionStart(GroupDetailSet.this, groupListModel.getData(), groupModel);
        }
    }


    GroupMember_Bean groupListModel;
    public void GroupMemberListMethod(Context context, String baseUrl, int Identity) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", myGroupId);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                groupListModel = new Gson().fromJson(result, GroupMember_Bean.class);
                String memberCount = String.format("查看%s名群成员", groupListModel.getData().size());
                group_member_ArrowItemView.getTvContent().setText(memberCount);
                imgFlow(group_Flow, groupListModel.getData(), Identity);
                addMembers(groupListModel.getData());
            }

            @Override
            public void failed(String... args) {

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
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", myGroupId);
        map.put(keyStr, valueStr);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                if (TextUtils.equals(changType, "3")) {
//                    String adressStr = valueStr.isEmpty() ? "未设置" : valueStr;
                    group_Name_ArrowItemView.getTvContent().setText(valueStr);
                    group_Name.setText(valueStr);
                    sendMes(groupModel, valueStr);
                } else if (TextUtils.equals(changType, "4")) {
                    group_Top_ArrowItemView.getTvContent().setText(valueStr);
                }
            }

            @Override
            public void failed(String... args) {
            }
        });
    }

    //发送消息 创建群聊发送一条邀请人信息
    private void sendMes(GruopInfo_Bean Model, String NameStr) {
        MyInfo myInfo = new MyInfo(mContext);
        String conversationId = Model.getData().getGroupHxId();
//        EMMessage message = EMMessage.createTxtSendMessage(mContext.getString(R.string.CREATE_GROUP_CONTENT), conversationId);
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
        message.setAttribute(MyConstant.GROUP_HEAD, Model.getData().getGroupHeadImg());
        DemoHelper.getInstance().getChatManager().sendMessage(message);
    }

    //发送消息 退出群聊
    private void sendMes(GruopInfo_Bean Model) {
        MyInfo myInfo = new MyInfo(mContext);
        String conversationId = Model.getData().getGroupHxId();
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        EMTextMessageBody txtBody = new EMTextMessageBody("");
        message.addBody(txtBody);
        message.setTo(conversationId);
        message.setChatType(EMMessage.ChatType.GroupChat);
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_GROUP_LEAVE);
//        message.setAttribute(MyConstant.USER_NAME, UserName);
        message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNickname());
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
        message.setAttribute(MyConstant.GROUP_NAME, Model.getData().getGroupName());
        message.setAttribute(MyConstant.GROUP_HEAD, Model.getData().getGroupHeadImg());
        DemoHelper.getInstance().getChatManager().sendMessage(message);
    }

    public void changeGroupNameMethod(Context context, String baseUrl, String changeNick) {
        MyInfo myInfo = new MyInfo(context);
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", myGroupId);
        map.put("nickname", changeNick);
        map.put("userId", myInfo.getUserInfo().getUserId());
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                group_Nick_ArrowItemView.getTvContent().setText(changeNick);
            }

            @Override
            public void failed(String... args) {
            }
        });
    }

    public void dissmissGroupMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", myGroupId);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                //1是群主
                if (groupModel.getData().getIdentity() == 1) {
                    Toast.makeText(context, "已解散", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "已退出", Toast.LENGTH_SHORT).show();
                    sendMes(groupModel);
                }
                DemoHelper.getInstance().getChatManager().deleteConversation(groupModel.getData().getGroupHxId(), true);
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void failed(String... args) {
            }
        });
    }

    /**
     * @param context
     * @param baseUrl
     * @param agreeType 1禁言2匿名3群成员保护4群通知
     * @param
     */
    public void groupManageMethod(Context context, String baseUrl, String agreeType, boolean isBlock, View switchView) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", myGroupId);
        map.put("type", agreeType);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                switchView.setEnabled(true);
            }

            @Override
            public void failed(String... args) {
                switchView.setEnabled(true);
            }
        });
    }


    /**
     * 消息免打扰
     *
     * @param context
     * @param baseUrl
     */
    public void DisturbMethod(Context context, String baseUrl, boolean isBlock, View switchView) {
//        site_progressbar.setVisibility(View.VISIBLE);
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupModel.getData().getGroupId());
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @SneakyThrows
            @Override
            public void success(String result) {

                if (isBlock) {
                    groupModel.getData().setIsDisturb(0);
                } else {
                    groupModel.getData().setIsDisturb(1);
                }
                isSilenceMethod(groupModel.getData().getIsDisturb());
                switchView.setEnabled(true);
                changePush();
            }

            @Override
            public void failed(String... args) {
                //                site_progressbar.setVisibility(View.GONE);
                switchView.setEnabled(true);
            }
        });
    }

    protected static final int REQUEST_CODE_LOCAL = 3;

    /**
     * select local image
     */
    protected void selectPicFromLocal() {
        EaseCompat.openImage(this, REQUEST_CODE_LOCAL);
    }

    /**
     * 选择本地图片处理结果
     *
     * @param data
     */
    protected void onActivityResultForLocalPhotos(@Nullable Intent data) {
        if (data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                saveFile.saveShareData(MyConstant.Chat_BG + HXgroupId, selectedImage.toString(), GroupDetailSet.this);
                finish();
                String filePath = EaseFileUtils.getFilePath(mContext, selectedImage);

                LiveDataBus.get().with(MyConstant.Chat_BG).setValue(selectedImage.toString());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOCAL) { // send local image
            onActivityResultForLocalPhotos(data);
        }
    }

    //群成员ID
    private void addMembers(List<GroupMember_Bean.DataDTO> data) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getIdentity() == 3) {
                members.add(data.get(i).getHxUserName());
            }
        }
    }

}