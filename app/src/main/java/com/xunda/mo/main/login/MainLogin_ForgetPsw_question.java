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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.xunda.mo.R;
import com.xunda.mo.model.FoegetPsw_QuestionList_Model;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainLogin_ForgetPsw_question extends AppCompatActivity {
    private Button right_Btn;
    private LinearLayout question_lin;
    Boolean ischeck = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainlogin_forgetpsw_question);

        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);

        initTitle();
        initView();
        initData();
    }


    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
//        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.colorFristWhite));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
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

        return_Btn.setOnClickListener(new MainLogin_ForgetPsw_question.return_Btn());
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
            Intent intent = new Intent(MainLogin_ForgetPsw_question.this, MainLogin_ForgetPsw_SetPsw.class);
            startActivity(intent);
        }
    }

    private void initView() {
        question_lin = findViewById(R.id.question_lin);
//        num_Btn = findViewById(R.id.num_Btn);
//        nonecode_txt = findViewById(R.id.nonecode_txt);
//        StaticData.changeShapColor(num_Btn, ContextCompat.getColor(this, R.color.yellow));
//        num_Btn.setOnClickListener(new MainLogin_ForgetPsw.num_BtnOnClick());
//        nonecode_txt.setOnClickListener(new MainLogin_ForgetPsw.nonecode_txtOnClick());
        QuestionList(this, question_lin, 0);
    }

    private void initData() {
        questionMethod(MainLogin_ForgetPsw_question.this,saveFile.User_UserQuestionList_Url,"0");
    }

    //问题列表
    public void questionMethod(Context context,String baseUrl, String type) {
        Map<String,Object> map = new HashMap<>();
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                FoegetPsw_QuestionList_Model baseModel = new Gson().fromJson(result, FoegetPsw_QuestionList_Model.class);
            }
            @Override
            public void failed(String... args) {

            }
        });

    }


    //    private int[] badgeArr = {R.drawable.pk_report, R.drawable.pk_allday, R.drawable.pk_zan, R.drawable.pk_zanranking, R.drawable.pk_rule,R.drawable.pk_rete_icon};
    private String[] nameArr = {"姓名", "地址", "明星"};
    ArrayList<TextView> questiontxt_List = new ArrayList<>();
    ArrayList<LinearLayout> questionlin_List = new ArrayList<>();
    ArrayList<ImageView> arrow_img_List = new ArrayList<>();

    public void QuestionList(Context context, LinearLayout myFlex, int count) {
        if (myFlex != null) {
            myFlex.removeAllViews();
        }
        int size = nameArr.length;
        for (int i = 0; i < size; i++) {
            View myView = LayoutInflater.from(context).inflate(R.layout.mainlogin_forgetpsw_questionlist, null);
            RelativeLayout qusetion_rel = myView.findViewById(R.id.qusetion_rel);
            TextView questionTxt = myView.findViewById(R.id.questionone_txt);
            LinearLayout choice_lin = myView.findViewById(R.id.choice_lin);
            ImageView arrow_img = myView.findViewById(R.id.arrow_img);
            arrow_img.setImageResource(R.mipmap.login_forgetpsw_down);
            questionTxt.setText(nameArr[i]);
            questiontxt_List.add(questionTxt);
            questionlin_List.add(choice_lin);
            arrow_img_List.add(arrow_img);
//            pk_img.setImageResource(badgeArr[i]);
//            final pk_Model.DataBean oneData = model.getData().get(i);

            RadioGroup.LayoutParams itemParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
//            StaticData.layoutParamsScale(itemParams, 216, 94);
//            int pad = (int) (Float.parseFloat(saveFile.getShareData("scale", context)) * 15);
//            itemParams.setMargins(pad, pad, pad, pad);
//            pk_Lin.setLayoutParams(itemParams);
            qusetion_rel.setTag(i);

            myFlex.addView(myView);
            List<String> list = new ArrayList<>();
            list.add("你父亲的生日");
            list.add("你母亲的生日");
            list.add("你最喜欢的电影");
            list.add("你最喜欢的城市");
            list.add("你毕业于哪所大学");

            qusetion_rel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (Integer) v.getTag();
                    if (ischeck) {
                        ischeck = false;
//                        arrow_img_List.get(tag).setImageResource(R.mipmap.login_forgetpsw_up);
                        arrow_img.setImageResource(R.mipmap.login_forgetpsw_up);
                        qusetion_rel.invalidate();
//                        arrow_img.postInvalidate();
                        QuestionChoiceList(choice_lin, list, tag);
                    } else {
                        ischeck = true;
                        arrow_img.setImageResource(R.mipmap.login_forgetpsw_down);
                        choice_lin.removeAllViews();
                    }

                }
            });
        }
    }


    public void QuestionChoiceList(LinearLayout myFlex, List<String> model, int textTag) {
        if (myFlex != null) {
            myFlex.removeAllViews();
        }
        int size = model.size();
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

            projectTxt.setText(model.get(i).toString());

            projectTxt.setTag(i);
            mylin.addView(projectTxt);
            mylin.addView(myview);
            myFlex.addView(mylin);
            projectTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (Integer) v.getTag();
                    questiontxt_List.get(textTag).setText(model.get(tag));
                    questionlin_List.get(textTag).removeAllViews();
                    ischeck = true;
                    arrow_img_List.get(textTag).setImageResource(R.mipmap.login_forgetpsw_down);
//                    choice_lin.removeAllViews();
                }
            });
        }
    }


}