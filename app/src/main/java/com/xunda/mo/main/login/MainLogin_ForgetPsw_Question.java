package com.xunda.mo.main.login;

import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.xunda.mo.R;
import com.xunda.mo.model.Security_QuestionList_Model;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainLogin_ForgetPsw_Question extends AppCompatActivity {
    private Button right_Btn;
    private LinearLayout question_lin;
    Boolean ischeck = true;
    private String phoneNumber;


    public static void actionStart(Context context,String phoneNumber) {
        Intent intent = new Intent(context, MainLogin_ForgetPsw_Question.class);
        intent.putExtra("phoneNumber",phoneNumber);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainlogin_forgetpsw_question);

        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);

        phoneNumber = getIntent().getStringExtra("phoneNumber");

        initTitle();
        initView();
        initData();
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
        cententTxt.setText("密保问题验证");
        right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.VISIBLE);
        right_Btn.setText("提交");
        right_Btn.setTextColor(ContextCompat.getColor(this, R.color.white));
        right_Btn.setBackgroundResource(R.drawable.loginbtn_radius);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 29, getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 54, getResources().getDisplayMetrics());
//        int margistop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        int margisright = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        layoutParams.setMargins(0, 0, margisright, 0);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        right_Btn.setLayoutParams(layoutParams);

        return_Btn.setOnClickListener(new MainLogin_ForgetPsw_Question.return_Btn());
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
            questionCheck(MainLogin_ForgetPsw_Question.this, saveFile.SecurityQuestion_Check);
        }
    }

    private void initView() {
        question_lin = findViewById(R.id.question_lin);
    }

    private void initData() {
        questionMethod(MainLogin_ForgetPsw_Question.this, saveFile.User_SecurityQuestionList_Url, "0");
    }

    Security_QuestionList_Model baseModel;
    //问题列表
    public void questionMethod(Context context, String baseUrl, String type) {
        Map<String, Object> map = new HashMap<>();
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
                Intent intent = new Intent(context, MainLogin_ForgetPsw_SetPsw.class);
                intent.putExtra("phoneNum",phoneNumber);
                startActivity(intent);
            }
            @Override
            public void failed(String... args) {
            }
        });
    }



    private String[] nameArr = {"", ""};
    ArrayList<TextView> questiontxt_List = new ArrayList<>();
    ArrayList<LinearLayout> questionlin_List = new ArrayList<>();
    ArrayList<EditText> answerTxt_List = new ArrayList<>();
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
            LinearLayout choice_lin = myView.findViewById(R.id.choice_lin);
            EditText phone_edit = myView.findViewById(R.id.phone_edit);
            ImageView arrow_img = myView.findViewById(R.id.arrow_img);
            arrow_img.setImageResource(R.mipmap.login_forgetpsw_down);
            questiontxt_List.add(questionTxt);
            questionlin_List.add(choice_lin);
            answerTxt_List.add(phone_edit);
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
                }else {
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
            //            projectTxt.setBackgroundResource(R.drawable.projectseek_comm_bg);
//            projectTxt.setBackgroundColor(ContextCompat.getColor(this, R.color.greyfour));
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
            projectTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (Integer) v.getTag();
                    choiceList[textTag] = false;
                    questiontxt_List.get(textTag).setText(model.getData().get(tag).getQuestion());
                    questionlin_List.get(textTag).removeAllViews();
                    arrow_img_List.get(textTag).setImageResource(R.mipmap.login_forgetpsw_down);
                }
            });
        }
    }


}