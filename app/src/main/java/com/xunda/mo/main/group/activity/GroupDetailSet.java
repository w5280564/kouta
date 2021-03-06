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

import androidx.annotation.NonNull;
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
import com.hyphenate.easeui.utils.EaseCompat;
import com.hyphenate.easeui.utils.EaseFileUtils;
import com.hyphenate.easeui.utils.MyEaseCommonUtils;
import com.hyphenate.easeui.utils.StringUtil;
import com.xunda.mo.R;
import com.xunda.mo.dialog.TwoButtonDialog;
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
import com.xunda.mo.main.me.activity.ChangeGroupNameActivity;
import com.xunda.mo.main.me.activity.ChangeMyGroupNickNameActivity;
import com.xunda.mo.main.me.activity.MeAndGroup_QRCode;
import com.xunda.mo.model.GroupMember_Bean;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class GroupDetailSet extends BaseInitActivity {
    private String HXgroupId;
    private SimpleDraweeView person_img;
    private TextView group_Name;
    private TextView group_content;
    private TextView remove_Txt;
    private TextView tv_id;
    private MyArrowItemView group_Name_ArrowItemView,  group_Nick_ArrowItemView, group_member_ArrowItemView,
            clear_ArrowItemView, group_Top_ArrowItemView, chatBg_ArrowItemView, group_Management_ArrowItemView;
    private MySwitchItemView member_quit_Switch, group_chatTop_Switch, disturb_Switch;
    private Group Muggle_Group;
    private FlowLayout group_Flow;
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
        tv_id = findViewById(R.id.tv_id);
        ConstraintLayout group_Code_ArrowItemView = findViewById(R.id.group_Code_ArrowItemView);
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
        TextView clearHistory_Txt = findViewById(R.id.clear_Txt);
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
        title_Include.setElevation(2f);//??????
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("????????????");
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
        group_chatTop_Switch.getSwitch().setChecked(!TextUtils.isEmpty(extField) && MyEaseCommonUtils.isTimestamp(extField));

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
            showMore(GroupDetailSet.this, right_Btn);
        }
    }

    private void showMore(final Context mContext, final View view) {
        View contentView = View.inflate(mContext, R.layout.chatdetailset_feedback, null);
        PopupWindow MorePopup = new BasePopupWindow(mContext);
        MorePopup.setWidth(RadioGroup.LayoutParams.MATCH_PARENT);
        MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setTouchable(true);
        MorePopup.setContentView(contentView);
        MorePopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        TextView change_txt = contentView.findViewById(R.id.change_txt);
        change_txt.setText("????????????");
        TextView newregistr_txt = contentView.findViewById(R.id.newregistr_txt);
        newregistr_txt.setText("??????");
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
        viewModel.getMessageChangeObservable().with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(this, event -> {
            if(event.isGroupLeave() && TextUtils.equals(HXgroupId, event.message)) {
                finish();
                return;
            }
            if(event.isGroupChange()) {
                onResume();
            }
        });
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

    //????????????
    private class clear_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            SearchGroupChatActivity.actionStart(mContext, HXgroupId);
        }
    }

    //?????????
    private class group_Name_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            if (groupModel==null) {
                return;
            }
            ChangeGroupNameActivity.actionStart(mContext,group_Name_ArrowItemView.getTvContent().getText().toString(),groupModel);
        }
    }


    //????????????
    private class group_Code_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            MeAndGroup_QRCode.actionGroupStart(GroupDetailSet.this, groupModel);
        }
    }

    //?????????
    private class group_Nick_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            ChangeMyGroupNickNameActivity.actionStart(mContext,group_Nick_ArrowItemView.getTvContent().getText().toString(),myGroupId);
        }
    }


    //???????????????
    private class group_Top_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            showTopDialog();
        }
    }

    //?????????
    private class group_Management_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            GroupAllMembers_Manage.actionStart(mContext, HXgroupId, Identity, members);
        }
    }

    //????????????
    private class group_chatTop_SwitchClick implements MySwitchItemView.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(MySwitchItemView buttonView, boolean isChecked) {
            insertConversionExdInfo(isChecked);
            LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP));
        }
    }

    //???????????????????????????????????????
    private void insertConversionExdInfo(boolean isInsertMessageTop) {
        String extField = conversation.getExtField();
        JSONObject jsonObject = null;
        if (!TextUtils.isEmpty(extField)) {
            try {
                jsonObject = new JSONObject(extField);
                jsonObject.put("isInsertMessageTop", isInsertMessageTop);
                jsonObject.put("topTimeMillis", isInsertMessageTop?System.currentTimeMillis():0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{
            jsonObject  = new JSONObject();
            try {
                jsonObject.put("isInsertMessageTop", isInsertMessageTop);
                jsonObject.put("topTimeMillis", isInsertMessageTop?System.currentTimeMillis():0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (jsonObject==null) {
            return;
        }
        conversation.setExtField(jsonObject.toString());
    }


    //??????????????????
    private void showTopDialog() {
        String content = TextUtils.equals(group_Top_ArrowItemView.getTvContent().getText().toString().trim(), "?????????")
                ? "" : group_Top_ArrowItemView.getTvContent().getText().toString().trim();
        GroupTopDialogFragment.showDialog(mContext,
                "???????????????",
                content,
                "?????????",
                (view, content1) -> {
                    //???????????????
                    if (!TextUtils.isEmpty(content1)) {
                        String changType = "4";
                        String keyStr = "groupNotice";
                        CreateGroupMethod(GroupDetailSet.this, saveFile.Group_UpdateInfo_Url, changType, keyStr, content1, "", "");
                    }
                }, (view, fragment) -> isChangeTop(fragment)
        );

    }

    //??????????????????
    private void isChangeTop(Dialog fragment) {
        new SimpleDialogFragment.Builder(mContext)
                .setTitle("??????")
                .showContent(true)
                .setContent("???????????????????????????")
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

    //?????????????????? ????????????
    private class disturb_SwitchClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            DisturbMethod(GroupDetailSet.this, saveFile.Group_Disturb_Url, disturb_Switch.getSwitch().isChecked(), disturb_Switch.getSwitch());
        }
    }

    private void changePush() {
        String hxUserName = groupModel.getData().getGroupHxId();
        boolean isPush = disturb_Switch.getSwitch().isChecked();
        viewModel.updatePushServiceForGroup(hxUserName, isPush);
    }

    //???????????????
    private class chatBg_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            selectPicFromLocal();
        }
    }

    //??????????????????
    private class clearHistory_TxtClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            showToastDialog(getString(R.string.em_chat_group_detail_clear_history_warning),1);
        }
    }


    /**
     * ??????dialog
     */
    private void showToastDialog(String content,int type) {
        TwoButtonDialog dialog = new TwoButtonDialog(this, content, "??????", "??????",
                new TwoButtonDialog.ConfirmListener() {

                    @Override
                    public void onClickRight() {
                        if (type==1) {//??????????????????
                            viewModel.clearHistory(HXgroupId);
                        }else{//??????/????????????
                            dissmissGroupMethod(GroupDetailSet.this, saveFile.Group_Out_Url);
                        }
                    }

                    @Override
                    public void onClickLeft() {

                    }
                });
        dialog.show();

    }


    //?????? ????????????
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


    //?????? ????????????
    private void isRemove() {
        int Identity = groupModel.getData().getIdentity();
        String content;
        if (Identity == 1) {
            content = "??????????????????????????????????????????????????????????????????";
            showToastDialog(content,2);
        } else {
            content = "??????????????????,????????????????????????????????????";
            showToastDialog(content,3);
        }
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
                String content = dataDTO.getGroupIntroduction().isEmpty() ? "????????????????????????????????????~" : dataDTO.getGroupIntroduction();
                group_content.setText(content);
                group_Name_ArrowItemView.getTvContent().setText(dataDTO.getGroupName());
                tv_id.setText(dataDTO.getGroupNum().toString());
                group_Nick_ArrowItemView.getTvContent().setText(dataDTO.getMyNickname());
                String topStr = TextUtils.isEmpty(dataDTO.getGroupNotice()) ? "?????????" : dataDTO.getGroupNotice();
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
     * ???????????????
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
     * ??????????????? ???????????????
     *
     * @param Identity 1??????2?????????3????????????
     */
    private void setGroupManagement(int Identity) {
        if (Identity == 3) {
            Muggle_Group.setVisibility(View.GONE);
            group_Name_ArrowItemView.setEnabled(false);
            group_Name_ArrowItemView.getArrow().setVisibility(View.GONE);
//            groupmember_remove.setVisibility(View.GONE);
            remove_Txt.setText("???????????????");
        } else if (Identity == 2) {
            Muggle_Group.setVisibility(View.VISIBLE);
//            groupmember_remove.setVisibility(View.VISIBLE);
            remove_Txt.setText("???????????????");
        } else if (Identity == 1) {
            Muggle_Group.setVisibility(View.VISIBLE);
//            groupmember_remove.setVisibility(View.VISIBLE);
            remove_Txt.setText("????????????");
        }
    }

    //???????????????
    public void imgFlow(FlowLayout myFlow, @NotNull List<GroupMember_Bean.DataDTO> imgList, int Identity) {
        if (myFlow != null) {
            myFlow.removeAllViews();
        }
        int size = imgList.size();
        //???????????????????????????2???10??? ??????????????????????????????
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
            assert myFlow != null;
            myFlow.addView(myView);

            myView.setOnClickListener(v -> {
                int isAnonymous = groupModel.getData().getIsAnonymous();
                int issProtect = groupModel.getData().getIsProtect();
                if (isAnonymous == 1 || issProtect == 1) {
                    Toast.makeText(mContext, "???????????????", Toast.LENGTH_SHORT).show();
                    return;
                }
                int tag = (int) v.getTag();
                String userID = imgList.get(tag).getUserId();
                String hxUserName = imgList.get(tag).getHxUserName();
                GroupFriend_Detail.actionStart(mContext, userID, hxUserName, groupModel);
            });
        }

        ImageView groupmember_add = new ImageView(this);
        groupmember_add.setBackgroundResource(R.mipmap.groupmember_add);
        groupmember_add.setLayoutParams(itemParams);
        ImageView groupmember_remove = new ImageView(this);
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

    //?????????????????????
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
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                groupListModel = new Gson().fromJson(result, GroupMember_Bean.class);
                String memberCount = String.format("??????%s????????????", groupListModel.getData().size());
                group_member_ArrowItemView.getTvContent().setText(memberCount);
                imgFlow(group_Flow, groupListModel.getData(), Identity);
                addMembers(groupListModel.getData());
                insertConversionExdInfoInGroup(result);
            }

            @Override
            public void failed(String... args) {

            }
        });
    }


    //????????????????????????????????????
    private void insertConversionExdInfoInGroup(String jsonMemberList) {
        String extField = conversation.getExtField();
        JSONObject jsonObject = null;
        if (!StringUtil.isBlank(extField)) {
            try {
                jsonObject = new JSONObject(extField);
                jsonObject.put("isInsertGroupOrFriendInfo", true);
                jsonObject.put("groupMemberList", jsonMemberList);
            } catch (JSONException e) {
                jsonObject = getJsonObjectGroup(jsonMemberList);
            }

        }else{
            jsonObject = getJsonObjectGroup(jsonMemberList);
        }

        conversation.setExtField(jsonObject.toString());
    }

    @NonNull
    private JSONObject getJsonObjectGroup(String jsonMemberList) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("isInsertGroupOrFriendInfo", true);
            jsonObject.put("groupMemberList", jsonMemberList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    /**
     * ??????????????????
     *
     * @param changType ???????????????1.?????? 2.?????? 3.??????????????? 4.??????????????? 5. 6. 7.???
     */
    public void CreateGroupMethod(Context context, String baseUrl, String changType, String keyStr, String valueStr, String keyCity, String valueCityStr) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", myGroupId);
        map.put(keyStr, valueStr);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Toast.makeText(context, "????????????", Toast.LENGTH_SHORT).show();
                if (TextUtils.equals(changType, "3")) {
//                    String adressStr = valueStr.isEmpty() ? "?????????" : valueStr;
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

    //???????????? ???????????????????????????????????????
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

    //???????????? ????????????
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

    public void dissmissGroupMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", myGroupId);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                //1?????????
                if (groupModel.getData().getIdentity() == 1) {
                    Toast.makeText(context, "?????????", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "?????????", Toast.LENGTH_SHORT).show();
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
     * @param agreeType 1??????2??????3???????????????4?????????
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
     * ???????????????
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
     * ??????????????????????????????
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

    //?????????ID
    private void addMembers(List<GroupMember_Bean.DataDTO> data) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getIdentity() == 3) {
                members.add(data.get(i).getHxUserName());
            }
        }
    }

}