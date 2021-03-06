package com.xunda.mo.main.group.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.chat.activicy.ChatActivity;
import com.xunda.mo.hx.section.dialog.DemoListDialogFragment;
import com.xunda.mo.hx.section.dialog.EditTextDialogFragment;
import com.xunda.mo.hx.section.dialog.SimpleDialogFragment;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.main.baseView.MyArrowItemView;
import com.xunda.mo.main.baseView.MySwitchItemView;
import com.xunda.mo.main.chat.activity.ChatFriend_AddFriend;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.main.me.activity.ChangeGroupUserNickNameActivity;
import com.xunda.mo.main.me.activity.ChangeMyGroupNickNameActivity;
import com.xunda.mo.main.me.activity.ChangeMyNickNameActivity;
import com.xunda.mo.model.Group_Details_Bean;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.MyLevel;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.StaticData;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;
import com.xunda.mo.view.LightningView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupFriend_Detail extends BaseInitActivity {
    private String addType;
    private ProgressBar site_progressbar;
    private MyArrowItemView nick_ArrowItemView;
    private String groupId;
    private String userId;
    private String hxUserName;
    private int myIdentity;
    private Group is_Owen_Group;
    private MyArrowItemView forbidden_ArrowItemView;
    private MySwitchItemView manage_Switch;
    private GruopInfo_Bean groupModel;
    private String UserName;
    private SimpleDraweeView person_img;
    private TextView nick_nameTxt, cententTxt, leID_Txt,  signature_Txt, grade_Txt;
    private LightningView vip_Txt;
    private Button right_Btn;
    private TextView send_mess_Txt, remove_Txt, add_Txt;
    private MySwitchItemView black_Switch;
    private LinearLayout garde_Lin, label_Lin;
    private ActivityResultLauncher mActivityResultLauncher;

    /**
     * @param context
     * @param toChatUsername ??????username  ???????????????HXusername
     * @param addType        1:???????????? 2:???????????? 3:??????????????? 4:???????????? 5:???????????? 6:??????????????? 7:??????????????? 8:??????????????????  9:???????????????????????????
     */
    public static void actionStart(Context context, String toChatUsername, EaseUser user, String addType) {
        Intent intent = new Intent(context, GroupFriend_Detail.class);
        intent.putExtra("toChatUsername", toChatUsername);
        intent.putExtra("user", (EaseUser) user);
        if (user.getContact() == 0) {
            intent.putExtra("isFriend", true);
        } else {
            intent.putExtra("isFriend", false);
        }
        intent.putExtra("addType", addType);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, String userId, String hxUserName, GruopInfo_Bean groupModel) {
        Intent intent = new Intent(context, GroupFriend_Detail.class);
        intent.putExtra("userId", userId);
        intent.putExtra("hxUserName", hxUserName);
        intent.putExtra("groupModel", groupModel);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_groupfriend_detail;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();
        person_img = findViewById(R.id.person_img);
        nick_nameTxt = findViewById(R.id.nick_nameTxt);
        leID_Txt = findViewById(R.id.leID_Txt);
        leID_Txt.setOnClickListener(new leID_TxtOnClick());
        vip_Txt = findViewById(R.id.vip_Txt);
        grade_Txt = findViewById(R.id.grade_Txt);
        signature_Txt = findViewById(R.id.signature_Txt);
        send_mess_Txt = findViewById(R.id.send_mess_Txt);
        remove_Txt = findViewById(R.id.remove_Txt);
        remove_Txt.setOnClickListener(new remove_TxtClick());
        add_Txt = findViewById(R.id.add_Txt);
        send_mess_Txt.setOnClickListener(new send_mess_TxtOnClick());
        site_progressbar = findViewById(R.id.site_progressbar);
        is_Owen_Group = findViewById(R.id.is_Owen_Group);
        forbidden_ArrowItemView = findViewById(R.id.forbidden_ArrowItemView);
        forbidden_ArrowItemView.setOnClickListener(new forbidden_ArrowItemViewClick());
        manage_Switch = findViewById(R.id.manage_Switch);
        manage_Switch.getSwitch().setOnClickListener(new manage_SwitchClick());

        nick_ArrowItemView = findViewById(R.id.nick_ArrowItemView);
        nick_ArrowItemView.setOnClickListener(new nick_ArrowItemViewClick());
        add_Txt.setOnClickListener(new add_TxtClick());
        black_Switch = findViewById(R.id.black_Switch);
        black_Switch.getSwitch().setOnClickListener(new black_SwitchClick());
        garde_Lin = findViewById(R.id.garde_Lin);
        label_Lin = findViewById(R.id.label_Lin);

        mActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result!=null) {
                    int resultCode = result.getResultCode();
                    if (resultCode==RESULT_OK) {
                        Intent mIntent = result.getData();
                        if (mIntent!=null) {
                            String changeNick = mIntent.getStringExtra("newName");
                            cententTxt.setText(changeNick);
                            nick_ArrowItemView.getTvContent().setText(changeNick);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        userId = intent.getStringExtra("userId");
        hxUserName = intent.getStringExtra("hxUserName");
        groupModel = (GruopInfo_Bean) intent.getSerializableExtra("groupModel");
        myIdentity = groupModel.getData().getIdentity();
        groupId = groupModel.getData().getGroupId();

    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setElevation(2f);//??????
        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        Button return_Btn = title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        cententTxt = title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("??????");
        right_Btn = title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.VISIBLE);
        right_Btn.setBackgroundResource(R.mipmap.adress_head_more);
        viewTouchDelegate.expandViewTouchDelegate(right_Btn, 50, 50, 50, 50);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        int marginsRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
        layoutParams.setMargins(0, 0, marginsRight, 0);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        right_Btn.setLayoutParams(layoutParams);

        return_Btn.setOnClickListener(new return_Btn());
        right_Btn.setOnClickListener(new right_Btn());
    }

    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }

    private class right_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            showMore(GroupFriend_Detail.this, right_Btn);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        String userName;
        String userID;
        if (TextUtils.isEmpty(userId)) {
            userName = "hxUserName";
            userID = hxUserName;
        } else {
            userName = "userId";
            userID = userId;
        }
        String url = saveFile.Group_UserInfo_Url;
        groupFriendMethod(GroupFriend_Detail.this, url, userName, userID);
    }


    //????????????
    private class nick_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Intent intent = new Intent(mContext, ChangeGroupUserNickNameActivity.class);
            intent.putExtra("name",nick_ArrowItemView.getTvContent().getText().toString());
            intent.putExtra("myGroupId",groupId);
            intent.putExtra("friendUserId",userId);
            mActivityResultLauncher.launch(intent);
        }
    }


    //???????????????
    private class black_SwitchClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            if (black_Switch.getSwitch().isChecked()) {
                isBlack(black_Switch.getSwitch());
            } else {
                BlockMethod(GroupFriend_Detail.this, saveFile.Group_SetBlack_Url, black_Switch.getSwitch().isChecked(), black_Switch.getSwitch());
            }
        }
    }

    //??????
    private class forbidden_ArrowItemViewClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (model.getData().getIsMute() == 0) {
                showSelectDialog();
            } else {
                showCancelProhibition(mContext, forbidden_ArrowItemView);
            }
        }
    }


    private class send_mess_TxtOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            ChatActivity.actionStart(mContext, hxUserName, EaseConstant.CHATTYPE_SINGLE);
        }
    }

    private class add_TxtClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            addType = "6";
            ChatFriend_AddFriend.actionStart(mContext, hxUserName, addType);
        }
    }

    //????????????
    private class remove_TxtClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            removeFriend();
        }
    }

    private void removeFriend() {
        RemoveMethod(GroupFriend_Detail.this, saveFile.group_MangerUser_Url);
    }

    //???????????????
    private class manage_SwitchClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            AddManageMethod(GroupFriend_Detail.this, saveFile.group_MangerSet_Url, manage_Switch.getSwitch().isChecked(), manage_Switch.getSwitch());
        }
    }


    //
    private void isBlack(Switch v) {
        new SimpleDialogFragment.Builder(mContext)
                .setTitle("??????")
                .showContent(true)
                .setContent("?????????????????????????????????????????????")
                .setOnConfirmClickListener(view -> BlockMethod(GroupFriend_Detail.this, saveFile.Group_SetBlack_Url, black_Switch.getSwitch().isChecked(), black_Switch.getSwitch()))
                .showCancelButton(true)
                .setOnCancelClickListener(view -> {
                    v.setEnabled(true);
                    v.setChecked(false);
                })
                .show();
    }

    private void showSelectDialog() {
        String titleStr = cententTxt.getText() + "???????????????";
        List<String> dataList = new ArrayList<>();
        dataList.add("??????10??????");
        dataList.add("??????1??????");
        dataList.add("??????24??????");
        dataList.add("??????1???");
        new DemoListDialogFragment.Builder(mContext)
                .setTitle(titleStr)
                .setData(dataList)
                .setCancelColorRes(R.color.yellowfive)
                .setWindowAnimations(R.style.animate_dialog)
                .setOnItemClickListener((view, position) -> {
                    int dataIndex = dataList.get(position).indexOf("???") + 1;
                    int dataLength = dataList.get(position).length();
                    String dataStr = dataList.get(position).substring(dataIndex, dataLength);
                    forbidden_ArrowItemView.getTvContent().setText("?????????????????????" + dataStr);
                    int timeType = position + 1;
                    AddForbiddenMethod(GroupFriend_Detail.this, saveFile.Group_UserMute_Url, timeType);
                })
                .show();
    }

    private void showCancelProhibition(final Context mContext, final View view) {
        View contentView = View.inflate(mContext, R.layout.group_cancelprohibition, null);
        PopupWindow MorePopup = new BasePopupWindow(mContext);
        MorePopup.setWidth(RadioGroup.LayoutParams.MATCH_PARENT);
        MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setTouchable(true);
        MorePopup.setContentView(contentView);
        MorePopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        TextView title_Txt = contentView.findViewById(R.id.title_Txt);
        TextView change_txt = contentView.findViewById(R.id.change_txt);
        TextView cancel_txt = contentView.findViewById(R.id.cancel_txt);
        String dataStr = StaticData.stampToDate(model.getData().getMuteEndTime());
        String titleStr = String.format("%1$s???????????????\n??????????????????%2$s", UserName, dataStr);
        title_Txt.setText(titleStr);

        change_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                AddForbiddenMethod(GroupFriend_Detail.this, saveFile.Group_UserMute_Url, 0);
                MorePopup.dismiss();
            }
        });
        cancel_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                MorePopup.dismiss();
            }
        });
    }


    Group_Details_Bean model;

    @SuppressLint("SetTextI18n")
    public void groupFriendMethod(Context mContext, String baseUrl, String keyStr, String valueStr) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put(keyStr, valueStr);
        xUtils3Http.get(mContext, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                model = new Gson().fromJson(result, Group_Details_Bean.class);
                Group_Details_Bean.DataDTO dataDTO = model.getData();
                Uri uri = Uri.parse(model.getData().getUserHead());
                person_img.setImageURI(uri);
                String name = TextUtils.isEmpty(dataDTO.getRemarkName()) ? dataDTO.getNickname() : dataDTO.getRemarkName();
                nick_nameTxt.setText("?????????" + name);
                String groupRemarkStr = TextUtils.isEmpty(dataDTO.getGroupRemarkName()) ? dataDTO.getRemarkName() : dataDTO.getGroupRemarkName();
                cententTxt.setText(groupRemarkStr);
                nick_ArrowItemView.getTvContent().setText(groupRemarkStr);
                leID_Txt.setText("Mo ID:" + dataDTO.getUserNum());
                String signature = dataDTO.getSignature();
                if (!TextUtils.isEmpty(signature)) {
                    signature_Txt.setText("???????????????" + dataDTO.getSignature());
                }
                grade_Txt.setText("LV." + dataDTO.getGrade());

                groupDisplay();
                if (model.getData().getIdentity() == 2) {
                    manage_Switch.getSwitch().setChecked(true);
                }
                if (model.getData().getIsBlack() == 1) {
                    black_Switch.getSwitch().setChecked(true);
                }
                if (model.getData().getIsMute() == 0) {
                    forbidden_ArrowItemView.getTvContent().setText("?????????");
                } else {
                    String dataStr = StaticData.getForbiddenTimeDate(model.getData().getMuteEndTime());
                    forbidden_ArrowItemView.getTvContent().setText("?????????????????????" + dataStr);
                }

                if (dataDTO.getVipType() == 0) {
                    vip_Txt.setVisibility(View.GONE);
                } else {
                    vip_Txt.setVisibility(View.VISIBLE);
                }

                tagList(label_Lin, mContext, dataDTO.getTag());
                UserName = groupRemarkStr;
                MyLevel.setGrade(garde_Lin, dataDTO.getGrade().intValue(), mContext);
            }

            @Override
            public void failed(String... args) {
            }
        });
    }

    /**
     * myIdentity ?????????????????????
     * seeIdentity ??????????????? ??????
     * isFriend ???????????????
     * isOneSelf ???????????????
     */
    private void groupDisplay() {
        MyInfo myInfo = new MyInfo(GroupFriend_Detail.this);
        int seeIdentity = model.getData().getIdentity();
        int isFriend = model.getData().getIsFriend();
        boolean isOneSelf = TextUtils.equals(myInfo.getUserInfo().getUserId(), userId);
        if (isOneSelf) {
            is_Owen_Group.setVisibility(View.GONE);
            forbidden_ArrowItemView.setVisibility(View.GONE);
            return;
        }
        if (myIdentity == 1) {
            if (seeIdentity == 2) {
                is_Owen_Group.setVisibility(View.VISIBLE);
                forbidden_ArrowItemView.setVisibility(View.GONE);
            } else if (seeIdentity == 3) {
                is_Owen_Group.setVisibility(View.VISIBLE);
            }
        } else if (myIdentity == 2) {
            if (seeIdentity == 1) {
                is_Owen_Group.setVisibility(View.GONE);
                forbidden_ArrowItemView.setVisibility(View.GONE);
            } else if (seeIdentity == 2) {
                is_Owen_Group.setVisibility(View.GONE);
                forbidden_ArrowItemView.setVisibility(View.GONE);
            } else if (seeIdentity == 3) {
                is_Owen_Group.setVisibility(View.VISIBLE);
            }
        } else {
            is_Owen_Group.setVisibility(View.GONE);
            forbidden_ArrowItemView.setVisibility(View.GONE);
        }

        if (isFriend == 0) {
            add_Txt.setVisibility(View.VISIBLE);
        } else {
            send_mess_Txt.setVisibility(View.VISIBLE);
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


    private void showMore(final Context mContext, final View view) {
        View contentView = View.inflate(mContext, R.layout.chatdetailset_feedback, null);
        PopupWindow MorePopup = new BasePopupWindow(mContext);
        MorePopup.setWidth(RadioGroup.LayoutParams.MATCH_PARENT);
        MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setTouchable(true);
        MorePopup.setContentView(contentView);
        MorePopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        TextView change_txt = contentView.findViewById(R.id.change_txt);
        TextView newregistr_txt = contentView.findViewById(R.id.newregistr_txt);
        TextView cancel_txt = contentView.findViewById(R.id.cancel_txt);
        String user = "user";
        change_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                String type = "2";
                GroupDetail_Report.actionStart(mContext, userId, user, type);
                MorePopup.dismiss();
            }
        });
        newregistr_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                String type = "1";
                GroupDetail_Report.actionStart(mContext, userId, user, type);
                MorePopup.dismiss();
            }
        });
        cancel_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                MorePopup.dismiss();
            }
        });
    }


    private class leID_TxtOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("text", StaticData.getNumbers(leID_Txt.getText().toString()));
            manager.setPrimaryClip(clipData);
            Toast.makeText(GroupFriend_Detail.this, "?????????????????????", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * ?????????????????????
     */
    public void BlockMethod(Context context, String baseUrl, boolean isBlock, View switchView) {
        site_progressbar.setVisibility(View.VISIBLE);
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put("userId", userId);
        xUtils3Http.post(GroupFriend_Detail.this, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                if (isBlock) {
                    Toast.makeText(context, "?????????????????????", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "?????????????????????", Toast.LENGTH_SHORT).show();
                }
                finish();
                site_progressbar.setVisibility(View.GONE);
                switchView.setEnabled(true);
            }

            @Override
            public void failed(String... args) {
                site_progressbar.setVisibility(View.GONE);
                switchView.setEnabled(true);
            }
        });
    }


    @SuppressLint("NewApi")
    public void AddManageMethod(Context context, String baseUrl, boolean isManage, View switchView) {
        site_progressbar.setVisibility(View.VISIBLE);
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put("userId", userId);
        xUtils3Http.post(GroupFriend_Detail.this, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                String type;
                if (isManage) {
                    Toast.makeText(context, "????????????????????????", Toast.LENGTH_SHORT).show();
                    type = MyConstant.MESSAGE_TYPE_ADDADMIN;
                } else {
                    Toast.makeText(context, "?????????????????????", Toast.LENGTH_SHORT).show();
                    type = MyConstant.MESSAGE_TYPE_DELETADMIN;
                }
                sendMes(groupModel, type);
                site_progressbar.setVisibility(View.GONE);
                switchView.setEnabled(true);
            }

            @Override
            public void failed(String... args) {
                site_progressbar.setVisibility(View.GONE);
                switchView.setEnabled(true);
            }
        });
    }

    @SuppressLint("NewApi")
    public void AddForbiddenMethod(Context context, String baseUrl, int timeType) {
        site_progressbar.setVisibility(View.VISIBLE);
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put("userId", userId);
        if (model.getData().getIsMute() == 0) {
            map.put("timeType", timeType);
            map.put("type", "1");
        } else {
            map.put("type", "2");
        }
        xUtils3Http.post(GroupFriend_Detail.this, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                if (model.getData().getIsMute() == 0) {
                    model.getData().setIsMute(1);
                } else {
                    model.getData().setIsMute(0);
                    forbidden_ArrowItemView.getTvContent().setText("?????????");
                }
                site_progressbar.setVisibility(View.GONE);
            }

            @Override
            public void failed(String... args) {
                site_progressbar.setVisibility(View.GONE);
            }

        });
    }

    /**
     * ????????????
     */
    public void RemoveMethod(Context context, String baseUrl) {
        site_progressbar.setVisibility(View.VISIBLE);
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put("type", "2");
        map.put("userIds", userId);
        xUtils3Http.post(GroupFriend_Detail.this, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Toast.makeText(context, "????????????", Toast.LENGTH_SHORT).show();
                String type = MyConstant.MESSAGE_TYPE_DELETUSER;
                sendMes(groupModel, type);
                finish();
                site_progressbar.setVisibility(View.GONE);
            }

            @Override
            public void failed(String... args) {
                site_progressbar.setVisibility(View.GONE);
            }
        });
    }


    //????????????
    private void sendMes(GruopInfo_Bean Model, String type) {
        MyInfo myInfo = new MyInfo(mContext);
        String conversationId = Model.getData().getGroupHxId();
//        EMMessage message = EMMessage.createTxtSendMessage(mContext.getString(R.string.CREATE_GROUP_CONTENT), conversationId);
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        EMTextMessageBody txtBody = new EMTextMessageBody("");
        message.addBody(txtBody);
        message.setTo(conversationId);
        message.setChatType(EMMessage.ChatType.GroupChat);
        message.setAttribute(MyConstant.MESSAGE_TYPE, type);
        message.setAttribute(MyConstant.USER_NAME, UserName);
        message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNickname());
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
        message.setAttribute(MyConstant.GROUP_NAME, Model.getData().getGroupName());
        message.setAttribute(MyConstant.GROUP_HEAD, Model.getData().getGroupHeadImg());
        DemoHelper.getInstance().getChatManager().sendMessage(message);
    }


}

