package com.xunda.mo.main.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.me.activity.AccountSecurityActivity;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.model.Security_QuestionList_Model;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Me_Set_SecurityQuestion extends BaseInitActivity {
    private Button right_Btn;
    private LinearLayout question_lin;
    Boolean ischeck = true;
    private String phoneNumber;
    MyInfo myInfo;

    public static void actionStart(Context context, String phoneNumber) {
        Intent intent = new Intent(context, Me_Set_SecurityQuestion.class);
        intent.putExtra("phoneNumber", phoneNumber);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_mainlogin_forgetpsw_question;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        phoneNumber = intent.getStringExtra("phoneNumber");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();
        question_lin = findViewById(R.id.question_lin);
    }

    @Override
    protected void initData() {
        super.initData();
        myInfo = new MyInfo(mContext);
        questionMethod(Me_Set_SecurityQuestion.this, saveFile.User_SecurityQuestionList_Url, "0");
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setElevation(2f);//阴影
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("设置密保问题");
        right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.VISIBLE);
        right_Btn.setText("提交");
        right_Btn.setTextColor(ContextCompat.getColor(this, R.color.blacktitle));

        return_Btn.setOnClickListener(new return_Btn());
        right_Btn.setOnClickListener(new right_BtnClickLister());
    }


    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }

    private class right_BtnClickLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            int isQuestion = myInfo.getUserInfo().getIsQuestion();
            String type = "0";
            if (isQuestion == 0) {
                type = "1";
            } else {
                type = "2";
            }
            questionAddOrUpdate(Me_Set_SecurityQuestion.this, saveFile.Question_AddOrUpdate, type);
        }
    }

    Security_QuestionList_Model baseModel;

    //问题列表
    public void questionMethod(Context context, String baseUrl, String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("phoneNum", phoneNumber);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                baseModel = new Gson().fromJson(result, Security_QuestionList_Model.class);
                QuestionList(context, question_lin, 0);
            }

            @Override
            public void failed(String... args) {
            }
        });
    }

    //问题上传与更新
    public void questionAddOrUpdate(Context context, String baseUrl, String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("questionOne", questiontxt_List.get(0).getText());
        map.put("questionTwo",  questiontxt_List.get(1).getText());
        map.put("questionThree",  questiontxt_List.get(2).getText());
        map.put("answerOne", answerTxt_List.get(0).getText());
        map.put("answerTwo", answerTxt_List.get(1).getText());
        map.put("answerThree", answerTxt_List.get(2).getText());
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Security_QuestionList_Model baseModel = new Gson().fromJson(result, Security_QuestionList_Model.class);
                if (TextUtils.equals(type, "1")) { //1是新增问题
                    myInfo.setOneIntData("isQuestion", 1);
                }
                Toast.makeText(context,"提交成功",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, AccountSecurityActivity.class);//回到账号设置页面
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            public void failed(String... args) {
            }
        });
    }


    private String[] nameArr = {"", "", ""};
    ArrayList<TextView> questiontxt_List = new ArrayList<>();
    ArrayList<EditText> answerTxt_List = new ArrayList<>();
    ArrayList<LinearLayout> questionlin_List = new ArrayList<>();
    ArrayList<ImageView> arrow_img_List = new ArrayList<>();
    private boolean[] choiceList = {false, false, false};

    public void QuestionList(Context context, LinearLayout myFlex, int count) {
        if (myFlex != null) {
            myFlex.removeAllViews();
        }
        int size = nameArr.length;
        for (int i = 0; i < size; i++) {
            View myView = LayoutInflater.from(context).inflate(R.layout.mainlogin_forgetpsw_questionlist, null);
            ConstraintLayout qusetion_Constraint = myView.findViewById(R.id.qusetion_Constraint);
            TextView questionTxt = myView.findViewById(R.id.questionone_txt);
            EditText phone_edit = myView.findViewById(R.id.phone_edit);
            LinearLayout choice_lin = myView.findViewById(R.id.choice_lin);
            ImageView arrow_img = myView.findViewById(R.id.arrow_img);
            arrow_img.setImageResource(R.mipmap.login_forgetpsw_down);
//            questionTxt.setText(nameArr[i]);
            questiontxt_List.add(questionTxt);
            answerTxt_List.add(phone_edit);
            questionlin_List.add(choice_lin);
            arrow_img_List.add(arrow_img);
            qusetion_Constraint.setTag(i);

            myFlex.addView(myView);
            qusetion_Constraint.setOnClickListener(v -> {
                int tag = (Integer) v.getTag();
                boolean onChoice = choiceList[tag];
                if (onChoice) {
                    choiceList[tag] = false;
                    arrow_img_List.get(tag).setImageResource(R.mipmap.login_forgetpsw_down);
                    choice_lin.removeAllViews();
                } else {
                    choiceList[tag] = true;
                    qusetion_Constraint.postInvalidate();
                    QuestionChoiceList(choice_lin, baseModel, tag);
                    arrow_img_List.get(tag).setImageResource(R.mipmap.login_forgetpsw_up);
                }
            });
        }
    }


    public void QuestionChoiceList(LinearLayout myFlex, Security_QuestionList_Model model, int textTag) {
        if (myFlex != null) {
            myFlex.removeAllViews();
        }
        int size = model.getData().size();
        for (int i = 0; i < size; i++) {
            LinearLayout mylin = new LinearLayout(this);
            mylin.setOrientation(LinearLayout.VERTICAL);
            mylin.setBackgroundColor(ContextCompat.getColor(this, R.color.greyfour));

            TextView projectTxt = new TextView(this);
            projectTxt.setTextColor(ContextCompat.getColor(this, R.color.blacktitlettwo));
            projectTxt.setGravity(Gravity.CENTER_VERTICAL);
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44, getResources().getDisplayMetrics());
            RelativeLayout.LayoutParams itemParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
            int margisleft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
            itemParams.setMargins(margisleft, 0, 0, 0);
            projectTxt.setLayoutParams(itemParams);

            View myview = new View(this);
            myview.setBackgroundColor(ContextCompat.getColor(this, R.color.greyfive));
            int viewheight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
            RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, viewheight);
            myview.setLayoutParams(viewParams);

            projectTxt.setText(model.getData().get(i).getQuestion());

            projectTxt.setTag(i);
            mylin.addView(projectTxt);
            mylin.addView(myview);
            myFlex.addView(mylin);
            projectTxt.setOnClickListener(v -> {
                int tag = (Integer) v.getTag();
                choiceList[textTag] = false;
                questiontxt_List.get(textTag).setText(model.getData().get(tag).getQuestion());
                questionlin_List.get(textTag).removeAllViews();
                ischeck = true;
                arrow_img_List.get(textTag).setImageResource(R.mipmap.login_forgetpsw_down);
//                    choice_lin.removeAllViews();
            });
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    v.clearFocus();
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

}