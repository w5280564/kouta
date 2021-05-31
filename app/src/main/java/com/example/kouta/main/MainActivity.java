package com.example.kouta.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kouta.R;
import com.example.kouta.network.saveFile;
import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ViewPager tab_viewpager;
    private List<Fragment> fragments;
    private TabLayout ac_tab_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 系统 6.0 以上 状态栏白底黑字的实现方法 style中修改背景色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);

//        if (saveFile.getShareData("phoneNum", MainActivity.this).equals("false")) {
//            Intent intent = new Intent(MainActivity.this, MainLogin_Register.class);
//            startActivity(intent);
//            finish();
//        } else {
//            Intent intent = new Intent(MainActivity.this, MainLogin_OldUsers.class);
//            startActivity(intent);
//            finish();
//        }


        initView();
    }

    private void initView() {
        tab_viewpager = findViewById(R.id.tab_viewpager);
        ac_tab_layout = findViewById(R.id.ac_tab_layout);
        ac_tab_layout.setSelectedTabIndicatorHeight(0);//去掉下导航条
//        ac_tab_layout.setHorizontalScrollBarEnabled(false);
        fragments = new ArrayList<>();
        fragments.add(Fragment_chat.newInstance("首页", ""));
        fragments.add(Fragment_adress.newInstance("联系人", ""));
        fragments.add(Fragment_Person.newInstance("我", ""));
//        fragments.add(Fragment_chat.newInstance("首页", ""));
        String[] tab_titles = new String[]{"聊天", "联系人", "我"};
        int[] tab_imgs = new int[]{R.drawable.tab_chat_selector, R.drawable.tab_adress_selector, R.drawable.tab_my_selector};
        setTabs(tab_titles, tab_imgs);
        //设置viewpager的adapter
        tab_viewpager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), fragments));
        //TabLayout与ViewPager的绑定
//        ac_tab_layout.setupWithViewPager(tab_viewpager);
        tab_viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(ac_tab_layout));
        ac_tab_layout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(tab_viewpager));
        tab_viewpager.setCurrentItem(0);

//        tab_viewpager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                return true;//true:消费事件
//            }
//        });
    }


    /**
     * @param tab_titles tab条目名字
     * @param tab_imgs   tab上条目上的图片
     * @description: 设置添加Tab
     */
    private void setTabs(String[] tab_titles, int[] tab_imgs) {
        for (int i = 0; i < tab_titles.length; i++) {
            //获取TabLayout的tab
            TabLayout.Tab tab = ac_tab_layout.newTab();
            //初始化条目布局view
            View view = getLayoutInflater().inflate(R.layout.tab_item, null);
            tab.setCustomView(view);
            //tab的文字
            TextView tvTitle = view.findViewById(R.id.tv_des);
            tvTitle.setText(tab_titles[i]);
            //tab的图片
            ImageView imgTab = view.findViewById(R.id.iv_top);
            imgTab.setImageResource(tab_imgs[i]);
            if (i == 0) {
                //设置第一个默认选中
                ac_tab_layout.addTab(tab, true);
            } else {
                ac_tab_layout.addTab(tab, false);
            }
        }
    }

    /**
     * Created by ruancw on 2018/5/28.
     * FragmentAdapter
     */

    public class FragmentAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragments;

        public FragmentAdapter(FragmentManager fragmentManager, List<Fragment> mFragments) {
            super(fragmentManager);
            this.mFragments = mFragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

    }


    public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }


    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }


}