package com.xunda.mo.main.me.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.baseView.MyArrowItemView;
import com.xunda.mo.main.login.MainLogin_QuestionFeedBack;
import com.xunda.mo.main.login.MainRegister_Agreement;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.StaticData;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

public class Me_About extends BaseInitActivity {

    private TextView version_Txt, login_txt;
    private MyArrowItemView email_ArrowItemView;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Me_About.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_me_about;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();
        version_Txt = findViewById(R.id.version_Txt);
        MyArrowItemView official_Website_ArrowItemView = findViewById(R.id.official_Website_ArrowItemView);
        official_Website_ArrowItemView.setOnClickListener(new officaialClick());
        email_ArrowItemView = findViewById(R.id.email_ArrowItemView);
        email_ArrowItemView.setOnClickListener(new emailClick());
        login_txt = findViewById(R.id.login_txt);
       MyArrowItemView feedBook_ArrowItemView = findViewById(R.id.feedBook_ArrowItemView);
        feedBook_ArrowItemView.setOnClickListener(new feedBook_Click());
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setElevation(2f);//阴影
        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
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

    @Override
    protected void initData() {
        super.initData();
        String versionName = "v" + StaticData.getversionName(mContext);
        version_Txt.setText(versionName);
        setSe(login_txt);
    }

    private class officaialClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("https://www.ahxunda.com/");
            intent.setData(uri);
            startActivity(intent);
        }
    }

    private class emailClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            StaticData.copy(email_ArrowItemView.getTvContent().toString(), mContext);
            Toast.makeText(mContext, "已复制到剪切板", Toast.LENGTH_SHORT).show();
        }
    }

    private class feedBook_Click implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            MainLogin_QuestionFeedBack.actionStart(mContext,"2");
        }
    }

    private void setSe(TextView login_txt) {
        String str = "《用户注册协议》|《隐私政策》";
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(str);
        final int start = str.indexOf("《");//第一个出现的位置
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                String url = xUtils3Http.BASE_URL + "service.html";
                MainRegister_Agreement.actionStart(mContext, url);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.blue));
                ds.setUnderlineText(false);
            }
        }, start, start + 8, 0);
        int end = str.lastIndexOf("《");
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                String url = xUtils3Http.BASE_URL + "privacy.html";
                MainRegister_Agreement.actionStart(mContext, url);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.blue));
                ds.setUnderlineText(false);
            }
        }, end, end + 6, 0);
        login_txt.setMovementMethod(LinkMovementMethod.getInstance());
        login_txt.setText(ssb, TextView.BufferType.SPANNABLE);
    }



}