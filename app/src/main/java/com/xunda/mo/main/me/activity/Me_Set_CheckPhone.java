package com.xunda.mo.main.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;

public class Me_Set_CheckPhone extends BaseInitActivity {

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Me_Set_CheckPhone.class);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.me_set_checkphone;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);


    }

}