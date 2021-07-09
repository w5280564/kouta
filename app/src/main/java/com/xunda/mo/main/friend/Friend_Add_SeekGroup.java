package com.xunda.mo.main.friend;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.xunda.mo.R;
import com.xunda.mo.main.MainLogin_Register;
import com.xunda.mo.model.AddFriend_UserList_Model;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import static com.xunda.mo.network.saveFile.getShareData;
import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

public class Friend_Add_SeekGroup extends AppCompatActivity {

    private View cancel_txt;
    private EditText seek_edit;
    private LinearLayout friend_lin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityfriend_addseek_group);

        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);

        initView();
    }

    private void initData(int type) {
        String searchStr = seek_edit.getText().toString().trim();
        AddFriendMethod(Friend_Add_SeekGroup.this, saveFile.BaseUrl + saveFile.User_SearchAll_Url + "?search=" + searchStr);

//        AddFriendMethod(Friend_Add_SeekGroup.this, saveFile.BaseUrl + saveFile.Group_SearchGroup_Url
//                + "?search=" + searchStr + "&type=" + type + "&pageNum=" + 1 + "&pageSize=" + 10);
    }

    private void initView() {
        cancel_txt = findViewById(R.id.cancel_txt);
        seek_edit = findViewById(R.id.seek_edit);
        friend_lin = findViewById(R.id.friend_lin);

        cancel_txt.setOnClickListener(new cancel_txtOnclickLister());
        seek_edit.setOnEditorActionListener(new SeekOnEditorListener());
    }

    private class cancel_txtOnclickLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private AddFriend_UserList_Model model;

    //
    public void AddFriendMethod(Context context, String baseUrl) {
        RequestParams params = new RequestParams(baseUrl);
        if (getShareData("JSESSIONID", context) != null) {
            params.setHeader("Authorization", getShareData("JSESSIONID", context));
        }
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
                if (resultString != null) {
                    model = new Gson().fromJson(resultString, AddFriend_UserList_Model.class);
                    if (model.getCode() == -1 || model.getCode() == 500) {
                        Intent intent = new Intent(context, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (model.getCode() == 200) {

                        FriendList(Friend_Add_SeekGroup.this, friend_lin);

                    } else {
                        Toast.makeText(context, model.getMsg(), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {

            }
        });
    }

    private class SeekOnEditorListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {//打开软键盘
                imm.hideSoftInputFromWindow(seek_edit.getWindowToken(), 0);
            }
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                searchData();
                initData(1);
            }
            return false;
        }
    }


    public void FriendList(Context context, LinearLayout myFlex) {
        if (myFlex != null) {
            myFlex.removeAllViews();
        }
        int size = model.getData().size();
        for (int i = 0; i < size; i++) {
            View myView = LayoutInflater.from(context).inflate(R.layout.addseek_friendlist, null);
            LinearLayout list_lin = myView.findViewById(R.id.list_lin);
            TextView tag_txt = myView.findViewById(R.id.tag_txt);
            TextView more_txt = myView.findViewById(R.id.more_txt);
            viewTouchDelegate.expandViewTouchDelegate(more_txt, 50, 50, 50, 50);
            String UserType = model.getData().get(i).getUserType();
            if (UserType.equals("nikeNameUser")) {
                tag_txt.setText("联系人");
            } else if (UserType.equals("userNumUser")) {
                tag_txt.setText("LeId");
            } else if (UserType.equals("phoneNumUser")) {
                tag_txt.setText("手机号");
            } else if (UserType.equals("tagUser")) {
                tag_txt.setText("含有该标签的人");
            } else if (UserType.equals("nameGroup")) {
                tag_txt.setText("群聊");
            } else if (UserType.equals("tagGroup")) {
                tag_txt.setText("含有该标签的群聊");
            }
            FriendListDetail(context, list_lin, myView, i, model.getData().get(i));
//            questiontxt_List.add(questionTxt);
//            questionlin_List.add(choice_lin);
//            pk_img.setImageResource(badgeArr[i]);
            RadioGroup.LayoutParams itemParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
//            pk_Lin.setLayoutParams(itemParams);
            list_lin.setTag(i);
            more_txt.setTag(i);
            myFlex.addView(myView);


            more_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (Integer) v.getTag();
                    String typeStr = model.getData().get(tag).getUserType().substring(model.getData().get(tag).getUserType().length() - 4);
                    if (typeStr.equals("User")) {
                        Intent intent = new Intent(context, Friend_Add_seekPerson_FriendList.class);
                        intent.putExtra("type", model.getData().get(tag).getUserType());
                        intent.putExtra("seekStr", seek_edit.getText().toString());
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(context, Friend_Add_seekPerson_GruopList.class);
                        intent.putExtra("type", model.getData().get(tag).getUserType());
                        intent.putExtra("seekStr", seek_edit.getText().toString());
                        startActivity(intent);

                    }


                }
            });
        }
    }

    public void FriendListDetail(Context context, LinearLayout myFlex, View view, int tag, AddFriend_UserList_Model.DataDTO model) {
        if (myFlex != null) {
            myFlex.removeAllViews();
        }
        String typeStr = model.getUserType().substring(model.getUserType().length() - 4);
        int size = 0;
        if (typeStr.equals("User")) {
            size = model.getUserList().size();
//            if (model.getUserList().isEmpty()) {
                view.setVisibility(View.GONE);
//            }
            return;
        } else {
            size = model.getGroupList().size();
            if (model.getGroupList().isEmpty()) {
                view.setVisibility(View.GONE);
            }
        }
        for (int i = 0; i < size; i++) {
            View myView = LayoutInflater.from(context).inflate(R.layout.addseek_friendlist_detail, null);
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
            RelativeLayout.LayoutParams itemParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
//            int margisleft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
//            itemParams.setMargins(margisleft, 0, 0, 0);
            myView.setLayoutParams(itemParams);

            SimpleDraweeView head_simple = myView.findViewById(R.id.head_simple);
            TextView name = myView.findViewById(R.id.name);
            TextView vipType_txt = myView.findViewById(R.id.vipType_txt);
            TextView contentid_txt = myView.findViewById(R.id.contentid_txt);

            if (typeStr.equals("User")) {
                AddFriend_UserList_Model.DataDTO.UserListDTO userListDTO = model.getUserList().get(i);
                if (userListDTO.getHeadImg() != null) {
                    Uri imgUri = Uri.parse(userListDTO.getHeadImg());
                    head_simple.setImageURI(imgUri);
                }
                long strVip = userListDTO.getVipType();
                name.setText(userListDTO.getNikeName());
                contentid_txt.setText("Le ID: " + userListDTO.getUserNum());

                if (strVip == 0) {
                    vipType_txt.setVisibility(View.GONE);
                    name.setTextColor(getColor(R.color.blacktitle));
                    contentid_txt.setTextColor(getColor(R.color.blacktitle));
                } else if (strVip == 1) {
                    vipType_txt.setVisibility(View.VISIBLE);
                    name.setTextColor(getColor(R.color.yellow));
                    contentid_txt.setTextColor(getColor(R.color.yellow));
                }

            } else {
                AddFriend_UserList_Model.DataDTO.GroupListDTO groupListDTO = model.getGroupList().get(i);
                if (groupListDTO.getGroupHeadImg() != null) {
                    Uri imgUri = Uri.parse(groupListDTO.getGroupHeadImg());
                    head_simple.setImageURI(imgUri);
                }
                name.setText(groupListDTO.getGroupName());
                contentid_txt.setText("ID: " + groupListDTO.getGroupNum());
            }

            myView.setTag(i);
            myFlex.addView(myView);

            myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (Integer) v.getTag();
                    if (typeStr.equals("User")) {
                        Toast.makeText(context, "用户Le ID "+model.getUserList().get(tag).getUserNum(),Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "群 ID "+model.getGroupList().get(tag).getGroupNum(),Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }
    }


}