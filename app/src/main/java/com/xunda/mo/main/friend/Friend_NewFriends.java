package com.xunda.mo.main.friend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.dialog.DemoDialogFragment;
import com.xunda.mo.hx.section.dialog.SimpleDialogFragment;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.friend.myAdapter.Friend_NewFriendList_Adapter;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.model.NewFriend_Bean;
import com.xunda.mo.model.baseModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;
import com.xunda.mo.xrecycle.XRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Friend_NewFriends extends BaseInitActivity {

    private XRecyclerView list_xrecycler;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Friend_NewFriends.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_friend_newfriends;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();
        list_xrecycler = findViewById(R.id.list_xrecycler);
        list_xrecycler.setLoadingListener(new listLoadingLister());
    }

    private int PageIndex;
    private int pageSize;

    @Override
    protected void initData() {
        super.initData();
        PageIndex = 1;
        pageSize = 10;
        NewFriendMethod(Friend_NewFriends.this, saveFile.Friend_ApplyList_Url);
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setElevation(2f);//阴影
        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("新朋友");
        Button right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.VISIBLE);
        right_Btn.setBackgroundResource(R.mipmap.adress_head_more);
        viewTouchDelegate.expandViewTouchDelegate(right_Btn, 50, 50, 50, 50);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        int margisright = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
        layoutParams.setMargins(0, 0, margisright, 0);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        right_Btn.setLayoutParams(layoutParams);

        return_Btn.setOnClickListener(new return_Btn());
        right_Btn.setOnClickListener(new right_BtnClick());
    }


    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }

    private class right_BtnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            showMore(Friend_NewFriends.this, v, 0);
        }
    }


    private class listLoadingLister implements XRecyclerView.LoadingListener {
        @Override
        public void onRefresh() {
            initData();
        }

        @Override
        public void onLoadMore() {
            PageIndex = PageIndex + 1;
            pageSize = 10;
            NewFriendMethod(Friend_NewFriends.this, saveFile.Friend_ApplyList_Url);
        }
    }

    Friend_NewFriendList_Adapter mAdapter;

    public void initlist(final Context context) {
        LinearLayoutManager mMangaer = new LinearLayoutManager(context);
        list_xrecycler.setLayoutManager(mMangaer);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        list_xrecycler.setHasFixedSize(true);
        mAdapter = new Friend_NewFriendList_Adapter(context, baseModel, Model);
        list_xrecycler.setAdapter(mAdapter);
        mAdapter.setOnItemClickLitener(new Friend_NewFriendList_Adapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
//                String addType = "8";
//                String friendUserId = baseModel.get(position).
//                ChatFriend_Detail.actionStart(mContext, friendUserId, addType);
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }

        });
        //接受、拒绝好友请求
        mAdapter.setOnItemAddRemoveClickLister(new Friend_NewFriendList_Adapter.OnItemAddRemoveClickLister() {
            @Override
            public void onItemAddClick(View view, int position) {
                String agreeType = "1";
                String friendApplyId = baseModel.get(position).getFriendApplyId();
                String hXUserName = baseModel.get(position).getHxUserName();
                AddRemoveMethod(Friend_NewFriends.this, saveFile.Friend_Agree_Url, agreeType, friendApplyId, hXUserName,position);
            }

            @Override
            public void onItemRemoveClick(View view, int position) {
                String agreeType = "2";
                String friendApplyId = baseModel.get(position).getFriendApplyId();
                String hXUserName = baseModel.get(position).getHxUserName();
                AddRemoveMethod(Friend_NewFriends.this, saveFile.Friend_Agree_Url, agreeType, friendApplyId, hXUserName,position);
            }
        });
    }

    //更多
    private void showMore(final Context mContext, final View view, final int pos) {
        View contentView = View.inflate(mContext, R.layout.popup_morechoice, null);
        PopupWindow MorePopup = new BasePopupWindow(mContext);
        MorePopup.setWidth(RadioGroup.LayoutParams.MATCH_PARENT);
        MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setTouchable(true);
        MorePopup.setContentView(contentView);
        MorePopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        TextView change_txt = contentView.findViewById(R.id.change_txt);
        change_txt.setText("全部拒绝");
        TextView newregistr_txt = contentView.findViewById(R.id.newregistr_txt);
        newregistr_txt.setText("全部清除");
        TextView cancel_txt = contentView.findViewById(R.id.cancel_txt);
        change_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                MorePopup.dismiss();
                rejectAll_AndClearMethod(Friend_NewFriends.this, saveFile.Friend_RejectAll_Url);
            }
        });
        newregistr_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                MorePopup.dismiss();
                clearHistory();
            }
        });
        cancel_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                MorePopup.dismiss();
            }
        });
    }

    //清除申请记录
    private void clearHistory() {
        new SimpleDialogFragment.Builder(mContext)
                .setTitle("是否清空全部历史记录？")
                .setOnConfirmClickListener(new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        rejectAll_AndClearMethod(Friend_NewFriends.this, saveFile.Friend_ClearApplyList_Url);
                    }
                })
                .showCancelButton(true)
                .show();
    }


    private List<NewFriend_Bean.DataDTO.ListDTO> baseModel;
    private NewFriend_Bean Model;

    public void NewFriendMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", PageIndex);
        map.put("pageSize", pageSize);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Model = new Gson().fromJson(result, NewFriend_Bean.class);
                if (PageIndex == 1) {
                    if (baseModel != null) {
                        baseModel.clear();
                    }
                    baseModel = new ArrayList<>();
                }

                if (Model.getData() != null) {
                    baseModel.addAll(Model.getData().getList());
                    if (PageIndex == 1) {
                        list_xrecycler.refreshComplete();
                        initlist(context);
                    } else {
                        list_xrecycler.loadMoreComplete();
                        mAdapter.addMoreData(baseModel);
                    }

                } else {
                    list_xrecycler.removeAllViews();
                    list_xrecycler.refreshComplete();
                    Toast.makeText(context, "没有搜到", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void failed(String... args) {

            }
        });

    }


    /**
     * 添加拒绝好友请求
     *
     * @param context
     * @param baseUrl
     * @param agreeType     1通过2拒绝
     * @param friendApplyId 好友申请ID
     */
    public void AddRemoveMethod(Context context, String baseUrl, String agreeType, String friendApplyId, String hXUserName,int position) {
        Map<String, Object> map = new HashMap<>();
        map.put("agreeType", agreeType);
        map.put("friendApplyId", friendApplyId);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                try {
                    if (TextUtils.equals(agreeType, "1")) {
//                        EMClient.getInstance().contactManager().acceptInvitation(hXUserName);
                        DemoHelper.getInstance().getContactManager().asyncAcceptInvitation(hXUserName, new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                singleSendMes(hXUserName,baseModel,position);
                            }

                            @Override
                            public void onError(int code, String error) {

                            }

                            @Override
                            public void onProgress(int progress, String status) {

                            }
                        });

                        Toast.makeText(mContext, "已通过", Toast.LENGTH_SHORT).show();
                    } else {
                        EMClient.getInstance().contactManager().declineInvitation(hXUserName);
                        Toast.makeText(mContext, "已拒绝", Toast.LENGTH_SHORT).show();
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(String... args) {
            }
        });
    }

    /**
     * 拒绝与清除申请记录
     *
     * @param context
     * @param baseUrl
     */
    public void rejectAll_AndClearMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                baseModel baseBean = new Gson().fromJson(result, baseModel.class);
                list_xrecycler.refreshComplete();
                Toast.makeText(context, baseBean.getMsg(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failed(String... args) {
            }
        });
    }


    //单人聊天发送扩展字段
    private void singleSendMes(String hxUserName,List<NewFriend_Bean.DataDTO.ListDTO> listData,int position) {
        MyInfo myInfo = new MyInfo(mContext);
        NewFriend_Bean.DataDTO.ListDTO oneData = listData.get(position);
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        EMTextMessageBody txtBody = new EMTextMessageBody("");
        message.addBody(txtBody);
        message.setTo(hxUserName);
        message.setChatType(EMMessage.ChatType.Chat);
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TYPE_APPLY);
        message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNickname());
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
        String toName = TextUtils.isEmpty(oneData.getRemark()) ? oneData.getNickname() : oneData.getRemark();
        message.setAttribute(MyConstant.TO_NAME, toName);
        message.setAttribute(MyConstant.TO_HEAD, oneData.getHeadImg());
        message.setAttribute(MyConstant.TO_LH, oneData.getLightStatus());
        message.setAttribute(MyConstant.TO_VIP, oneData.getVipType());
        DemoHelper.getInstance().getChatManager().sendMessage(message);
    }


}