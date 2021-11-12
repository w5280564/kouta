package com.xunda.mo.main.group.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMGroup;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.baseView.MyArrowItemView;
import com.xunda.mo.main.baseView.MySwitchItemView;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class GroupAllMembers_Manage extends BaseInitActivity {
    private int Identity;
    private String HXgroupId;
    private String myGroupId;
    private MyArrowItemView groupManage_data_ArrowItemView, groupManage_set_ArrowItemView, groupManage_blacklist_ArrowItemView,
            groupManage_Forbidden_ArrowItemView, groupManage_apply_ArrowItemView, groupManage_Seek_ArrowItemView
            , groupManage_AddGroup_ArrowItemView,groupManage_Transfer_ArrowItemView;
    private View OwenOrManage_Group;
    private MySwitchItemView groupManage_chat_Switch, groupManage_Forbidden_Switch, groupManage_Protect_Switch;
    private List<String> members;

    public static void actionStart(Context context, String HXgroupId, int Identity, List<String> members) {
        Intent intent = new Intent(context, GroupAllMembers_Manage.class);
        intent.putExtra("HXgroupId", HXgroupId);
        intent.putExtra("Identity", Identity);
        intent.putExtra("members", (Serializable) members);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.group_allmembers_manage;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        Identity = intent.getIntExtra("Identity", 0);
        HXgroupId = intent.getStringExtra("HXgroupId");
        members = (List<String>) intent.getSerializableExtra("members");
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();
        ScrollView my_Scroll = findViewById(R.id.my_Scroll);
        OverScrollDecoratorHelper.setUpOverScroll(my_Scroll);
        OwenOrManage_Group = findViewById(R.id.OwenOrManage_Group);
        groupManage_data_ArrowItemView = findViewById(R.id.groupManage_data_ArrowItemView);
        groupManage_data_ArrowItemView.setOnClickListener(new groupManage_data_ArrowItemViewClick());
        groupManage_set_ArrowItemView = findViewById(R.id.groupManage_set_ArrowItemView);
        groupManage_set_ArrowItemView.setOnClickListener(new groupManage_set_ArrowItemViewClick());
        groupManage_blacklist_ArrowItemView = findViewById(R.id.groupManage_blacklist_ArrowItemView);
        groupManage_blacklist_ArrowItemView.setOnClickListener(new groupManage_blacklist_ArrowItemViewClick());
        groupManage_apply_ArrowItemView = findViewById(R.id.groupManage_apply_ArrowItemView);
        groupManage_apply_ArrowItemView.setOnClickListener(new groupManage_apply_ArrowItemViewClick());
        groupManage_Forbidden_ArrowItemView = findViewById(R.id.groupManage_Forbidden_ArrowItemView);
        groupManage_Forbidden_ArrowItemView.setOnClickListener(new groupManage_Forbidden_ArrowItemViewClick());
        groupManage_chat_Switch = findViewById(R.id.groupManage_chat_Switch);
        groupManage_chat_Switch.getSwitch().setOnClickListener(new groupManage_chat_SwitchClick());
        groupManage_Forbidden_Switch = findViewById(R.id.groupManage_Forbidden_Switch);
        groupManage_Forbidden_Switch.getSwitch().setOnClickListener(new groupManage_Forbidden_SwitchClick());
        groupManage_Protect_Switch = findViewById(R.id.groupManage_Protect_Switch);
        groupManage_Protect_Switch.getSwitch().setOnClickListener(new groupManage_Protect_SwitchClick());
        groupManage_AddGroup_ArrowItemView = findViewById(R.id.groupManage_AddGroup_ArrowItemView);
        groupManage_AddGroup_ArrowItemView.setOnClickListener(new groupManage_AddGroup_ArrowItemViewClick());
        groupManage_Seek_ArrowItemView = findViewById(R.id.groupManage_Seek_ArrowItemView);
        groupManage_Seek_ArrowItemView.setOnClickListener(new groupManage_Seek_ArrowItemViewClick());
        groupManage_Transfer_ArrowItemView = findViewById(R.id.groupManage_Transfer_ArrowItemView);
        groupManage_Transfer_ArrowItemView.setOnClickListener(new groupManage_Transfer_ArrowItemViewClick());

        owenOrManage();
    }

    private void owenOrManage() {
        if (Identity == 1) {
            OwenOrManage_Group.setVisibility(View.VISIBLE);
        } else if (Identity == 2) {
            OwenOrManage_Group.setVisibility(View.GONE);
        }
    }


    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(GroupAllMembers_Manage.this, R.color.white));
        title_Include.setElevation(2f);//阴影
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("群管理");
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
        GroupMethod(GroupAllMembers_Manage.this, saveFile.Group_MyGroupInfo_Url);
    }

    private class groupManage_data_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            GroupDetail_Edit.actionStart(mContext, Identity, groupModel);
        }
    }

    private class groupManage_set_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            GroupAllMembers_Manage_SetupManage.actionStart(mContext, myGroupId);
        }
    }

    private class groupManage_blacklist_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            GroupAllMembers_Manage_BlackList.actionStart(mContext, myGroupId);
        }
    }

    private class groupManage_Forbidden_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            GroupAllMembers_Manage_ForbiddenList.actionStart(mContext, myGroupId);
        }
    }

    private class groupManage_apply_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            GroupAllMembers_Manage_Apply.actionStart(mContext, myGroupId);
        }
    }

    private class groupManage_chat_SwitchClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            String baseUrl = saveFile.Group_AnonymousOrMute_Url;
            String Type = "2";
            groupManageMethod(GroupAllMembers_Manage.this, baseUrl, Type, groupManage_chat_Switch.getSwitch().isChecked(), groupManage_chat_Switch.getSwitch());
        }
    }

    private class groupManage_Forbidden_SwitchClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            String baseUrl = saveFile.Group_AnonymousOrMute_Url;
            String Type = "1";
            groupManageMethod(GroupAllMembers_Manage.this, baseUrl, Type, groupManage_Forbidden_Switch.getSwitch().isChecked(), groupManage_Forbidden_Switch.getSwitch());
        }
    }

    private class groupManage_Protect_SwitchClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            String baseUrl = saveFile.Group_AnonymousOrMute_Url;
            String Type = "3";
            groupManageMethod(GroupAllMembers_Manage.this, baseUrl, Type, groupManage_Protect_Switch.getSwitch().isChecked(), groupManage_Protect_Switch.getSwitch());
        }
    }

    private class groupManage_AddGroup_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            setAddGroup();
        }
    }

    private class groupManage_Seek_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            setSeekGroup();
        }
    }

    private class groupManage_Transfer_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            GroupAllMembers_Manage_ChangeGroupLeader.actionStart(GroupAllMembers_Manage.this,groupModel, myGroupId, Identity);
        }
    }

    //只设置一组数据就好
    private void setAddGroup() {
        final List<String> mOptionsItems = new ArrayList<>();
//        mOptionsItems.add("不允许任何人加群");
        mOptionsItems.add("允许任何人加群");
        mOptionsItems.add("需要身份验证，由管理员审核");
        mOptionsItems.add("只允许群成员邀请");
        mOptionsItems.add("只允许群主和管理员邀请");
        mOptionsItems.add("只允许vip加入");
        OptionsPickerView pvOptions = new OptionsPickerBuilder(GroupAllMembers_Manage.this, (options1, option2, options3, v) -> {
            groupManage_AddGroup_ArrowItemView.getTvContent().setText(mOptionsItems.get(options1));
            String changType = "1";
            int valueStr = options1 + 2; // +1  1是不允许任何人加群
            ChangeGroupSetMethod(GroupAllMembers_Manage.this,  saveFile.Group_Way_Url, changType, valueStr);
        }).build();
        pvOptions.setNPicker(mOptionsItems, null, null);
        pvOptions.show();
    }

    private void setSeekGroup() {
        final List<String> mOptionsItems = new ArrayList<>();
        mOptionsItems.add("仅通过群号");
        mOptionsItems.add("通过群号或关键字");
        mOptionsItems.add("不允许");
        OptionsPickerView pvOptions = new OptionsPickerBuilder(GroupAllMembers_Manage.this, (options1, option2, options3, v) -> {
            groupManage_Seek_ArrowItemView.getTvContent().setText(mOptionsItems.get(options1));
            String changType = "2";
            int valueStr = options1 + 1;
            ChangeGroupSetMethod(GroupAllMembers_Manage.this,  saveFile.Group_Way_Url, changType, valueStr);
        }).build();
        pvOptions.setNPicker(mOptionsItems, null, null);
        pvOptions.show();
    }


    GruopInfo_Bean groupModel;
    public void GroupMethod(Context context, String baseUrl) {
        Map<String,Object> map = new HashMap<>();
        map.put("groupHxId",HXgroupId);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                groupModel = new Gson().fromJson(result, GruopInfo_Bean.class);
                GruopInfo_Bean.DataDTO dataDTO = groupModel.getData();
                myGroupId = dataDTO.getGroupId();
                Identity = dataDTO.getIdentity();
                int isAnonymous = dataDTO.getIsAnonymous();
                int isAllMute = dataDTO.getIsAllMute();
                int isProtect = dataDTO.getIsProtect();
                if (isAnonymous == 1) {
                    groupManage_chat_Switch.getSwitch().setChecked(true);
                }
                if (isAllMute == 1) {
                    groupManage_Forbidden_Switch.getSwitch().setChecked(true);
                }
                if (isProtect == 1) {
                    groupManage_Protect_Switch.getSwitch().setChecked(true);
                }
                int FindWay = groupModel.getData().getJoinWay();
                String findStr = GroupAdd(FindWay);
                groupManage_AddGroup_ArrowItemView.getTvContent().setText(findStr);

                int JoinWay = groupModel.getData().getFindWay();
                String joinStr = GroupSearchMethod(JoinWay);
                groupManage_Seek_ArrowItemView.getTvContent().setText(joinStr);
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
        Map<String,Object> map = new HashMap<>();
        map.put("groupId", myGroupId);
        map.put("type", agreeType);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                switchView.setEnabled(true);
                if (TextUtils.equals(agreeType, "1")) {
                    if (isBlock) {
                        muteAllMembers();
                    } else {
                        unmuteAllMembers();
                    }
                } else if (TextUtils.equals(agreeType, "2")) {
                    LiveDataBus.get().with(MyConstant.GROUP_CHAT_ANONYMOUS).postValue(isBlock);
                } else if (TextUtils.equals(agreeType, "3")) {

                }
            }
            @Override
            public void failed(String... args) {
            }
        });

    }

    /**
     * 修改加群或查找方式
     *
     * @param context
     * @param baseUrl
     * @param changType
     * @param way
     */
    public void ChangeGroupSetMethod(Context context, String baseUrl, String changType, int way) {
        Map<String,Object> map = new HashMap<>();
        map.put("groupId", myGroupId);
        map.put("type", changType);
        map.put("way", way);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void failed(String... args) {
            }
        });

    }


    private void muteAllMembers() {
        DemoHelper.getInstance().getGroupManager().muteAllMembers(HXgroupId, new EMValueCallBack<EMGroup>() {
            @Override
            public void onSuccess(EMGroup value) {

            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    private void unmuteAllMembers() {
        DemoHelper.getInstance().getGroupManager().unmuteAllMembers(HXgroupId, new EMValueCallBack<EMGroup>() {
            @Override
            public void onSuccess(EMGroup value) {

            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    /**
     * @param findWay 加群方式 1不允许任何人加群，2允许任何人加群，3需要身份验证，由管理员审核4，只允许群成员邀请5只允许群主和管理员邀请6只允许vip加入
     */
    private String GroupAdd(int findWay) {
        String findStr;
        switch (findWay) {
            case 1:
                findStr = "不允许任何人加群";
                break;
            case 2:
                findStr = "允许任何人加群";
                break;
            case 3:
                findStr = "需要身份验证，由管理员审核";
                break;
            case 4:
                findStr = "只允许群成员邀请";
                break;
            case 5:
                findStr = "只允许群主和管理员邀请";
                break;
            case 6:
                findStr = "只允许vip加入";
                break;
            default:
                findStr = "";
                break;
        }
        return findStr;
    }

    /**
     * @param JoinWay 查找方式 1仅通过群号2通过群号或关键字3不允许
     */
    private String GroupSearchMethod(int JoinWay) {
        String findStr = "";
        switch (JoinWay) {
            case 1:
                findStr = "仅通过群号";
                break;
            case 2:
                findStr = "通过群号或关键字";
                break;
            case 3:
                findStr = "不允许";
                break;
            default:
                findStr = "";
                break;
        }
        return findStr;
    }


}