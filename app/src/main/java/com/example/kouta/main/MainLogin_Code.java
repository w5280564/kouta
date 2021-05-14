package com.example.kouta.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kouta.R;
import com.example.kouta.staticdata.NoDoubleClickListener;
import com.example.kouta.staticdata.TimerTextView;
import com.example.kouta.staticdata.viewTouchDelegate;

public class MainLogin_Code extends AppCompatActivity {

    private Button right_Btn;
    private TimerTextView timer_txt;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login_code);

        String LoginPhoneNume = getIntent().getStringExtra("LoginPhoneNume");
        initTitle();

        initView();
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
//        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.colorFristWhite));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            title_Include.setElevation(2f);//阴影
        }
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("手机号注册");
        right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.VISIBLE);
        right_Btn.setText("问题反馈");
//        StaticData.ViewScale(return_Btn, 100, 100);
//        StaticData.ViewScale(title_Include, 0, 88);

        return_Btn.setOnClickListener(new return_Btn());
//        right_Btn.setOnClickListener(new right_Btn());
    }

    private class return_Btn implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            finish();
        }
    }

    private void initView() {

        inputMethodManager = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
        timer_txt = findViewById(R.id.timer_txt);
        View capt_view = findViewById(R.id.capt_view);



        timer_txt.setOnClickListener(new TimerOnClickLister());
    }

    private class TimerOnClickLister extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            startTimer();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void startTimer() {
        if (this != null && timer_txt != null) {
            timer_txt.setBackground(null);
            timer_txt.setText("获取验证码");
            timer_txt.setTextColor(ContextCompat.getColor(MainLogin_Code.this,R.color.greytwo));
            timer_txt.startTimer(new TimerTextView.TimerListener() {
                @Override
                public void onFinish() {
                    timer_txt.setTextColor(ContextCompat.getColor(MainLogin_Code.this,R.color.yellowthree));
                    timer_txt.setText("重新获取验证码");
                }
            });
        }
    }

    /**
     * 判断软键盘 弹出
     */
    public void showSoftInput() {
        if (inputMethodManager.isActive()) {
            inputMethodManager.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
        }
    }

    /**
     * 关闭软键盘 *针对于 有一个EdtxtView * @param input_email
     */
    public void hideSoftInput(EditText input_email) {
        if (inputMethodManager.isActive()) {
            // 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
            inputMethodManager.hideSoftInputFromWindow(input_email.getWindowToken(), 0);
        }
    }


}
