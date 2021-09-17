package com.xunda.mo.main.discover.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.me.activity.Me_CouponCard;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;

public class Discover_Welfare_Card extends BaseInitActivity {

    private ConstraintLayout exp_Con, coupon_Con, attempt_Con;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Discover_Welfare_Card.class);
        context.startActivity(intent);
    }


    //    private ActivityMainBinding binding;
    @Override
    protected int getLayoutId() {
        return R.layout.discover_welfare_card;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();
        //关键代码
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        View rootView = binding.getRoot();
//        setContentView(rootView);
        exp_Con = findViewById(R.id.exp_Con);
        exp_Con.setOnClickListener(new exp_ConClick());
        coupon_Con = findViewById(R.id.coupon_Con);
        coupon_Con.setOnClickListener(new coupon_ConClick());
        attempt_Con = findViewById(R.id.attempt_Con);
        attempt_Con.setOnClickListener(new attempt_ConClick());
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setElevation(2f);//阴影
        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("我的优惠券");
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


    private class exp_ConClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Me_CouponCard.actionStart(mContext,"经验券","false");
        }
    }

    private class coupon_ConClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Me_CouponCard.actionStart(mContext,"折扣券","false");
        }
    }

    private class attempt_ConClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Me_CouponCard.actionStart(mContext,"体验券","false");
        }
    }


}