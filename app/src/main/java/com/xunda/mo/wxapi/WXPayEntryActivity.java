package com.xunda.mo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.dialog.Success_DialogFragment;

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
			View view = LayoutInflater.from(WXPayEntryActivity.this).inflate(R.layout.wx_success_back, null);
			TextView  success_Txt = view.findViewById(R.id.success_Txt);
			TextView  ensure_Txt = view.findViewById(R.id.ensure_Txt);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(view);
			int code = resp.errCode;
			if (code == 0){
				success_Txt.setText("你已成功开通会员");
			}else 	if (code == -1){
				success_Txt.setText("支付不成功");
			}else 	if (code == -2){
				success_Txt.setText("支付已取消");
			}
//			builder.setTitle(R.string.app_tip);
//			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
			builder.show();
			ensure_Txt.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
////			showSucess(WXPayEntryActivity.this,pay_Txt,0);


//			success_Con.setVisibility(View.VISIBLE);
//			int code = resp.errCode;
//			String suessStr = "";
//			if (code == 0){
//				 suessStr = "你已成功开通会员";
//			}else 	if (code == -1){
//				suessStr = "支付不成功";
//			}else 	if (code == -2){
//				suessStr = "支付已取消";
//			}
//			success_Txt.setText(suessStr);
//
//			Toast.makeText(WXPayEntryActivity.this,suessStr,Toast.LENGTH_SHORT).show();
//			finish();
//			isSyccess(suessStr,WXPayEntryActivity.this);
		}
	}

	//
	private void isSyccess(String changString, BaseInitActivity context) {
		new Success_DialogFragment.Builder(context)
				.setTitle("提示")
				.showContent(true)
				.setContent(changString)
				.setOnConfirmClickListener(view -> {

				})
				.showCancelButton(true)
				.show();
	}


}