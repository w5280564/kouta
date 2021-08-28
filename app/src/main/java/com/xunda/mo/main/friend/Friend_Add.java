package com.xunda.mo.main.friend;

import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.xunda.mo.R;
import com.xunda.mo.main.discover.activity.Discover_QRCode;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.main.friend.myAdapter.Friend_Seek_GroupList_Adapter;
import com.xunda.mo.model.AddFriend_FriendGroup_Model;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.xUtils3Http;
import com.xunda.mo.xrecycle.XRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Friend_Add extends AppCompatActivity {

    private TextView add_left, add_right;
    private TextView seek_txt, MoID_Txt;
    private View seekPerson_InClue, seekGroup_InClue;
    private int tag;
    private XRecyclerView group_Xrecycler;
    private RelativeLayout person_qr_rel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_add);

        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);

        initView();
        initData();
    }

    private void initView() {
        Button return_Btn = findViewById(R.id.return_Btn);
        add_left = findViewById(R.id.add_left);
        add_right = findViewById(R.id.add_right);
        seek_txt = findViewById(R.id.seek_txt);
        seekPerson_InClue = findViewById(R.id.seekperson_inclue);
        MoID_Txt = seekPerson_InClue.findViewById(R.id.MoID_Txt);
        person_qr_rel = seekPerson_InClue.findViewById(R.id.person_qr_rel);
        person_qr_rel.setOnClickListener(new person_qr_relClick());
        seekGroup_InClue = findViewById(R.id.seekgroup_inclue);
        View seek_lin = findViewById(R.id.seek_lin);
        group_Xrecycler = seekGroup_InClue.findViewById(R.id.group_Xrecycler);
        group_Xrecycler.setPullRefreshEnabled(false);
        group_Xrecycler.setLoadingMoreEnabled(false);

        tag = 0;
        changeView(0);
        add_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeView(0);
            }
        });
        add_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeView(1);
            }
        });

        return_Btn.setOnClickListener(new return_BtnonClickLister());
        seek_lin.setOnClickListener(new seek_linClickLister());
    }

    private class return_BtnonClickLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }


    private int PageIndex;
    private int pageSize;

    private void initData() {
        MyInfo myInfo = new MyInfo(Friend_Add.this);
        MoID_Txt.setText("我的Mo ID：" + myInfo.getUserInfo().getUserNum());

        PageIndex = 1;
        pageSize = 10;
        AddFriendMethod(Friend_Add.this, saveFile.Group_SearchGroup_Url);
//        AddFriendMethod(Friend_Add_SeekPerson.this, saveFile.BaseUrl + saveFile.User_SearchAll_Url + "?search=" + searchStr + "&type=" + type + "&pageNum=" + 1 + "&pageSize=" + 10);
    }

    private class seek_linClickLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (tag == 0) {
                Intent intent = new Intent(Friend_Add.this, Friend_Add_SeekFriend.class);
                startActivity(intent);
            } else if (tag == 1) {
                Intent intent = new Intent(Friend_Add.this, Friend_Add_SeekGroup.class);
                startActivity(intent);
            }
        }
    }

    private void changeView(int i) {
        if (i == 0) {
            //设置背景色及字体颜色
            tag = 0;
            add_left.setBackgroundResource(R.drawable.friend_add_left);
            add_left.setTextColor(getResources().getColor(R.color.white, null));
            add_right.setBackground(null);
            add_right.setTextColor(getResources().getColor(R.color.yellow, null));
            seek_txt.setText("MoID/昵称/手机号/标签/群");
            seekPerson_InClue.setVisibility(View.VISIBLE);
            seekGroup_InClue.setVisibility(View.GONE);
        } else if (i == 1) {
            tag = 1;
            add_right.setBackgroundResource(R.drawable.friend_add_right);
            add_right.setTextColor(getResources().getColor(R.color.white, null));
            add_left.setBackground(null);
            add_left.setTextColor(getResources().getColor(R.color.yellow, null));
            seek_txt.setText("群ID/群名称/群标签");
            seekPerson_InClue.setVisibility(View.GONE);
            seekGroup_InClue.setVisibility(View.VISIBLE);
        }
    }

    Friend_Seek_GroupList_Adapter mAdapter;

    public void initlist(final Context context) {
        LinearLayoutManager mMangaer = new LinearLayoutManager(context);
        group_Xrecycler.setLayoutManager(mMangaer);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        group_Xrecycler.setHasFixedSize(true);
        mAdapter = new Friend_Seek_GroupList_Adapter(context, baseModel, Model);
        group_Xrecycler.setAdapter(mAdapter);
        mAdapter.setOnItemClickLitener(new Friend_Seek_GroupList_Adapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                String GroupId = baseModel.get(position).getGroupId();
                Friend_Group_Detail.actionStart(Friend_Add.this, GroupId);

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
        map.put("type", "0");
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
//                        list_xrecycler.refreshComplete();
                        initlist(context);
                    } else {
//                        list_xrecycler.loadMoreComplete();
//                        mAdapter.addMoreData(baseModel);
                    }
                } else {
//                    list_xrecycler.removeAllViews();
//                    list_xrecycler.refreshComplete();
                    Toast.makeText(context, "没有搜到", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failed(String... args) {
            }
        });

    }


    private class person_qr_relClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Discover_QRCode.actionStart(Friend_Add.this);
        }
    }
}