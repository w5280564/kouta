package com.xunda.mo.main.chat.activity;

import static com.xunda.mo.staticdata.AppConstant.ak;
import static com.xunda.mo.staticdata.AppConstant.bucketName;
import static com.xunda.mo.staticdata.AppConstant.endPoint;
import static com.xunda.mo.staticdata.AppConstant.sk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.AuthTypeEnum;
import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.main.baseView.FlowLayout;
import com.xunda.mo.main.baseView.MyArrowItemView;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.GlideEnGine;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;

public class ChatComplaint extends BaseInitActivity {


    private Button next_Btn;
    private EditText content_edit;
    private ImageButton add_photo_Img;
    private ArrayList<String> photoPaths;
    private FlowLayout photoLayout;
    private ObsClient obsClient;
    String pictures;
    private MyArrowItemView complaint_ArrowItemView;
    private String reasons;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ChatComplaint.class);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_complaint;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();
        photoLayout = findViewById(R.id.photoLayout);
        content_edit = findViewById(R.id.content_edit);
        next_Btn = findViewById(R.id.next_Btn);
        next_Btn.setOnClickListener(new next_BtnOnclickLister());
        add_photo_Img = findViewById(R.id.add_photo_Img);
        add_photo_Img.setOnClickListener(new add_photo_ImgOnclickLister());
        complaint_ArrowItemView = findViewById(R.id.complaint_ArrowItemView);
        complaint_ArrowItemView.setOnClickListener(new complaint_ArrowItemViewClick());

        photoPaths = new ArrayList<>();
        initObsClient();
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setElevation(2f);//??????
        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("??????");
        Button right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.GONE);
        return_Btn.setOnClickListener(new return_Btn());
    }

    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }

    private class add_photo_ImgOnclickLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            setPhotoMetod(ChatComplaint.this);
        }
    }


    private class next_BtnOnclickLister extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
//            String contenttxt = content_edit.getText().toString().trim();
//            if (contenttxt.isEmpty()) {
//                Toast.makeText(ChatComplaint.this, "????????????", Toast.LENGTH_SHORT).show();
//                return;
//            }
            if (TextUtils.isEmpty(reasons)) {
                Toast.makeText(ChatComplaint.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                return;
            }
            if (photoPaths.isEmpty()) {
                Toast.makeText(ChatComplaint.this, "????????????????????????", Toast.LENGTH_SHORT).show();
            } else {
                AsyncTask<Void, Void, String> task = new PostObjectTask();
                task.execute();
            }
        }
    }

    private void initObsClient() {
        ObsConfiguration config = new ObsConfiguration();
        config.setSocketTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setEndPoint(endPoint);
        config.setAuthType(AuthTypeEnum.OBS);
        // ??????ObsClient??????
        obsClient = new ObsClient(ak, sk, config);
    }


    private void setPhotoMetod(Context context) {
        int choice = 3 - photoPaths.size();
        PictureSelector.create((Activity) context)
                .openGallery(PictureConfig.TYPE_IMAGE)
                .imageEngine(GlideEnGine.createGlideEngine()) //?????????????????? ??????Glide????????????
                .imageSpanCount(4)// ?????????????????? int
                .maxSelectNum(choice)
                .selectionMode(PictureConfig.MULTIPLE)// ?????? or ?????? PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .isSingleDirectReturn(true)//PictureConfig.SINGLE???????????????????????????
                .isAndroidQTransform(true)//Android Q???????????????????????????????????????????????????
                .isPreviewImage(true)// ????????????????????? true or false
                .isCamera(false)// ???????????????????????? true or false
                .imageFormat(PictureMimeType.JPEG)// ??????????????????????????????,??????jpeg
                .isZoomAnim(true)// ?????????????????? ???????????? ??????true
//                    .setOutputCameraPath(Const.getImgPath())// ???????????????????????????,?????????
//                    .isEnableCrop(true)// ???????????? true or false
                .isCompress(true)// ???????????? true or false
//                    .compressSavePath(Const.getImgPath())//????????????????????????
//                    .loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())// ????????????????????????????????????????????????10??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
//                .theme(R.style.picture_default_style)// xml???????????? R.style.picture_default_style???picture_WeChat_style or ????????????Demo
                .freeStyleCropEnabled(true)// ???????????????????????? true or false
                .showCropGrid(true)// ?????????????????????????????? ???????????????????????????false    true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//????????????onActivityResult code
    }

    List<String> pathNameList;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {// ????????????
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                List<String> pathList = new ArrayList<>();
                pathNameList = new ArrayList<>();
                int size = selectList.size();
                for (int i = 0; i < size; i++) {
                    if (isQ()) {
                        pathList.add(selectList.get(i).getAndroidQToPath());
                    } else {
                        pathList.add(selectList.get(i).getRealPath());
                    }
                    pathNameList.add(selectList.get(i).getFileName());
                }
                imgFlow(photoLayout, pathList);
            }
        }
    }

    private boolean isQ() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true;
        }
        return false;
    }

    //???????????????
    public void imgFlow(com.xunda.mo.main.baseView.FlowLayout myFlow, final List<String> imgList) {
        int size = imgList.size();
        for (int i = 0; i < size; i++) {
            photoPaths.add(imgList.get(i));
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(width, height);
            int margisright = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            itemParams.setMargins(margisright, margisright, margisright, margisright);

            final SimpleDraweeView mySimple = new SimpleDraweeView(this);
            add_photo_Img.setLayoutParams(itemParams);
            mySimple.setLayoutParams(itemParams);
            addSimplePath(mySimple, imgList.get(i));//??????????????????

            mySimple.setTag(i);
//            mySimpleArr.add(mySimple);
            int position = photoLayout.getChildCount() - 1;//??????
            myFlow.addView(mySimple, position);
            mySimple.setOnLongClickListener(view -> {
                for (int i12 = 0; i12 < photoLayout.getChildCount(); i12++) {
                    if (view == photoLayout.getChildAt(i12)) {
                        deletePhotoByPosition(i12);
                        break;
                    }
                }
                photoLayout.removeView(view);
                return true;
            });
            mySimple.setOnClickListener(v -> {
                mySimple.setFocusable(false);
                int tag = (Integer) v.getTag();
                List<String> myArr = new ArrayList<>();
                for (int i1 = 0; i1 < imgList.size(); i1++) {
                    myArr.add(imgList.get(i1));
                }
            });
        }
        ChangeAddPhotoImage();//??????????????????????????????
    }

    public void deletePhotoByPosition(int position) {
        photoPaths.remove(position);
        ChangeAddPhotoImage();
    }


    /**
     * ????????????????????????????????????
     */
    private void ChangeAddPhotoImage() {
        if (photoPaths.size() >= 3) {
            add_photo_Img.setVisibility(View.GONE);
        } else {
            add_photo_Img.setVisibility(View.VISIBLE);
        }
    }

    private void addSimplePath(SimpleDraweeView simple, String path) {
        Uri uri = Uri.fromFile(new File(path));
        int width = 50, height = 50;
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
//                .setOldController(simple.getController())
                .setImageRequest(request).build();
        simple.setController(controller);
    }

    class PostObjectTask extends AsyncTask<Void, Void, String> {
        @SneakyThrows
        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder sbf = new StringBuilder();
            try {
                int size = photoPaths.size();
                String objectName = "";
                for (int i = 0; i < size; i++) {
//                objectName = "user/" + userId + "/" + pathNameList.get(i);//?????????????????????????????????
                    objectName = "question_back/" + pathNameList.get(i);//?????????????????????????????????
                    FileInputStream fis = new FileInputStream(photoPaths.get(i));
                    obsClient.putObject(bucketName, objectName, fis); // localfile?????????????????????????????????????????????????????????????????????
                    sbf.append(objectName).append(",");
                }
                return "";
            } catch (ObsException e) {
                sbf.append("Response Code:" + e.getResponseCode())
                        .append("Error Message:" + e.getErrorMessage())
                        .append("Error Code:" + e.getErrorCode())
                        .append("Request ID:" + e.getErrorRequestId())
                        .append("Host ID:" + e.getErrorHostId());
                return sbf.toString();
            } catch (Exception e) {
                sbf.append(e.getMessage());
                return "";
            } finally {
                if (obsClient != null) {
                    try {
                        obsClient.close();
                    } catch (IOException ignored) {
                    }
                }
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (TextUtils.isEmpty(s)) {
                Toast.makeText(mContext, "??????????????????", Toast.LENGTH_SHORT).show();
            } else {
                pictures = s;
                QuestionMethod(ChatComplaint.this, saveFile.Receptionist_Complaint);
            }
        }
    }

    //????????????
    public void QuestionMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("reasons", reasons);
        map.put("picture", pictures);
        map.put("remark", content_edit.getText().toString());
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Toast.makeText(context, "?????????????????????", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void failed(String... args) {
            }
        });

    }

    private void showMore(final Context mContext, final View view, final int pos) {
        View contentView = View.inflate(mContext, R.layout.complaint_popup, null);
        PopupWindow MorePopup = new BasePopupWindow(mContext);
        MorePopup.setWidth(RadioGroup.LayoutParams.MATCH_PARENT);
        MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setTouchable(true);
        MorePopup.setContentView(contentView);
        MorePopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        TextView popup_one = contentView.findViewById(R.id.popup_one);
        TextView popup_two = contentView.findViewById(R.id.popup_two);
        TextView popup_three = contentView.findViewById(R.id.popup_three);
        TextView popup_four = contentView.findViewById(R.id.popup_four);
        TextView cancel_txt = contentView.findViewById(R.id.cancel_txt);
        popup_one.setOnClickListener(v -> {
            reasons = "9";
            complaint_ArrowItemView.getTvContent().setText(popup_one.getText());
            MorePopup.dismiss();
        });
        popup_two.setOnClickListener(v -> {
            reasons = "10";
            complaint_ArrowItemView.getTvContent().setText(popup_two.getText());
            MorePopup.dismiss();
        });
        popup_three.setOnClickListener(v -> {
            reasons = "11";
            complaint_ArrowItemView.getTvContent().setText(popup_three.getText());
            MorePopup.dismiss();
        });
        popup_four.setOnClickListener(v -> {
            reasons = "12";
            complaint_ArrowItemView.getTvContent().setText(popup_four.getText());
            MorePopup.dismiss();
        });

        cancel_txt.setOnClickListener(v -> {
            MorePopup.dismiss();
        });
    }


    private class complaint_ArrowItemViewClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showMore(ChatComplaint.this, complaint_ArrowItemView, 0);
        }
    }
}