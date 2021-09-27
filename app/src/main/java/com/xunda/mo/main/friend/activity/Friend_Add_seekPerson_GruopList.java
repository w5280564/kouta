package com.xunda.mo.main.friend.activity;

import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.xunda.mo.R;
import com.xunda.mo.main.friend.myAdapter.Friend_Seek_GroupList_Adapter;
import com.xunda.mo.model.AddFriend_FriendGroup_Model;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.xUtils3Http;
import com.xunda.mo.xrecycle.XRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Friend_Add_seekPerson_GruopList extends AppCompatActivity implements XRecyclerView.LoadingListener {
    private View cancel_txt;
    private EditText seek_edit;
    private LinearLayout friend_lin;
    private String type;
    private String seekStr;
    private XRecyclerView list_xrecycler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendadd_seekperson_friendlist);

        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);

        initView();
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        seekStr = intent.getStringExtra("seekStr");
        seek_edit.setText(seekStr);
        seek_edit.setSelection(seekStr.length());

//        if (type.equals("NickNameUser")) {
//            seek_edit.setHint("搜索用户昵称");
//            type = "1";
//        } else if (type.equals("userNumUser")) {
//            seek_edit.setHint("搜索用户ID");
//            type = "2";
//        } else if (type.equals("phoneNumUser")) {
//            seek_edit.setHint("搜索用户手机号");
//            type = "3";
//        } else if (type.equals("tagUser")) {
//            seek_edit.setHint("搜索用户标签");
//            type = "4";
//        } else

        if (type.equals("nameGroup")) {
            seek_edit.setHint("搜索群昵称/ID");
            type = "1";
        } else if (type.equals("tagGroup")) {
            seek_edit.setHint("搜索群标签");
            type = "2";
        }
        initData(type, seekStr);
    }

    private void initView() {
        cancel_txt = findViewById(R.id.cancel_txt);
        seek_edit = findViewById(R.id.seek_edit);
        friend_lin = findViewById(R.id.friend_lin);
        list_xrecycler = findViewById(R.id.list_xrecycler);
        list_xrecycler.setLoadingListener(this);


        cancel_txt.setOnClickListener(new cancel_txtOnclickLister());
        seek_edit.setOnEditorActionListener(new SeekOnEditorListener());
    }

    @Override
    public void onRefresh() {
        String searchStr = seek_edit.getText().toString();
        initData(type, searchStr);
    }

    @Override
    public void onLoadMore() {
        PageIndex = PageIndex + 1;
        pageSize = 10;
        AddFriendMethod(Friend_Add_seekPerson_GruopList.this, saveFile.Group_SearchGroup_Url);
    }

    private class cancel_txtOnclickLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class SeekOnEditorListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {//打开软键盘
                imm.hideSoftInputFromWindow(seek_edit.getWindowToken(), 0);
            }
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                searchData();
                String searchStr = seek_edit.getText().toString().trim();
                initData(type, searchStr);
            }
            return false;
        }
    }

    private int PageIndex;
    private int pageSize;

    private void initData(String type, String seekStr) {
        PageIndex = 1;
        pageSize = 10;
        AddFriendMethod(Friend_Add_seekPerson_GruopList.this, saveFile.Group_SearchGroup_Url);
    }


    Friend_Seek_GroupList_Adapter mAdapter;

    public void initlist(final Context context) {
        LinearLayoutManager mMangaer = new LinearLayoutManager(context);
        list_xrecycler.setLayoutManager(mMangaer);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        list_xrecycler.setHasFixedSize(true);
        mAdapter = new Friend_Seek_GroupList_Adapter(context, baseModel, Model);
        list_xrecycler.setAdapter(mAdapter);
        mAdapter.setOnItemClickLitener(new Friend_Seek_GroupList_Adapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                String GroupId = baseModel.get(position).getGroupId();
                Friend_Group_Detail.actionStart(Friend_Add_seekPerson_GruopList.this, GroupId);

            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
    }


    private List<AddFriend_FriendGroup_Model.DataDTO.ListDTO> baseModel;
    private AddFriend_FriendGroup_Model Model;

    public void AddFriendMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("search", seekStr);
        map.put("type", type);
        map.put("pageNum", PageIndex);
        map.put("pageSize", pageSize);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Model = new Gson().fromJson(result, AddFriend_FriendGroup_Model.class);
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


}