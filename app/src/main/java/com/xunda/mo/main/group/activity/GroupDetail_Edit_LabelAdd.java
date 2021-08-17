package com.xunda.mo.main.group.activity;

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
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.model.baseDataModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupDetail_Edit_LabelAdd extends BaseInitActivity {

    private LinearLayout label_Lin;
    private TextView Label_Add;
    List<String> tagS = new ArrayList<>();
    private EditText query;

    public static void actionStart(Context context, int Identity, GruopInfo_Bean groupModel) {
        Intent intent = new Intent(context, GroupDetail_Edit_LabelAdd.class);
        intent.putExtra("Identity", Identity);
        intent.putExtra("groupModel", (Serializable) groupModel);
        context.startActivity(intent);
    }

    private int Identity;
    private GruopInfo_Bean groupModel;

    @Override
    protected int getLayoutId() {
        return R.layout.groupdetail_edit_labeladd;

    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupModel = (GruopInfo_Bean) intent.getSerializableExtra("groupModel");
        Identity = intent.getIntExtra("Identity", 5);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();
        Label_Add = findViewById(R.id.Label_Add);
        query = findViewById(R.id.query);
        Label_Add.setOnClickListener(new Label_AddClick());
        label_Lin = findViewById(R.id.label_Lin);
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(GroupDetail_Edit_LabelAdd.this, R.color.white));
        title_Include.setElevation(2f);//阴影
        Button return_Btn = title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("编辑群标签");
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

        labelFlow(label_Lin, GroupDetail_Edit_LabelAdd.this, groupModel.getData().getTag());
    }

    private class Label_AddClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (tagS.size() >= 3) {
                Toast.makeText(GroupDetail_Edit_LabelAdd.this, "标签只能有三个", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(query.getText().toString())) {
                Toast.makeText(GroupDetail_Edit_LabelAdd.this, "请输入新标签", Toast.LENGTH_SHORT).show();
                return;
            }
            tagS.add(query.getText().toString());
            String str = StringUtils.join(tagS, ",");
            labelFlow(label_Lin, GroupDetail_Edit_LabelAdd.this, str);
            changeGroupMethod(GroupDetail_Edit_LabelAdd.this, saveFile.Group_UpdateInfo_Url);
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
                for (int k = 0; k < myFlow.getChildCount(); k++) {
                    if (view == myFlow.getChildAt(k)) {
                        myFlow.removeViewAt(k);
                        tagS.remove(k);
                        changeGroupMethod(GroupDetail_Edit_LabelAdd.this, saveFile.Group_UpdateInfo_Url);
                        break;
                    }
                }

            });

        }
    }

    /**
     * @param context
     * @param baseUrl
     */
    public void changeGroupMethod(Context context, String baseUrl) {
        String str = StringUtils.join(tagS, ",");
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupModel.getData().getGroupId());
        map.put("tag", str);
        xUtils3Http.post(mContext, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                baseDataModel baseModel = new Gson().fromJson(result, baseDataModel.class);
                Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                LiveDataBus.get().with(MyConstant.MY_GROUP_LABEL).setValue(str);
            }
            @Override
            public void failed(String... args) {
            }
        });
    }


}