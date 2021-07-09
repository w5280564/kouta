package com.xunda.mo.main.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;

public class UserDetail_Set extends BaseInitActivity {

    private TextView cententTxt;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, UserDetail_Set.class);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_userdetail_set;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();
    }


    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setElevation(2f);//阴影
        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("个人设置");
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


}