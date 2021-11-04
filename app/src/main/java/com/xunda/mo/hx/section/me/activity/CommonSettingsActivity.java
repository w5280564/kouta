package com.xunda.mo.hx.section.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMPushConfigs;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.interfaceOrImplement.OnResourceParseCallback;
import com.xunda.mo.hx.common.model.DemoModel;
import com.xunda.mo.hx.common.widget.ArrowItemView;
import com.xunda.mo.hx.common.widget.SwitchItemView;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.dialog.TimePickerDialogFragment;
import com.xunda.mo.hx.section.me.viewmodels.OfflinePushSetViewModel;
import com.xunda.mo.main.baseView.MyArrowItemView;

public class CommonSettingsActivity extends BaseInitActivity implements View.OnClickListener, SwitchItemView.OnCheckedChangeListener, EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;
    private ArrowItemView itemNotification;
    private ArrowItemView itemCallOption;
    private SwitchItemView itemTyping;
    private SwitchItemView itemSwitchSpeaker;
    private SwitchItemView itemChatroom;
    private SwitchItemView itemDeleteMsg;
    private SwitchItemView itemAutoFile;
    private SwitchItemView itemAutoDownload;
    private SwitchItemView itemAutoAcceptGroup;
    private SwitchItemView itemSwitchChatroomDeleteMsg;

    private DemoModel settingsModel;
    private EMOptions chatOptions;
    private SwitchItemView switchPushNoDisturb;
    private OfflinePushSetViewModel viewModel;
    EMPushConfigs mPushConfigs;
    private int startTime;
    private int endTime;
    private boolean shouldUpdateToServer;
    private MyArrowItemView itemPushTimeRange;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, CommonSettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_common_settings;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        itemNotification = findViewById(R.id.item_notification);
        itemCallOption = findViewById(R.id.item_call_option);
        itemTyping = findViewById(R.id.item_switch_typing);
        itemSwitchSpeaker = findViewById(R.id.item_switch_speaker);
        itemChatroom = findViewById(R.id.item_switch_chatroom);
        itemDeleteMsg = findViewById(R.id.item_switch_delete_msg);
        itemAutoFile = findViewById(R.id.item_switch_auto_file);
        itemAutoDownload = findViewById(R.id.item_switch_auto_download);
        itemAutoAcceptGroup = findViewById(R.id.item_switch_auto_accept_group);
        itemSwitchChatroomDeleteMsg = findViewById(R.id.item_switch_chatroom_delete_msg);
        switchPushNoDisturb = findViewById(R.id.switch_push_no_disturb);
        itemPushTimeRange = findViewById(R.id.item_push_time_range);

    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        itemNotification.setOnClickListener(this);
        itemCallOption.setOnClickListener(this);
        itemTyping.setOnCheckedChangeListener(this);
        itemSwitchSpeaker.setOnCheckedChangeListener(this);
        itemChatroom.setOnCheckedChangeListener(this);
        itemDeleteMsg.setOnCheckedChangeListener(this);
        itemAutoFile.setOnCheckedChangeListener(this);
        itemAutoDownload.setOnCheckedChangeListener(this);
        itemAutoAcceptGroup.setOnCheckedChangeListener(this);
        itemSwitchChatroomDeleteMsg.setOnCheckedChangeListener(this);
        switchPushNoDisturb.setOnClickListener(this);
        itemPushTimeRange.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        settingsModel = DemoHelper.getInstance().getModel();
        chatOptions = EMClient.getInstance().getOptions();

        itemTyping.getSwitch().setChecked(settingsModel.isShowMsgTyping());
        itemSwitchSpeaker.getSwitch().setChecked(settingsModel.getSettingMsgSpeaker());
        itemChatroom.getSwitch().setChecked(settingsModel.isChatroomOwnerLeaveAllowed());
        itemDeleteMsg.getSwitch().setChecked(settingsModel.isDeleteMessagesAsExitGroup());
        itemAutoFile.getSwitch().setChecked(settingsModel.isSetTransferFileByUser());
        itemAutoDownload.getSwitch().setChecked(settingsModel.isSetAutodownloadThumbnail());
        itemAutoAcceptGroup.getSwitch().setChecked(settingsModel.isAutoAcceptGroupInvitation());
        itemSwitchChatroomDeleteMsg.getSwitch().setChecked(settingsModel.isDeleteMessagesAsExitChatRoom());

        viewModel = new ViewModelProvider(this).get(OfflinePushSetViewModel.class);
        viewModel = new ViewModelProvider(this).get(OfflinePushSetViewModel.class);
        viewModel.getConfigsObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EMPushConfigs>() {
                @Override
                public void onSuccess(EMPushConfigs data) {
                    mPushConfigs = data;
                    processPushConfigs();
                }
            });
        });

        viewModel.getDisableObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    itemPushTimeRange.getTvContent().setText(getTimeRange(startTime, endTime));
                    shouldUpdateToServer = false;
                }
            });
        });

        viewModel.getEnableObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {

                }
            });
        });



        viewModel.getPushConfigs();

    }

    private void processPushConfigs(){
        if(mPushConfigs == null)
            return;
        startTime = mPushConfigs.getNoDisturbStartHour();
        endTime = mPushConfigs.getNoDisturbEndHour();
        if(startTime < 0) {
            startTime = 0;
        }
        if(endTime < 0) {
            endTime = 0;
        }
        itemPushTimeRange.getTvContent().setText(getTimeRange(startTime, endTime));
        if(mPushConfigs.isNoDisturbOn()) {
            switchPushNoDisturb.getSwitch().setChecked(mPushConfigs.isNoDisturbOn());
            setOptionsVisible(true);
            if(shouldUpdateToServer) {
                viewModel.disableOfflinePush(startTime, endTime);
            }
        }
    }

    private String getTimeRange(int start, int end) {
        return getTimeToString(start) + "~" + getTimeToString(end);
    }

    private String getTimeToString(int hour) {
        return getDoubleDigit(hour) + ":00";
    }

    private String getDoubleDigit(int num) {
        return num > 10 ? String.valueOf(num) : "0" + num;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_notification :
                OfflinePushSettingsActivity.actionStart(mContext);
                break;
            case R.id.item_call_option :
                CallOptionActivity.actionStart(mContext);
                break;
            case R.id.switch_push_no_disturb :
                boolean checked = switchPushNoDisturb.getSwitch().isChecked();
                switchPushNoDisturb.getSwitch().setChecked(!checked);
                if(switchPushNoDisturb.getSwitch().isChecked()) {
                    viewModel.getPushConfigs();
                    shouldUpdateToServer = true;
                    setOptionsVisible(true);
                }else {
                    setOptionsVisible(false);
                    viewModel.enableOfflinePush();
                }
                break;
            case R.id.item_push_time_range :
                showTimePicker();
                break;
        }

    }

    private void setOptionsVisible(boolean visible) {
        itemPushTimeRange.setVisibility(visible ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onCheckedChanged(SwitchItemView buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.item_switch_typing :
                settingsModel.showMsgTyping(isChecked);
                break;
            case R.id.item_switch_speaker :
                settingsModel.setSettingMsgSpeaker(isChecked);
                break;
            case R.id.item_switch_chatroom :
                settingsModel.allowChatroomOwnerLeave(isChecked);
                chatOptions.allowChatroomOwnerLeave(isChecked);
                break;
            case R.id.item_switch_delete_msg :
                settingsModel.setDeleteMessagesAsExitGroup(isChecked);
                chatOptions.setDeleteMessagesAsExitGroup(isChecked);
                break;
            case R.id.item_switch_auto_file :
                settingsModel.setTransfeFileByUser(isChecked);
                chatOptions.setAutoTransferMessageAttachments(isChecked);
                break;
            case R.id.item_switch_auto_download :
                settingsModel.setAutodownloadThumbnail(isChecked);
                chatOptions.setAutoDownloadThumbnail(isChecked);
                break;
            case R.id.item_switch_auto_accept_group :
                settingsModel.setAutoAcceptGroupInvitation(isChecked);
                chatOptions.setAutoAcceptGroupInvitation(isChecked);
                break;
            case R.id.item_switch_chatroom_delete_msg:
                settingsModel.setDeleteMessagesAsExitChatRoom(isChecked);
                chatOptions.setDeleteMessagesAsExitChatRoom(isChecked);
                break;

        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    private void showTimePicker() {
        new TimePickerDialogFragment.Builder(mContext)
                .setTitle(R.string.demo_no_disturb_time)
                .setConfirmColor(R.color.em_color_brand)
                .showCancelButton(true)
                .showMinute(false)
                .setStartTime(getTimeToString(startTime))
                .setEndTime(getTimeToString(endTime))
                .setOnTimePickCancelListener(R.string.cancel, view -> {

                })
                .setOnTimePickSubmitListener(R.string.confirm, (view, start, end) -> {
                    try {
                        int startHour = Integer.parseInt(getHour(start));
                        int endHour = Integer.parseInt(getHour(end));
                        if(startHour != endHour) {
                            startTime = startHour;
                            endTime = endHour;
                            viewModel.disableOfflinePush(startTime, endTime);
                        }else {
                            showToast(R.string.offline_time_rang_error);
                        }

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                })
                .show();
    }

    private String getHour(String time) {
        return time.contains(":") ? time.substring(0, time.indexOf(":")) : time;
    }
}
