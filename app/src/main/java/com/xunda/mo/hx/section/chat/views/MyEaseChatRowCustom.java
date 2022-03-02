package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.manager.EaseDingMessageHelper;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.xunda.mo.R;
import com.xunda.mo.main.baseView.FlowLayout;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.model.baseDataModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.xUtils3Http;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyEaseChatRowCustom extends BaseChatRowWithNameAndHeader {

    private TextView contentView, question_Txt;
    private FlowLayout label_Flow;

    public MyEaseChatRowCustom(Context context, boolean isSender) {
        super(context, isSender);
    }

    public MyEaseChatRowCustom(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(!showSenderType ?
                R.layout.ease_row_received_custom : R.layout.ease_row_sent_custom, this);
    }

    @Override
    protected void onFindViewById() {
        contentView = findViewById(R.id.tv_chatcontent);
        question_Txt = findViewById(R.id.question_Txt);
        label_Flow = findViewById(R.id.label_Flow);
    }

    @Override
    public void onSetUpView() {
        if (message.getChatType() == EMMessage.ChatType.GroupChat) {
            setAvatarAndNick();
        }
    }


    @Override
    protected void setAvatarAndNick() {
        if (isSender()) {
            EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
            CharSequence final_CharSequence = txtBody.getMessage();
            contentView.setText(EaseSmileUtils.getSmiledText(context, final_CharSequence));
            String headUrl = message.getStringAttribute(MyConstant.SEND_HEAD, "");
            Glide.with(getContext()).load(headUrl).placeholder(R.mipmap.img_pic_none).error(R.mipmap.img_pic_none).into(userAvatarView);
        } else {

            Map<String, Object> mapExt = message.ext();
            String sendName = message.getStringAttribute(MyConstant.SEND_NAME, "");
            String headUrl = message.getStringAttribute(MyConstant.SEND_HEAD, "");

            question_Txt.setVisibility(GONE);
            label_Flow.setVisibility(GONE);
            //没有sendName就是机器客服
            if (TextUtils.isEmpty(sendName)) {
                if (mapExt != null) {
                    String questionStr = (String) mapExt.get(MyConstant.QUESTIONS);
                    //没有问题列表 显示客服答案
                    if (!TextUtils.isEmpty(questionStr)) {
                        String content = (String) mapExt.get(MyConstant.CONTENT);
                        contentView.setText(content);
                        question_Txt.setVisibility(VISIBLE);
                        label_Flow.setVisibility(VISIBLE);
                        labelFlow(label_Flow, context, questionStr);
                    } else {
                        String content = (String) mapExt.get(MyConstant.MSG);
                        contentView.setText(content);
                    }
                }
                usernickView.setText("Mo客服");
                userAvatarView.setImageResource(R.mipmap.adress_head_service);
            } else {
                EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
                CharSequence final_CharSequence = txtBody.getMessage();
                contentView.setText(EaseSmileUtils.getSmiledText(context, final_CharSequence));
                usernickView.setText(sendName);
                Glide.with(getContext()).load(headUrl).placeholder(R.mipmap.img_pic_none).error(R.mipmap.img_pic_none).into(userAvatarView);
            }


        }
    }



    public void onAckUserUpdate(final int count) {
        if (ackedView != null && isSender()) {
            ackedView.post(new Runnable() {
                @Override
                public void run() {
                    ackedView.setVisibility(VISIBLE);
                    ackedView.setText(String.format(getContext().getString(R.string.group_ack_read_count), count));
                }
            });
        }
    }

    @Override
    protected void onMessageCreate() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (statusView != null) {
            statusView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onMessageSuccess() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if (statusView != null) {
            statusView.setVisibility(View.GONE);
        }

        // Show "1 Read" if this msg is a ding-type msg.
        if (isSender() && EaseDingMessageHelper.get().isDingMessage(message) && ackedView != null) {
            ackedView.setVisibility(VISIBLE);
            int count = message.groupAckCount();
            ackedView.setText(String.format(getContext().getString(R.string.group_ack_read_count), count));
        }

        // Set ack-user list change listener.
        EaseDingMessageHelper.get().setUserUpdateListener(message, userUpdateListener);
    }

    @Override
    protected void onMessageError() {
        super.onMessageError();
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if (statusView != null) {
            statusView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onMessageInProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (statusView != null) {
            statusView.setVisibility(View.GONE);
        }
    }

    private EaseDingMessageHelper.IAckUserUpdateListener userUpdateListener =
            new EaseDingMessageHelper.IAckUserUpdateListener() {
                @Override
                public void onUpdate(List<String> list) {
                    onAckUserUpdate(list.size());
                }
            };


    public void labelFlow(FlowLayout myFlow, Context mContext, String tag) {
        if (myFlow != null) {
            myFlow.removeAllViews();
        }
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        String[] tagS = tag.split(",");
        for (int i = 0; i < tagS.length; i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.question_label, null);
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int margins = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, mContext.getResources().getDisplayMetrics());
            itemParams.setMargins(0, margins, margins, 0);
            view.setLayoutParams(itemParams);
            TextView label_Name = view.findViewById(R.id.label_Name);
            label_Name.setText(tagS[i]);
            label_Name.setTag(i);
            myFlow.addView(view);
            label_Name.setOnClickListener(v -> {
                int viewTag = (int) v.getTag();
                String question = tagS[viewTag];
                sendServiceMes(message.conversationId(), question);
                serviceAnswerData(mContext, saveFile.Receptionist_Answer, question);
            });
        }
    }

    //点击问题
    private void sendServiceMes(String conversationId, String sendTxt) {
        MyInfo myInfo = new MyInfo(context);
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        EMTextMessageBody txtBody = new EMTextMessageBody(sendTxt);
        message.addBody(txtBody);
        message.setTo(conversationId);
        message.setChatType(EMMessage.ChatType.GroupChat);
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MO_CUSTOMER);
        message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNickname());
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    //问题
    public void serviceAnswerData(final Context context, String baseUrl, String question) {
        Map<String, Object> map = new HashMap<>();
        map.put("question", question);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                baseDataModel model = new Gson().fromJson(result, baseDataModel.class);
            }

            @Override
            public void failed(String... args) {
            }
        });
    }


}
