package com.xunda.mo.main.group.adapter;

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
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.modules.contact.model.EaseContactSetStyle;
import com.hyphenate.easeui.widget.EaseImageView;
import com.xunda.mo.R;
import com.xunda.mo.hx.section.domain.MyEaseUser;

public class MyGroupHead_ListAdapter extends EaseBaseRecyclerViewAdapter<MyEaseUser> {
    private EaseContactSetStyle contactSetModel;

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.base_contact_item, parent, false);
        return new CustomViewHolder(view);
    }

    public void setSettingModel(EaseContactSetStyle settingModel) {
        this.contactSetModel = settingModel;
    }


    public void addItem(int id, int image, String name) {
        MyEaseUser bean = new MyEaseUser();
//        bean.setId(id);
//        bean.setResourceId(image);
//        bean.setName(name);
        this.addData(bean);
    }

    public void addItem(int id, String image, String name) {
        MyEaseUser bean = new MyEaseUser();
//        bean.setId(id);
//        bean.setImage(image);
//        bean.setName(name);
        this.addData(bean);
    }

    private class CustomViewHolder extends ViewHolder<MyEaseUser> {
        private TextView mHeader;
        private EaseImageView mAvatar;
        private TextView mName,vipType_txt;
        private ConstraintLayout clUser;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            mHeader = findViewById(R.id.header);
            mAvatar = findViewById(R.id.avatar);
            mName = findViewById(R.id.name);
            clUser = findViewById(R.id.cl_user);
            vipType_txt = findViewById(R.id.vipType_txt);
            if(contactSetModel != null) {
                float titleTextSize = contactSetModel.getTitleTextSize();
                if(titleTextSize != 0) {
                    mName.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
                }
                int titleTextColor = contactSetModel.getTitleTextColor();
                if(titleTextColor != 0) {
                    mName.setTextColor(titleTextColor);
                }
                Drawable avatarDefaultSrc = contactSetModel.getAvatarDefaultSrc();
                if(avatarDefaultSrc != null) {
                    mAvatar.setImageDrawable(avatarDefaultSrc);
                }
                float avatarRadius = contactSetModel.getAvatarRadius();
                if(avatarRadius != 0) {
                    mAvatar.setRadius((int) avatarRadius);
                }
                float borderWidth = contactSetModel.getBorderWidth();
                if(borderWidth != 0) {
                    mAvatar.setBorderWidth((int) borderWidth);
                }
                int borderColor = contactSetModel.getBorderColor();
                if(borderColor != 0) {
                    mAvatar.setBorderColor(borderColor);
                }
                mAvatar.setShapeType(contactSetModel.getShapeType());
                float avatarSize = contactSetModel.getAvatarSize();
                if(avatarSize != 0) {
                    ViewGroup.LayoutParams mAvatarLayoutParams = mAvatar.getLayoutParams();
                    mAvatarLayoutParams.height = (int) avatarSize;
                    mAvatarLayoutParams.width = (int) avatarSize;
                }
                float itemHeight = contactSetModel.getItemHeight();
                if(itemHeight != 0) {
                    ViewGroup.LayoutParams userLayoutParams = clUser.getLayoutParams();
                    userLayoutParams.height = (int) itemHeight;
                }
                Drawable bgDrawable = contactSetModel.getBgDrawable();
                clUser.setBackground(bgDrawable);
            }
        }

        @Override
        public void setData(MyEaseUser item, int position) {
            String header = item.getInitialLetter();
            mHeader.setVisibility(View.GONE);
//            if (position == 0 || (header != null && !header.equals(getItem(position - 1).getInitialLetter()))) {
            if (position == 0) {
                if (!TextUtils.isEmpty(header)) {
                    mHeader.setVisibility(View.VISIBLE);
                    if (contactSetModel != null) {
                        mHeader.setVisibility(contactSetModel.isShowItemHeader() ? View.VISIBLE : View.GONE);
                    }
//                    mHeader.setText(header);
                    mHeader.setText("群主、管理员");
                }
            }

            String name = item.getNickname();
            int nameLength = name.length();
            String nameAndNum = name + " (" + item.getUserNum() + ")";
            mName.setText(nameAndNum);
            if (item.getVipType() == 0){
                vipType_txt.setVisibility(View.GONE);
                mName.setTextColor(ContextCompat.getColor(mContext, R.color.blacktitle));
            }else {
                vipType_txt.setVisibility(View.VISIBLE);
                mName.setTextColor(ContextCompat.getColor(mContext, R.color.yellowfive));
            }

            Glide.with(mContext)
                    .load(item.getAvatar())
                    .error(contactSetModel.getAvatarDefaultSrc() != null ? contactSetModel.getAvatarDefaultSrc()
                            : ContextCompat.getDrawable(mContext, R.drawable.ease_default_avatar))
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
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(mContext  .getColor(R.color.yellowfive));
        RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(1.1f);
        spannableString.setSpan(foregroundColorSpan, 0, nameLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(relativeSizeSpan, 0, nameLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        viewName.setText(spannableString);
    }

}

