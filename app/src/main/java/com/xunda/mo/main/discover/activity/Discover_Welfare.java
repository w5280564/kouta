package com.xunda.mo.main.discover.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.discover.adapter.Discover_Welfare_Adapter;
import com.xunda.mo.model.Discover_WelfareCardList_Bean;
import com.xunda.mo.model.Discover_Welfare_Bean;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.HashMap;
import java.util.Map;

public class Discover_Welfare extends BaseInitActivity implements View.OnClickListener {
    private LinearLayout monday_Lin, tuesday_Lin, wednesday_Lin, thursday_Lin, friday_Lin, saturday_Lin;
    private ImageView monday_Img, tuesday_Img, wednesday_Img, thursday_Img, friday_Img, saturday_Img, sunday_Img;
    private TextView monday_Txt, tuesday_Txt, wednesday_Txt, thursday_Txt, friday_Txt, saturday_Txt, sunday_Txt, sundayCount_Txt, integral_Txt;
    private ConstraintLayout sunday_Con;
    private RecyclerView card_Recycler;
    private TextView right_Name;
    private Button return_Btn;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Discover_Welfare.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_discover_welfare;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        return_Btn = findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setOnClickListener(new return_BtnClick());
        monday_Lin = findViewById(R.id.monday_Lin);
        monday_Img = findViewById(R.id.monday_Img);
        monday_Txt = findViewById(R.id.monday_Txt);
        tuesday_Lin = findViewById(R.id.tuesday_Lin);
        tuesday_Img = findViewById(R.id.tuesday_Img);
        tuesday_Txt = findViewById(R.id.tuesday_Txt);
        wednesday_Lin = findViewById(R.id.wednesday_Lin);
        wednesday_Img = findViewById(R.id.wednesday_Img);
        wednesday_Txt = findViewById(R.id.wednesday_Txt);
        thursday_Lin = findViewById(R.id.thursday_Lin);
        thursday_Img = findViewById(R.id.thursday_Img);
        thursday_Txt = findViewById(R.id.thursday_Txt);
        friday_Lin = findViewById(R.id.friday_Lin);
        friday_Img = findViewById(R.id.friday_Img);
        friday_Txt = findViewById(R.id.friday_Txt);
        saturday_Lin = findViewById(R.id.saturday_Lin);
        saturday_Img = findViewById(R.id.saturday_Img);
        saturday_Txt = findViewById(R.id.saturday_Txt);
        sunday_Con = findViewById(R.id.sunday_Con);
        sunday_Img = findViewById(R.id.sunday_Img);
        sunday_Txt = findViewById(R.id.sunday_Txt);
        sundayCount_Txt = findViewById(R.id.sundayCount_Txt);
        integral_Txt = findViewById(R.id.integral_Txt);
        card_Recycler = findViewById(R.id.card_Recycler);
        right_Name = findViewById(R.id.right_Name);
        right_Name.setOnClickListener(new right_NameClick());
    }

    @Override
    protected void initListener() {
        super.initListener();
        monday_Lin.setOnClickListener(this);
        tuesday_Lin.setOnClickListener(this);
        wednesday_Lin.setOnClickListener(this);
        thursday_Lin.setOnClickListener(this);
        friday_Lin.setOnClickListener(this);
        saturday_Lin.setOnClickListener(this);
        sunday_Con.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        changeData();
        cardData();
    }

    private void changeData() {
        welfareData(Discover_Welfare.this, saveFile.Sign_Log);
    }

    private void cardData() {
        AllExchangeCardData(Discover_Welfare.this, saveFile.ExchangeLog_AllExchangeCard);
    }

    private class return_BtnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class right_NameClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Discover_Welfare_Card.actionStart(mContext);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.monday_Lin:
            case R.id.tuesday_Lin:
            case R.id.wednesday_Lin:
            case R.id.thursday_Lin:
            case R.id.friday_Lin:
            case R.id.saturday_Lin:
            case R.id.sunday_Con:
                clickSignIn(v);
                break;
        }
    }

    private void clickSignIn(View view) {
        wefareSignIn(Discover_Welfare.this, saveFile.Sign_Do);
        view.setEnabled(false);
    }

    Discover_Welfare_Bean baseModel;
    @SuppressLint("SetTextI18n")
    public void welfareData(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                baseModel = new Gson().fromJson(result, Discover_Welfare_Bean.class);
                int isMonday = baseModel.getData().getIsMonday();
                isSignIn(monday_Lin, monday_Img, monday_Txt, isMonday, 0);
                int isTuesday = baseModel.getData().getIsTuesday();
                isSignIn(tuesday_Lin, tuesday_Img, tuesday_Txt, isTuesday, 1);
                int isWednesday = baseModel.getData().getIsWednesday();
                isSignIn(wednesday_Lin, wednesday_Img, wednesday_Txt, isWednesday, 2);
                int isThursday = baseModel.getData().getIsThursday();
                isSignIn(thursday_Lin, thursday_Img, thursday_Txt, isThursday, 3);
                int isIsFriday = baseModel.getData().getIsFriday();
                isSignIn(friday_Lin, friday_Img, friday_Txt, isIsFriday, 4);
                int isSaturday = baseModel.getData().getIsSaturday();
                isSignIn(saturday_Lin, saturday_Img, saturday_Txt, isSaturday, 5);
                int isSunday = baseModel.getData().getIsSunday();
                isSignIn(sunday_Con, sunday_Img, sunday_Txt, isSunday, 6);
            }

            @Override
            public void failed(String... args) {
            }
        });
    }

    Discover_WelfareCardList_Bean cardListModel;
    @SuppressLint("SetTextI18n")
    public void AllExchangeCardData(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
       map.put("systemVersion", "2");
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                 cardListModel = new Gson().fromJson(result, Discover_WelfareCardList_Bean.class);
                int integral = cardListModel.getData().getIntegral();
                integral_Txt.setText("我的积分:" + integral);
                initlist(context);
            }

            @Override
            public void failed(String... args) {
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void wefareSignIn(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Discover_Welfare_Bean  doModel = new Gson().fromJson(result, Discover_Welfare_Bean.class);
                integral_Txt.setText("我的积分:" + doModel.getData().getIntegral());
                Toast.makeText(context,doModel.getMsg(),Toast.LENGTH_SHORT).show();
                changeData();
            }

            @Override
            public void failed(String... args) {
            }
        });
    }


    Discover_Welfare_Adapter mAdapter;
    public void initlist(final Context context) {
        LinearLayoutManager mMangaer = new LinearLayoutManager(context);
        card_Recycler.setLayoutManager(mMangaer);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        card_Recycler.setHasFixedSize(true);
        mAdapter = new Discover_Welfare_Adapter(context, cardListModel.getData().getExchangeCardVos());
        card_Recycler.setAdapter(mAdapter);

        mAdapter.setOnItemAddRemoveClickLister((view, position) -> {
            String cardId = cardListModel.getData().getExchangeCardVos().get(position).getSystemCardId();
            exchangeData(context, saveFile.ExchangeLog_ExchangeCard,cardId);
        });
    }

    private void exchangeData(Context context, String baseUrl, String cardId) {
        Map<String, Object> map = new HashMap<>();
        map.put("systemCardId",cardId);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Toast.makeText(context,"已兑换",Toast.LENGTH_SHORT).show();
               cardData();
            }
            @Override
            public void failed(String... args) {
            }
        });
    }


    /**
     * @param monday_lin
     * @param monday_img
     * @param monday_txt
     * @param signIn     今天是否签到 0未签到 1签到
     * @param isWeekDay  今天是周几 0-6对应周一到周末
     */
    private void isSignIn(View monday_lin, ImageView monday_img, TextView monday_txt, int signIn, int isWeekDay) {
        int todaySign = baseModel.getData().getTodaySign();
        monday_lin.setEnabled(false);
        //今天之前的日期
        if (isWeekDay < todaySign) {
            dayBeforeSign(monday_lin, monday_img, monday_txt, signIn);
        } else if (isWeekDay == todaySign && todaySign < 6) {
            monday_txt.setText("今天");
            todaySign(monday_lin, monday_img, monday_txt, signIn);
            monday_lin.setEnabled(true);
        } else if (isWeekDay == 6) {
            monday_lin.setEnabled(true);
            sundaySign(monday_lin, monday_img, monday_txt, signIn);
        }
    }

    //今天之前的样式
    private void dayBeforeSign(View monday_lin, ImageView monday_img, TextView monday_txt, int signIn) {
        monday_lin.setBackgroundResource(R.drawable.welfare_radius_white);
        monday_img.setImageResource(R.mipmap.signin_no_icon);
        monday_txt.setTextColor(ContextCompat.getColor(this, R.color.greytwo));
        if (signIn == 1) {
            monday_lin.setBackgroundResource(R.drawable.welfare_radius_white);
            monday_img.setImageResource(R.mipmap.signin_success_icon);
            monday_txt.setTextColor(ContextCompat.getColor(this, R.color.yellowfive));
        }
    }

    //今天的样式
    private void todaySign(View monday_lin, ImageView monday_img, TextView monday_txt, int signIn) {
        monday_lin.setBackgroundResource(R.drawable.welfare_radius_yellow);
        monday_img.setImageResource(R.mipmap.signin_icon);
        monday_txt.setTextColor(ContextCompat.getColor(this, R.color.white));
        if (signIn == 1) {
            monday_lin.setBackgroundResource(R.drawable.welfare_radius_white);
            monday_img.setImageResource(R.mipmap.signin_success_icon);
            monday_txt.setTextColor(ContextCompat.getColor(this, R.color.yellowfive));
        }
    }

    //周末的样式
    private void sundaySign(View monday_lin, ImageView monday_img, TextView monday_txt, int signIn) {
        monday_lin.setBackgroundResource(R.drawable.welfare_radius_yellow);
        monday_img.setImageResource(R.mipmap.signin_gift_icon);
        monday_txt.setTextColor(ContextCompat.getColor(this, R.color.white));
        if (signIn == 1) {
            monday_lin.setBackgroundResource(R.drawable.welfare_radius_white);
            monday_img.setImageResource(R.mipmap.signin_success_icon);
            monday_txt.setTextColor(ContextCompat.getColor(this, R.color.yellowfive));
            monday_txt.setText("已签到");
            sundayCount_Txt.setTextColor(ContextCompat.getColor(this, R.color.yellowfive));
        }
        String countStr = "7天";
        int length = countStr.length() -1;
        setName(countStr,length,sundayCount_Txt);
    }

    /**
     * @param name       要显示的数据
     * @param nameLength 要放大改颜色的字体长度
     * @param viewName
     */
    private void setName(String name, int nameLength, TextView viewName) {
        SpannableString spannableString = new SpannableString(name);
        RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(2.2f);//字放大
        spannableString.setSpan(relativeSizeSpan, 0, nameLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        viewName.setText(spannableString);
    }



}