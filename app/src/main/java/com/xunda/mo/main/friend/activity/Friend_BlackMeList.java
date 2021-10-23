package com.xunda.mo.main.friend.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.dialog.SimpleDialogFragment;
import com.xunda.mo.main.friend.myAdapter.Friend_BlackList_Adapter;
import com.xunda.mo.model.GroupBlackList_Bean;
import com.xunda.mo.model.baseModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;
import com.xunda.mo.xrecycle.XRecyclerView;

import java.util.HashMap;
import java.util.Map;

import lombok.SneakyThrows;

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
        cententTxt.setText("拉黑我的好友");
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
        GroupApplyLIstMethod(Friend_BlackMeList.this, saveFile.Friend_Blacks);
    }

    private class listLoadingLister implements XRecyclerView.LoadingListener {
        @Override
        public void onRefresh() {
            initData();
        }

        @Override
        public void onLoadMore() {
            GroupApplyLIstMethod(Friend_BlackMeList.this,  saveFile.Friend_Blacks );
        }
    }



    Friend_BlackList_Adapter mAdapter;

    public void initlist(final Context context) {
        LinearLayoutManager mMangaer = new LinearLayoutManager(context);
        list_xrecycler.setLayoutManager(mMangaer);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        list_xrecycler.setHasFixedSize(true);
        mAdapter = new Friend_BlackList_Adapter(context, Model.getData());
        list_xrecycler.setAdapter(mAdapter);

        mAdapter.setOnItemAddRemoveClickLister((view, position) -> {
            removeFriend(position);
        });
    }



    private GroupBlackList_Bean Model;
    public void GroupApplyLIstMethod(Context context, String baseUrl) {
        Map<String,Object> map = new HashMap<>();
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Model = new Gson().fromJson(result, GroupBlackList_Bean.class);
                initlist(context);
                list_xrecycler.refreshComplete();
            }
            @Override
            public void failed(String... args) {
            }
        });
    }

    private void removeFriend(int pos) {
        String content = String.format("将联系人“%1$s”删除，同时删除与该联系人的聊天记录",Model.getData().get(pos).getNickname());
        // 是否删除好友
        new SimpleDialogFragment.Builder(mContext)
                .setTitle("删除联系人")
                .showContent(true)
                .setContent(content)
                .setOnConfirmClickListener(view -> {
                  RemoveMethod(Friend_BlackMeList.this, saveFile.Friend_Delete_Url, pos);
                    mAdapter.removeData(pos);
                })

                .showCancelButton(true)
                .show();
    }


    /**
     * 删除好友
     *
     * @param context
     * @param baseUrl
     */
    public void RemoveMethod(Context context, String baseUrl,int pos) {
        Map<String, Object> map = new HashMap<>();
        map.put("friendUserId", Model.getData().get(pos).getUserId());
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @SneakyThrows
            @Override
            public void success(String result) {
                baseModel baseBean = new Gson().fromJson(result, baseModel.class);
                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                String HxUserName = Model.getData().get(pos).getHxUserName();
                DemoHelper.getInstance().getContactManager().deleteContact(HxUserName, false);
                DemoHelper.getInstance().getChatManager().deleteConversation(HxUserName, true);

            }
            @Override
            public void failed(String... args) {
            }
        });
    }




}