package com.xunda.mo.main.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.dialog.SimpleDialogFragment;
import com.xunda.mo.main.me.adapter.Me_Manage_BlackList_Adapter;
import com.xunda.mo.model.GroupBlackList_Bean;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Me_Manage_BlackList extends BaseInitActivity {

    private RecyclerView base_Recycler;
    private String groupId;
    private EditText query_Edit;
    private ImageButton search_clear;
    private String friendUserId;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Me_Manage_BlackList.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.group_allmembers_manage_blacklist;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        query_Edit = findViewById(R.id.query_Edit);
        query_Edit.addTextChangedListener(new query_EditChangeListener());
        search_clear = findViewById(R.id.search_clear);
        search_clear.setOnClickListener(new search_clearClick());

        base_Recycler = findViewById(R.id.base_Recycler);
        initTitle();
    }


    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(Me_Manage_BlackList.this, R.color.white));
        title_Include.setElevation(2f);//阴影
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("黑名单");
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
        friendBlackListMethod(Me_Manage_BlackList.this, saveFile.Friend_Blacks_List);
    }


    private class query_EditChangeListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String keyword = s.toString();
            if (!TextUtils.isEmpty(keyword)) {
                search_clear.setVisibility(View.VISIBLE);
                SearchGroupMember(keyword);
            } else {
                search_clear.setVisibility(View.INVISIBLE);
//                getGroupList();
                initlist(Me_Manage_BlackList.this);
            }
        }
    }

    private class search_clearClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            query_Edit.getText().clear();
            initlist(Me_Manage_BlackList.this);
        }
    }

    private void SearchGroupMember(String keyword) {
        List<GroupBlackList_Bean.DataDTO> searchList = new ArrayList<>();
        for (int i = 0; i < groupListModel.getData().size(); i++) {
            GroupBlackList_Bean.DataDTO dataDTO = groupListModel.getData().get(i);
            String userNickName = dataDTO.getNickname();
            String userNum = dataDTO.getUserNum().toString();
            if (userNickName.contains(keyword) || userNum.contains(keyword)) {
                searchList.add(groupListModel.getData().get(i));
            }
        }
        mAdapter.setData(searchList);
    }


    Me_Manage_BlackList_Adapter mAdapter;

    public void initlist(final Context context) {
        LinearLayoutManager mMangaer = new LinearLayoutManager(context);
        base_Recycler.setLayoutManager(mMangaer);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        base_Recycler.setHasFixedSize(true);
        mAdapter = new Me_Manage_BlackList_Adapter(context, groupListModel.getData());
        base_Recycler.setAdapter(mAdapter);

        mAdapter.setOnItemClickLitener(new Me_Manage_BlackList_Adapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onItemLongClick(View view, int position) {
                friendUserId =  groupListModel.getData().get(position).getUserId();
                isRemove(position);

            }
        });
    }


    private void isRemove(int pos) {
        String content = "";
        content = "移出黑名单";
        new SimpleDialogFragment.Builder(mContext)
                .setTitle("提示")
                .showContent(true)
                .setContent(content)
                .setOnConfirmClickListener(view -> {
                    BlackMethod(Me_Manage_BlackList.this, saveFile.Friend_SetBlack_Url, false);
                    mAdapter.removeData(pos);
                })
                .showCancelButton(true)
                .show();
    }


    GroupBlackList_Bean groupListModel;

    public void friendBlackListMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                groupListModel = new Gson().fromJson(result, GroupBlackList_Bean.class);
                initlist(context);
            }

            @Override
            public void failed(String... args) {

            }
        });

    }

    /**
     * 拉人移除黑名单
     *
     * @param context
     * @param baseUrl
     */
    public void BlackMethod(Context context, String baseUrl, boolean isBlock) {
        Map<String, Object> map = new HashMap<>();
        map.put("friendUserId", friendUserId);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Toast.makeText(context, "移除成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failed(String... args) {
            }
        });
    }



}