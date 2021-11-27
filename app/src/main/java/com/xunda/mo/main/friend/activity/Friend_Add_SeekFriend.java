package com.xunda.mo.main.friend.activity;

import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.xunda.mo.R;
import com.xunda.mo.main.chat.activity.ChatFriend_Detail;
import com.xunda.mo.model.AddFriend_UserList_Model;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;
import com.xunda.mo.view.LightningView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Friend_Add_SeekFriend extends AppCompatActivity {

    private View cancel_txt;
    private EditText seek_edit;
    private LinearLayout friend_lin;
    String searchStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityfriend_addseek_person);

        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);

        initView();
    }

    private void initData(int type) {
        searchStr = seek_edit.getText().toString().trim();
        AddFriendMethod(Friend_Add_SeekFriend.this, saveFile.User_SearchAll_Url);
    }

    private void initView() {
        cancel_txt = findViewById(R.id.cancel_txt);
        seek_edit = findViewById(R.id.seek_edit);
        friend_lin = findViewById(R.id.friend_lin);

        cancel_txt.setOnClickListener(new cancel_txtOnclickLister());
        seek_edit.setOnEditorActionListener(new SeekOnEditorListener());
        seek_edit.addTextChangedListener(new seek_editTextWatcher());
    }

    private class cancel_txtOnclickLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private AddFriend_UserList_Model model;

    public void AddFriendMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("search", searchStr);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                model = new Gson().fromJson(result, AddFriend_UserList_Model.class);
                FriendList(Friend_Add_SeekFriend.this, friend_lin, 0);
            }

            @Override
            public void failed(String... args) {

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
                initData(2);
            }
            return false;
        }
    }

    private class seek_editTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            initData(2);
        }
    }


    public void FriendList(Context context, LinearLayout myFlex, int count) {
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
            if (UserType.equals("nicknameUser")) {
                tag_txt.setText("联系人");
            } else if (UserType.equals("userNumUser")) {
                tag_txt.setText("Mo ID");
            } else if (UserType.equals("phoneNumUser")) {
                tag_txt.setText("手机号");
            } else if (UserType.equals("tagUser")) {
                tag_txt.setText("含有该标签的人");
            } else {
                myView.setVisibility(View.GONE);
            }
//            else if (UserType.equals("nameGroup")) {
//                tag_txt.setText("群聊");
//            } else if (UserType.equals("tagGroup")) {
//                tag_txt.setText("含有该标签的群聊");
//            }
            FriendListDetail(context, list_lin, myView, i, model.getData().get(i));
            list_lin.setTag(i);
            more_txt.setTag(i);
            myFlex.addView(myView);
            more_txt.setOnClickListener(v -> {
                int tag = (Integer) v.getTag();
                String typeStr = model.getData().get(tag).getUserType().substring(model.getData().get(tag).getUserType().length() - 4);
                if (typeStr.equals("User")) {
                    Intent intent = new Intent(context, Friend_Add_seekPerson_FriendList.class);
                    intent.putExtra("type", model.getData().get(tag).getUserType());
                    intent.putExtra("seekStr", seek_edit.getText().toString());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(context, Friend_Add_seekPerson_GruopList.class);
                    intent.putExtra("type", model.getData().get(tag).getUserType());
                    intent.putExtra("seekStr", seek_edit.getText().toString());
                    startActivity(intent);

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
            if (model.getUserList().isEmpty()) {
                view.setVisibility(View.GONE);
            }
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
            LightningView vipType_txt = myView.findViewById(R.id.vipType_txt);
            TextView contentid_txt = myView.findViewById(R.id.contentid_txt);

            if (typeStr.equals("User")) {
                AddFriend_UserList_Model.DataDTO.UserListDTO userListDTO = model.getUserList().get(i);
                if (userListDTO.getHeadImg() != null) {
                    Uri imgUri = Uri.parse(userListDTO.getHeadImg());
                    head_simple.setImageURI(imgUri);
                }
                long strVip = userListDTO.getVipType();
                name.setText(userListDTO.getNickname());
                contentid_txt.setText("Mo ID: " + userListDTO.getUserNum());
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

            myView.setOnClickListener(v -> {
                int tag1 = (Integer) v.getTag();
                if (typeStr.equals("User")) {
                    String friendUserId = model.getUserList().get(tag1).getUserId();
                    String addType = "2";
                    ChatFriend_Detail.actionStart(context, friendUserId, addType);
                } else {
                    String GroupId = model.getGroupList().get(tag1).getGroupId();
                    Friend_Group_Detail.actionStart(Friend_Add_SeekFriend.this, GroupId);
                }
            });
        }
    }


}