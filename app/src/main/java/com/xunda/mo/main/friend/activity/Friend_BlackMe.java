package com.xunda.mo.main.friend.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.baseView.MyArrowItemView;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;

public class Friend_BlackMe extends BaseInitActivity {
    private MyArrowItemView blackMe_ArrowItemView;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Friend_BlackMe.class);
//        intent.putExtra("groupId", groupId);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_friend_blackme;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        blackMe_ArrowItemView = findViewById(R.id.blackMe_ArrowItemView);
        blackMe_ArrowItemView.setOnClickListener(new blackMe_ArrowItemViewClick());
        initTitle();
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(Friend_BlackMe.this, R.color.white));
        title_Include.setElevation(2f);//阴影
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("清理好友");
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

    private class blackMe_ArrowItemViewClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            MyInfo myInfo = new MyInfo(mContext);
            if (myInfo.getUserInfo().getVipType() == 1){
                Friend_BlackMeList.actionStart(mContext);
            }else {

            }
        }
    }
}