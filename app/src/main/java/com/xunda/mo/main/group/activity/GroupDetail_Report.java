package com.xunda.mo.main.group.activity;

import static com.xunda.mo.staticdata.AppConstant.ak;
import static com.xunda.mo.staticdata.AppConstant.bucketName;
import static com.xunda.mo.staticdata.AppConstant.endPoint;
import static com.xunda.mo.staticdata.AppConstant.sk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private ImageButton add_photo_Img;
    private Button next_Btn;
    private EditText content_edit;
    private ObsClient obsClient;
    private String toReportId, userOrGroup;
    private com.xunda.mo.main.baseView.FlowLayout photoLayout;
    private int reportType;

    public static void actionStart(Context context, String toReportId, String userOrGroup) {
        Intent intent = new Intent(context, GroupDetail_Report.class);
        intent.putExtra("toReportId", toReportId);
        intent.putExtra("userOrGroup", userOrGroup);
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
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();
        report_ArrowItemView = findViewById(R.id.report_ArrowItemView);
        report_ArrowItemView.setOnClickListener(new report_ArrowItemViewClick());
        photoLayout = findViewById(R.id.photoLayout);
        add_photo_Img = (ImageButton) findViewById(R.id.add_photo_Img);
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
        // 创建ObsClient实例
        obsClient = new ObsClient(ak, sk, config);
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(GroupDetail_Report.this, R.color.white));
        title_Include.setElevation(2f);//阴影
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("举报");
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

    //举报原因
    private void setReport() {
        final List<String> mOptionsItems = new ArrayList<>();
        mOptionsItems.add("垃圾广告");
        mOptionsItems.add("色情低俗");
        mOptionsItems.add("骚扰信息");
        mOptionsItems.add("欺诈骗钱");
        mOptionsItems.add("赌博骗局");
        mOptionsItems.add("造谣传谣");
        mOptionsItems.add("违法违规信息");
        mOptionsItems.add("其他");
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
            String contenttxt = content_edit.getText().toString().trim();
            String reportStr = report_ArrowItemView.getTvContent().getText().toString().trim();

            if (TextUtils.equals(reportStr, "请选择")) {
                Toast.makeText(GroupDetail_Report.this, "请选择举报原因", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(contenttxt)) {
                Toast.makeText(GroupDetail_Report.this, "不能为空", Toast.LENGTH_SHORT).show();
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
                .imageEngine(GlideEnGine.createGlideEngine()) //图片加载空白 加入Glide加载图片
                .imageSpanCount(4)// 每行显示个数 int
                .maxSelectNum(choice)
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .isSingleDirectReturn(true)//PictureConfig.SINGLE模式下是否直接返回
                .isAndroidQTransform(true)//Android Q版本下是否需要拷贝文件至应用沙盒内
                .isPreviewImage(true)// 是否可预览图片 true or false
                .isCamera(false)// 是否显示拍照按钮 true or false
                .imageFormat(PictureMimeType.JPEG)// 拍照保存图片格式后缀,默认jpeg
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .isCompress(true)// 是否压缩 true or false
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }


    List<String> pathNameList;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    List<String> pathList = new ArrayList<>();
                    pathNameList = new ArrayList<>();
                    if (pathList != null) {
                        pathList.clear();
                    }
                    int size = selectList.size();
                    for (int i = 0; i < size; i++) {
//                        pathList.add(selectList.get(i).getRealPath());
                        pathList.add(selectList.get(i).getAndroidQToPath());
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

    //图片选择器
    public void imgFlow(FlowLayout myFlow, final List<String> imgList) {
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
            addSimplePath(mySimple, imgList.get(i));//多图修改尺寸

            mySimple.setTag(i);
//            mySimpleArr.add(mySimple);
            int position = photoLayout.getChildCount() - 1;//下标
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
        ChangeAddPhotoImage();//检查增加图片是否显示
    }

    public void deletePhotoByPosition(int position) {
        photoPaths.remove(position);
        ChangeAddPhotoImage();
    }


    /**
     * 检查并切换增加图片的图标
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

    class PostObjectTask extends AsyncTask<Void, Void, String> {
        @SneakyThrows
        @Override
        protected String doInBackground(Void... voids) {
            StringBuffer sbf = new StringBuffer();
            try {
                int size = photoPaths.size();
                String objectName = "";
                for (int i = 0; i < size; i++) {
                    objectName = "report/" + toReportId + "/" + pathNameList.get(i);//对应上传之后的文件名称
                    FileInputStream fis = new FileInputStream(new File(photoPaths.get(i)));
                    obsClient.putObject(bucketName, objectName, fis); // localfile为待上传的本地文件路径，需要指定到具体的文件名
//                    addAcl(obsClient,bucketName,objectName,photoPaths.get(i));
                    sbf.append(objectName).append(",");
                }
                return sbf.toString();
            } catch (ObsException e) {
                sbf.append("\n\n");
                sbf.append("Response Code:" + e.getResponseCode())
                        .append("\n\n")
                        .append("Error Message:" + e.getErrorMessage())
                        .append("\n\n")
                        .append("Error Code:" + e.getErrorCode())
                        .append("\n\n")
                        .append("Request ID:" + e.getErrorRequestId())
                        .append("\n\n")
                        .append("Host ID:" + e.getErrorHostId());
                return sbf.toString();
            } catch (Exception e) {
                sbf.append("\n\n");
                sbf.append(e.getMessage());
                return sbf.toString();
            } finally {
                if (obsClient != null) {
                    try {
                        /*
                         * Close obs client
                         */
                        obsClient.close();
                    } catch (IOException e) {
                    }
                }
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Log.i("abc", " result.getStatusCode():" + s);
            pictures = s;
            reportMethod(GroupDetail_Report.this, saveFile.Report_CreatReportLog_Url);
        }
    }

    String pictures = "";

    //问题反馈
    public void reportMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("picture", pictures);
        map.put("reasons", reportType);
        map.put("remark", content_edit.getText().toString());
        map.put("toReportId", toReportId);
        if (TextUtils.equals(userOrGroup, "user")) {
            map.put("type", "2");
        } else {
            map.put("type", "4");
        }
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Toast.makeText(context, "举报已上传", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void failed(String... args) {
            }
        });
    }


}