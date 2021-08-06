package com.xunda.mo.main.login;

import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.xunda.mo.R;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.StaticData;
import com.xunda.mo.staticdata.viewTouchDelegate;

public class MainLogin_OldUsers extends AppCompatActivity {

    private Button num_Btn;
    private TextView psw_txt,phone_txt;
    private String LoginPhoneNume;
    private View more_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login_old_users);

        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);

//        LoginPhoneNume = getIntent().getStringExtra("LoginPhoneNume");
        LoginPhoneNume = saveFile.getShareData("phoneNum",MainLogin_OldUsers.this);
        initView();

    }

    //栈复用页面
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initView();
    }

    private void initView() {

        phone_txt = findViewById(R.id.phone_txt);
        num_Btn = findViewById(R.id.num_Btn);
        psw_txt = findViewById(R.id.psw_txt);
        more_txt = findViewById(R.id.more_txt);
        viewTouchDelegate.expandViewTouchDelegate(more_txt, 50, 50, 50, 50);
        StaticData.changeShapColor(num_Btn, ContextCompat.getColor(MainLogin_OldUsers.this, R.color.yellow));
        num_Btn.setOnClickListener(new num_BtnOnClick());
        psw_txt.setOnClickListener(new psw_txtOnClick());
        more_txt.setOnClickListener(new more_txtOnClick());

        phone_txt.setText("+86  "+ LoginPhoneNume);
    }

    private class num_BtnOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
//            if (!StaticData.isPhone(phone_edit.getText().toString())){
//                Toast.makeText(MainLogin_OldUser_Phone.this, "请输入正确手机号码", Toast.LENGTH_SHORT).show();
//                return;
//            }
            Intent intent = new Intent(MainLogin_OldUsers.this, MainLogin_Code.class);
            intent.putExtra("LoginPhoneNume", LoginPhoneNume);
            intent.putExtra("TitleName", "验证码登录");
            startActivity(intent);

        }
    }

    private class psw_txtOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Intent intent = new Intent(MainLogin_OldUsers.this, MainLogin_OldUser_Psd.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        }
    }

    //更多
    private void showMore(final Context mContext, final View view, final int pos) {
        View contentView = View.inflate(mContext, R.layout.popup_morechoice, null);
        PopupWindow MorePopup = new BasePopupWindow(mContext);
        MorePopup.setWidth(RadioGroup.LayoutParams.MATCH_PARENT);
        MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setTouchable(true);
        MorePopup.setContentView(contentView);
        MorePopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        TextView change_txt = contentView.findViewById(R.id.change_txt);
        TextView newregistr_txt = contentView.findViewById(R.id.newregistr_txt);
        TextView cancel_txt = contentView.findViewById(R.id.cancel_txt);
        change_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                Intent intent = new Intent(MainLogin_OldUsers.this, MainLogin_OldUser_Phone.class);
                startActivity(intent);
                MorePopup.dismiss();
            }
        });
        newregistr_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                Intent intent = new Intent(MainLogin_OldUsers.this, MainRegister.class);
                startActivity(intent);
                MorePopup.dismiss();
            }
        });
        cancel_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                MorePopup.dismiss();
            }
        });
//        contentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MorePopup.dismiss();
//            }
//        });
    }

    private class more_txtOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            showMore(MainLogin_OldUsers.this, v, 0);
        }
    }
}