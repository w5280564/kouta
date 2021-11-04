package com.xunda.mo.main.me;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.gson.Gson;
import com.xunda.mo.R;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.section.base.BaseInitFragment;
import com.xunda.mo.hx.section.chat.activicy.ChatActivity;
import com.xunda.mo.hx.section.me.activity.SetIndexActivity;
import com.xunda.mo.main.baseView.MyArrowItemView;
import com.xunda.mo.main.discover.activity.Discover_Welfare_Card;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.main.me.activity.Me_About;
import com.xunda.mo.main.me.activity.Me_VIP;
import com.xunda.mo.main.me.activity.UserDetail_Set;
import com.xunda.mo.model.UserDetail_Bean;
import com.xunda.mo.model.baseDataModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.MyLevel;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.HashMap;
import java.util.Map;

public class MeFragment extends BaseInitFragment {
    private ConstraintLayout head_Constraint;
    private MyArrowItemView item_set, version_set, item_service_set, item_coupon_set, item_vip_set;
    private LinearLayout garde_Lin;
    private ImageView head_Image;
    private TextView nick_Txt, moId_Txt, vipType_txt;

    @Override
    protected int getLayoutId() {
        return R.layout.mefragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        head_Constraint = findViewById(R.id.head_Constraint);
        head_Constraint.setOnClickListener(new head_ConstraintClick());
        item_set = findViewById(R.id.item_set);
        item_set.setOnClickListener(new item_setClick());
        version_set = findViewById(R.id.version_set);
        version_set.setOnClickListener(new version_setClick());
        garde_Lin = findViewById(R.id.garde_Lin);
        head_Image = findViewById(R.id.head_Image);
        nick_Txt = findViewById(R.id.nick_Txt);
        moId_Txt = findViewById(R.id.moId_Txt);
        vipType_txt = findViewById(R.id.vipType_txt);
        item_service_set = findViewById(R.id.item_service_set);
        item_service_set.setOnClickListener(new item_service_setClick());
        item_coupon_set = findViewById(R.id.item_coupon_set);
        item_coupon_set.setOnClickListener(new item_coupon_setClick());
        item_vip_set = findViewById(R.id.item_vip_set);
        item_vip_set.setOnClickListener(new item_vip_setClick());
    }


    @Override
    protected void initData() {
        super.initData();
//        String versionName = "版本" + StaticData.getversionName(requireContext());
//        version_set.getTvContent().setText(versionName);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLevel.setGrade(garde_Lin, 0, requireContext());
        UserMethod(requireContext(), saveFile.User_GetUserInfo_Url);
    }

    private class head_ConstraintClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            UserDetail_Set.actionStart(requireContext());
        }
    }

    private class item_setClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            SetIndexActivity.actionStart(mContext);
        }
    }

    private class item_service_setClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            customerServiceData(getActivity(), saveFile.Receptionist_MyMoRoom);
        }
    }

    private class item_coupon_setClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Discover_Welfare_Card.actionStart(mContext);
        }
    }

    private class item_vip_setClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Me_VIP.actionStart(mContext);
        }
    }


    UserDetail_Bean userModel;

    @SuppressLint("SetTextI18n")
    public void UserMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                userModel = new Gson().fromJson(result, UserDetail_Bean.class);
                UserDetail_Bean.DataDTO dataDTO = userModel.getData();
                Glide.with(requireContext()).load(dataDTO.getHeadImg()).transforms(new CenterCrop(), new RoundedCorners(9)).into(head_Image);
                nick_Txt.setText(dataDTO.getNickname());
                String moIDStr = String.format("Mo ID:%s", dataDTO.getUserNum());
                moId_Txt.setText(moIDStr);

                isVipMethod(context, dataDTO.getVipType());

                MyInfo myInfo = new MyInfo(context);
                myInfo.updateData(userModel.getData());
            }

            @Override
            public void failed(String... args) {

            }
        });
    }

    private void isVipMethod(Context context, Integer vipType) {
        nick_Txt.setTextColor(ContextCompat.getColor(context, R.color.blacktitlettwo));
        moId_Txt.setTextColor(ContextCompat.getColor(context, R.color.blacktitlettwo));
        vipType_txt.setVisibility(View.GONE);
        if (vipType == 1) {
            nick_Txt.setTextColor(ContextCompat.getColor(context, R.color.yellowfive));
            moId_Txt.setTextColor(ContextCompat.getColor(context, R.color.yellowfive));
            vipType_txt.setVisibility(View.VISIBLE);
        }
    }

    //客服
    public void customerServiceData(final Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                baseDataModel model = new Gson().fromJson(result, baseDataModel.class);
                //跳转到群组聊天页面
                ChatActivity.actionStart(mContext, model.getData(), DemoConstant.CHATTYPE_GROUP);
            }

            @Override
            public void failed(String... args) {
            }
        });
    }

    //关于我们
    private class version_setClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Me_About.actionStart(mContext);
        }
    }
}
