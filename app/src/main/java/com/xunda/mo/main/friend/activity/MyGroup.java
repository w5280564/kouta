package com.xunda.mo.main.friend.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baozi.treerecyclerview.adpater.TreeRecyclerAdapter;
import com.baozi.treerecyclerview.adpater.TreeRecyclerType;
import com.baozi.treerecyclerview.factory.ItemHelperFactory;
import com.baozi.treerecyclerview.item.TreeItem;
import com.google.gson.Gson;
import com.xunda.mo.R;
import com.xunda.mo.hx.section.search.SearchFriendsActivity;
import com.xunda.mo.model.Friend_MyGroupBean;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.SetStatusBar;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyGroup extends AppCompatActivity {

    private RecyclerView group_Rv;
    private TreeRecyclerAdapter treeRecyclerAdapter;


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MyGroup.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);

        SetStatusBar.StatusBar(this);
        SetStatusBar.MIUISetStatusBarLightMode(this.getWindow(), true);
        SetStatusBar.FlymeSetStatusBarLightMode(this.getWindow(), true);

        initTitle();
        initView();
        initData();
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        title_Include.setElevation(2f);//阴影
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("群聊");
        Button right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.GONE);

        return_Btn.setOnClickListener(new return_Btn());
    }

    private class return_Btn implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            finish();
        }
    }

    private void initView() {

        group_Rv = findViewById(R.id.group_Rv);
        group_Rv.setLayoutManager(new LinearLayoutManager(this));
        group_Rv.setItemAnimator(new DefaultItemAnimator());
        //可折叠
        treeRecyclerAdapter = new TreeRecyclerAdapter(TreeRecyclerType.SHOW_EXPAND);
        group_Rv.setAdapter(treeRecyclerAdapter);
        findViewById(R.id.ll_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchFriendsActivity.actionStart(MyGroup.this);
            }
        });


    }


    private void initData() {
        groupData(MyGroup.this,  saveFile.Group_MyGroupList_Url);
    }

    //
    Friend_MyGroupBean model;
    public void groupData(final Context context, String baseUrl) {
        Map<String,Object> map = new HashMap<>();
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                model = new Gson().fromJson(result, Friend_MyGroupBean.class);
                //创建item
                //新的
                List<TreeItem> items = ItemHelperFactory.createItems(model.getData());
                //添加到adapter
                treeRecyclerAdapter.getItemManager().replaceAllItem(items);
            }
            @Override
            public void failed(String... args) {

            }
        });
    }




}