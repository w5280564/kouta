package com.xunda.mo.main.me.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.xunda.mo.R;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.section.base.BaseInitFragment;
import com.xunda.mo.main.discover.adapter.Card_Adapter;
import com.xunda.mo.main.me.activity.Me_VIP;
import com.xunda.mo.model.CardFragment_Bean;
import com.xunda.mo.model.baseDataModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.HashMap;
import java.util.Map;

public class CardFragment extends BaseInitFragment {
    String type;
    String status,isVip;
    private RecyclerView card_Recycler;

    public static CardFragment newInstance(String type, String status,String isVip) {
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putString("status", status);
        args.putString("isVip", isVip);
        CardFragment fragment = new CardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab;
    }

    @Override
    protected void initArgument() {
        super.initArgument();
        type = getArguments().getString("type");
        status = getArguments().getString("status");
        isVip = getArguments().getString("isVip");
        if (TextUtils.equals(type, "我的经验券")) {
            type = "3";
        } else if (TextUtils.equals(type, "我的折扣券")) {
            type = "1";
        } else if (TextUtils.equals(type, "我的体验券")) {
            type = "2";
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        card_Recycler = getView().findViewById(R.id.card_Recycler);
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    public void onResume() {
        super.onResume();
        cardData(mContext, saveFile.UserCard_CardList);
    }

    CardFragment_Bean cardListModel;

    @SuppressLint("SetTextI18n")
    public void cardData(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("status", status);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                cardListModel = new Gson().fromJson(result, CardFragment_Bean.class);
                initlist(context);
            }
            @Override
            public void failed(String... args) {
            }
        });
    }

    Card_Adapter mAdapter;
    public void initlist(final Context context) {
        LinearLayoutManager mMangaer = new LinearLayoutManager(context);
        card_Recycler.setLayoutManager(mMangaer);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        card_Recycler.setHasFixedSize(true);
        mAdapter = new Card_Adapter(context, cardListModel.getData());
        card_Recycler.setAdapter(mAdapter);
        mAdapter.setOnItemAddRemoveClickLister((view, position) -> {
            if (TextUtils.equals(type, "1")) {//1是折扣券
                if (TextUtils.equals(isVip,"true")){
                    CardFragment_Bean.DataDTO dataDTO =   cardListModel.getData().get(position);
                    LiveDataBus.get().with("discountCard").postValue(dataDTO);
                    requireActivity().finish();
                }else {
                Me_VIP.actionStart(mContext);
                }
            }else {
                String cardId = cardListModel.getData().get(position).getUserCardId();
                useCardData(context, saveFile.UserCard_Use, cardId);
                mAdapter.removeData(position);//先使用再删除
            }

        });
    }

    private void useCardData(Context context, String baseUrl, String userCardId) {
        Map<String, Object> map = new HashMap<>();
        map.put("userCardId",userCardId);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                baseDataModel model = new Gson().fromJson(result, baseDataModel.class);
                Toast.makeText(context,model.getData(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failed(String... args) {
            }
        });
    }


}
