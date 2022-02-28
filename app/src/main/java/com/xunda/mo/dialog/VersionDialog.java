package com.xunda.mo.dialog;

import static com.luck.picture.lib.tools.ScreenUtils.getScreenWidth;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.easeui.utils.StringUtil;
import com.xunda.mo.R;

/**
 * 版本升级弹出框
 */
public class VersionDialog extends Dialog implements
		View.OnClickListener {

	private VersionConfirmListener listener;
	private Context mContext;
	private TextView tv_update,tv_cancel,tv_content,tv_version;
	private String update_content,version;
	private int isForceUpdate;

	public VersionDialog(Context context,String update_content,String version,int isForceUpdate,VersionConfirmListener confirmListener) {
		super(context, R.style.CenterDialogStyle);
		this.listener = confirmListener;
		this.mContext = context;
		this.update_content = update_content;
		this.version = version;
		this.isForceUpdate = isForceUpdate;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.version_dialog);
		setCanceledOnTouchOutside(false);
		initView();
		initEvent();
	}

	private void initView() {
		LinearLayout ll_parent = findViewById(R.id.ll_parent);
		int screenWidth =  getScreenWidth(mContext);//屏幕的宽度
		int parentWidth = (int) (screenWidth /5f * 4);//弹出框的宽度
		int parentHeight = (int) (parentWidth*1.32f);//弹出框的高度
		ViewGroup.LayoutParams layoutParams = ll_parent.getLayoutParams();
		layoutParams.width = parentWidth;
		layoutParams.height = parentHeight;
		ll_parent.setLayoutParams(layoutParams);
		tv_version =  findViewById(R.id.tv_version);
		tv_cancel =  findViewById(R.id.tv_cancel);
		tv_update =  findViewById(R.id.tv_update);
		tv_content = findViewById(R.id.tv_content);
		tv_content.setText(StringUtil.isBlank(update_content)?"":update_content);
		tv_cancel.setVisibility(isForceUpdate==0?View.VISIBLE:View.GONE);//0推荐更新1强制
		String versionName = "发现新版本 V" + version;
		tv_version.setText(versionName);
	}
	
	
	private void initEvent() {
		findViewById(R.id.tv_cancel).setOnClickListener(this);
		tv_update.setOnClickListener(this);
	}
	
	/**
	 * 回调接口对象
	 */

	public interface VersionConfirmListener {
		void onDownload();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.tv_cancel:
			dismiss();
			break;
		case R.id.tv_update:
			listener.onDownload();
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {

	}
}
