package com.xunda.mo.hx.section.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.main.baseView.MyArrowItemView;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.main.login.MainLogin_ForgetPsw_OrQuestion;
import com.xunda.mo.main.me.activity.Me_Set_PSW;
import com.xunda.mo.main.me.activity.Me_Set_SecurityQuestion;
import com.xunda.mo.model.Main_ForgetPsw_Model;
import com.xunda.mo.model.Security_QuestionList_Model;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AccountSecurityActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, View.OnClickListener {
    private EaseTitleBar titleBar;
    private MyArrowItemView item_phone, item_psw, item_question;
    private String LoginID;
    MyInfo myInfo;
    private LinearLayout question_lin;
    private String phoneNumber;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, AccountSecurityActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_account_security;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        item_phone = findViewById(R.id.item_phone);
        item_psw = findViewById(R.id.item_psw);
        item_question = findViewById(R.id.item_question);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        item_psw.setOnClickListener(this);
        item_question.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        myInfo = new MyInfo(mContext);
        int isQuestion = myInfo.getUserInfo().getIsQuestion();
        if (isQuestion == 1) {
            item_question.getTvContent().setText("已设置");
        }
        LoginID = myInfo.getUserInfo().getUserNum() + "";
        baseMethod(AccountSecurityActivity.this, saveFile.User_GetPhone_Url);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_phone:
                break;
            case R.id.item_psw:
                Me_Set_PSW.actionStart(mContext);
                break;
            case R.id.item_question:
                int isQuestion = myInfo.getUserInfo().getIsQuestion();
                phoneNumber = myInfo.getUserInfo().getPhoneNum();
                if (isQuestion == 1) {
                    questionMethod(mContext, saveFile.User_SecurityQuestionList_Url, "0");
                } else {
                    String titleName = "验证手机号";
                    String type = "2";
                    MainLogin_ForgetPsw_OrQuestion.actionStart(mContext, titleName, baseModel.getData(), LoginID,type);
                }
                break;
        }
    }


    Main_ForgetPsw_Model baseModel;

    public void baseMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("userNum", LoginID);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                baseModel = new Gson().fromJson(result, Main_ForgetPsw_Model.class);
//                id_txt.setText("为保护您的账号安全，请您输入完整的手机号码：\n" + baseModel.getData());
                item_phone.getTvContent().setText(baseModel.getData());
            }

            @Override
            public void failed(String... args) {

            }
        });
    }

    private void questionMore(Context mContext, final View view, final int pos) {
        View contentView = View.inflate(mContext, R.layout.question_popup, null);
        PopupWindow MorePopup = new BasePopupWindow(mContext);
        MorePopup.setWidth(RadioGroup.LayoutParams.MATCH_PARENT);
        MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setTouchable(true);
//        MorePopup.setBackgroundDrawable(new BitmapDrawable());
        MorePopup.setContentView(contentView);
        MorePopup.showAtLocation(view, Gravity.CENTER, 0, 0);
        LinearLayout question_lin = contentView.findViewById(R.id.question_lin);
        TextView goOn_Txt = contentView.findViewById(R.id.goOn_Txt);
        QuestionList(mContext, question_lin, 0);
        goOn_Txt.setOnClickListener(v -> {
            MorePopup.dismiss();
            questionCheck(mContext, saveFile.SecurityQuestion_Check);
        });
//        cancel_txt.setOnClickListener(v -> {
//            MorePopup.dismiss();
//        });


    }

    Security_QuestionList_Model questionModel;

    //问题列表
    public void questionMethod(Context context, String baseUrl, String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("phoneNum", phoneNumber);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                questionModel = new Gson().fromJson(result, Security_QuestionList_Model.class);
                questionMore(mContext, item_question, 0);
            }

            @Override
            public void failed(String... args) {
            }
        });
    }

    //问题检验
    public void questionCheck(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("phoneNum", phoneNumber);
        map.put("questionOne", questiontxt_List.get(0).getText());
        map.put("questionTwo",  questiontxt_List.get(1).getText());
        map.put("answerOne", answerTxt_List.get(0).getText());
        map.put("answerTwo", answerTxt_List.get(1).getText());
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Security_QuestionList_Model baseModel = new Gson().fromJson(result, Security_QuestionList_Model.class);
                Me_Set_SecurityQuestion.actionStart(mContext, phoneNumber);
            }

            @Override
            public void failed(String... args) {
            }
        });
    }



    private String[] nameArr = {"", ""};
    ArrayList<TextView> questiontxt_List = new ArrayList<>();
    ArrayList<EditText> answerTxt_List = new ArrayList<>();
    ArrayList<LinearLayout> questionlin_List = new ArrayList<>();
    ArrayList<ImageView> arrow_img_List = new ArrayList<>();
    private boolean[] choiceList = {false, false};

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
                    arrow_img_List.get(tag).setImageResource(R.mipmap.login_forgetpsw_up);
                    QuestionChoiceList(choice_lin,qusetion_Constraint,questionTxt, questionModel, tag);
                }
            });
        }
    }


    public void QuestionChoiceList(LinearLayout myFlex,ConstraintLayout qusetion_Constraint,TextView questionTxt, Security_QuestionList_Model model, int textTag) {
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
                arrow_img_List.get(textTag).setImageResource(R.mipmap.login_forgetpsw_down);
                questionTxt.setText(model.getData().get(tag).getQuestion());
                myFlex.removeAllViews();
            });
        }
    }




}
