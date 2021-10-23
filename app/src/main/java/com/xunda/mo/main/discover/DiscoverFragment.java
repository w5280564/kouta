package com.xunda.mo.main.discover;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ScrollView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitFragment;
import com.xunda.mo.main.baseView.MyArrowItemView;
import com.xunda.mo.main.discover.activity.Discover_Invite;
import com.xunda.mo.main.discover.activity.Discover_QRCode;
import com.xunda.mo.main.discover.activity.Discover_Welfare;
import com.xunda.mo.main.friend.activity.Friend_Add;
import com.xunda.mo.staticdata.NoDoubleClickListener;

import java.util.ArrayList;
import java.util.List;

public class DiscoverFragment extends BaseInitFragment {
    private MyArrowItemView item_search_set, item_scan_set, item_invite_set, item_welfare_set;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_discover;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        ScrollView my_Scroll = findViewById(R.id.my_Scroll);
//        OverScrollDecoratorHelper.setUpOverScroll(my_Scroll);
        item_welfare_set = findViewById(R.id.item_welfare_set);
        item_welfare_set.setOnClickListener(new item_welfare_setClick());
        item_search_set = findViewById(R.id.item_search_set);
        item_search_set.setOnClickListener(new item_search_setClick());
        item_invite_set = findViewById(R.id.item_invite_set);
        item_invite_set.setOnClickListener(new item_invite_setClick());
        item_scan_set = findViewById(R.id.item_scan_set);
        item_scan_set.setOnClickListener(new item_scan_setClick());
    }
//    private class item_welfare_setClick extends NoDoubleClickListener {
//        @Override
//        protected void onNoDoubleClick(View v) {
//            Discover_Welfare.actionStart(requireContext());
//        }
//    }

    private class item_welfare_setClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Discover_Welfare.actionStart(requireContext());
        }
    }

    private class item_search_setClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Intent intent = new Intent(mContext, Friend_Add.class);
            startActivity(intent);
        }
    }

    private class item_invite_setClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Discover_Invite.actionStart(requireContext());
        }
    }

    private class item_scan_setClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            startCamera(mContext);
        }
    }


    public void startCamera(Activity context) {
        //监听授权
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(context, permissions, 1);
        } else {
            //打开相机录制视频
            Intent captureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            //判断相机是否正常。
            if (captureIntent.resolveActivity(context.getPackageManager()) != null) {
                Discover_QRCode.actionStart(context);
            }

        }
    }

}
