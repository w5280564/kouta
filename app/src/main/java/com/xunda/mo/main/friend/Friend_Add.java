package com.xunda.mo.main.friend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.xunda.mo.R;

import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

public class Friend_Add extends AppCompatActivity {

    private TextView add_left, add_right;
    private TextView seek_txt;
    private View seekPerson_InClue, seekGroup_InClue;
    private int tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_add);

        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);

        initView();
    }

    private void initView() {


        Button return_Btn = findViewById(R.id.return_Btn);
        add_left = findViewById(R.id.add_left);
        add_right = findViewById(R.id.add_right);
        seek_txt = findViewById(R.id.seek_txt);
        seekPerson_InClue = findViewById(R.id.seekperson_inclue);
        seekGroup_InClue = findViewById(R.id.seekgroup_inclue);
        View seek_lin = findViewById(R.id.seek_lin);

        tag = 0;
        changeView(0);
        add_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeView(0);
            }
        });
        add_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeView(1);
            }
        });

        return_Btn.setOnClickListener(new return_BtnonClickLister());
        seek_lin.setOnClickListener(new seek_linClickLister());
    }

    private class return_BtnonClickLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class seek_linClickLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (tag == 0) {
                Intent intent = new Intent(Friend_Add.this, Friend_Add_SeekFriend.class);
                startActivity(intent);
            } else if (tag == 1) {
                Intent intent = new Intent(Friend_Add.this, Friend_Add_SeekGroup.class);
                startActivity(intent);
            }
        }
    }

    private void changeView(int i) {
        if (i == 0) {
            //设置背景色及字体颜色
            tag = 0;
            add_left.setBackgroundResource(R.drawable.friend_add_left);
            add_left.setTextColor(getResources().getColor(R.color.white, null));
            add_right.setBackground(null);
            add_right.setTextColor(getResources().getColor(R.color.yellow, null));
            seek_txt.setText("LeID/昵称/手机号/标签/群");
            seekPerson_InClue.setVisibility(View.VISIBLE);
            seekGroup_InClue.setVisibility(View.GONE);
        } else if (i == 1) {
            tag = 1;
            add_right.setBackgroundResource(R.drawable.friend_add_right);
            add_right.setTextColor(getResources().getColor(R.color.white, null));
            add_left.setBackground(null);
            add_left.setTextColor(getResources().getColor(R.color.yellow, null));
            seek_txt.setText("群ID/群名称/群标签");
            seekPerson_InClue.setVisibility(View.GONE);
            seekGroup_InClue.setVisibility(View.VISIBLE);
        }
    }


}