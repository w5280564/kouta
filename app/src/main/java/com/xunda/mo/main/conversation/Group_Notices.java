package com.xunda.mo.main.conversation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.model.EaseEvent;
import com.xunda.mo.R;
import com.xunda.mo.dialog.TwoButtonDialogWithTitle;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.main.group.adapter.GroupNotice_Adapter;
import com.xunda.mo.model.Group_notices_Bean;
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

public class Group_Notices extends BaseInitActivity {
    private XRecyclerView list_xrecycler;
    private String conversationId;

    public static void actionStart(Context context, String conversationId) {
        Intent intent = new Intent(context, Group_Notices.class);
        intent.putExtra("conversationId", conversationId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_friend_newfriends;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        conversationId = intent.getStringExtra("conversationId");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();
        list_xrecycler = findViewById(R.id.list_xrecycler);
        list_xrecycler.setLoadingListener(new listLoadingLister());
        makeAllMsgRead();
    }

    private int PageIndex;
    private int pageSize;

    @Override
    protected void initData() {
        super.initData();
        PageIndex = 1;
        pageSize = 10;
        GroupApplyLIstMethod(Group_Notices.this, saveFile.Group_Notify_Url);
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setElevation(2f);//??????
        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("?????????");
        Button right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
//        right_Btn.setVisibility(View.VISIBLE);
//        right_Btn.setBackgroundResource(R.mipmap.adress_head_more);
//        viewTouchDelegate.expandViewTouchDelegate(right_Btn, 50, 50, 50, 50);
//        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
//        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics());
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
//        int margisright = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
//        layoutParams.setMargins(0, 0, margisright, 0);
//        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
//        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        right_Btn.setLayoutParams(layoutParams);

        return_Btn.setOnClickListener(new return_Btn());
//        right_Btn.setOnClickListener(new right_BtnClick());
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
            showMore(Group_Notices.this, v, 0);
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
            GroupApplyLIstMethod(Group_Notices.this,  saveFile.Group_Notify_Url );
        }
    }

    GroupNotice_Adapter mAdapter;

    public void initlist(final Context context) {
        LinearLayoutManager mMangaer = new LinearLayoutManager(context);
        list_xrecycler.setLayoutManager(mMangaer);
        //????????????????????????item????????????????????????????????????????????????????????????
        list_xrecycler.setHasFixedSize(true);
        mAdapter = new GroupNotice_Adapter(context, baseModel, Model);
        list_xrecycler.setAdapter(mAdapter);
        //????????????????????????
        mAdapter.setOnItemAddRemoveClickLister(new GroupNotice_Adapter.OnItemAddRemoveClickLister() {
            @Override
            public void onItemAddClick(View view, int position) {
                String agreeType = "1";
                String groupApplyId = baseModel.get(position).getGroupApplyId();
                AddRemoveMethod(Group_Notices.this, saveFile.Group_AgreeApply_Url, agreeType, groupApplyId);
            }

            @Override
            public void onItemRemoveClick(View view, int position) {
                String agreeType = "2";
                String groupApplyId = baseModel.get(position).getGroupApplyId();
                AddRemoveMethod(Group_Notices.this, saveFile.Group_AgreeApply_Url, agreeType, groupApplyId);
            }

            @Override
            public void onItemBlackClick(View view, int position) {
                String agreeType = "3";
                String groupApplyId = baseModel.get(position).getGroupApplyId();
                AddRemoveMethod(Group_Notices.this, saveFile.Group_AgreeApply_Url, agreeType, groupApplyId);
            }
        });

    }

    //??????
    String groupApplyId = "";
    private void showMore(final Context mContext, final View view, final int pos) {
        View contentView = View.inflate(mContext, R.layout.popup_morechoice, null);
        PopupWindow MorePopup = new BasePopupWindow(mContext);
        MorePopup.setWidth(RadioGroup.LayoutParams.MATCH_PARENT);
        MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setTouchable(true);
        MorePopup.setContentView(contentView);
        MorePopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        TextView change_txt = contentView.findViewById(R.id.change_txt);
        change_txt.setText("????????????");
        TextView newregistr_txt = contentView.findViewById(R.id.newregistr_txt);
        newregistr_txt.setText("????????????");
        TextView cancel_txt = contentView.findViewById(R.id.cancel_txt);
        StringBuffer stringBuffer = new StringBuffer();
        if (!baseModel.isEmpty()) {
            for (int i = 0; i < baseModel.size(); i++) {
                stringBuffer.append(baseModel.get(i).getGroupApplyId() + ",");
            }
        }
        groupApplyId = stringBuffer.toString();
        change_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                MorePopup.dismiss();
                rejectAll_AndClearMethod(Group_Notices.this,  saveFile.Group_UpdateApplyListByIds_Url, groupApplyId);
            }
        });
        newregistr_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                MorePopup.dismiss();
                showToastDialog();
            }
        });
        cancel_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                MorePopup.dismiss();
            }
        });
    }


    /**
     * ??????dialog
     */
    private void showToastDialog() {
        TwoButtonDialogWithTitle dialog = new TwoButtonDialogWithTitle(this, "?????????????????????????????????", "??????", "??????",
                new TwoButtonDialogWithTitle.ConfirmListener() {

                    @Override
                    public void onClickRight() {
                        rejectAll_AndClearMethod(Group_Notices.this,  saveFile.Group_DeleteApplyListByIds_Url, groupApplyId);
                    }

                    @Override
                    public void onClickLeft() {

                    }
                });
        dialog.show();

    }


    private List<Group_notices_Bean.DataDTO> baseModel;
    private Group_notices_Bean Model;

    public void GroupApplyLIstMethod(Context context, String baseUrl) {
        Map<String,Object> map = new HashMap<>();
        map.put("pageNum",PageIndex);
        map.put("pageSize",pageSize);
//        map.put("groupId",conversationId);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Model = new Gson().fromJson(result, Group_notices_Bean.class);
                if (PageIndex == 1) {
                    if (baseModel != null) {
                        baseModel.clear();
                    }
                    baseModel = new ArrayList<>();
                }

                if (Model.getData() != null) {
                    baseModel.addAll(Model.getData());
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
                    Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void failed(String... args) {
            }
        });
    }


    /**
     * ????????????????????????
     *
     * @param context
     * @param baseUrl
     * @param agreeType    1??????2?????? 3??????
     * @param groupApplyId ????????????ID
     */
    public void AddRemoveMethod(Context context, String baseUrl, String agreeType, String groupApplyId) {
        Map<String,Object> map = new HashMap<>();
        map.put("groupApplyId",groupApplyId);
        map.put("type",agreeType);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                if (TextUtils.equals(agreeType, "1")) {
                    Toast.makeText(mContext, "?????????", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.equals(agreeType, "2")) {
                    Toast.makeText(mContext, "?????????", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.equals(agreeType, "3")) {
                    Toast.makeText(mContext, "?????????", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void failed(String... args) {
            }
        });
    }

    /**
     * ???????????????????????????
     *
     * @param context
     * @param baseUrl
     */
    public void rejectAll_AndClearMethod(Context context, String baseUrl, String groupApplyId) {
        Map<String,Object> map = new HashMap<>();
        map.put("groupApplyId",groupApplyId);
        map.put("groupId",conversationId);
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


    public void makeAllMsgRead() {
        EMConversation conversation = DemoHelper.getInstance().getChatManager().getConversation(conversationId);
        conversation.markAllMessagesAsRead();
        LiveDataBus.get().with(DemoConstant.NOTIFY_CHANGE).postValue(EaseEvent.create(DemoConstant.NOTIFY_CHANGE, EaseEvent.TYPE.NOTIFY));
    }

}