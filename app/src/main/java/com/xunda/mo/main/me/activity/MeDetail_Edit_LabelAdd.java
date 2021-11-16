package com.xunda.mo.main.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.xunda.mo.R;
import com.xunda.mo.dialog.TwoButtonDialog;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.model.baseDataModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeDetail_Edit_LabelAdd extends BaseInitActivity {

    private LinearLayout label_Lin;
    private TextView Label_Add;
    List<String> tagS = new ArrayList<>();
    private EditText query;
    private String tagString;

    public static void actionStart(Context context,  String tagString) {
        Intent intent = new Intent(context, MeDetail_Edit_LabelAdd.class);
        intent.putExtra("tagString", tagString);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.groupdetail_edit_labeladd;

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
        Label_Add = findViewById(R.id.Label_Add);
        query = findViewById(R.id.query);
        Label_Add.setOnClickListener(new Label_AddClick());
        label_Lin = findViewById(R.id.label_Lin);
        findViewById(R.id.search_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query.setText("");
            }
        });
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(MeDetail_Edit_LabelAdd.this, R.color.white));
        title_Include.setElevation(2f);//阴影
        Button return_Btn = title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("编辑个人标签");
        Button right_Btn = title_Include.findViewById(R.id.right_Btn);
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

        labelFlow(label_Lin, MeDetail_Edit_LabelAdd.this, tagString);
    }

    private class Label_AddClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (tagS.size() >= 3) {
                Toast.makeText(MeDetail_Edit_LabelAdd.this, "标签不能超过3个", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(query.getText().toString())) {
                Toast.makeText(MeDetail_Edit_LabelAdd.this, "请输入新标签", Toast.LENGTH_SHORT).show();
                return;
            }
            tagS.add(query.getText().toString());
            String str = StringUtils.join(tagS, ",");
            labelFlow(label_Lin, MeDetail_Edit_LabelAdd.this, str);
            String changType = "6";
            ChangeUserMethod(MeDetail_Edit_LabelAdd.this, saveFile.User_Update_Url,changType);
        }
    }


    //
    public void labelFlow(LinearLayout myFlow, Context mContext, String tagStr) {
        if (myFlow != null) {
            myFlow.removeAllViews();
            tagS.clear();
        }
        List<String> iconList = new ArrayList<>();
        if (!TextUtils.isEmpty(tagStr)) {
            iconList = Arrays.asList(tagStr.split(","));
        }
        tagS.addAll(iconList);
        for (int i = 0; i < tagS.size(); i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.group_label_add, null);
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, mContext.getResources().getDisplayMetrics());
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
            view.setLayoutParams(itemParams);
            ImageView remove_Img = view.findViewById(R.id.remove_Img);
            TextView label_Name = view.findViewById(R.id.label_Name);
            label_Name.setText(tagS.get(i));
            myFlow.addView(view);
            remove_Img.setOnClickListener(v -> {
                showToastDialog(myFlow,view);
            });

        }
    }

    /**
     * 提示dialog
     */
    private void showToastDialog(LinearLayout myFlow,View view ) {
        TwoButtonDialog dialog = new TwoButtonDialog(this, "您确定要删除该标签吗？", "取消", "确定",
                new TwoButtonDialog.ConfirmListener() {

                    @Override
                    public void onClickRight() {
                        for (int k = 0; k < myFlow.getChildCount(); k++) {
                            if (view == myFlow.getChildAt(k)) {
                                myFlow.removeViewAt(k);
                                tagS.remove(k);
                                String changType = "6";
                                ChangeUserMethod(MeDetail_Edit_LabelAdd.this, saveFile.User_Update_Url,changType);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onClickLeft() {

                    }
                });
        dialog.show();

    }



    /**
     * 修改用户信息
     *
     * @param context
     * @param baseUrl
     * @param changType 修改类型（1.头像 2.昵称 3.性别 4.生日 5.地区 6.个人标签 7.个性签名）
     */
    public void ChangeUserMethod(Context context, String baseUrl, String changType) {
        String str = StringUtils.join(tagS, ",");
        Map<String, Object> map = new HashMap<>();
        map.put("changType", changType);
        map.put("tag", str);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                query.setText("");
                LiveDataBus.get().with(MyConstant.MY_LABEL).setValue(str);

            }
            @Override
            public void failed(String... args) {
            }
        });
    }



}