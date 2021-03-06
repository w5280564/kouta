package com.xunda.mo.main.group.activity;

import static com.xunda.mo.staticdata.AppConstant.ak;
import static com.xunda.mo.staticdata.AppConstant.bucketName;
import static com.xunda.mo.staticdata.AppConstant.endPoint;
import static com.xunda.mo.staticdata.AppConstant.sk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
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

public class GroupDetail_Report extends BaseInitActivity {

    private MyArrowItemView report_ArrowItemView;
    private ImageView add_photo_Img;
    private Button next_Btn;
    private EditText content_edit;
    private ObsClient obsClient;
    private String toReportId, userOrGroup, type;
    private com.xunda.mo.main.baseView.FlowLayout photoLayout;
    private int reportType;

    public static void actionStart(Context context, String toReportId, String userOrGroup, String type) {
        Intent intent = new Intent(context, GroupDetail_Report.class);
        intent.putExtra("toReportId", toReportId);
        intent.putExtra("userOrGroup", userOrGroup);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.group_detailreport;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        toReportId = intent.getStringExtra("toReportId");
        userOrGroup = intent.getStringExtra("userOrGroup");
        type = intent.getStringExtra("type");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();
        report_ArrowItemView = findViewById(R.id.report_ArrowItemView);
        report_ArrowItemView.setOnClickListener(new report_ArrowItemViewClick());
        photoLayout = findViewById(R.id.photoLayout);
        add_photo_Img = findViewById(R.id.add_photo_Img);
        add_photo_Img.setOnClickListener(new add_photo_ImgOnclickLister());
        content_edit = findViewById(R.id.content_edit);
        next_Btn = findViewById(R.id.next_Btn);
        next_Btn.setOnClickListener(new next_BtnClick());

        photoPaths = new ArrayList<>();
        initObsClient();
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

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(GroupDetail_Report.this, R.color.white));
        title_Include.setElevation(2f);//??????
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

    private class report_ArrowItemViewClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            setReport();
        }
    }

    //????????????
    private void setReport() {
        final List<String> mOptionsItems = new ArrayList<>();
        mOptionsItems.add("????????????");
        mOptionsItems.add("????????????");
        mOptionsItems.add("????????????");
        mOptionsItems.add("????????????");
        mOptionsItems.add("????????????");
        mOptionsItems.add("????????????");
        mOptionsItems.add("??????????????????");
        mOptionsItems.add("??????");
        OptionsPickerView pvOptions = new OptionsPickerBuilder(GroupDetail_Report.this, (options1, option2, options3, v) -> {
            report_ArrowItemView.getTvContent().setText(mOptionsItems.get(options1));
            reportType = options1 + 1;
        }).build();
        pvOptions.setPicker(mOptionsItems);
        pvOptions.show();
    }

    private class add_photo_ImgOnclickLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            setPhotoMetod(GroupDetail_Report.this);
        }
    }

    private class next_BtnClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            String content_txt = content_edit.getText().toString().trim();
            String reportStr = report_ArrowItemView.getTvContent().getText().toString().trim();

            if (TextUtils.equals(reportStr, "?????????")) {
                Toast.makeText(GroupDetail_Report.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(content_txt)) {
                Toast.makeText(GroupDetail_Report.this, "????????????", Toast.LENGTH_SHORT).show();
                return;
            }
            if (photoPaths.isEmpty()) {
                reportMethod(GroupDetail_Report.this, saveFile.Report_CreatReportLog_Url);

            } else {
                AsyncTask<Void, Void, String> task = new PostObjectTask();
                task.execute();
            }
        }
    }

    private ArrayList<String> photoPaths;
    private int maxPic = 6;

    private void setPhotoMetod(Context context) {
        int choice = maxPic - photoPaths.size();
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
                .isCompress(true)// ???????????? true or false
                .freeStyleCropEnabled(true)// ???????????????????????? true or false
                .showCropGrid(true)// ?????????????????????????????? ???????????????????????????false    true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//????????????onActivityResult code
    }


    List<String> pathNameList;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // ????????????
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    List<String> pathList = new ArrayList<>();
                    pathNameList = new ArrayList<>();
                    if (pathList != null) {
                        pathList.clear();
                    }
                    int size = selectList.size();
                    for (int i = 0; i < size; i++) {
                        if (isQ()){
                            pathList.add(selectList.get(i).getAndroidQToPath());
                        }else {
                            pathList.add(selectList.get(i).getRealPath());
                        }
                        pathNameList.add(selectList.get(i).getFileName());
                    }
                    if (pathList != null) {
                        imgFlow(photoLayout, pathList);
                    }
                    break;
                default:
                    break;
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
    public void imgFlow(FlowLayout myFlow, final List<String> imgList) {
        int size = imgList.size();
        for (int i = 0; i < size; i++) {
            photoPaths.add(imgList.get(i));
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(width, height);
            int marginLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
            itemParams.setMargins(marginLeft, 0, 0, marginLeft);

            final SimpleDraweeView mySimple = new SimpleDraweeView(this);
            add_photo_Img.setLayoutParams(itemParams);
            mySimple.setLayoutParams(itemParams);
            addSimplePath(mySimple, imgList.get(i));//??????????????????

            mySimple.setTag(i);
            int position = photoLayout.getChildCount() - 1;//??????
            myFlow.addView(mySimple, position);
            mySimple.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    for (int i = 0; i < photoLayout.getChildCount(); i++) {
                        if (view == photoLayout.getChildAt(i)) {
                            deletePhotoByPosition(i);
                            break;
                        }
                    }
                    photoLayout.removeView(view);
                    return true;
                }
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
        if (photoPaths.size() >= maxPic) {
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

    @SuppressLint("StaticFieldLeak")
    class PostObjectTask extends AsyncTask<Void, Void, String> {
        @SneakyThrows
        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder sbf = new StringBuilder();
            try {
                int size = photoPaths.size();
                String objectName = "";
                for (int i = 0; i < size; i++) {
                    objectName = "report/" + toReportId + "/" + pathNameList.get(i);//?????????????????????????????????
                    FileInputStream fis = new FileInputStream(photoPaths.get(i));
                    obsClient.putObject(bucketName, objectName, fis); // localfile?????????????????????????????????????????????????????????????????????
//                    addAcl(obsClient,bucketName,objectName,photoPaths.get(i));
                    sbf.append(objectName).append(",");
                }
                return sbf.toString();
            } catch (ObsException e) {
                sbf.append("Response Code:" + e.getResponseCode())
                        .append("Error Message:" + e.getErrorMessage())
                        .append("Error Code:" + e.getErrorCode())
                        .append("Request ID:" + e.getErrorRequestId())
                        .append("Host ID:" + e.getErrorHostId());
                return "";
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
                reportMethod(GroupDetail_Report.this, saveFile.Report_CreatReportLog_Url);
            }
        }
    }

    String pictures = "";

    //????????????
    public void reportMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("picture", pictures);
        map.put("reasons", reportType);
        map.put("remark", content_edit.getText().toString());
        map.put("toReportId", toReportId);
        map.put("type", type);
//        if (TextUtils.equals(userOrGroup, "user")) {
//            map.put("type", type);
//        } else {
//            map.put("type", "4");
//        }
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Toast.makeText(context, "???????????????", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void failed(String... args) {
            }
        });
    }


}