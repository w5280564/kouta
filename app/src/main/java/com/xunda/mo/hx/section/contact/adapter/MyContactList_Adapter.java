package com.xunda.mo.hx.section.contact.adapter;

import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.modules.contact.model.EaseContactSetStyle;
import com.hyphenate.easeui.provider.EaseUserProfileProvider;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseImageView;
import com.xunda.mo.R;
import com.xunda.mo.main.constant.MyConstant;

import org.json.JSONException;
import org.json.JSONObject;

public class MyContactList_Adapter extends EaseBaseRecyclerViewAdapter<EaseUser> {

    private EaseContactSetStyle contactSetModel;

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater.from(mContext).inflate(R.layout.my_contact_item, parent, false));
    }

    public void setSettingModel(EaseContactSetStyle settingModel) {
        this.contactSetModel = settingModel;
    }


    private class ContactViewHolder extends ViewHolder<EaseUser> {
        private TextView mHeader;
        private EaseImageView mAvatar;
        private TextView mName, vipType_txt;
        private TextView mSignature;
        private TextView mUnreadMsgNumber;
        private ConstraintLayout clUser;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            mHeader = findViewById(R.id.header);
            mAvatar = findViewById(R.id.avatar);
            mName = findViewById(R.id.name);
            mSignature = findViewById(R.id.signature);
            mUnreadMsgNumber = findViewById(R.id.unread_msg_number);
            clUser = findViewById(R.id.cl_user);
            vipType_txt = findViewById(R.id.vipType_txt);
            EaseUserUtils.setUserAvatarStyle(mAvatar);
            if (contactSetModel != null) {
                float headerTextSize = contactSetModel.getHeaderTextSize();
                if (headerTextSize != 0) {
                    mHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, headerTextSize);
                }
                int headerTextColor = contactSetModel.getHeaderTextColor();
                if (headerTextColor != 0) {
                    mHeader.setTextColor(headerTextColor);
                }
                Drawable headerBgDrawable = contactSetModel.getHeaderBgDrawable();
                if (headerBgDrawable != null) {
                    mHeader.setBackground(headerBgDrawable);
                }
                float titleTextSize = contactSetModel.getTitleTextSize();
                if (titleTextSize != 0) {
                    mName.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
                }
                int titleTextColor = contactSetModel.getTitleTextColor();
                if (titleTextColor != 0) {
                    mName.setTextColor(titleTextColor);
                }
                Drawable avatarDefaultSrc = contactSetModel.getAvatarDefaultSrc();
                if (avatarDefaultSrc != null) {
                    mAvatar.setImageDrawable(avatarDefaultSrc);
                }
                float avatarRadius = contactSetModel.getAvatarRadius();
                if (avatarRadius != 0) {
                    mAvatar.setRadius((int) avatarRadius);
                }
                float borderWidth = contactSetModel.getBorderWidth();
                if (borderWidth != 0) {
                    mAvatar.setBorderWidth((int) borderWidth);
                }
                int borderColor = contactSetModel.getBorderColor();
                if (borderColor != 0) {
                    mAvatar.setBorderColor(borderColor);
                }
                mAvatar.setShapeType(contactSetModel.getShapeType());
                float avatarSize = contactSetModel.getAvatarSize();
                if (avatarSize != 0) {
                    ViewGroup.LayoutParams mAvatarLayoutParams = mAvatar.getLayoutParams();
                    mAvatarLayoutParams.height = (int) avatarSize;
                    mAvatarLayoutParams.width = (int) avatarSize;
                }
                float itemHeight = contactSetModel.getItemHeight();
                if (itemHeight != 0) {
                    ViewGroup.LayoutParams userLayoutParams = clUser.getLayoutParams();
                    userLayoutParams.height = (int) itemHeight;
                }
                Drawable bgDrawable = contactSetModel.getBgDrawable();
                clUser.setBackground(bgDrawable);
            }
        }

        @Override
        public void setData(EaseUser item, int position) {
            EaseUserProfileProvider provider = EaseIM.getInstance().getUserProvider();
            if (provider != null) {
                EaseUser user = provider.getUser(item.getUsername());
                if (user != null) {
                    item = user;
                }
            }
            String header = item.getInitialLetter();
            mHeader.setVisibility(View.GONE);
            if (position == 0 || (header != null && !header.equals(getItem(position - 1).getInitialLetter()))) {
                if (!TextUtils.isEmpty(header)) {
                    mHeader.setVisibility(View.VISIBLE);
                    if (contactSetModel != null) {
                        mHeader.setVisibility(contactSetModel.isShowItemHeader() ? View.VISIBLE : View.GONE);
                    }
                    mHeader.setText(header);
                }
            }

            try {
                String selectInfoExt = item.getExt();
                if (selectInfoExt != null) {
                    JSONObject jsonObject = new JSONObject(selectInfoExt);//用户资料扩展属性
                    if (jsonObject != null) {
                        String remarkName = jsonObject.getString(MyConstant.REMARK_NAME);
                        String name = TextUtils.isEmpty(remarkName) ? item.getNickname() : remarkName;
                        int nameLength = name.length();
                        String nameAndNum = name + " (" + jsonObject.getString(MyConstant.USER_NUM) + ")";
                        setName(nameAndNum, nameLength, mName);

                        String vipTypeString = jsonObject.getString(MyConstant.VIP_TYPE);
                        if (TextUtils.equals(vipTypeString, "0")) {
                            vipType_txt.setVisibility(View.GONE);
                        } else {
                            vipType_txt.setVisibility(View.VISIBLE);
                            mName.setTextColor(ContextCompat.getColor(mContext, R.color.yellowfive));
                        }
                        String on_LightStatus = jsonObject.getString(MyConstant.ONLINE_STATUS);
                        mSignature.setText(on_LightStatus);

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Glide.with(mContext).load(item.getAvatar())
                    .error(contactSetModel.getAvatarDefaultSrc() != null ? contactSetModel.getAvatarDefaultSrc()
                            : ContextCompat.getDrawable(mContext, R.drawable.mo_icon))
                    .into(mAvatar);


        }
    }

    /**
     * @param name       要显示的数据
     * @param nameLength 要放大改颜色的字体长度
     * @param viewName
     */
    private void setName(String name, int nameLength, TextView viewName) {
        SpannableString spannableString = new SpannableString(name);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(mContext.getColor(R.color.yellowfive));
//        StyleSpan styleSpan_B = new StyleSpan(Typeface.BOLD);//粗体
        RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(1.1f);//字放大
//        spannableString.setSpan(styleS
//        pan_B, 0, nameLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//        spannableString.setSpan(foregroundColorSpan, 0, nameLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(relativeSizeSpan, 0, nameLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        viewName.setText(spannableString);
    }


}
