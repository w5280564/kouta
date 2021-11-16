package com.xunda.mo.main.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.xunda.mo.R;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.baseView.FlowLayout;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;

public class MeDetail_Edit_Label extends BaseInitActivity {
    private FlowLayout label_Flow;
    private String tagString;

    public static void actionStart(Context context,  String tagString) {
        Intent intent = new Intent(context, MeDetail_Edit_Label.class);
        intent.putExtra("tagString", tagString);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.gropudetail_edit_label;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        tagString = intent.getStringExtra("tagString");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();

        label_Flow = findViewById(R.id.label_Flow);

        LiveDataBus.get().with(MyConstant.MY_LABEL, String.class).observe(this, s -> {
            tagString = s;
            labelFlow(label_Flow, MeDetail_Edit_Label.this, s);
        });
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(MeDetail_Edit_Label.this, R.color.white));
        title_Include.setElevation(2f);//阴影
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("个人标签");
        Button right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        viewTouchDelegate.expandViewTouchDelegate(right_Btn, 50, 50, 50, 50);
        right_Btn.setVisibility(View.VISIBLE);
        right_Btn.setText("编辑");
        return_Btn.setOnClickListener(new return_Btn());
        right_Btn.setOnClickListener(new right_BtnClick());

    }

    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }

    private class right_BtnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            MeDetail_Edit_LabelAdd.actionStart(mContext, tagString);
        }
    }
    @Override
    protected void initData() {
        super.initData();
        String tag = tagString;
        labelFlow(label_Flow, MeDetail_Edit_Label.this, tag);
    }

    public void labelFlow(FlowLayout myFlow, Context mContext, String tag) {
        if (myFlow != null) {
            myFlow.removeAllViews();
        }
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        String[] tagS = tag.split(",");
        for (int i = 0; i < tagS.length; i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.group_label, null);
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, mContext.getResources().getDisplayMetrics());
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, mContext.getResources().getDisplayMetrics());
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(width, height);
            int margins = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, mContext.getResources().getDisplayMetrics());
            int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, mContext.getResources().getDisplayMetrics());
            itemParams.setMargins(margins, marginTop, 0, 0);
            view.setLayoutParams(itemParams);
            TextView label_Name = view.findViewById(R.id.label_Name);
            label_Name.setText(tagS[i]);
            label_Name.setTag(i);
            myFlow.addView(view);
            label_Name.setOnClickListener(v -> {
            });
        }
    }


}