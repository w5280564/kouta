package com.xunda.mo.main.chat.activity;

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
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
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
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.common.utils.PreferenceManager;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.dialog.DemoDialogFragment;
import com.xunda.mo.hx.section.dialog.EditTextDialogFragment;
import com.xunda.mo.hx.section.dialog.SimpleDialogFragment;
import com.xunda.mo.hx.section.group.fragment.GroupEditFragment;
import com.xunda.mo.main.MainLogin_Register;
import com.xunda.mo.main.baseView.MyArrowItemView;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.model.UserDetail_Bean;
import com.xunda.mo.model.UserDetail_Check_Bean;
import com.xunda.mo.model.baseDataModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.GlideEnGine;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.StaticData;
import com.xunda.mo.staticdata.viewTouchDelegate;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.SneakyThrows;

import static com.xunda.mo.staticdata.AppConstant.ak;
import static com.xunda.mo.staticdata.AppConstant.bucketName;
import static com.xunda.mo.staticdata.AppConstant.endPoint;
import static com.xunda.mo.staticdata.AppConstant.sk;

public class UserDetail_Set extends BaseInitActivity {

    private TextView cententTxt;
    private SimpleDraweeView person_img;
    private MyArrowItemView friend_ArrowItemView, sex_ArrowItemView, birthday_ArrowItemView, adress_ArrowItemView, signature_ArrowItemView, ID_ArrowItemView;
    private View quit_Btn;
    private static String CHANGE_HEAD = "1";
    private static String CHANGE_NICK = "2";
    private static String CHANGE_SEX = "3";
    private ObsClient obsClient;

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

        person_img = findViewById(R.id.person_img);
        person_img.setOnClickListener(new person_imgClick());
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

        quit_Btn = findViewById(R.id.quit_Btn);
        quit_Btn.setOnClickListener(new quit_BtnClick());
        WheelView sexWheel = findViewById(R.id.sexWheel);
        sexWheel.setCyclic(false);

        initCity();
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
            ChangeUserMethod(UserDetail_Set.this, saveFile.BaseUrl + saveFile.User_Update_Url, changType, "areaCode", areaCodeValue, keyCity, cityStr);
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
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("个人设置");
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

    @Override
    protected void initData() {
        super.initData();
        UserMethod(UserDetail_Set.this, saveFile.BaseUrl + saveFile.User_GetUserInfo_Url);
        CheckUpdateMethod(UserDetail_Set.this, saveFile.BaseUrl + saveFile.User_CheckUpdateUser_Url);
//        AllDistrictsMethod(UserDetail_Set.this, saveFile.BaseUrl + saveFile.Common_AllDistricts_Url);
    }

    //修改头像
    private class person_imgClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            isChangeHead("1", "用户头像7天内只能修改1次");
        }
    }

    //修改昵称
    private class friend_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            isChangeHead("2", "用户昵称7天内只能修改1次");
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
        OptionsPickerView pvOptions = new OptionsPickerBuilder(UserDetail_Set.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                sex_ArrowItemView.getTvContent().setText(mOptionsItems.get(options1));
                String sexValue = "";
                if (TextUtils.equals(mOptionsItems.get(options1), "男")) {
                    sexValue = "0";
                } else {
                    sexValue = "1";
                }
                String changType = "3";
                ChangeUserMethod(UserDetail_Set.this, saveFile.BaseUrl + saveFile.User_Update_Url, changType, "sex", sexValue, "", "");
            }
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
    private void setTime() {
        TimePickerView pvTime = new TimePickerBuilder(UserDetail_Set.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = sdf.format(date);
//                birthday_ArrowItemView.getTvContent().setText(dateString);
                String changType = "4";
                ChangeUserMethod(UserDetail_Set.this, saveFile.BaseUrl + saveFile.User_Update_Url, changType, "birthday", dateString, "", "");
            }
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
            logout();
        }
    }

    UserDetail_Bean userModel;

    @SuppressLint("SetTextI18n")
    public void UserMethod(Context context, String baseUrl) {
        RequestParams params = new RequestParams(baseUrl);
        if (saveFile.getShareData("JSESSIONID", context) != null) {
            params.setHeader("Authorization", saveFile.getShareData("JSESSIONID", context));
        }
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
                if (resultString != null) {
                    userModel = new Gson().fromJson(resultString, UserDetail_Bean.class);
                    if (userModel.getCode() == -1 || userModel.getCode() == 500) {
                        Intent intent = new Intent(context, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (userModel.getCode() == 200) {
                        UserDetail_Bean.DataDTO dataDTO = userModel.getData();
                        Uri uri = Uri.parse(userModel.getData().getHeadImg());
                        person_img.setImageURI(uri);

                        friend_ArrowItemView.getTvContent().setText(dataDTO.getNikeName());
                        if (dataDTO.getSex() == 0) {
                            sex_ArrowItemView.getTvContent().setText("男");
                        } else {
                            sex_ArrowItemView.getTvContent().setText("女");
                        }
                        birthday_ArrowItemView.getTvContent().setText(dataDTO.getBirthday());
                        adress_ArrowItemView.getTvContent().setText(dataDTO.getAreaName());
                        signature_ArrowItemView.getTvContent().setText(dataDTO.getSignature());
                        ID_ArrowItemView.getTvContent().setText(dataDTO.getUserNum().intValue() + "");


                    } else {
                        Toast.makeText(context, userModel.getMsg(), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT).show();
                }

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

    //检验头像昵称 性别是否能修改
    @SuppressLint("SetTextI18n")
    public void CheckUpdateMethod(Context context, String baseUrl) {
        RequestParams params = new RequestParams(baseUrl);
        if (saveFile.getShareData("JSESSIONID", context) != null) {
            params.setHeader("Authorization", saveFile.getShareData("JSESSIONID", context));
        }
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
                if (resultString != null) {
                    UserDetail_Check_Bean model = new Gson().fromJson(resultString, UserDetail_Check_Bean.class);
                    if (model.getCode() == -1 || model.getCode() == 500) {
                        Intent intent = new Intent(context, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (model.getCode() == 200) {
                        if (model.getData().getIsUpdateHead() == 0) {
                            person_img.setEnabled(false);
                        }
                        if (model.getData().getIsUpdateNickName() == 0) {
                            friend_ArrowItemView.setEnabled(false);
                        }
                        if (model.getData().getIsUpdateSex() == 0) {
                            sex_ArrowItemView.setEnabled(false);
                        }
                    } else {
                        Toast.makeText(context, model.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT).show();
                }

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


    void logout() {
        final ProgressDialog pd = new ProgressDialog(this);
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        DemoHelper.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        // show login screen
                        saveFile.clearShareData("JSESSIONID", UserDetail_Set.this);
                        Intent intent = new Intent(UserDetail_Set.this, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        pd.dismiss();
                        Toast.makeText(UserDetail_Set.this, "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
                    }
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
                .setOnConfirmClickListener(new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        if (TextUtils.equals(changString, CHANGE_HEAD)) {
                            startCamera();
                        } else if (TextUtils.equals(changString, CHANGE_NICK)) {
                            changeNick();
                        } else if (TextUtils.equals(changString, CHANGE_SEX)) {
                            setSex();
                        }
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
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
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
                .openGallery(PictureConfig.TYPE_ALL)
                .imageEngine(GlideEnGine.createGlideEngine()) //图片加载空白 加入Glide加载图片
                .imageSpanCount(4)// 每行显示个数 int
                .maxSelectNum(choice)
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .isSingleDirectReturn(true)//PictureConfig.SINGLE模式下是否直接返回
                .isAndroidQTransform(true)//Android Q版本下是否需要拷贝文件至应用沙盒内
                .isPreviewImage(true)// 是否可预览图片 true or false
                .isCamera(true)// 是否显示拍照按钮 true or false
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
                .setConfirmClickListener(new EditTextDialogFragment.ConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view, String content) {
                        if (!TextUtils.isEmpty(content)) {
//                            itemGroupName.getTvContent().setText(content);
                            String changType = "2";
                            ChangeUserMethod(UserDetail_Set.this, saveFile.BaseUrl + saveFile.User_Update_Url, changType, "nikeName", content, "", "");
                        }
                    }
                })
                .setTitle("设置昵称")
                .show();
    }


    private void showSignatureDialog() {
        GroupEditFragment.showDialog(mContext,
                "签名",
                signature_ArrowItemView.getTvContent().getText().toString().trim(),
                "请输入个性签名",
                new GroupEditFragment.OnSaveClickListener() {
                    @Override
                    public void onSaveClick(View view, String content) {
                        //群简介
                        if (!TextUtils.isEmpty(content)) {
//                            signature_ArrowItemView.getTvContent().setText(content);
                            String changType = "7";
                            ChangeUserMethod(UserDetail_Set.this, saveFile.BaseUrl + saveFile.User_Update_Url, changType, "signature", content, "", "");
                        }
                    }
                });
    }


    /**
     * 修改用户信息
     *
     * @param context
     * @param baseUrl
     * @param changType 修改类型（1.头像 2.昵称 3.性别 4.生日 5.地区 6.个人标签 7.个性签名）
     * @param keyStr    headImg nikeName sex
     * @param valueStr
     */
    public void ChangeUserMethod(Context context, String baseUrl, String changType, String keyStr, String valueStr, String keyCity, String valueCityStr) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("changType", changType);
            obj.put(keyStr, valueStr);
            if (TextUtils.equals(changType, "5")) {
                obj.put(keyCity, valueCityStr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(baseUrl);
        if (saveFile.getShareData("JSESSIONID", context) != null) {
            params.setHeader("Authorization", saveFile.getShareData("JSESSIONID", context));
        }
        params.setAsJsonContent(true);
        params.setBodyContent(obj.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
                if (resultString != null) {
                    baseDataModel baseModel = new Gson().fromJson(resultString, baseDataModel.class);
                    if (baseModel.getCode() == 200) {
                        Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                        if (TextUtils.equals(changType, "1")) {
                            //修改本地存储的自己头像
                            MyInfo info = new MyInfo(UserDetail_Set.this);
                            String HxUserName = info.getUserInfo().getHxUserName();
                            DemoHelper.getInstance().getUserInfo(HxUserName).setAvatar(baseModel.getData());

                            changeFileHead(baseModel.getData());
                        } else if (TextUtils.equals(changType, "2")) {
                            friend_ArrowItemView.getTvContent().setText(valueStr);
                        } else if (TextUtils.equals(changType, "3")) {
//                            sex_ArrowItemView.getTvContent().setText(valueStr);
                        } else if (TextUtils.equals(changType, "4")) {
                            birthday_ArrowItemView.getTvContent().setText(valueStr);
                        } else if (TextUtils.equals(changType, "5")) {
                            adress_ArrowItemView.getTvContent().setText(valueCityStr);
                        } else if (TextUtils.equals(changType, "7")) {
                            signature_ArrowItemView.getTvContent().setText(valueStr);
                        }

                    }
                } else {
                    Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT).show();
                }

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

    String objectName = "";

    class PostObjectTask extends AsyncTask<Void, Void, String> {
        @SneakyThrows
        @Override
        protected String doInBackground(Void... voids) {
            StringBuffer sbf = new StringBuffer();
            try {
                objectName = "user/headImg/" + userModel.getData().getUserId() + "/" + selectList.get(0).getFileName();//对应上传之后的文件名称
                FileInputStream fis = new FileInputStream(new File(selectList.get(0).getAndroidQToPath()));
                obsClient.putObject(bucketName, objectName, fis); // localfile为待上传的本地文件路径，需要指定到具体的文件名
                sbf.append(objectName);
                return sbf.toString();
            } catch (ObsException e) {
                sbf.append("\n\n");
                sbf.append("Response Code:" + e.getResponseCode())
                        .append("\n\n").append("Error Message:" + e.getErrorMessage())
                        .append("\n\n").append("Error Code:" + e.getErrorCode())
                        .append("\n\n").append("Request ID:" + e.getErrorRequestId())
                        .append("\n\n").append("Host ID:" + e.getErrorHostId());
                return sbf.toString();
            } catch (Exception e) {
                sbf.append("\n\n");
                sbf.append(e.getMessage());
                return sbf.toString();
            } finally {
                if (obsClient != null) {
                    try {
                        //Close obs client
                        obsClient.close();
                    } catch (IOException e) {
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String pictures) {
            super.onPostExecute(pictures);
//            Log.i("abc", " result.getStatusCode():" + s);
            person_img.setEnabled(false);
            Uri uri = Uri.parse("file:///" + selectList.get(0).getAndroidQToPath());
            person_img.setImageURI(uri);
            String changType = "1";
            ChangeUserMethod(UserDetail_Set.this, saveFile.BaseUrl + saveFile.User_Update_Url, changType, "headImg", pictures, "", "");
        }
    }

    private void changeFileHead(String HeadUrl) {
        MyInfo myInfo = new MyInfo(UserDetail_Set.this);
//        String headUrl = myInfo.getUserInfo().getHeadImg();
        myInfo.setOneData("HeadImg", HeadUrl);
        String nick = myInfo.getUserInfo().getNikeName();
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


}