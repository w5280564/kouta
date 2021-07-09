package com.xunda.mo.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.xunda.mo.R;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;

import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

public class MainLogin_ForgetPsw_ID extends AppCompatActivity {

    private Button right_Btn;
    private EditText id_edit;
    private View fork_img;
    private View next_Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain_loginforget_psw_id);

        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);

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
        cententTxt.setText("忘记密码");
        right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.GONE);


        return_Btn.setOnClickListener(new return_Btn());
//        right_Btn.setOnClickListener(new right_Btn());
    }

    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }

    private void initView() {
        next_Btn = findViewById(R.id.next_Btn);
//        StaticData.changeShapColor(num_Btn, ContextCompat.getColor(this, R.color.grey));
        id_edit = findViewById(R.id.id_edit);
        fork_img = findViewById(R.id.fork_img);
        next_Btn.setOnClickListener(new next_BtnOnClick());
        fork_img.setOnClickListener(new fork_imgOnClick());
    }


    private class fork_imgOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            id_edit.setText("");
        }
    }

    private class next_BtnOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            if (id_edit.getText().toString().isEmpty()){
                Toast.makeText(MainLogin_ForgetPsw_ID.this, "请输入ID", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainLogin_ForgetPsw_ID.this, MainLogin_ForgetPsw.class);
            intent.putExtra("LoginID", id_edit.getText().toString());
            startActivity(intent);
        }
    }


}