package com.xunda.mo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.SubscribeMessage;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelbiz.WXOpenBusinessView;
import com.tencent.mm.opensdk.modelbiz.WXOpenBusinessWebview;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xiaomi.mipush.sdk.Constants;
import com.xunda.mo.R;

import java.lang.ref.WeakReference;


//public class WXEntryActivity extends WXCallbackActivity implements IWXAPIEventHandler {
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
//
//	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
//
//	private Button gotoBtn, regBtn, launchBtn, checkBtn, payBtn, favButton;
//
//	// IWXAPI 是第三方app和微信通信的openapi接口
//    private IWXAPI api;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.entry);
//
//        // 通过WXAPIFactory工厂，获取IWXAPI的实例
////    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
//
//    	regBtn = (Button) findViewById(R.id.reg_btn);
//    	regBtn.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// 将该app注册到微信
//			    api.registerApp(Constants.APP_ID);
//			}
//		});
//
//        gotoBtn = (Button) findViewById(R.id.goto_send_btn);
//        gotoBtn.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
////		        startActivity(new Intent(WXEntryActivity.this, SendToWXActivity.class));
//		        finish();
//			}
//		});
//
//        launchBtn = (Button) findViewById(R.id.launch_wx_btn);
//        launchBtn.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Toast.makeText(WXEntryActivity.this, "launch result = " + api.openWXApp(), Toast.LENGTH_LONG).show();
//			}
//		});
//
//        checkBtn = (Button) findViewById(R.id.check_timeline_supported_btn);
//        checkBtn.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				int wxSdkVersion = api.getWXAppSupportAPI();
//				if (wxSdkVersion >= TIMELINE_SUPPORTED_VERSION) {
//					Toast.makeText(WXEntryActivity.this, "wxSdkVersion = " + Integer.toHexString(wxSdkVersion) + "\ntimeline supported", Toast.LENGTH_LONG).show();
//				} else {
//					Toast.makeText(WXEntryActivity.this, "wxSdkVersion = " + Integer.toHexString(wxSdkVersion) + "\ntimeline not supported", Toast.LENGTH_LONG).show();
//				}
//			}
//		});
//
//        payBtn = (Button) findViewById(R.id.goto_pay_btn);
//        payBtn.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
////				startActivity(new Intent(WXEntryActivity.this, PayActivity.class));
//		        finish();
//			}
//		});
//
//        favButton = (Button) findViewById(R.id.goto_fav_btn);
//        favButton.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
////				startActivity(new Intent(WXEntryActivity.this, AddFavoriteToWXActivity.class));
//				finish();
//			}
//		});
//
//        // debug
//
//        // debug end
//
//        api.handleIntent(getIntent(), this);
//    }
//
//	@Override
//	protected void onNewIntent(Intent intent) {
//		super.onNewIntent(intent);
//
//		setIntent(intent);
//        api.handleIntent(intent, this);
//	}
//
//	// 微信发送请求到第三方应用时，会回调到该方法
//	@Override
//	public void onReq(BaseReq req) {
//		Toast.makeText(this, "openid = " + req.openId, Toast.LENGTH_SHORT).show();
//
//		switch (req.getType()) {
//		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
//			goToGetMsg();
//			break;
//		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
//			goToShowMsg((ShowMessageFromWX.Req) req);
//			break;
//		case ConstantsAPI.COMMAND_LAUNCH_BY_WX:
//			Toast.makeText(this, R.string.launch_from_wx, Toast.LENGTH_SHORT).show();
//			break;
//		default:
//			break;
//		}
//	}
//
//	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
//	@Override
//	public void onResp(BaseResp resp) {
//		Toast.makeText(this, "openid = " + resp.openId, Toast.LENGTH_SHORT).show();
//		if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
//			Toast.makeText(this, "code = " + ((SendAuth.Resp) resp).code, Toast.LENGTH_SHORT).show();
//		}
//
//		int result = 0;
//
//		switch (resp.errCode) {
//		case BaseResp.ErrCode.ERR_OK:
//			result = R.string.errcode_success;
//			break;
//		case BaseResp.ErrCode.ERR_USER_CANCEL:
//			result = R.string.errcode_cancel;
//			break;
//		case BaseResp.ErrCode.ERR_AUTH_DENIED:
//			result = R.string.errcode_deny;
//			break;
//		default:
//			result = R.string.errcode_unknown;
//			break;
//		}
//
//		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
//	}
//
//	private void goToGetMsg() {
////		Intent intent = new Intent(this, GetFromWXActivity.class);
////		intent.putExtras(getIntent());
////		startActivity(intent);
//		finish();
//	}
//
//	private void goToShowMsg(ShowMessageFromWX.Req showReq) {
//		WXMediaMessage wxMsg = showReq.message;
//		WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;
//
//		StringBuffer msg = new StringBuffer(); // 组织一个待显示的消息内容
//		msg.append("description: ");
//		msg.append(wxMsg.description);
//		msg.append("\n");
//		msg.append("extInfo: ");
//		msg.append(obj.extInfo);
//		msg.append("\n");
//		msg.append("filePath: ");
//		msg.append(obj.filePath);
//
////		Intent intent = new Intent(this, ShowFromWXActivity.class);
////		intent.putExtra(Constants.ShowMsgActivity.STitle, wxMsg.title);
////		intent.putExtra(Constants.ShowMsgActivity.SMessage, msg.toString());
////		intent.putExtra(Constants.ShowMsgActivity.BAThumbData, wxMsg.thumbData);
////		startActivity(intent);
//		finish();
//	}
private static String TAG = "MicroMsg.WXEntryActivity";

	private IWXAPI api;
	private MyHandler handler;

	private static class MyHandler extends Handler {
		private final WeakReference<WXEntryActivity> wxEntryActivityWeakReference;

		public MyHandler(WXEntryActivity wxEntryActivity){
			wxEntryActivityWeakReference = new WeakReference<WXEntryActivity>(wxEntryActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			int tag = msg.what;
			switch (tag) {
//				case NetworkUtil.GET_TOKEN: {
//					Bundle data = msg.getData();
//					JSONObject json = null;
//					try {
//						json = new JSONObject(data.getString("result"));
//						String openId, accessToken, refreshToken, scope;
//						openId = json.getString("openid");
//						accessToken = json.getString("access_token");
//						refreshToken = json.getString("refresh_token");
//						scope = json.getString("scope");
////						Intent intent = new Intent(wxEntryActivityWeakReference.get(), SendToWXActivity.class);
////						intent.putExtra("openId", openId);
////						intent.putExtra("accessToken", accessToken);
////						intent.putExtra("refreshToken", refreshToken);
////						intent.putExtra("scope", scope);
////						wxEntryActivityWeakReference.get().startActivity(intent);
//					} catch (JSONException e) {
//						Log.e(TAG, e.getMessage());
//					}
//				}
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
		handler = new MyHandler(this);

		try {
			Intent intent = getIntent();
			api.handleIntent(intent, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		setIntent(intent);
		api.handleIntent(intent, this);
	}

	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq req) {
		switch (req.getType()) {
			case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
//				goToGetMsg();
				break;
			case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
//				goToShowMsg((ShowMessageFromWX.Req) req);
				break;
			case ConstantsAPI.COMMAND_LAUNCH_BY_WX:
//				Toast.makeText(this, R.string.launch_from_wx, Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
		}


		finish();
	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
		int result = 0;

		switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				result = R.string.errcode_success;
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				result = R.string.errcode_cancel;
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				result = R.string.errcode_deny;
				break;
			case BaseResp.ErrCode.ERR_UNSUPPORT:
//				result = R.string.errcode_unsupported;
				break;
			default:
				result = R.string.errcode_unknown;
				break;
		}
//		Toast.makeText(this, getString(result) + ", type=" + resp.getType(), Toast.LENGTH_SHORT).show();

		if (resp.getType() == ConstantsAPI.COMMAND_SUBSCRIBE_MESSAGE) {
			SubscribeMessage.Resp subscribeMsgResp = (SubscribeMessage.Resp) resp;
			String text = String.format("openid=%s\ntemplate_id=%s\nscene=%d\naction=%s\nreserved=%s",
					subscribeMsgResp.openId, subscribeMsgResp.templateID, subscribeMsgResp.scene, subscribeMsgResp.action, subscribeMsgResp.reserved);

			Toast.makeText(this, text, Toast.LENGTH_LONG).show();
		}

		if (resp.getType() == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
			WXLaunchMiniProgram.Resp launchMiniProgramResp = (WXLaunchMiniProgram.Resp) resp;
			String text = String.format("openid=%s\nextMsg=%s\nerrStr=%s",
					launchMiniProgramResp.openId, launchMiniProgramResp.extMsg,launchMiniProgramResp.errStr);

			Toast.makeText(this, text, Toast.LENGTH_LONG).show();
		}

		if (resp.getType() == ConstantsAPI.COMMAND_OPEN_BUSINESS_VIEW) {
			WXOpenBusinessView.Resp launchMiniProgramResp = (WXOpenBusinessView.Resp) resp;
			String text = String.format("openid=%s\nextMsg=%s\nerrStr=%s\nbusinessType=%s",
					launchMiniProgramResp.openId, launchMiniProgramResp.extMsg,launchMiniProgramResp.errStr,launchMiniProgramResp.businessType);

			Toast.makeText(this, text, Toast.LENGTH_LONG).show();
		}

		if (resp.getType() == ConstantsAPI.COMMAND_OPEN_BUSINESS_WEBVIEW) {
			WXOpenBusinessWebview.Resp response = (WXOpenBusinessWebview.Resp) resp;
			String text = String.format("businessType=%d\nresultInfo=%s\nret=%d",response.businessType,response.resultInfo,response.errCode);

			Toast.makeText(this, text, Toast.LENGTH_LONG).show();
		}

//		if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
//			SendAuth.Resp authResp = (SendAuth.Resp)resp;
//			final String code = authResp.code;
//			NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/oauth2/access_token?" +
//							"appid=%s&secret=%s&code=%s&grant_type=authorization_code", "wxd930ea5d5a258f4f",
//					"1d6d1d57a3dd063b36d917bc0b44d964", code), NetworkUtil.GET_TOKEN);
//		}
		finish();
	}

//	private void goToGetMsg() {
//		Intent intent = new Intent(this, GetFromWXActivity.class);
//		intent.putExtras(getIntent());
//		startActivity(intent);
//		finish();
//	}

//	private void goToShowMsg(ShowMessageFromWX.Req showReq) {
//		WXMediaMessage wxMsg = showReq.message;
//		WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;
//
//		StringBuffer msg = new StringBuffer();
//		msg.append("description: ");
//		msg.append(wxMsg.description);
//		msg.append("\n");
//		msg.append("extInfo: ");
//		msg.append(obj.extInfo);
//		msg.append("\n");
//		msg.append("filePath: ");
//		msg.append(obj.filePath);
//
//		Intent intent = new Intent(this, ShowFromWXActivity.class);
//		intent.putExtra(Constants.ShowMsgActivity.STitle, wxMsg.title);
//		intent.putExtra(Constants.ShowMsgActivity.SMessage, msg.toString());
//		intent.putExtra(Constants.ShowMsgActivity.BAThumbData, wxMsg.thumbData);
//		startActivity(intent);
//		finish();
//	}
}