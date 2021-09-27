package com.xunda.mo.main.friend.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.model.Group_Apply_Bean;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;
import com.xunda.mo.xrecycle.XRecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Friend_BlackMeList extends BaseInitActivity {

    private XRecyclerView list_xrecycler;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Friend_BlackMeList.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_friend_blackmelist;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();
        list_xrecycler = findViewById(R.id.list_xrecycler);
        list_xrecycler.setLoadingListener(new listLoadingLister());
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(Friend_BlackMeList.this, R.color.white));
        title_Include.setElevation(2f);//阴影
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("删除我的好友");
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

    private int PageIndex;
    private int pageSize;
    @Override
    protected void initData() {
        super.initData();
        PageIndex = 1;
        pageSize = 10;
        GroupApplyLIstMethod(Friend_BlackMeList.this, saveFile.Friend_Blacks);
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
            GroupApplyLIstMethod(Friend_BlackMeList.this,  saveFile.Friend_Blacks );
        }
    }

    private List<Group_Apply_Bean.DataDTO.ListDTO> baseModel;
    private Group_Apply_Bean Model;

    public void GroupApplyLIstMethod(Context context, String baseUrl) {
        Map<String,Object> map = new HashMap<>();
        map.put("pageNum",PageIndex);
        map.put("pageSize",pageSize);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
//                Model = new Gson().fromJson(result, Group_Apply_Bean.class);
//                if (PageIndex == 1) {
//                    if (baseModel != null) {
//                        baseModel.clear();
//                    }
//                    baseModel = new ArrayList<>();
//                }
//
//                if (Model.getData() != null) {
//                    baseModel.addAll(Model.getData().getList());
//                    if (PageIndex == 1) {
//                        list_xrecycler.refreshComplete();
//                        initlist(context);
//                    } else {
//                        list_xrecycler.loadMoreComplete();
//                        mAdapter.addMoreData(baseModel);
//                    }
//                } else {
//                    list_xrecycler.removeAllViews();
//                    list_xrecycler.refreshComplete();
//                    Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
//                }
            }
            @Override
            public void failed(String... args) {
            }
        });
    }




}