package com.xunda.mo.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.material.internal.FlowLayout;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.AccessControlList;
import com.obs.services.model.AuthTypeEnum;
import com.obs.services.model.CompleteMultipartUploadResult;
import com.obs.services.model.ProgressListener;
import com.obs.services.model.ProgressStatus;
import com.obs.services.model.PutObjectRequest;
import com.obs.services.model.UploadFileRequest;
import com.xunda.mo.R;
import com.xunda.mo.model.Main_QuestionFeedBack_Model;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.GlideEnGine;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.StaticData;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.utils.PermissionUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;

import static com.xunda.mo.staticdata.AppConstant.ak;
import static com.xunda.mo.staticdata.AppConstant.bucketName;
import static com.xunda.mo.staticdata.AppConstant.endPoint;
import static com.xunda.mo.staticdata.AppConstant.sk;
import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

public class MainLogin_QuestionFeedBack extends AppCompatActivity {

    private EditText phone_edit, email_edit, content_edit;
    private FlowLayout photoLayout;
    private ArrayList<String> photoPaths;
    private ImageButton add_photo_Img;
    private Button next_Btn;

    private ObsClient obsClient;
//    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain_login_questionfeedback);

        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);

//        userId = saveFile.getShareData("userId", this);
        photoPaths = new ArrayList<>();
        initTitle();
        initView();
        checkNeedPermissions();
//        verifyStoragePermissions(MainLogin_QuestionFeedBack.this);

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
        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            title_Include.setElevation(2f);//阴影
        }
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("问题反馈");
//        right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
//        right_Btn.setVisibility(View.GONE);


        return_Btn.setOnClickListener(new return_Btn());
//        right_Btn.setOnClickListener(new right_Btn());
    }

    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }

    private void initView() {
        phone_edit = findViewById(R.id.phone_edit);
        email_edit = findViewById(R.id.email_edit);
        content_edit = findViewById(R.id.content_edit);
        photoLayout = (FlowLayout) findViewById(R.id.photoLayout);
        add_photo_Img = (ImageButton) findViewById(R.id.add_photo_Img);
        next_Btn = findViewById(R.id.next_Btn);

        add_photo_Img.setOnClickListener(new add_photo_ImgOnclickLister());
        next_Btn.setOnClickListener(new next_BtnOnclickLister());
    }

    private class next_BtnOnclickLister extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            String phonetxt = phone_edit.getText().toString().trim();
            String email = email_edit.getText().toString().trim();
            String contenttxt = content_edit.getText().toString().trim();

            if (!StaticData.isPhone(phonetxt) || email.isEmpty() || contenttxt.isEmpty()) {
                Toast.makeText(MainLogin_QuestionFeedBack.this, "不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (photoPaths.isEmpty()) {
                QuestionMethod(MainLogin_QuestionFeedBack.this, saveFile.BaseUrl + saveFile.User_PublicQuestionBack_Url);

            } else {
                AsyncTask<Void, Void, String> task = new PostObjectTask();
                task.execute();
            }


//            new Thread() {
//                @SneakyThrows
//                @RequiresApi(api = Build.VERSION_CODES.O)
//                @Override
//                public void run() {
//                    super.run();
//                    // upload(obsClient, localfile);
////                    String localfile2 = photoPaths.get(0);
//                    File filename = new File(photoPaths.get(0));
//                    String local = "file://" + filename.toPath();
//                    String localfile2 = filename.toURI().toString();
//                    int size = photoPaths.size();
////                    for (int i = 0; i < size; i++) {
//                    uploadPoint(obsClient, photoPaths.get(0), pathNameList.get(0));
////                    }
//                }
//            }.start();

        }


    }

    String pictures;

    //问题反馈
    public void QuestionMethod(Context context, String baseUrl) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("phoneNum", phone_edit.getText().toString());
            obj.put("email", email_edit.getText().toString());
            obj.put("remark", content_edit.getText().toString());
            obj.put("picObsUrl", pictures);
            obj.put("loginStatus", "0");
            obj.put("equipmentVersion", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(baseUrl);
        params.setAsJsonContent(true);
        params.setBodyContent(obj.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
                if (resultString != null) {
                    Main_QuestionFeedBack_Model baseModel = new Gson().fromJson(resultString, Main_QuestionFeedBack_Model.class);
                    if (baseModel.getCode() == 200) {
//                    Login_Model baseModel = new Gson().fromJson(resultString, Login_Model.class);
//                    if (baseModel.isIsSuccess() && !baseModel.getData().equals("[]")) {
                        Toast.makeText(context, "反馈已上传", Toast.LENGTH_SHORT).show();
                        finish();
//
//                        saveFile.saveShareData("phoneNum", baseModel.getData().getPhoneNum(), MainLogin_Code.this);
//                        Intent intent = new Intent(context, MainActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
                    }

//            }
//                    } else {
//                        Toast.makeText(MainLogin_Code.this, "数据获取失败", Toast.LENGTH_SHORT).show();
//                    }
                } else {
                    Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT).show();
                }
//                    JPushInterface.resumePush(Man_Login.this);//注册
//                    JPushInterface.setAliasAndTags(Man_Login.this,JsonGet.getReturnValue(resultString, "userid"),null);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {

            }
        });
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
//                    Intent i = new Intent(this, ImagePagerActivity.class);
////                    i.putExtra("imgArr", (Parcelable) myBean);
//                    i.putExtra("imgArr", (Serializable) myArr);
//                    i.putExtra("tag", tag);
//                    startActivity(i);
//                    mySimple.setFocusable(true);
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

    private static final int REQUEST_IMAGE = 1000;
    public static final int REQUEST_CODE_IMAGE_PICK_PERSONHEAD = 35;

    private class add_photo_ImgOnclickLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            setPhotoMetod(MainLogin_QuestionFeedBack.this);
//            int choice = 9 - photoPaths.size();
//            ImgOptions options = new ImgOptions(choice, 1, true);
//            startActivityForResult(SelectedPhotoActivity.makeIntent(MainLogin_QuestionFeedBack.this, options), REQUEST_IMAGE);
        }
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

    private void setPhotoMetod(Context context) {
        int choice = 3 - photoPaths.size();
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
//                    .setOutputCameraPath(Const.getImgPath())// 自定义拍照保存路径,可不填
//                    .isEnableCrop(true)// 是否裁剪 true or false
                .isCompress(true)// 是否压缩 true or false
//                    .compressSavePath(Const.getImgPath())//压缩图片保存地址
//                    .loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
//                .theme(R.style.picture_default_style)// xml样式配制 R.style.picture_default_style、picture_WeChat_style or 更多参考Demo
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    Uri imgUri;
    String path;
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == 255 && data != null) {
////            projectModel = (List<ProjectModel>) data.getSerializableExtra("projectModel");
////            projectModel = saveFile.getGosnClass(this, "moreModel", ProjectModel.class);
////
////            projectList(project_Lin, projectModel);
//
//        } else if (requestCode == REQUEST_IMAGE) {
//            if (resultCode == RESULT_OK) {
//                List<String> pathList = data.getStringArrayListExtra(EXTRA_DATA);
//                if (pathList != null) {
//                    imgFlow(photoLayout, pathList);
//                }
//            }
//        } else if (resultCode == Activity.RESULT_OK) {//修改头像
//            path = data.getStringExtra("path");
//            imgUri = Uri.parse("file:///" + path);
//
//            Uri uri = data.getData();
//            Log.e("TAG", uri.toString());
//
////            String filePath = getRealPathFromURI(uri);
////            bitmap1 = getresizePhoto(filePath);
////            imageView1.setImageBitmap(bitmap1);
//
////            paadd_Simple.setImageURI(imgUri);
//
////            if (!TextUtils.isEmpty(path)) {
////                if (requestCode == REQUEST_CODE_IMAGE_PICK_PERSONHEAD) {
////                    compressSingleListener(new File(path), Luban.FIRST_GEAR, REQUEST_CODE_IMAGE_PICK_PERSONHEAD);
////                }
////            }
//        }
//
//    }

    //先定义
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.CAMER"};

    //然后通过一个函数来申请
    public void verifyStoragePermissions(Activity activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            PermissionUtils.requestPermission(MainLogin_QuestionFeedBack.this, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, permissionGrant);
//        }

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final PermissionUtils.PermissionGrant permissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE:
//                Toast.makeText(ImagePickerActivity.this, "读取存储权限已打开", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(Person_Set.this,Person_Set_Cache.class);
//                    startActivity(intent);
                    break;
            }
        }
    };

    /*申请权限的回调*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, permissionGrant);
    }

    private void checkNeedPermissions() {
        //6.0以上需要动态申请权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //多个权限一起申请
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 1);
        }

    }


    StringBuffer sbf = new StringBuffer();

    class PostObjectTask extends AsyncTask<Void, Void, String> {
        @SneakyThrows
        @Override
        protected String doInBackground(Void... voids) {
            try {
                int size = photoPaths.size();
                String objectName = "";
                for (int i = 0; i < size; i++) {
//                objectName = "user/" + userId + "/" + pathNameList.get(i);//对应上传之后的文件名称
                    objectName = "question_back/" + pathNameList.get(i);//对应上传之后的文件名称
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
            QuestionMethod(MainLogin_QuestionFeedBack.this, saveFile.BaseUrl + saveFile.User_PublicQuestionBack_Url);
        }
    }


    public static void addAcl(ObsClient client,String bucketname,String objectname,String localfile) {
        PutObjectRequest request = new PutObjectRequest();
        request.setBucketName(bucketname);
        request.setObjectKey(objectname);
        request.setFile(new File(localfile)); // localfile为待上传的本地文件路径，需要指定到具体的文件名
        // 设置对象访问权限为公共读
        request.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);
        client.putObject(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * 断点续传
     *
     * @param obsClient
     * @param localfile
     */
    private void uploadPoint(ObsClient obsClient, String localfile, String namePath) throws FileNotFoundException {
        String objectName = "android/1000112/" + namePath;//对应上传之后的文件名称

//        FileInputStream fis = new FileInputStream(new File(localfile));
//        obsClient.putObject(bucketName, objectName, fis); // localfile为待上传的本地文件路径，需要指定到具体的文件名
        int size = photoPaths.size();
//        for (int i = 0; i < size; i++) {
//            String objectName = "android/1000112/" + pathNameList.get(i);//对应上传之后的文件名称

//            FileInputStream fis = new FileInputStream(new File(photoPaths.get(i)));
//            obsClient.putObject(bucketName, objectName, fis); // localfile为待上传的本地文件路径，需要指定到具体的文件名

//                uploadPoint(obsClient, photoPaths.get(i), pathNameList.get(i));

//            request = new UploadFileRequest(bucketName, objectName);
//            request.setUploadFile(photoPaths.get(i)); // localfile为上传的本地文件路径，需要指定到具体的文件名
//        }

        UploadFileRequest request = new UploadFileRequest(bucketName, objectName);
        request.setUploadFile(localfile); // localfile为上传的本地文件路径，需要指定到具体的文件名
        // 设置分段上传时的最大并发数
        request.setTaskNum(5);
        // 设置分段大小为10MB
        request.setPartSize(10 * 1024 * 1024);
        // 开启断点续传模式
        request.setEnableCheckpoint(true);
        request.setProgressListener(new ProgressListener() {
            @Override
            public void progressChanged(ProgressStatus status) {
                // 获取上传平均速率
                Log.i("PutObject", "AverageSpeed:" + status.getAverageSpeed());
                // 获取上传进度百分比
                Log.i("PutObject", "TransferPercentage:" + status.getTransferPercentage());
            }
        });
        // 每上传1MB数据反馈上传进度
        request.setProgressInterval(1024 * 1024L);
        try {
            // 进行断点续传上传
            CompleteMultipartUploadResult result = obsClient.uploadFile(request);
            Log.i("PutObject", " result.getStatusCode():" + result.getStatusCode());
            if (result.getStatusCode() == 200) {
                //解决在子线程中调用Toast的异常情况处理
                Looper.prepare();
                Toast.makeText(MainLogin_QuestionFeedBack.this, "上传成功", Toast.LENGTH_SHORT).show();
                Looper.loop();

            }

        } catch (ObsException e) {
            // 发生异常时可再次调用断点续传上传接口进行重新上传
            Toast.makeText(MainLogin_QuestionFeedBack.this, e.getErrorCode(), Toast.LENGTH_SHORT).show();
        }
    }


}