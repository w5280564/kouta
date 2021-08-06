package com.xunda.mo.main;

import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.domain.EaseAvatarOptions;
import com.hyphenate.easeui.ui.base.EaseBaseFragment;
import com.xunda.mo.R;
import com.xunda.mo.hx.section.contact.fragment.ContactListFragment;
import com.xunda.mo.hx.section.contact.viewmodels.ContactsViewModel;
import com.xunda.mo.hx.section.conversation.ConversationListFragment;
import com.xunda.mo.staticdata.RsaEncodeMethod;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ViewPager tab_viewpager;
    private List<Fragment> fragments;
    private TabLayout ac_tab_layout;
    private EMGroup mDBData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);

        //设置头像配置属性
        EaseAvatarOptions avatarOptions = new EaseAvatarOptions();
        //设置头像形状为圆形，1代表圆形，2代表方形
        avatarOptions.setAvatarShape(2);
        EaseIM.getInstance().setAvatarOptions(avatarOptions);

//        if (saveFile.getShareData("phoneNum", MainActivity.this).equals("false")) {
//            Intent intent = new Intent(MainActivity.this, MainLogin_Register.class);
//            startActivity(intent);
//            finish();
//        } else {
//            Intent intent = new Intent(MainActivity.this, MainLogin_OldUsers.class);
//            startActivity(intent);
//            finish();
//        }

        RsaEncodeMethod.rsaEncode("测试");

        initView();
//        initData();
        initViewModel();
        startLocation();
    }

    private void initView() {
        tab_viewpager = findViewById(R.id.tab_viewpager);
        ac_tab_layout = findViewById(R.id.ac_tab_layout);
        ac_tab_layout.setSelectedTabIndicatorHeight(0);//去掉下导航条
        fragments = new ArrayList<>();
        EaseBaseFragment mConversationListFragment = new ConversationListFragment();
//        EaseBaseFragment mConversationListFragment = new EaseConversationListFragment();
        fragments.add(mConversationListFragment);

//        EaseBaseFragment mContactListFragment = new EaseContactListFragment();
//        fragments.add(mContactListFragment);

        ContactListFragment mFriendsFragment = new ContactListFragment();
        fragments.add(mFriendsFragment);
        //use EaseChatFratFragment
//       EaseChatFragment chatFragment = new EaseChatFragment();
//        //pass parameters to chat fragment
//        chatFragment.setArguments(getIntent().getExtras());
//        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();

//        fragments.add(Fragment_chat.newInstance("首页", ""));
//        fragments.add(Fragment_adress.newInstance("联系人", ""));
        fragments.add(Fragment_Person.newInstance("我", ""));
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

    private void initViewModel() {
        //加载联系人
        ContactsViewModel contactsViewModel = new ViewModelProvider(this).get(ContactsViewModel.class);
        contactsViewModel.loadContactList(true);
    }



    public void startLocation() {
        //监听授权
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.
                permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.
                permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.
                permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
//            initView(savedInstanceState);
            // requestLocation();
        }
    }


}