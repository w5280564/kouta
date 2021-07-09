package com.xunda.mo.hx.section.chat.activicy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easecallkit.base.EaseCallUserInfo;
import com.hyphenate.easecallkit.livedatas.EaseLiveDataBus;
import com.hyphenate.easecallkit.utils.EaseCallKitUtils;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.util.EMLog;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.common.interfaceOrImplement.OnResourceParseCallback;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.chat.model.KV;
import com.xunda.mo.hx.section.chat.viewmodel.ConferenceInviteViewModel;
import com.xunda.mo.hx.section.conference.ConferenceInviteActivity;
import com.xunda.mo.main.MainLogin_Register;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.model.adress_Model;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.pinyin.PinyinUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xunda.mo.network.saveFile.getShareData;


public class SelectUserCardActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener {
    private static final String TAG = SelectUserCardActivity.class.getSimpleName();
    private List<KV<String, Integer>> contacts = new ArrayList<>();
    private ContactsAdapter contactsAdapter;
    private EaseTitleBar mTitleBar;
    private ListView mListView;
    private TextView start_btn;
    private static String groupId;
    private String[] exist_member;
    private String toUser;
    private int selectIndex = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_conference_invite;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        toUser = intent.getStringExtra("toUser");
//        model = intent.getSerializableExtra("model");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar = findViewById(R.id.title_bar);
        contactsAdapter = new ContactsAdapter(mContext, contacts);
        contactsAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectIndex = position;
                contactsAdapter.notifyActual();
            }
        });

        mListView = findViewById(R.id.listView);
        mListView.setAdapter(contactsAdapter);
        start_btn = findViewById(R.id.btn_start);
        start_btn.setVisibility(View.GONE);

        addHeader();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mTitleBar.setOnBackPressListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        ConferenceInviteViewModel viewModel = new ViewModelProvider(this).get(ConferenceInviteViewModel.class);
        viewModel.getConferenceInvite().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<KV<String, Integer>>>() {
                @Override
                public void onSuccess(List<KV<String, Integer>> data) {
                    contacts = data;
                    contactsAdapter.setData(contacts);
                }
            });
        });

        LiveDataBus.get().with(DemoConstant.CONTACT_ADD, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                if (contactsAdapter != null) {
                    contactsAdapter.notifyDataSetChanged();
                }
            }
        });


        LiveDataBus.get().with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                if (contactsAdapter != null) {
                    contactsAdapter.notifyDataSetChanged();
                }
            }
        });
        LiveDataBus.get().with(DemoConstant.CONTACT_UPDATE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                if (contactsAdapter != null) {
                    contactsAdapter.notifyDataSetChanged();
                }
            }
        });
        viewModel.getConferenceMembers(groupId, exist_member);

        adressData(SelectUserCardActivity.this, saveFile.BaseUrl + saveFile.User_Friendlist_Url, "0");
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }


    private void addHeader() {
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.ease_search_bar, null);
        EditText query = headerView.findViewById(R.id.query);
        ImageView queryClear = headerView.findViewById(R.id.search_clear);
        query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contactsAdapter.filter(s);
                if (!TextUtils.isEmpty(s)) {
                    queryClear.setVisibility(View.VISIBLE);
                } else {
                    queryClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        queryClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideKeyboard();
            }
        });
        mListView.addHeaderView(headerView);
    }


    @Override
    public void onBackPress(View view) {
        onBackPressed();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 发送名片提示框
     */
    private void sendUserCardDisplay(String userId) {
        EMLog.i(TAG, " sendUserCardDisplay user:" + toUser);
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectUserCardActivity.this);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(SelectUserCardActivity.this, R.layout.demo_activity_send_user_card, null);
        Button send_btn = dialogView.findViewById(R.id.btn_send);
        Button cancel_btn = dialogView.findViewById(R.id.btn_cancel);

        TextView userNickView = dialogView.findViewById(R.id.user_nick_name);
        TextView user_moid_Txt = dialogView.findViewById(R.id.user_moid_Txt);
        TextView userIdView = dialogView.findViewById(R.id.userId_view);
        ImageView headView = dialogView.findViewById(R.id.head_view);
        EaseUser selectUser = DemoHelper.getInstance().getUserInfo(userId);

        if (selectUser != null) {
            userNickView.setText(selectUser.getNickname());
            Glide.with(mContext).load(selectUser.getAvatar()).placeholder(R.drawable.em_login_logo).into(headView);
        } else {
            userNickView.setText(selectUser.getUsername());
        }
        dialog.setView(dialogView);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER | Gravity.CENTER;
        dialog.show();

        EaseUser toEaseUser = DemoHelper.getInstance().getUserInfo(toUser);

        MyInfo myInfo = new MyInfo(this);
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CUSTOM);
//        JSONObject obj = new JSONObject();
        try {

//            obj.put(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TYPE_USERCARD);
//            obj.put(MyConstant.SEND_NAME, myInfo.getUserInfo().getNikeName());
//            obj.put(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
//            obj.put(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
//            obj.put(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
//            obj.put(MyConstant.TO_NAME, user.getUsername());
//            obj.put(MyConstant.TO_HEAD, user.getAvatar());
//            obj.put(MyConstant.TO_LH, extJsonObject.get("lightStatus"));
//            obj.put(MyConstant.TO_VIP, extJsonObject.get("vipType"));
//            message.setAttribute(MyConstant.EXT, obj);
            message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TYPE_USERCARD);
            message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNikeName());
            message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
            message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
            message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());

            String toExt = toEaseUser.getExt();
            JSONObject to_Ext_Json = new JSONObject(toExt);//用户资料扩展属性
            String toName = TextUtils.isEmpty(to_Ext_Json.getString("remarkName")) ? toEaseUser.getNickname() : to_Ext_Json.getString("remarkName");
            message.setAttribute(MyConstant.TO_NAME, toName);
            message.setAttribute(MyConstant.TO_HEAD, toEaseUser.getAvatar());
            message.setAttribute(MyConstant.TO_LH, to_Ext_Json.getString("lightStatus"));
            message.setAttribute(MyConstant.TO_VIP, to_Ext_Json.getString("vipType"));

            String selectInfoExt = selectUser.getExt();
            JSONObject select_Ext_JsonObject = new JSONObject(selectInfoExt);//用户资料扩展属性
            EMCustomMessageBody body = new EMCustomMessageBody(DemoConstant.USER_CARD_EVENT);
            Map<String, String> params = new HashMap<>();
//            params.put(DemoConstant.USER_CARD_ID, userId);
//            params.put(DemoConstant.USER_CARD_NICK, selectUser.getNickname());
//            params.put(DemoConstant.USER_CARD_AVATAR, selectUser.getAvatar());

            params.put(MyConstant.UID, userId);
            params.put(MyConstant.USER_ID, select_Ext_JsonObject.get("userId").toString());
            params.put(MyConstant.UNUM, select_Ext_JsonObject.get("userNum").toString());
            params.put(MyConstant.AVATAR, selectUser.getAvatar());
            params.put(MyConstant.NICK_NAME, selectUser.getNickname());
            params.put(MyConstant.HX_NAME, selectUser.getUsername());
            body.setParams(params);
            message.setBody(body);
            message.setTo(toUser);

            String MOID = select_Ext_JsonObject.get("userNum").toString();
//            userIdView.setText("[个人名片] " + MOID);
            user_moid_Txt.setText("Mo ID：" + MOID);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EMConversation conversation = EMClient.getInstance().chatManager().getConversation(userId, EMConversation.EMConversationType.Chat, true);
                message.setMessageStatusCallback(new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        EMLog.d(TAG, "sendCustomMsg user card success");
                        showToast("发送用户名片成功");
                        LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
                        dialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onError(int code, String error) {
                        EMLog.d(TAG, "sendCustomMsg user card failed code:" + code + "  errorMsg:" + error);
                        showToast("发送用户名片失败");
                        LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
                        dialog.dismiss();
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
                EMClient.getInstance().chatManager().sendMessage(message);
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }


    private class ContactsAdapter extends BaseAdapter {
        private Context context;
        private List<KV<String, Integer>> filteredContacts = new ArrayList<>();
        private List<KV<String, Integer>> contacts = new ArrayList<>();

        private ContactFilter mContactFilter;
        public ConferenceInviteActivity.ICheckItemChangeCallback checkItemChangeCallback;
        private OnItemClickListener mOnItemClickListener;


        public ContactsAdapter(Context context, List<KV<String, Integer>> contacts) {
            this.context = context;
            this.contacts = contacts;
            filteredContacts.addAll(contacts);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mOnItemClickListener = listener;
        }

        @Override
        public int getCount() {
            return filteredContacts == null ? 0 : filteredContacts.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredContacts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View contentView = convertView;
            ViewHolder viewHolder = null;
            if (contentView == null) {
                contentView = LayoutInflater.from(mContext).inflate(R.layout.demo_usercard_item, null);
                viewHolder = new ViewHolder(contentView, position);
                viewHolder.setmOnItemClickListener(mOnItemClickListener);
                contentView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) contentView.getTag();
                viewHolder.setmOnItemClickListener(mOnItemClickListener);
            }
            //viewHolder.reset();

            KV<String, Integer> contact = filteredContacts.get(position);
            String userName = contact.getFirst();
            EaseUserUtils.setUserAvatar(mContext, userName, viewHolder.headerImage);
            EaseUserUtils.setUserNick(userName, viewHolder.nameText);
            if (position == selectIndex) {
                sendUserCardDisplay(userName);
            }
            return contentView;
        }

        @Override
        public void notifyDataSetChanged() {
            filteredContacts.clear();
            filteredContacts.addAll(contacts);
            notifyActual();
        }

        private void notifyActual() {
            super.notifyDataSetChanged();
        }

        public void setData(List<KV<String, Integer>> data) {
            contacts = data;
            if (data != null) {
                this.filteredContacts.addAll(data);
            }
            notifyDataSetChanged();
        }


        void filter(CharSequence constraint) {
            if (mContactFilter == null) {
                mContactFilter = new ContactFilter(contacts);
            }

            mContactFilter.filter(constraint, new IFilterCallback() {
                @Override
                public void onFilter(List<KV<String, Integer>> filtered) {
                    filteredContacts.clear();
                    filteredContacts.addAll(filtered);
                    if (!filtered.isEmpty()) {
                        notifyActual();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            });
        }

        private class ViewHolder {
            View view;
            ImageView headerImage;
            TextView nameText;
            int position;
            private OnItemClickListener mOnItemClickListener;

            public ViewHolder(View view, int position) {
                this.view = view;
                this.position = position;
                headerImage = view.findViewById(R.id.head_icon);
                nameText = view.findViewById(R.id.name);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(v, position);
                        }
                    }
                });
            }

            public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
                this.mOnItemClickListener = mOnItemClickListener;
            }
        }

        private class ContactFilter extends Filter {
            private IFilterCallback mFilterCallback;
            private List<KV<String, Integer>> contacts;

            public ContactFilter(List<KV<String, Integer>> contacts) {
                this.contacts = contacts;
            }

            public void filter(CharSequence constraint, IFilterCallback callback) {
                this.mFilterCallback = callback;
                super.filter(constraint);
            }

            @Override
            protected FilterResults performFiltering(CharSequence prefix) {
                FilterResults results = new FilterResults();
                if (prefix == null || prefix.length() == 0) {
                    results.values = contacts;
                    results.count = contacts.size();
                } else {
                    String prefixString = prefix.toString();
                    int count = contacts.size();
                    List<KV<String, Integer>> newValues = new ArrayList<>();
                    for (int i = 0; i < count; i++) {
                        KV<String, Integer> user = contacts.get(i);
                        String username = user.getFirst();
                        if (username.startsWith(prefixString)) {
                            newValues.add(user);
                        } else {
                            String[] splits = username.split(" ");
                            if (splits.length == 0) {
                                continue;
                            }
                            List<String> words = new ArrayList<>();
                            for (int j = splits.length - 1; j >= 0; j--) {
                                if (!splits[j].isEmpty()) {
                                    words.add(splits[j]);
                                } else {
                                    break;
                                }
                            }
                            for (String word : words) {
                                if (word.startsWith(prefixString)) {
                                    newValues.add(user);
                                    break;
                                }
                            }
                        }
                    }
                    results.values = newValues;
                    results.count = newValues.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                List<KV<String, Integer>> result = results.values != null ? (List<KV<String, Integer>>) results.values : new ArrayList<>();
                if (mFilterCallback != null) {
                    mFilterCallback.onFilter(result);
                }
            }
        }

    }

    interface IFilterCallback {
        void onFilter(List<KV<String, Integer>> filtered);
    }

    public interface ICheckItemChangeCallback {
        void onCheckedItemChanged(View v, String username, int state);
    }

    //联系人列表
    public void adressData(final Context context, String baseUrl, String projectId) {
        RequestParams params = new RequestParams(baseUrl);
        if (getShareData("JSESSIONID", context) != null) {
            params.setHeader("Authorization", getShareData("JSESSIONID", context));
        }
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
//                resultString = "{msg:dclO,code:200, data:[{ciphertext:sSE3,fireType:436905937989336.8,headImg:X%kU,hxUserName:5CQo,isCiphertext:-7165580097951464, lightStatus:6140465580380565,remarkName:JCC39l,userId:gSlQQ,userNum:5762441269959617,vipType:-3782876530108332.5}]}";
                if (resultString != null) {
                    adress_Model model = new Gson().fromJson(resultString, adress_Model.class);
                    if (model.getCode() == -1 || model.getCode() == 500) {
                        Intent intent = new Intent(context, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (model.getCode() == 200) {

                        List<EaseUser> data = new ArrayList<>();
                        for (int i = 0; i < model.getData().size(); i++) {
                            adress_Model.DataDTO dataDTO = model.getData().get(i);
                            EaseUser user = new EaseUser();
//                            KV<String, Integer> user = new KV<>();
//                            user.setContact(dataDTO.);
                            user.setUsername(dataDTO.getHxUserName());
                            user.setNickname(dataDTO.getNikeName());
                            // 正则表达式，判断首字母是否是英文字母
                            String pinyin = PinyinUtils.getPingYin(dataDTO.getNikeName());
                            String sortString = pinyin.substring(0, 1).toUpperCase();
                            if (sortString.matches("[A-Z]")) {
                                user.setInitialLetter(PinyinUtils.getFirstSpell(sortString));
                            } else {
                                user.setInitialLetter("#");
                            }
                            user.setAvatar(dataDTO.getHeadImg());
                            user.setBirth("");
                            user.setContact(0);
                            user.setEmail("");
                            user.setGender(0);
                            user.setBirth("");
                            user.setSign("");
                            user.setExt("");
                            user.setPhone("");
                            data.add(user);

                            //通知callKit更新头像昵称
                            EaseCallUserInfo info = new EaseCallUserInfo(dataDTO.getNikeName(), dataDTO.getHeadImg());
                            info.setUserId(info.getUserId());
                            EaseLiveDataBus.get().with(EaseCallKitUtils.UPDATE_USERINFO).postValue(info);
                        }

                        List<KV<String, Integer>> contacts = new ArrayList<>();

//                        contactsAdapter.setData(data);

                    } else {
                        Toast.makeText(context, model.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                String errStr = throwable.getMessage();
                if (errStr.equals("Authorization")) {
                    Intent intent = new Intent(context, MainLogin_Register.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onCancelled(Callback.CancelledException e) {
            }

            @Override
            public void onFinished() {
            }
        });
    }


}
