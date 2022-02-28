package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.xunda.mo.R;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.StaticData;

/**
 * location row
 */
public class MyEaseChatRowLocation extends BaseChatRowWithNameAndHeader {
    private TextView locationView;
    private TextView tvLocationName;
    private EMLocationMessageBody locBody;

    public MyEaseChatRowLocation(Context context, boolean isSender) {
        super(context, isSender);
    }

    public MyEaseChatRowLocation(Context context, EMMessage message, int position, Object adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(!showSenderType ? R.layout.myease_row_received_location
                : R.layout.myease_row_sent_location, this);
    }

    @Override
    protected void onFindViewById() {
        locationView = findViewById(R.id.tv_location);
        tvLocationName = findViewById(R.id.tv_location_name);
        tv_user_role = findViewById(R.id.tv_user_role);
    }

    @Override
    protected void onSetUpView() {
        locBody = (EMLocationMessageBody) message.getBody();

        String Address = locBody.getAddress();
        String locName = Address.substring(0, Address.indexOf(","));
        String location = Address.substring(Address.indexOf(",") + 1);
        tvLocationName.setText(locName);
        locationView.setText(location);

        if (isSender()) {
            StaticData.changeShapColor(bubbleLayout, ContextCompat.getColor(context, R.color.white));
        }
    }

    @Override
    protected void onMessageCreate() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (statusView != null) {
            statusView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onMessageSuccess() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if (statusView != null) {
            statusView.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onMessageError() {
        super.onMessageError();
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if (statusView != null) {
            statusView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onMessageInProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (statusView != null) {
            statusView.setVisibility(View.GONE);
        }
    }

}
