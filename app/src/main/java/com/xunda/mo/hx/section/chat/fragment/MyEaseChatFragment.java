package com.xunda.mo.hx.section.chat.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.manager.EaseDingMessageHelper;
import com.hyphenate.easeui.modules.chat.EaseChatLayout;
import com.hyphenate.easeui.modules.chat.EaseChatMessageListLayout;
import com.hyphenate.easeui.modules.chat.interfaces.OnAddMsgAttrsBeforeSendEvent;
import com.hyphenate.easeui.modules.chat.interfaces.OnChatLayoutListener;
import com.hyphenate.easeui.modules.chat.interfaces.OnMenuChangeListener;
import com.hyphenate.easeui.modules.menu.EasePopupWindowHelper;
import com.hyphenate.easeui.modules.menu.MenuItemBean;
import com.hyphenate.easeui.ui.base.EaseBaseFragment;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseCompat;
import com.hyphenate.easeui.utils.EaseFileUtils;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;
import com.hyphenate.util.VersionUtils;
import com.xunda.mo.R;
import com.xunda.mo.hx.section.chat.activicy.MyEaseGaodeMapActivity;
import com.xunda.mo.staticdata.MarqueeTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyEaseChatFragment extends EaseBaseFragment implements OnChatLayoutListener, OnMenuChangeListener, OnAddMsgAttrsBeforeSendEvent {
    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    protected static final int REQUEST_CODE_DING_MSG = 4;
    protected static final int REQUEST_CODE_SELECT_VIDEO = 11;
    protected static final int REQUEST_CODE_SELECT_FILE = 12;
    private static final String TAG = MyEaseChatFragment.class.getSimpleName();
    public EaseChatLayout chatLayout;
    public String conversationId;
    public  int chatType;
    public String historyMsgId;
    public boolean isRoam;
    public boolean isMessageInit;
    public OnChatLayoutListener listener;

    protected File cameraFile;
    public ConstraintLayout top_Constraint;
    public TextView top_Txt;
    public Button cancel_Btn;
    public ImageView horn_icon;
    public MarqueeTextView horn_Txt;
    public ConstraintLayout horn_Con;
    public EaseImageView head_Img;
    public TextView horn_Name;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initArguments();
        return LayoutInflater.from(container.getContext()).inflate(getLayoutId(), null);
    }

    private int getLayoutId() {
        return R.layout.myease_fragment_chat_list;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();

    }

    public void initArguments() {
        Bundle bundle = getArguments();
        if(bundle != null) {
            conversationId = bundle.getString(EaseConstant.EXTRA_CONVERSATION_ID);
            chatType = bundle.getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
            historyMsgId = bundle.getString(EaseConstant.HISTORY_MSG_ID);
            isRoam = bundle.getBoolean(EaseConstant.EXTRA_IS_ROAM, false);
        }
    }

    public void initView() {
        top_Constraint = findViewById(R.id.top_Constraint);
        top_Txt = findViewById(R.id.top_Txt);
        cancel_Btn = findViewById(R.id.cancel_Btn);
        chatLayout = findViewById(R.id.layout_chat);
        chatLayout.getChatMessageListLayout().setItemShowType(EaseChatMessageListLayout.ShowType.NORMAL);
        chatLayout.getChatMessageListLayout().setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray));
        horn_Txt = findViewById(R.id.horn_Txt);
        horn_icon = findViewById(R.id.horn_icon);
        horn_Con = findViewById(R.id.horn_Con);
        head_Img = findViewById(R.id.head_Img);
        horn_Name = findViewById(R.id.horn_Name);
    }

    public void initListener() {
        chatLayout.setOnChatLayoutListener(this);
        chatLayout.setOnPopupWindowItemClickListener(this);
        chatLayout.setOnAddMsgAttrsBeforeSendEvent(this);
    }

    public void initData() {
        if(!TextUtils.isEmpty(historyMsgId)) {
            chatLayout.init(EaseChatMessageListLayout.LoadDataType.HISTORY, conversationId, chatType);
            chatLayout.loadData(historyMsgId);
        }else {
            if(isRoam) {
                chatLayout.init(EaseChatMessageListLayout.LoadDataType.ROAM, conversationId, chatType);
            }else {
                chatLayout.init(conversationId, chatType);
            }
            chatLayout.loadDefaultData();
        }
        isMessageInit = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isMessageInit) {
            chatLayout.getChatMessageListLayout().refreshMessages();
        }
    }

    public void setOnChatLayoutListener(OnChatLayoutListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onBubbleClick(EMMessage message) {
        if(listener != null) {
            return listener.onBubbleClick(message);
        }
        return false;
    }

    @Override
    public boolean onBubbleLongClick(View v, EMMessage message) {
        if(listener != null) {
            return listener.onBubbleLongClick(v, message);
        }
        return false;
    }

    @Override
    public void onUserAvatarClick(String username) {
        if(listener != null) {
            listener.onUserAvatarClick(username);
        }
    }

    public void getListener(){
    }


    @Override
    public void onUserAvatarLongClick(String username) {
        if(listener != null) {
            listener.onUserAvatarLongClick(username);
        }
    }

    @Override
    public void onChatExtendMenuItemClick(View view, int itemId) {
        if(itemId == R.id.extend_item_take_picture) {
            startImg();
        }else if(itemId == R.id.extend_item_picture) {
            selectPicFromLocal();
        }else if(itemId == R.id.extend_item_location) {
            startLocation();
        }else if(itemId == R.id.extend_item_video) {
            selectVideoFromLocal();
        }else if(itemId == R.id.extend_item_file) {
            selectFileFromLocal();
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void onChatSuccess(EMMessage message) {
        // you can do something after sending a successful message
    }


    @Override
    public void onChatError(int code, String errorMsg) {
        if(listener != null) {
            listener.onChatError(code, errorMsg);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            chatLayout.getChatInputMenu().hideExtendContainer();
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                onActivityResultForCamera(data);
            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                onActivityResultForLocalPhotos(data);
            } else if (requestCode == REQUEST_CODE_MAP) { // location
                onActivityResultForMapLocation(data);
            } else if (requestCode == REQUEST_CODE_DING_MSG) { // To send the ding-type msg.
                onActivityResultForDingMsg(data);
            }else if(requestCode == REQUEST_CODE_SELECT_FILE) {
                onActivityResultForLocalFiles(data);
            }
        }
    }

    /**
     * select picture from camera
     */
    protected void selectPicFromCamera() {
        if(!checkSdCardExist()) {
            return;
        }
        cameraFile = new File(PathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser()
                + System.currentTimeMillis() + ".jpg");
        //noinspection ResultOfMethodCallIgnored
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, EaseCompat.getUriForFile(getContext(), cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    /**
     * select local image
     */
    protected void selectPicFromLocal() {
        EaseCompat.openImage(this, REQUEST_CODE_LOCAL);

    }

    /**
     * 启动定位
     * @param requestCode
     */
    protected void startMapLocation(int requestCode) {
//        EaseBaiduMapActivity.actionStartForResult(this, requestCode);
        MyEaseGaodeMapActivity.actionStartForResult(this, requestCode);
    }

    /**
     * select local video
     */
    protected void selectVideoFromLocal() {

    }

    /**
     * select local file
     */
    protected void selectFileFromLocal() {
        Intent intent = new Intent();
        if(VersionUtils.isTargetQ(getActivity())) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }else {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                intent.setAction(Intent.ACTION_GET_CONTENT);
            }else {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            }
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    /**
     * 相机返回处理结果
     * @param data
     */
    protected void onActivityResultForCamera(Intent data) {
        if (cameraFile != null && cameraFile.exists()) {
            chatLayout.sendImageMessage(Uri.parse(cameraFile.getAbsolutePath()));
        }
    }

    /**
     * 选择本地图片处理结果
     * @param data
     */
    protected void onActivityResultForLocalPhotos(@Nullable Intent data) {
        if (data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                String filePath = EaseFileUtils.getFilePath(mContext, selectedImage);
                if(!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
                    chatLayout.sendImageMessage(Uri.parse(filePath));
                }else {
                    EaseFileUtils.saveUriPermission(mContext, selectedImage, data);
                    chatLayout.sendImageMessage(selectedImage);
                }
            }
        }
    }

    /**
     * 地图定位结果处理
     * @param data
     */
    protected void onActivityResultForMapLocation(@Nullable Intent data) {
        if(data != null) {
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);
            String locationAddress = data.getStringExtra("address");
            if (locationAddress != null && !locationAddress.equals("")) {
                chatLayout.sendLocationMessage(latitude, longitude, locationAddress);
            } else {
                if(listener != null) {
                    listener.onChatError(-1, getResources().getString(R.string.unable_to_get_loaction));
                }
            }
        }
    }

    protected void onActivityResultForDingMsg(@Nullable Intent data) {
        if(data != null) {
            String msgContent = data.getStringExtra("msg");
            EMLog.i(TAG, "To send the ding-type msg, content: " + msgContent);
            // Send the ding-type msg.
            EMMessage dingMsg = EaseDingMessageHelper.get().createDingMessage(conversationId, msgContent);
            chatLayout.sendMessage(dingMsg);
        }
    }

    /**
     * 本地文件选择结果处理
     * @param data
     */
    protected void onActivityResultForLocalFiles(@Nullable Intent data) {
        if (data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String filePath = EaseFileUtils.getFilePath(mContext, uri);
                if(!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
                    chatLayout.sendFileMessage(Uri.parse(filePath));
                }else {
                    EaseFileUtils.saveUriPermission(mContext, uri, data);
                    chatLayout.sendFileMessage(uri);
                }
            }
        }
    }

    /**
     * 检查sd卡是否挂载
     * @return
     */
    protected boolean checkSdCardExist() {
        return EaseCommonUtils.isSdcardExist();
    }

    @Override
    public void onPreMenu(EasePopupWindowHelper helper, EMMessage message) {

    }

    @Override
    public boolean onMenuItemClick(MenuItemBean item, EMMessage message) {
        return false;
    }

    @Override
    public void addMsgAttrsBeforeSend(EMMessage message) {

    }

    public void startImg() {
        //监听授权
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.  permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {//摄像与录制
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(getActivity(), permissions, 1);
        } else {
            selectPicFromCamera();
        }
    }
    public void startLocation() {
        //监听授权
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.  permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {//摄像与录制
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(getActivity(), permissions, 1);
        } else {
            startMapLocation(REQUEST_CODE_MAP);
        }
    }



}

