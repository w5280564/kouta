package com.xunda.mo.hx.section.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.easeui.widget.EaseTitleBar;
import com.xunda.mo.R;
import com.xunda.mo.hx.common.widget.ArrowItemView;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.login.MainRegister_Agreement;
import com.xunda.mo.main.me.activity.Me_Manage_BlackList;
import com.xunda.mo.staticdata.xUtils3Http;

public class PrivacyIndexActivity extends BaseInitActivity implements View.OnClickListener, EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;
    private ArrowItemView itemBlackManager;
    private ArrowItemView itemEquipmentManager,item_privacy_manager,item_manager;
    private TextView privacy_txt;

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, PrivacyIndexActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_privacy_index;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        itemBlackManager = findViewById(R.id.item_black_manager);
        itemEquipmentManager = findViewById(R.id.item_equipment_manager);
        item_privacy_manager = findViewById(R.id.item_privacy_manager);
        item_manager = findViewById(R.id.item_manager);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        itemBlackManager.setOnClickListener(this);
        itemEquipmentManager.setOnClickListener(this);
        item_privacy_manager.setOnClickListener(this);
        item_manager.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_black_manager :
//                ContactBlackListActivity.actionStart(mContext);
                Me_Manage_BlackList.actionStart(mContext);
                break;
            case R.id.item_equipment_manager :
                break;
            case R.id.item_privacy_manager:
                String url = xUtils3Http.BASE_URL + "service.html";
                MainRegister_Agreement.actionStart(PrivacyIndexActivity.this, url);
                break;
            case R.id.item_manager:
                String urlSe = xUtils3Http.BASE_URL + "privacy.html";
                MainRegister_Agreement.actionStart(PrivacyIndexActivity.this, urlSe);
                break;
        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }


}
