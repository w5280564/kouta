package com.xunda.mo.main.baseView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.hyphenate.easeui.widget.EaseImageView;
import com.xunda.mo.R;

public class MyArrowItemView extends ConstraintLayout {
    private EaseImageView avatar;
    private TextView tvTitle;
    private TextView tv_tip;
    private TextView tvContent;
    private ImageView ivArrow;
    private View viewDivider;
    private String title;
    private String tipStr;
    private String content;
    private int titleColor;
    private int contentColor;
    private float titleSize;
    private float contentSize;
    private View root;
    private ImageView tv_img;

    public MyArrowItemView(Context context) {
        this(context, null);
    }

    public MyArrowItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyArrowItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        root = LayoutInflater.from(context).inflate(R.layout.my_arrowitemview, this);
        avatar = findViewById(R.id.avatar);
        tvTitle = findViewById(R.id.tv_title);
        tv_tip = findViewById(R.id.tv_tip);
        tvContent = findViewById(R.id.tv_content);
        ivArrow = findViewById(R.id.iv_arrow);
        viewDivider = findViewById(R.id.view_divider);
        tv_img = findViewById(R.id.tv_img);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ArrowItemView);
        int titleResourceId = a.getResourceId(R.styleable.ArrowItemView_arrowItemTitle, -1);
        title = a.getString(R.styleable.ArrowItemView_arrowItemTitle);
        if (titleResourceId != -1) {
            title = getContext().getString(titleResourceId);
        }
        tvTitle.setText(title);

        int titleColorId = a.getResourceId(R.styleable.ArrowItemView_arrowItemTitleColor, -1);
        titleColor = a.getColor(R.styleable.ArrowItemView_arrowItemTitleColor, ContextCompat.getColor(getContext(), R.color.em_color_common_text_black));
        if (titleColorId != -1) {
            titleColor = ContextCompat.getColor(getContext(), titleColorId);
        }
        tvTitle.setTextColor(titleColor);

        int titleSizeId = a.getResourceId(R.styleable.ArrowItemView_arrowItemTitleSize, -1);
        titleSize = a.getDimension(R.styleable.ArrowItemView_arrowItemTitleSize, sp2px(getContext(), 14));
        if (titleSizeId != -1) {
            titleSize = getResources().getDimension(titleSizeId);
        }
        tvTitle.getPaint().setTextSize(titleSize);


        boolean showTip = a.getBoolean(R.styleable.ArrowItemView_arrowItemshowTip, false);
        tv_tip.setVisibility(showTip ? VISIBLE : GONE);

        int tipResourceId = a.getResourceId(R.styleable.ArrowItemView_arrowItemTip, -1);
        tipStr = a.getString(R.styleable.ArrowItemView_arrowItemTip);
        if (tipResourceId != -1) {
            tipStr = getContext().getString(tipResourceId);
        }
        tv_tip.setText(tipStr);


        int contentResourceId = a.getResourceId(R.styleable.ArrowItemView_arrowItemContent, -1);
        content = a.getString(R.styleable.ArrowItemView_arrowItemContent);
        if (contentResourceId != -1) {
            content = getContext().getString(contentResourceId);
        }
        tvContent.setText(content);

        int contentColorId = a.getResourceId(R.styleable.ArrowItemView_arrowItemContentColor, -1);
        contentColor = a.getColor(R.styleable.ArrowItemView_arrowItemContentColor, ContextCompat.getColor(getContext(), R.color.em_color_common_text_gray));
        if (contentColorId != -1) {
            contentColor = ContextCompat.getColor(getContext(), contentColorId);
        }
        tvContent.setTextColor(contentColor);

        int contentSizeId = a.getResourceId(R.styleable.ArrowItemView_arrowItemContentSize, -1);
        contentSize = a.getDimension(R.styleable.ArrowItemView_arrowItemContentSize, 14);
        if (contentSizeId != -1) {
            contentSize = getResources().getDimension(contentSizeId);
        }
        tvContent.setTextSize(contentSize);

        boolean showDivider = a.getBoolean(R.styleable.ArrowItemView_arrowItemShowDivider, true);
        viewDivider.setVisibility(showDivider ? VISIBLE : GONE);

        boolean showArrow = a.getBoolean(R.styleable.ArrowItemView_arrowItemShowArrow, true);
        ivArrow.setVisibility(showArrow ? VISIBLE : GONE);


        boolean showTvImg = a.getBoolean(R.styleable.ArrowItemView_arrowItemShowtvImg, false);
        tv_img.setVisibility(showTvImg ? VISIBLE : GONE);

//        int tv_imgSrcResourceId = a.getResourceId(R.styleable.ArrowItemView_tvImgItemSrc, -1);
//        if(tv_imgSrcResourceId != -1) {
//            tv_img.setImageResource(tv_imgSrcResourceId);
//        }

//        int ArrowHeightId = a.getResourceId(R.styleable.ArrowItemView_arrowHeight, -1);
//        float ArrowHeight = a.getDimension(R.styleable.ArrowItemView_arrowHeight, 0);
//        if(ArrowHeightId != -1) {
//            ArrowHeight = getResources().getDimension(ArrowHeightId);
//        }
//
//        int ArrowWidthId = a.getResourceId(R.styleable.ArrowItemView_arrowWidth, -1);
//        float ArrowWidth = a.getDimension(R.styleable.ArrowItemView_arrowWidth, 0);
//        if(ArrowWidthId != -1) {
//            ArrowWidth = getResources().getDimension(ArrowHeightId);
//        }

        boolean showAvatar = a.getBoolean(R.styleable.ArrowItemView_arrowItemShowAvatar, false);
        avatar.setVisibility(showAvatar ? VISIBLE : GONE);

        int avatarSrcResourceId = a.getResourceId(R.styleable.ArrowItemView_arrowItemAvatarSrc, -1);
        if (avatarSrcResourceId != -1) {
            avatar.setImageResource(avatarSrcResourceId);
        }

        int avatarHeightId = a.getResourceId(R.styleable.ArrowItemView_arrowItemAvatarHeight, -1);
        float height = a.getDimension(R.styleable.ArrowItemView_arrowItemAvatarHeight, 0);
        if (avatarHeightId != -1) {
            height = getResources().getDimension(avatarHeightId);
        }

        int avatarWidthId = a.getResourceId(R.styleable.ArrowItemView_arrowItemAvatarWidth, -1);
        float width = a.getDimension(R.styleable.ArrowItemView_arrowItemAvatarWidth, 0);
        if (avatarWidthId != -1) {
            width = getResources().getDimension(avatarWidthId);
        }

        a.recycle();

        ViewGroup.LayoutParams params = avatar.getLayoutParams();
        params.height = height == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) height;
        params.width = width == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) width;
    }

    public TextView getTvContent() {
        return tvContent;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public EaseImageView getAvatar() {
        return avatar;
    }
    public ImageView getIMG() { return tv_img; }
    public ImageView getArrow() { return ivArrow; }
    public TextView getTip() {
        return tv_tip; }

    /**
     * sp to px
     *
     * @param context
     * @param value
     * @return
     */
    public static float sp2px(Context context, float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, context.getResources().getDisplayMetrics());
    }
}
