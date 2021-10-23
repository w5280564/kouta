package com.xunda.mo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xunda.mo.R;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String APP_ID = "wxdd3384bb6c79b2ca";
    private IWXAPI api;
    private TextView pay_Txt;
    private ConstraintLayout success_Con;
    private TextView success_Txt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, APP_ID);
        api.handleIntent(getIntent(), this);
        success_Con = findViewById(R.id.success_Con);
        success_Txt = findViewById(R.id.success_Txt);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//			View view = LayoutInflater.from(WXPayEntryActivity.this).inflate(R.layout.wx_success_back, null);
//			TextView  success_Txt = view.findViewById(R.id.success_Txt);
//			TextView  ensure_Txt = view.findViewById(R.id.ensure_Txt);
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setView(view);
//			int code = resp.errCode;
//			if (code == 0){
//				success_Txt.setText("你已成功开通会员");
//			}else 	if (code == -1){
//				success_Txt.setText("支付不成功");
//			}else 	if (code == -2){
//				success_Txt.setText("支付已取消");
//			}
//			builder.show();
//			ensure_Txt.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					finish();
//				}
//			});
            setView(resp.errCode);
        }
    }


    private void setView(int errCode) {
        View view = LayoutInflater.from(WXPayEntryActivity.this).inflate(R.layout.wx_success_back, null);
        TextView success_Txt = view.findViewById(R.id.success_Txt);
        TextView ensure_Txt = view.findViewById(R.id.ensure_Txt);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        int code = errCode;
        if (code == 0) {
            success_Txt.setText("你已成功开通会员");
        } else if (code == -1) {
            success_Txt.setText("支付不成功");
        } else if (code == -2) {
            success_Txt.setText("支付已取消");
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setOnCancelListener(dialog -> {
            dialog.dismiss();
            finish();
        });

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
        alertDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        ensure_Txt.setOnClickListener(v -> {
            alertDialog.dismiss();
            WXPayEntryActivity.this.finish();
        });
    }

}