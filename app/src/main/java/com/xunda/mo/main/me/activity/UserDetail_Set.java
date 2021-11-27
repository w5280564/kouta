package com.xunda.mo.main.me.activity;

import static com.xunda.mo.staticdata.AppConstant.ak;
import static com.xunda.mo.staticdata.AppConstant.bucketName;
import static com.xunda.mo.staticdata.AppConstant.endPoint;
import static com.xunda.mo.staticdata.AppConstant.sk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.contrarywind.view.WheelView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMUserInfo;
import com.hyphenate.easeui.model.EaseEvent;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citypickerview.CityPickerView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.AuthTypeEnum;
import com.xunda.mo.R;
import com.xunda.mo.dialog.TwoButtonDialog;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.common.utils.PreferenceManager;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.dialog.EditTextDialogFragment;
import com.xunda.mo.hx.section.dialog.SimpleDialogFragment;
import com.xunda.mo.hx.section.group.fragment.GroupEditFragment;
import com.xunda.mo.main.baseView.MyArrowItemView;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.main.login.MainLogin_Register;
import com.xunda.mo.model.UserDetail_Bean;
import com.xunda.mo.model.UserDetail_Check_Bean;
import com.xunda.mo.model.baseDataModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.GlideEnGine;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.StaticData;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;

public class UserDetail_Set extends BaseInitActivity {

    private TextView label_Txt;
    private SimpleDraweeView person_img;
    private MyArrowItemView friend_ArrowItemView, sex_ArrowItemView, birthday_ArrowItemView, adress_ArrowItemView, signature_ArrowItemView,
            ID_ArrowItemView, QRcode_ArrowItemView;
    private static final String CHANGE_HEAD = "1";
    private static final String CHANGE_NICK = "2";
    private static final String CHANGE_SEX = "3";
    private ObsClient obsClient;
    private LinearLayout label_Lin;
    private String tagString;
    private View head_arrow;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, UserDetail_Set.class);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_userdetail_set;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();

        ConstraintLayout head_Constraint = findViewById(R.id.head_Constraint);
        person_img = findViewById(R.id.person_img);
        person_img.setOnClickListener(new person_imgClick());
        head_arrow = findViewById(R.id.head_arrow);
        friend_ArrowItemView = findViewById(R.id.friend_ArrowItemView);
        friend_ArrowItemView.setOnClickListener(new friend_ArrowItemViewClick());
        sex_ArrowItemView = findViewById(R.id.sex_ArrowItemView);
        sex_ArrowItemView.setOnClickListener(new sex_ArrowItemViewClick());
        birthday_ArrowItemView = findViewById(R.id.birthday_ArrowItemView);
        birthday_ArrowItemView.setOnClickListener(new birthday_ArrowItemViewClick());
        adress_ArrowItemView = findViewById(R.id.adress_ArrowItemView);
        adress_ArrowItemView.setOnClickListener(new adress_ArrowItemViewClick());
        signature_ArrowItemView = findViewById(R.id.signature_ArrowItemView);
        signature_ArrowItemView.setOnClickListener(new signature_ArrowItemViewClick());
        ID_ArrowItemView = findViewById(R.id.ID_ArrowItemView);
        ID_ArrowItemView.setOnClickListener(new ID_ArrowItemViewItemViewClick());
        QRcode_ArrowItemView = findViewById(R.id.QRcode_ArrowItemView);
        QRcode_ArrowItemView.setOnClickListener(new QRcode_ArrowItemViewClick());
        ConstraintLayout label_Constraint = findViewById(R.id.label_Constraint);
        label_Constraint.setOnClickListener(new label_ConstraintClick());
        label_Lin = findViewById(R.id.label_Lin);
        label_Txt = findViewById(R.id.label_Txt);

        View quit_Btn = findViewById(R.id.quit_Btn);
        quit_Btn.setOnClickListener(new quit_BtnClick());
        WheelView sexWheel = findViewById(R.id.sexWheel);
        sexWheel.setCyclic(false);

        initCity();
        initObsClient();
        LiveDataBus.get().with(MyConstant.MY_LABEL, String.class).observe(this, s -> {
            tagString = s;
            tagList(label_Lin, UserDetail_Set.this, s);
        });
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

    //申明对象
    CityPickerView mPicker = new CityPickerView();

    private void initCity() {
        //等数据加载完毕再初始化并显示Picker,以免还未加载完数据就显示,造成APP崩溃。
        //预先加载仿iOS滚轮实现的全部数据
        mPicker.init(this);
        //添加默认的配置，不需要自己定义，当然也可以自定义相关熟悉，详细属性请看demo
        CityConfig cityConfig = new CityConfig.Builder().build();
        mPicker.setConfig(cityConfig);
        mPicker.setOnCityItemClickListener(new mPickerCityItemClick());
    }

    //监听选择点击事件及返回结果
    private class mPickerCityItemClick extends OnCityItemClickListener {
        @Override
        public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
            //省份province 城市city 地区district
            String cityStr = province + " " + city + " " + district;
//            adress_ArrowItemView.getTvContent().setText(cityStr);
//            ToastUtils.showShortToast(UserDetail_Set.this, province.getId() + city.getId() + district.getId());
            String areaCodeValue = district.getId();
            String keyCity = "areaName";
            String changType = "5";
            ChangeUserMethod(UserDetail_Set.this, saveFile.User_Update_Url, changType, "areaCode", areaCodeValue, keyCity, cityStr);
        }

        @Override
        public void onCancel() {
//            ToastUtils.showLongToast(UserDetail_Set.this, "已取消");
        }
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setElevation(2f);//阴影
        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        Button return_Btn = title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("个人设置");
        Button right_Btn = title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.GONE);
        return_Btn.setOnClickListener(new return_Btn());
    }

    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }

    @Override
    protected void initData() {
        super.initData();
        UserMethod(UserDetail_Set.this, saveFile.User_GetUserInfo_Url);
//        AllDistrictsMethod(UserDetail_Set.this, saveFile.BaseUrl + saveFile.Common_AllDistricts_Url);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckUpdateMethod(UserDetail_Set.this, saveFile.User_CheckUpdateUser_Url);
    }

    private class label_ConstraintClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            MeDetail_Edit_Label.actionStart(mContext, tagString);
        }
    }


    //修改头像
    private class person_imgClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            isChangeHead("1", "用户头像24小时内只能修改1次");
        }
    }

    //修改昵称
    private class friend_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            isChangeHead("2", "用户昵称24小时内只能修改1次");
        }
    }

    //修改性别
    private class sex_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            isChangeHead("3", "用户性别只能修改1次，请谨慎修改");
        }
    }

    //性别选择 只设置一组数据就好
    private void setSex() {
        final List<String> mOptionsItems = new ArrayList<>();
        mOptionsItems.add("男");
        mOptionsItems.add("女");
        OptionsPickerView pvOptions = new OptionsPickerBuilder(UserDetail_Set.this, (options1, option2, options3, v) -> {
            sex_ArrowItemView.getTvContent().setText(mOptionsItems.get(options1));
            String sexValue;
            if (TextUtils.equals(mOptionsItems.get(options1), "男")) {
                sexValue = "0";
            } else {
                sexValue = "1";
            }
            String changType = "3";
            ChangeUserMethod(UserDetail_Set.this, saveFile.User_Update_Url, changType, "sex", sexValue, "", "");
        }).build();
        pvOptions.setPicker(mOptionsItems);
        pvOptions.show();
    }


    private class birthday_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            setTime();
        }
    }

    //时间选择器
    @SuppressLint("SimpleDateFormat")
    private void setTime() {
        TimePickerView pvTime = new TimePickerBuilder(UserDetail_Set.this, (date, v) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = sdf.format(date);
//                birthday_ArrowItemView.getTvContent().setText(dateString);
            String changType = "4";
            ChangeUserMethod(UserDetail_Set.this, saveFile.User_Update_Url, changType, "birthday", dateString, "", "");
        }).build();
        pvTime.show();
    }

    private class adress_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            //显示
            mPicker.showCityPicker();
        }
    }

    private class signature_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            showSignatureDialog();
        }
    }

    private class quit_BtnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            showExitDialog();
        }
    }


    public void tagList(LinearLayout label_Lin, Context mContext, String tag) {
        if (label_Lin != null) {
            label_Lin.removeAllViews();
        }
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        String[] tagS = tag.split(",");
        for (int i = 0; i < tagS.length; i++) {
            TextView textView = new TextView(mContext);
            textView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            textView.setBackgroundResource(R.drawable.group_label_radius);
            textView.setText(tagS[i]);
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int margins = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, mContext.getResources().getDisplayMetrics());
            itemParams.setMargins(0, 0, margins, 0);
            textView.setLayoutParams(itemParams);
            textView.setTag(i);
            label_Lin.addView(textView);
            textView.setOnClickListener(v -> {
            });
        }
    }

    UserDetail_Bean userModel;

    @SuppressLint("SetTextI18n")
    public void UserMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                userModel = new Gson().fromJson(result, UserDetail_Bean.class);
                UserDetail_Bean.DataDTO dataDTO = userModel.getData();
                Uri uri = Uri.parse(userModel.getData().getHeadImg());
                person_img.setImageURI(uri);

                friend_ArrowItemView.getTvContent().setText(dataDTO.getNickname());
                if (TextUtils.isEmpty(dataDTO.getNickname())) {
                    friend_ArrowItemView.getTvContent().setText("未设置");
                }
                if (dataDTO.getSex() == 0) {
                    sex_ArrowItemView.getTvContent().setText("男");
                } else {
                    sex_ArrowItemView.getTvContent().setText("女");
                }
                birthday_ArrowItemView.getTvContent().setText(dataDTO.getBirthday());
                if (TextUtils.isEmpty(dataDTO.getBirthday())) {
                    birthday_ArrowItemView.getTvContent().setText("未设置");
                }
                adress_ArrowItemView.getTvContent().setText(dataDTO.getAreaName());
                if (TextUtils.isEmpty(dataDTO.getAreaName())) {
                    adress_ArrowItemView.getTvContent().setText("未设置");
                }
                signature_ArrowItemView.getTvContent().setText(dataDTO.getSignature());
                if (TextUtils.isEmpty(dataDTO.getSignature())) {
                    signature_ArrowItemView.getTvContent().setText("未设置");
                }
                ID_ArrowItemView.getTvContent().setText(dataDTO.getUserNum() + "");
                String tag = userModel.getData().getTag();
                tagString = tag;
                if (TextUtils.isEmpty(tag)) {
                    label_Txt.setVisibility(View.VISIBLE);
                } else {
                    tagList(label_Lin, context, tag);
                }
            }

            @Override
            public void failed(String... args) {

            }
        });
    }

    //检验头像昵称 性别是否能修改
    @SuppressLint("SetTextI18n")
    public void CheckUpdateMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                UserDetail_Check_Bean model = new Gson().fromJson(result, UserDetail_Check_Bean.class);
                if (model.getData().getIsUpdateHead() == 0) {
                    person_img.setEnabled(false);
                    head_arrow.setVisibility(View.GONE);
                }
                if (model.getData().getIsUpdateNickName() == 0) {
                    friend_ArrowItemView.setEnabled(false);
                    friend_ArrowItemView.getArrow().setVisibility(View.GONE);
                }
                if (model.getData().getIsUpdateSex() == 0) {
                    sex_ArrowItemView.setEnabled(false);
                    sex_ArrowItemView.getArrow().setVisibility(View.GONE);
                }
            }

            @Override
            public void failed(String... args) {

            }
        });
    }


    void logout() {
        final ProgressDialog pd = new ProgressDialog(this);
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        DemoHelper.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    pd.dismiss();
                    // show login screen
                    MyInfo myInfo = new MyInfo(UserDetail_Set.this);
                    myInfo.clearInfoData(UserDetail_Set.this);
                    saveFile.clearShareData("JSESSIONID", UserDetail_Set.this);
                    Intent intent = new Intent(UserDetail_Set.this, MainLogin_Register.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(() -> {
                    pd.dismiss();
                    Toast.makeText(UserDetail_Set.this, "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    //检验头像、昵称、性别是否能修改
    private void isChangeHead(String changString, String content) {
        new SimpleDialogFragment.Builder(mContext)
                .setTitle("提示")
                .showContent(true)
                .setContent(content)
                .setOnConfirmClickListener(view -> {
                    if (TextUtils.equals(changString, CHANGE_HEAD)) {
                        startCamera();
                    } else if (TextUtils.equals(changString, CHANGE_NICK)) {
                        changeNick();
                    } else if (TextUtils.equals(changString, CHANGE_SEX)) {
                        setSex();
                    }
                })
                .showCancelButton(true)
                .show();
    }

    public void startCamera() {
        //监听授权
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(UserDetail_Set.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[0]);
            ActivityCompat.requestPermissions(UserDetail_Set.this, permissions, 1);
        } else {
            //打开相机录制视频
            Intent captureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            //判断相机是否正常。
            if (captureIntent.resolveActivity(UserDetail_Set.this.getPackageManager()) != null) {
                setPhotoMetod(UserDetail_Set.this);
            }

        }
    }


    private void setPhotoMetod(Context context) {
        int choice = 1;
        PictureSelector.create((Activity) context)
                .openGallery(PictureConfig.TYPE_IMAGE)
                .imageEngine(GlideEnGine.createGlideEngine()) //图片加载空白 加入Glide加载图片
                .imageSpanCount(4)// 每行显示个数 int
                .maxSelectNum(choice)
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .isSingleDirectReturn(true)//PictureConfig.SINGLE模式下是否直接返回
                .isAndroidQTransform(true)//Android Q版本下是否需要拷贝文件至应用沙盒内
                .isPreviewImage(true)// 是否可预览图片 true or false
                .isCamera(true)// 是否显示拍照按钮 true or false
                .isEnableCrop(true)//开启裁剪
                .cropImageWideHigh(200, 200)//裁剪尺寸
                .withAspectRatio(1, 1)//裁剪比例1：1是正方形
                .freeStyleCropEnabled(true)//裁剪框是否可拖拽
//                .imageFormat(PictureMimeType.PNG_Q)//拍照图片格式后缀,默认jpeg, PictureMimeType.PNG，Android Q使用PictureMimeType.PNG_Q
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .isCompress(true)// 是否压缩 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    List<LocalMedia> selectList;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    if (!selectList.isEmpty()) {
                        AsyncTask<Void, Void, String> task = new PostObjectTask();
                        task.execute();
                    }
                    break;
            }
        }
    }


    //设置昵称
    private void changeNick() {
        new EditTextDialogFragment.Builder(mContext)
                .setContent(friend_ArrowItemView.getTvContent().getText().toString())
                .setConfirmClickListener((view, content) -> {
                    if (!TextUtils.isEmpty(content)) {
//                            itemGroupName.getTvContent().setText(content);
                        String changType = "2";
                        ChangeUserMethod(UserDetail_Set.this, saveFile.User_Update_Url, changType, "nickname", content, "", "");
                    }
                })
                .setTitle("设置昵称")
                .show();
    }


    private void showSignatureDialog() {
      String contentStr =   TextUtils.equals(signature_ArrowItemView.getTvContent().getText().toString(),"未设置")?"":signature_ArrowItemView.getTvContent().getText().toString().trim();
        GroupEditFragment.showDialog(mContext,
                "个性签名",
                contentStr,
                "请输入个性签名",
                (view, content) -> {
                    //群简介
                    if (!TextUtils.isEmpty(content)) {
//                            signature_ArrowItemView.getTvContent().setText(content);
                        String changType = "7";
                        ChangeUserMethod(UserDetail_Set.this, saveFile.User_Update_Url, changType, "signature", content, "", "");
                    }
                });
    }


    /**
     * 修改用户信息
     *
     * @param context
     * @param baseUrl
     * @param changType 修改类型（1.头像 2.昵称 3.性别 4.生日 5.地区 6.个人标签 7.个性签名）
     * @param keyStr    headImg NickName sex
     * @param valueStr
     */
    public void ChangeUserMethod(Context context, String baseUrl, String changType, String keyStr, String valueStr, String keyCity, String valueCityStr) {
        Map<String, Object> map = new HashMap<>();
        map.put("changType", changType);
        map.put(keyStr, valueStr);
        if (TextUtils.equals(changType, "5")) {
            map.put(keyCity, valueCityStr);
        }
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                baseDataModel baseModel = new Gson().fromJson(result, baseDataModel.class);
                Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                MyInfo info = new MyInfo(UserDetail_Set.this);
                if (TextUtils.equals(changType, "1")) {
                    //修改环信本地存储的自己头像
                    String HxUserName = info.getUserInfo().getHxUserName();
                    DemoHelper.getInstance().getUserInfo(HxUserName).setAvatar(baseModel.getData());
                    info.setOneData("HeadImg", baseModel.getData());
                    changeFileHead(baseModel.getData());
                    person_img.setEnabled(false);
                    head_arrow.setVisibility(View.GONE);
                } else if (TextUtils.equals(changType, "2")) {
                    friend_ArrowItemView.getTvContent().setText(valueStr);
                    info.setOneData("nickname", valueStr);
                    friend_ArrowItemView.setEnabled(false);
                    friend_ArrowItemView.getArrow().setVisibility(View.GONE);
                } else if (TextUtils.equals(changType, "3")) {
//                            sex_ArrowItemView.getTvContent().setText(valueStr);
                    sex_ArrowItemView.setEnabled(false);
                    sex_ArrowItemView.getArrow().setVisibility(View.GONE);
                } else if (TextUtils.equals(changType, "4")) {
                    birthday_ArrowItemView.getTvContent().setText(valueStr);
                } else if (TextUtils.equals(changType, "5")) {
                    adress_ArrowItemView.getTvContent().setText(valueCityStr);
                } else if (TextUtils.equals(changType, "7")) {
                    signature_ArrowItemView.getTvContent().setText(valueStr);
                }
            }

            @Override
            public void failed(String... args) {
            }
        });
    }

    String fileName;
    @SuppressLint("StaticFieldLeak")
    class PostObjectTask extends AsyncTask<Void, Void, String> {
        @SneakyThrows
        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder sbf = new StringBuilder();
            try {
                fileName = selectList.get(0).getRealPath();
                if (isQ()) {
                    fileName = selectList.get(0).getAndroidQToPath();
                }
//                objectName = "user/" + userModel.getData().getUserId() + "/headImg/" + selectList.get(0).getFileName();//对应上传之后的文件名称
                String  objectName = "user/" + userModel.getData().getUserId() + "/headImg/" + System.currentTimeMillis() + ".jpg";//对应上传之后的文件名称
                FileInputStream fis = new FileInputStream(fileName);
                obsClient.putObject(bucketName, objectName, fis); // localfile为待上传的本地文件路径，需要指定到具体的文件名
                sbf.append(objectName);
                return sbf.toString();
            } catch (ObsException e) {
                sbf.append("Response Code:").append(e.getResponseCode())
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
        protected void onPostExecute(String pictures) {
            super.onPostExecute(pictures);
            if (TextUtils.isEmpty(pictures)) {
                Toast.makeText(mContext, "上传图片失败", Toast.LENGTH_SHORT).show();
            } else {
                person_img.setEnabled(false);
                Uri uri = Uri.parse("file:///" + fileName);
                person_img.setImageURI(uri);
                String changType = "1";
                ChangeUserMethod(UserDetail_Set.this, saveFile.User_Update_Url, changType, "headImg", pictures, "", "");
            }

        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }
    }

    private boolean isQ() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true;
        }
        return false;
    }


    private void changeFileHead(String HeadUrl) {
        MyInfo myInfo = new MyInfo(UserDetail_Set.this);
//        String headUrl = myInfo.getUserInfo().getHeadImg();
        myInfo.setOneData("HeadImg", HeadUrl);
        String nick = myInfo.getUserInfo().getNickname();
        EMUserInfo userInfo = new EMUserInfo();
        userInfo.setAvatarUrl(HeadUrl);
        userInfo.setNickName(nick);
        EMClient.getInstance().userInfoManager().updateOwnInfoByAttribute(EMUserInfo.EMUserInfoType.AVATAR_URL, HeadUrl, new EMValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                PreferenceManager.getInstance().setCurrentUserAvatar(HeadUrl);
                EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT);
//                event.message = myInfo.getUserInfo().getHxUserName();
                event.message = HeadUrl;
                LiveDataBus.get().with(DemoConstant.AVATAR_CHANGE).postValue(event);
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    private class ID_ArrowItemViewItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            String number = StaticData.getNumbers(ID_ArrowItemView.getTvContent().getText().toString());
            ClipData clipData = ClipData.newPlainText("text", number);
            manager.setPrimaryClip(clipData);
            Toast.makeText(UserDetail_Set.this, "已复制到剪切板", Toast.LENGTH_SHORT).show();
        }
    }

    private class QRcode_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            MeAndGroup_QRCode.actionUserStart(UserDetail_Set.this);
        }
    }


    /**
     * 提示dialog
     */
    private void showExitDialog() {
        TwoButtonDialog dialog = new TwoButtonDialog(this, "确定退出登录吗？", "取消", "确定",
                new TwoButtonDialog.ConfirmListener() {

                    @Override
                    public void onClickRight() {
                        logout();
                    }

                    @Override
                    public void onClickLeft() {

                    }
                });
        dialog.show();

    }


}