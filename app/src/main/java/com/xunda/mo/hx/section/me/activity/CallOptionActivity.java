package com.xunda.mo.hx.section.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.hyphenate.easeui.widget.EaseTitleBar;
import com.xunda.mo.R;
import com.xunda.mo.hx.common.utils.PreferenceManager;
import com.xunda.mo.hx.common.widget.SwitchItemView;
import com.xunda.mo.hx.section.base.BaseInitActivity;

/**
 * Created by linan on 16/11/29.
 */
public class CallOptionActivity extends BaseInitActivity implements SwitchItemView.OnCheckedChangeListener, EaseTitleBar.OnBackPressListener  {
    private EaseTitleBar titleBar;
    private SwitchItemView rlSwitchOfflineCallPush;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, CallOptionActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_call_option;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        rlSwitchOfflineCallPush = findViewById(R.id.rl_switch_offline_call_push);
    }

    @Override
    protected void initListener() {
        super.initListener();
        rlSwitchOfflineCallPush.setOnCheckedChangeListener(this);
        titleBar.setOnBackPressListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        rlSwitchOfflineCallPush.getSwitch().setChecked(PreferenceManager.getInstance().isPushCall());
    }



    @Override
    public void onCheckedChanged(SwitchItemView buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.rl_switch_offline_call_push:
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }


    private abstract class MyTextChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
