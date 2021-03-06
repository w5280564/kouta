package com.xunda.mo.hx.section.me.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.xunda.mo.R;
import com.xunda.mo.dialog.TwoButtonDialog;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.baseView.MyArrowItemView;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.main.login.MainLogin_Register;
import com.xunda.mo.main.me.activity.Me_Safety;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.dialog.BaseDialogFragment;

public class SetIndexActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, View.OnClickListener {
    private EaseTitleBar titleBar;
    private MyArrowItemView itemSecurity;
    private MyArrowItemView itemNotification;
    private MyArrowItemView itemCommonSet;
    private MyArrowItemView itemPrivacy, item_se;
    private Button btnLogout;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SetIndexActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_set_index;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        itemSecurity = findViewById(R.id.item_security);
        itemNotification = findViewById(R.id.item_notification);
        itemCommonSet = findViewById(R.id.item_common_set);
        itemPrivacy = findViewById(R.id.item_privacy);
        btnLogout = findViewById(R.id.btn_logout);
        item_se = findViewById(R.id.item_se);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        itemSecurity.setOnClickListener(this);
        itemNotification.setOnClickListener(this);
        itemCommonSet.setOnClickListener(this);
        itemPrivacy.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        item_se.setOnClickListener(this);
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_security://???????????????
                AccountSecurityActivity.actionStart(mContext);
                break;
            case R.id.item_notification://????????????
                MessageReceiveSetActivity.actionStart(mContext);
                break;
            case R.id.item_common_set://??????
                CommonSettingsActivity.actionStart(mContext);
                break;
            case R.id.item_privacy://??????
                PrivacyIndexActivity.actionStart(mContext);
                break;
            case R.id.btn_logout:
                showExitDialog();
                break;
            case R.id.item_se:
                Me_Safety.actionStart(mContext);
                break;
        }
    }


    /**
     * ??????dialog
     */
    private void showExitDialog() {
        TwoButtonDialog dialog = new TwoButtonDialog(this, "????????????????????????", "??????", "??????",
                new TwoButtonDialog.ConfirmListener() {

                    @Override
                    public void onClickRight() {
                        logout();
                    }

                    @Override
                    public void onClickLeft() {

                    }
                });
        dialog.show();

    }


    void logout() {
        final ProgressDialog pd = new ProgressDialog(this);
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        DemoHelper.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        // show login screen
                        MyInfo myInfo = new MyInfo(SetIndexActivity.this);
                        myInfo.clearInfoData(SetIndexActivity.this);
                        saveFile.clearShareData("JSESSIONID", SetIndexActivity.this);
                        Intent intent = new Intent(SetIndexActivity.this, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }
            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        Toast.makeText(SetIndexActivity.this, "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
//    void logout() {
//        final ProgressDialog pd = new ProgressDialog(mContext);
//        String st = getResources().getString(R.string.Are_logged_out);
//        pd.setMessage(st);
//        pd.setCanceledOnTouchOutside(false);
//        pd.show();
//        DemoHelper.getInstance().logout(true,new EMCallBack() {
//
//            @Override
//            public void onSuccess() {
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        pd.dismiss();
//                        // show login screen
//                        finishOtherActivities();
////                        startActivity(new Intent(mContext, LoginActivity.class));
//                        Intent intent = new Intent(mContext, MainLogin_Register.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                });
//            }
//
//            @Override
//            public void onProgress(int progress, String status) {
//
//            }
//
//            @Override
//            public void onError(int code, String message) {
//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        pd.dismiss();
//                        Toast.makeText(mContext, "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//    }

}
