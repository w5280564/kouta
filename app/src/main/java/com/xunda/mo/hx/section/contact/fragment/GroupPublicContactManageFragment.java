package com.xunda.mo.hx.section.contact.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupInfo;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.xunda.mo.R;
import com.xunda.mo.hx.common.net.Resource;
import com.xunda.mo.hx.section.base.BaseInitFragment;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.common.interfaceOrImplement.OnResourceParseCallback;
import com.xunda.mo.hx.section.chat.activicy.ChatActivity;
import com.xunda.mo.hx.section.contact.adapter.PublicGroupContactAdapter;
import com.xunda.mo.hx.section.contact.viewmodels.GroupContactViewModel;
import com.xunda.mo.hx.section.group.GroupHelper;
import com.xunda.mo.hx.section.group.activity.GroupSimpleDetailActivity;

import java.util.List;

public class GroupPublicContactManageFragment extends BaseInitFragment implements OnRefreshLoadMoreListener, OnItemClickListener {
    public SmartRefreshLayout srlRefresh;
    public RecyclerView rvList;
    public PublicGroupContactAdapter mAdapter;
    private int page_size = 20;
    private String cursor;
    private GroupContactViewModel viewModel;
    private List<EMGroup> allJoinGroups;

    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_group_public_contact_manage;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        srlRefresh = findViewById(R.id.srl_refresh);
        rvList = findViewById(R.id.rv_list);
    }

    @Override
    protected void initListener() {
        super.initListener();
        srlRefresh.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(mContext).get(GroupContactViewModel.class);
        viewModel.getPublicGroupObservable().observe(getViewLifecycleOwner(), new Observer<Resource<EMCursorResult<EMGroupInfo>>>() {
            @Override
            public void onChanged(Resource<EMCursorResult<EMGroupInfo>> emCursorResultResource) {
                parseResource(emCursorResultResource, new OnResourceParseCallback<EMCursorResult<EMGroupInfo>>() {
                    @Override
                    public void onSuccess(EMCursorResult<EMGroupInfo> data) {
                        List<EMGroupInfo> groups = data.getData();
                        cursor = data.getCursor();
                        mAdapter.setData(groups);
                    }

                    @Override
                    public void hideLoading() {
                        super.hideLoading();
                        if(srlRefresh != null) {
                            srlRefresh.finishRefresh();
                        }
                    }
                });
            }
        });

        viewModel.getMorePublicGroupObservable().observe(getViewLifecycleOwner(), new Observer<Resource<EMCursorResult<EMGroupInfo>>>() {
            @Override
            public void onChanged(Resource<EMCursorResult<EMGroupInfo>> emCursorResultResource) {
                parseResource(emCursorResultResource, new OnResourceParseCallback<EMCursorResult<EMGroupInfo>>() {
                    @Override
                    public void onSuccess(EMCursorResult<EMGroupInfo> data) {
                        cursor = data.getCursor();
                        List<EMGroupInfo> groups = data.getData();
                        mAdapter.addData(groups);
                    }

                    @Override
                    public void hideLoading() {
                        super.hideLoading();
                        if(srlRefresh != null) {
                            srlRefresh.finishLoadMore();
                        }
                    }
                });
            }
        });

        viewModel.getAllGroupsObservable().observe(getViewLifecycleOwner(), new Observer<Resource<List<EMGroup>>>() {
            @Override
            public void onChanged(Resource<List<EMGroup>> listResource) {
                parseResource(listResource, new OnResourceParseCallback<List<EMGroup>>() {
                    @Override
                    public void onSuccess(@Nullable List<EMGroup> data) {
                        allJoinGroups = data;
                        //????????????????????????????????????????????????
                        getData();
                    }

                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                        //?????????????????????????????????
                        getData();
                    }
                });
            }
        });

    }

    @Override
    protected void initData() {
        super.initData();
        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new PublicGroupContactAdapter();
        rvList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        //getData();
    }

    public void getData() {
        viewModel.getPublicGroups(page_size);
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        if(cursor != null) {
            viewModel.getMorePublicGroups(page_size, cursor);
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        getData();
    }

    @Override
    public void onItemClick(View view, int position) {
        EMGroupInfo item = mAdapter.getItem(position);
        if(GroupHelper.isJoinedGroup(allJoinGroups, item.getGroupId())) {
            ChatActivity.actionStart(mContext, item.getGroupId(), DemoConstant.CHATTYPE_GROUP);
        }else {
            GroupSimpleDetailActivity.actionStart(mContext, item.getGroupId());
        }
    }
}
