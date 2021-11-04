package com.xunda.mo.staticdata;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.xunda.mo.R;

@SuppressLint("AppCompatCustomView")
public class SearchEditText extends EditText implements View.OnFocusChangeListener, TextWatcher {
    private static final String TAG = "SearchEditText";
    /**
     * 删除按钮与右边编辑框之间的paddingRight距离
     */
    private int paddingRightValueBetweenRightBorderAndDel = 0;

    /**
     * 图标是否默认在左边
     */
    private boolean isIconLeft = false;

    /**
     * 控件的图片资源
     */
    private Drawable[] mDrawables;

    /**
     * drawableLeft:搜索图标; drawableDel:删除按钮图标
     */
    private Drawable drawableLeft, drawableDel;

    /**
     * 记录点击坐标
     */
    private int eventX, eventY;

    /**
     * 控件区域
     */
    private Rect mRect;

    public SearchEditText(Context context) {
        super(context);
        init();
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SearchEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas){

        if(isIconLeft){
            if(length() < 1){
                drawableDel = null;
            }
            this.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableDel, null);  //在edittext的左上右下设置Drawable
            super.onDraw(canvas);
        }else {
            if(mDrawables == null){
                mDrawables = getCompoundDrawables(); //返回包含左上右下四个位置的Drawable数组
            }
            if(drawableLeft == null){
                drawableLeft = mDrawables[0];
            }
            float textWidth = getPaint().measureText(getHint().toString());
            int drawablePadding = getCompoundDrawablePadding();  //返回Drawable和text之间的padding值
            int drawableWidth = drawableLeft.getIntrinsicWidth();   //获得Drawable的固有宽度
            float bodyWidth = textWidth + drawablePadding + drawableWidth;
            canvas.translate((getWidth() - bodyWidth - getPaddingLeft() - getPaddingRight()) / 2, 0);
            super.onDraw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(drawableDel != null && event.getAction() == MotionEvent.ACTION_UP){
            eventX = (int)event.getRawX();
            eventY = (int)event.getRawY();
            if(mRect == null){
                mRect = new Rect();
            }
            getGlobalVisibleRect(mRect);  // edittext在屏幕中的坐标
            mRect.left = mRect.right - drawableDel.getIntrinsicWidth() - 10;
            if(mRect.contains(eventX, eventY)){
                setText("");
            }
        }
//        if(drawableDel != null && event.getAction() == MotionEvent.ACTION_DOWN){
//            eventX = (int)event.getRawX();
//            eventY = (int)event.getRawY();
//            if(mRect == null){
//                mRect = new Rect();
//            }
//            getGlobalVisibleRect(mRect);
//            mRect.left = mRect.right - drawableDel.getIntrinsicWidth();
//            if(mRect.contains(eventX, eventY)){
//                drawableDel = this.getResources().getDrawable(R.mipmap.clear_edit);
//            }else {
//                drawableDel = this.getResources().getDrawable(R.mipmap.clear_edit);
//            }
//        }
        return super.onTouchEvent(event);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(this.length() < 1){
            drawableDel = null;
        }else {
            drawableDel = this.getResources().getDrawable(R.mipmap.edit_clear);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(TextUtils.isEmpty(getText().toString())){
            isIconLeft = hasFocus;
        }
    }

    public void setPaddingRightValueBetweenRightBorderAndDel(int dimenID){
        this.paddingRightValueBetweenRightBorderAndDel = dimenID;
    }

    private int getPaddingRightValueBetweenRightBorderAndDel(){
        return this.getResources().getDimensionPixelOffset(paddingRightValueBetweenRightBorderAndDel);
    }
}