package com.xunda.mo.main.group.activity;

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
import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.dialog.DemoDialogFragment;
import com.xunda.mo.hx.section.dialog.SimpleDialogFragment;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.main.group.adapter.GroupAllMembers_Manage_ApplyList_Adapter;
import com.xunda.mo.model.Group_Apply_Bean;
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

public class GroupAllMembers_Manage_Apply extends BaseInitActivity {
    private XRecyclerView list_xrecycler;
    private String groupId;

    public static void actionStart(Context context, String groupId) {
        Intent intent = new Intent(context, GroupAllMembers_Manage_Apply.class);
        intent.putExtra("groupId", groupId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_friends;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupId = intent.getStringExtra("groupId");
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
        GroupApplyLIstMethod(GroupAllMembers_Manage_Apply.this, saveFile.Group_GroupApplyList_Url);
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setElevation(2f);//阴影
        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("申请列表");
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
            showMore(GroupAllMembers_Manage_Apply.this, v, 0);
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
            GroupApplyLIstMethod(GroupAllMembers_Manage_Apply.this,  saveFile.Group_GroupApplyList_Url );
        }
    }

    GroupAllMembers_Manage_ApplyList_Adapter mAdapter;

    public void initlist(final Context context) {
        LinearLayoutManager mMangaer = new LinearLayoutManager(context);
        list_xrecycler.setLayoutManager(mMangaer);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        list_xrecycler.setHasFixedSize(true);
        mAdapter = new GroupAllMembers_Manage_ApplyList_Adapter(context, baseModel, Model);
        list_xrecycler.setAdapter(mAdapter);
        //接受、拒绝、拉黑
        mAdapter.setOnItemAddRemoveClickLister(new GroupAllMembers_Manage_ApplyList_Adapter.OnItemAddRemoveClickLister() {
            @Override
            public void onItemAddClick(View view, int position) {
                String agreeType = "1";
                String groupApplyId = baseModel.get(position).getGroupApplyId();
                AddRemoveMethod(GroupAllMembers_Manage_Apply.this, saveFile.Group_AgreeApply_Url, agreeType, groupApplyId);
            }

            @Override
            public void onItemRemoveClick(View view, int position) {
                String agreeType = "2";
                String groupApplyId = baseModel.get(position).getGroupApplyId();
                AddRemoveMethod(GroupAllMembers_Manage_Apply.this, saveFile.Group_AgreeApply_Url, agreeType, groupApplyId);
            }

            @Override
            public void onItemBlackClick(View view, int position) {
                String agreeType = "3";
                String groupApplyId = baseModel.get(position).getGroupApplyId();
                AddRemoveMethod(GroupAllMembers_Manage_Apply.this, saveFile.Group_AgreeApply_Url, agreeType, groupApplyId);
            }
        });

    }

    //更多
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
        change_txt.setText("全部拒绝");
        TextView newregistr_txt = contentView.findViewById(R.id.newregistr_txt);
        newregistr_txt.setText("全部清除");
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
                rejectAll_AndClearMethod(GroupAllMembers_Manage_Apply.this,  saveFile.Group_UpdateApplyListByIds_Url, groupApplyId);
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
                        rejectAll_AndClearMethod(GroupAllMembers_Manage_Apply.this,  saveFile.Group_DeleteApplyListByIds_Url, groupApplyId);
                    }
                })
                .showCancelButton(true)
                .show();
    }


    private List<Group_Apply_Bean.DataDTO.ListDTO> baseModel;
    private Group_Apply_Bean Model;

    public void GroupApplyLIstMethod(Context context, String baseUrl) {
        Map<String,Object> map = new HashMap<>();
        map.put("pageNum",PageIndex);
        map.put("pageSize",pageSize);
        map.put("groupId",groupId);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Model = new Gson().fromJson(result, Group_Apply_Bean.class);
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
                    Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
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
     * @param agreeType    1通过2拒绝 3拉黑
     * @param groupApplyId 好友申请ID
     */
    public void AddRemoveMethod(Context context, String baseUrl, String agreeType, String groupApplyId) {
        Map<String,Object> map = new HashMap<>();
        map.put("groupApplyId",groupApplyId);
        map.put("type",agreeType);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                if (TextUtils.equals(agreeType, "1")) {
                    Toast.makeText(mContext, "已通过", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.equals(agreeType, "2")) {
                    Toast.makeText(mContext, "已拒绝", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.equals(agreeType, "3")) {
                    Toast.makeText(mContext, "已拉黑", Toast.LENGTH_SHORT).show();
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
    public void rejectAll_AndClearMethod(Context context, String baseUrl, String groupApplyId) {
        Map<String,Object> map = new HashMap<>();
        map.put("groupApplyId",groupApplyId);
        map.put("groupId",groupId);
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


}