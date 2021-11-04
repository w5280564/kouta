package com.xunda.mo.main.me.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.hyphenate.EMCallBack;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.me.activity.MultiDeviceActivity;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.main.baseView.MyArrowItemView;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.main.login.MainLogin_Register;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.HashMap;
import java.util.Map;

public class Me_Safety extends BaseInitActivity implements View.OnClickListener {

    private MyArrowItemView item_se;
    private MyArrowItemView itemEquipments;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Me_Safety.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_me_safety;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();

        item_se = findViewById(R.id.item_se);
        item_se.setOnClickListener(new item_seClick());
        itemEquipments = findViewById(R.id.item_equipments);
    }

    @Override
    protected void initListener() {
        super.initListener();
        itemEquipments.setOnClickListener(this);
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(Me_Safety.this, R.color.white));
        title_Include.setElevation(2f);//阴影
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("安全");
        Button right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.GONE);

        return_Btn.setOnClickListener(new return_Btn());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_equipments ://多端多设备管理
                MultiDeviceActivity.actionStart(mContext);
                break;
        }
    }

    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }

    private class item_seClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showsafety(mContext,item_se);
        }
    }

    private void showsafety(final Context mContext, final View view) {
        View contentView = View.inflate(mContext, R.layout.me_safety_popup, null);
        PopupWindow MorePopup = new BasePopupWindow(mContext);
        MorePopup.setWidth(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setTouchable(true);
        MorePopup.setContentView(contentView);
        MorePopup.showAtLocation(view, Gravity.CENTER, 0, 0);
        Button cancel_Btn = contentView.findViewById(R.id.cancel_Btn);
        Button ensure_Btn = contentView.findViewById(R.id.ensure_Btn);
        cancel_Btn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                MorePopup.dismiss();
            }
        });
        ensure_Btn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
               deleteMethod(mContext, saveFile.User_Delete);
            }
        });
    }

    public void deleteMethod(Context context, String baseUrl) {
        Map<String,Object> map = new HashMap<>();
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                logout();
            }
            @Override
            public void failed(String... args) {
            }
        });
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
                runOnUiThread(() -> {
                    pd.dismiss();
                    // show login screen
                    MyInfo myInfo = new MyInfo(Me_Safety.this);
                    myInfo.clearInfoData(Me_Safety.this);
                    saveFile.clearShareData("JSESSIONID", Me_Safety.this);
                    Intent intent = new Intent(Me_Safety.this, MainLogin_Register.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(() -> {
                    pd.dismiss();
                    Toast.makeText(Me_Safety.this, "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }




}