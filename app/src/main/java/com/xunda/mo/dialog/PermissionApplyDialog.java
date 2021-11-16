package com.xunda.mo.dialog;

import static com.luck.picture.lib.tools.ScreenUtils.getScreenWidth;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xunda.mo.R;

/**
 * 权限申请引导Dialog
 */
public class PermissionApplyDialog extends Dialog implements View.OnClickListener {
	private Context mContext;
	private String content;
	private boolean isNotAskAgain;
	private TextView tv_content,tv_right_button;
	private OnJumpToSettingListener mListener;

	public PermissionApplyDialog(Context context, String content,boolean isNotAskAgain,OnJumpToSettingListener mListener) {
		super(context, R.style.CenterDialogStyle);
		this.mContext = context;
		this.content = content;
		this.isNotAskAgain = isNotAskAgain;
		this.mListener = mListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_permission_apply);
		setCanceledOnTouchOutside(false);
		initView();
		initEvent();
	}



	private void initView() {
		LinearLayout ll_parent = findViewById(R.id.ll_parent);
		int screenWidth = getScreenWidth(mContext);// 屏幕的宽度
		int parentWidth = (int) (screenWidth / 5f * 4);// 弹出框的宽度
		ViewGroup.LayoutParams layoutParams = ll_parent.getLayoutParams();
		layoutParams.width = parentWidth;
		ll_parent.setLayoutParams(layoutParams);

		tv_content = findViewById(R.id.tv_content);
		tv_right_button = findViewById(R.id.tv_right_button);
		tv_content.setText(TextUtils.isEmpty(content)?"":content);
		tv_right_button.setText(isNotAskAgain?mContext.getString(R.string.setting):mContext.getString(R.string.open));
	}


	private void initEvent() {
		findViewById(R.id.tv_cancel).setOnClickListener(this);
		tv_right_button.setOnClickListener(this);
	}



	@Override
	public void onBackPressed() {

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.tv_cancel:
				dismiss();
				break;

			case R.id.tv_right_button:
				dismiss();
				mListener.clickRightButton();
				break;
		}

	}


	public interface OnJumpToSettingListener{
		void clickRightButton();
	}
}
