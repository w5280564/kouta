/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xunda.mo.hx.section.chat.activicy;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.ui.base.EaseBaseActivity;
import com.hyphenate.easeui.utils.EaseFileUtils;
import com.hyphenate.util.EMLog;
import com.xunda.mo.R;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.staticdata.SaveViewUtils;

/**
 * download and show original image
 * 
 */
public class MyEaseShowBigImageActivity extends EaseBaseActivity {
	private static final String TAG = "ShowBigImage"; 
	private ProgressDialog pd;
	private PhotoView image;
	private int default_res = R.drawable.ease_default_image;
	private String filename;
	private Bitmap bitmap;
	private boolean isDownloaded;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_show_big_image);
		super.onCreate(savedInstanceState);
		setFitSystemForTheme(true, R.color.black, false);
		image = findViewById(R.id.image);
		ProgressBar loadLocalPb = (ProgressBar) findViewById(R.id.pb_load_local);
		default_res = getIntent().getIntExtra("default_image", R.drawable.mo_icon);
		Uri uri = getIntent().getParcelableExtra("uri");
		filename = getIntent().getExtras().getString("filename");
		String msgId = getIntent().getExtras().getString("messageId");
		EMLog.d(TAG, "show big msgId:" + msgId );

		//show the image if it exist in local path
		if (EaseFileUtils.isFileExistByUri(this, uri)) {
            Glide.with(this).load(uri).into(image);
		} else if(msgId != null) {
		    downloadImage(msgId);
		}else {
			image.setImageResource(default_res);
		}

		image.setOnClickListener(v -> finish());
		image.setOnLongClickListener(v -> {
			showMore(MyEaseShowBigImageActivity.this, image, 0);
			return false;
		});

	}
	
	/**
	 * download image
	 * 
	 * @param msgId
	 */
	@SuppressLint("NewApi")
	private void downloadImage(final String msgId) {
        EMLog.e(TAG, "download with messageId: " + msgId);
		String str1 = getResources().getString(R.string.Download_the_pictures);
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setCanceledOnTouchOutside(false);
		pd.setMessage(str1);
		pd.show();
        final EMMessage msg = EMClient.getInstance().chatManager().getMessage(msgId);
        final EMCallBack callback = new EMCallBack() {
			public void onSuccess() {
			    EMLog.e(TAG, "onSuccess" );
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (!isFinishing() && !isDestroyed()) {
							if (pd != null) {
								pd.dismiss();
							}
							isDownloaded = true;
							Uri localUrlUri = ((EMImageMessageBody) msg.getBody()).getLocalUri();
							Glide.with(MyEaseShowBigImageActivity.this)
									.load(localUrlUri)
									.apply(new RequestOptions().error(default_res))
									.into(image);
						}
					}
				});
			}

			public void onError(final int error, String message) {
				EMLog.e(TAG, "offline file transfer error:" + message);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (MyEaseShowBigImageActivity.this.isFinishing() || MyEaseShowBigImageActivity.this.isDestroyed()) {
						    return;
						}
                        image.setImageResource(default_res);
                        pd.dismiss();
                        if (error == EMError.FILE_NOT_FOUND) {
							Toast.makeText(getApplicationContext(), R.string.Image_expired, Toast.LENGTH_SHORT).show();
						}
					}
				});
			}

			public void onProgress(final int progress, String status) {
				EMLog.d(TAG, "Progress: " + progress);
				final String str2 = getResources().getString(R.string.Download_the_pictures_new);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
                        if (MyEaseShowBigImageActivity.this.isFinishing() || MyEaseShowBigImageActivity.this.isDestroyed()) {
                            return;
                        }
						pd.setMessage(str2 + progress + "%");
					}
				});
			}
		};
		msg.setMessageStatusCallback(callback);
		EMClient.getInstance().chatManager().downloadAttachment(msg);
	}

	@Override
	public void onBackPressed() {
		if (isDownloaded)
			setResult(RESULT_OK);
		finish();
	}


	private void showMore(final Context mContext, final View view, final int pos) {
		View contentView = View.inflate(mContext, R.layout.chatdetailset_feedback, null);
		PopupWindow MorePopup = new BasePopupWindow(mContext);
		MorePopup.setWidth(RadioGroup.LayoutParams.MATCH_PARENT);
		MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
		MorePopup.setTouchable(true);
		MorePopup.setContentView(contentView);
		MorePopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		TextView change_txt = contentView.findViewById(R.id.change_txt);
		change_txt.setVisibility(View.GONE);
		TextView popupTitle_Txt = contentView.findViewById(R.id.popupTitle_Txt);
		popupTitle_Txt.setText("保存图片到相册");
		TextView newregistr_txt = contentView.findViewById(R.id.newregistr_txt);
		newregistr_txt.setText("保存");
		TextView cancel_txt = contentView.findViewById(R.id.cancel_txt);

		newregistr_txt.setOnClickListener(v -> {
			SaveViewUtils.saveBitmap(MyEaseShowBigImageActivity.this,image);
			MorePopup.dismiss();
		});
		cancel_txt.setOnClickListener(v -> {
			MorePopup.dismiss();
		});
	}


}
