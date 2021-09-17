package com.xunda.mo.main.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.me.fragment.CardFragment;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;

import java.util.ArrayList;
import java.util.List;

public class Me_CouponCard extends BaseInitActivity {

    private TabLayout card_Tab;
    private ViewPager card_ViewPager;
    private List<CardFragment> tabFragmentList = new ArrayList<>();
    private TextView cententTxt;
    private String cardName,isVip;

    public static void actionStart(Context context, String cardName,String isVip) {
        Intent intent = new Intent(context, Me_CouponCard.class);
        intent.putExtra("cardName", cardName);
        intent.putExtra("isVip", isVip);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.me_coupon_card;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        card_Tab = findViewById(R.id.card_Tab);
        card_ViewPager = findViewById(R.id.card_ViewPager);
        initTitle();
        initTab();
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        cardName = intent.getStringExtra("cardName");
        isVip = intent.getStringExtra("isVip");
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setElevation(2f);//阴影
        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("");
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

    private void initTab() {
        List<String> tabsList = new ArrayList<>();
        tabsList.add("未使用");
        tabsList.add("已使用");
        String titleType = "";
        if (TextUtils.equals(cardName, "经验券")) {
            titleType = "我的经验券";
//            cententTxt.setText("我的经验券");
        } else if (TextUtils.equals(cardName, "折扣券")) {
            tabsList.add("已过期");
//            cententTxt.setText("我的折扣券");
            titleType = "我的折扣券";
        } else if (TextUtils.equals(cardName, "体验券")) {
            tabsList.add("已过期");
//            cententTxt.setText("我的体验券");
            titleType = "我的体验券";
        }
        cententTxt.setText(titleType);

        card_Tab.setTabMode(TabLayout.MODE_FIXED);
        card_Tab.setTabIndicatorFullWidth(false);//下标跟字一样宽
        card_Tab.setSelectedTabIndicatorColor(ContextCompat.getColor(Me_CouponCard.this, R.color.greytwo));
//        card_Tab.setTabTextColors(ContextCompat.getColor(Me_CouponCard.this,R.color.greytwo),ContextCompat.getColor(Me_CouponCard.this,R.color.yellowfive));
//        card_Tab.setSelectedTabIndicatorColor(ContextCompat.getColor(Me_CouponCard.this,R.color.yellowfive));
        //添加tab
        int sizes = tabsList.size();
        for (int i = 0; i < sizes; i++) {
            card_Tab.addTab(card_Tab.newTab().setText(tabsList.get(i)));
            tabFragmentList.add(CardFragment.newInstance(titleType, i + "",isVip));
        }

        card_ViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return tabFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return tabFragmentList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return tabsList.get(position);
            }
        });
        //设置TabLayout和ViewPager联动
        card_Tab.setupWithViewPager(card_ViewPager, false);
    }


}