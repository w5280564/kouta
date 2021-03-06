package com.xunda.mo.main.login;

import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.xunda.mo.R;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.permission.PermissionManager;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.ArrayList;
import java.util.List;

public class MainLogin_Register extends AppCompatActivity {

    private Button login_Btn, regster_Btn;
    private final static int REQUEST_READ_PHONE_STATE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain_login_register);

        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);

        if (saveFile.getShareData("phoneNum", MainLogin_Register.this).equals("false")) {
//            Intent intent = new Intent(MainRegister.this, MainLogin_Register.class);
//            startActivity(intent);
//            finish();
        } else {
            Intent intent = new Intent(MainLogin_Register.this, MainLogin_OldUsers.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        initView();
        initData();

        if (getFirstInit()) {
            startDialog();
        }
    }

    private void initData() {

    }


    private void initView() {
        login_Btn = findViewById(R.id.login_Btn);
        regster_Btn = findViewById(R.id.regster_Btn);
        login_Btn.setOnClickListener(new login_Btnlister());
        regster_Btn.setOnClickListener(new regster_BtnOnClick());
    }


    private class login_Btnlister extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Intent intent = new Intent(MainLogin_Register.this, MainLogin_OldUser_Psd.class);
            startActivity(intent);
//            startLocation();
        }
    }

    private class regster_BtnOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Intent intent = new Intent(MainLogin_Register.this, MainRegister.class);
            startActivity(intent);
        }
    }

    private void startDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(MainLogin_Register.this).inflate(R.layout.dialog_initmate, null);
        alertDialog.setView(view);
        alertDialog.show();
        alertDialog.setCancelable(false);
        TextView tvContent = view.findViewById(R.id.tv_content);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        TextView tvAgree = view.findViewById(R.id.tv_agree);

        String str = "    ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????" +
                "??????????????????:??????????????????????????????????????????????????????,?????????????????????????????????????????????????????????????????????" +
                "???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????,???????????????????????????????????????????????????";

        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(str);
        final int start = str.indexOf("???");//????????????????????????
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
//                String  url =  "file:///android_asset/privacy.html";
                String url = xUtils3Http.BASE_URL + "service.html";
                MainRegister_Agreement.actionStart(MainLogin_Register.this, url);
//                Toast.makeText(MainLogin_Register.this, "??????????????????", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.blue));
                ds.setUnderlineText(false);
            }
        }, start, start + 6, 0);
        int end = str.lastIndexOf("???");
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
//                String  url =  "file:///android_asset/service.html";
                String url = xUtils3Http.BASE_URL + "privacy.html";
                MainRegister_Agreement.actionStart(MainLogin_Register.this, url);
//                Toast.makeText(MainLogin_Register.this, "??????????????????", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.blue));
                ds.setUnderlineText(false);
            }
        }, end, end + 6, 0);

        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        tvContent.setText(ssb, TextView.BufferType.SPANNABLE);
        tvCancel.setOnClickListener(v -> {
            alertDialog.cancel();
            finish();
        });

        tvAgree.setOnClickListener(v -> {
            saveFirstInit(false);
            alertDialog.cancel();
        });

    }

    public void saveFirstInit(boolean is) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("launch", is);
        editor.commit();
    }

    public boolean getFirstInit() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean("launch", true);
    }

    public void startLocation() {
        //????????????
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainLogin_Register.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {//???????????? ?????????????????????????????????????????????IMEI???IMSI?????????
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
//            ActivityCompat.requestPermissions(MainLogin_Register.this, permissions, REQUEST_READ_PHONE_STATE);
            String tip = "????????????????????????";
            PermissionManager.requestPermission(MainLogin_Register.this,tip,REQUEST_READ_PHONE_STATE,permissions);
        } else {
            startActivity();
        }
    }

    /*???????????????????????????*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_PHONE_STATE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                //????????????
                startActivity();
            } else {
                //2?????????????????? ????????????
//                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
//                    Toast.makeText(this, "????????????app??????????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
//                    startActivity(getAppDetailSettingIntent());
                    startActivity();
//                }
            }
        }
    }

    private void startActivity() {
        Intent intent = new Intent(MainLogin_Register.this, MainLogin_OldUser_Psd.class);
        startActivity(intent);
    }

    /**
     * ????????????????????????intent??????????????????????????????????????????????????????????????????????????????????????????
     * @return
     */
    private Intent getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        return localIntent;
    }




//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode ==MY_LBS_PERMISSION_REQUEST_CODE) {
//            boolean isAllGranted =true;
//            for (int grant : grantResults) {
//                if (grant != PackageManager.PERMISSION_GRANTED) {
//                    isAllGranted =false;
//                    break;
//                }
//            }
//            if (isAllGranted) {
//                Log.e("TAG", "onRequestPermissionsResult ??????");
//            }else {
//                List notAsk =new ArrayList<>();
//                for (String permission : permissions) {
//                    Log.e("TAG", "onRequestPermissionsResult - ?????? -");
//                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission) && ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//                        notAsk.add(permission);
//                    }
//                }
//                if (notAsk.size() >0) {//??????????????????
//                    Log.e("TAG", "onRequestPermissionsResult ??????????????????");
//                }else {
//                    Log.e("TAG", "onRequestPermissionsResult ????????????");
//                }
//            }
//        }
//    }


}