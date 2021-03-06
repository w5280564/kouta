package com.xunda.mo.dialog;

import static com.luck.picture.lib.tools.ScreenUtils.getScreenWidth;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xunda.mo.R;

/**
 * 两个按钮的弹出框
 */
public class TwoButtonDialog extends Dialog implements
        View.OnClickListener {

    private ConfirmListener listener;
    private String content, left, right;
    private Context mContext;
    private TextView tv_content, tv_right, tv_left;

    public TwoButtonDialog(Context context, String content, String left,
                               String right, ConfirmListener confirmListener) {
        super(context, R.style.CenterDialogStyle);
        this.listener = confirmListener;
        this.content = content;
        this.left = left;
        this.right = right;
        this.mContext = context;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_two_button);
        setCanceledOnTouchOutside(true);
        initView();
        initEvent();

    }

    private void initView() {
        LinearLayout ll_parent = findViewById(R.id.ll_parent);
        int screenWidth = getScreenWidth(mContext);// 屏幕的宽度
        int parentWidth = (int) (screenWidth / 5f * 4);//弹出框的宽度
        ViewGroup.LayoutParams layoutParams = ll_parent.getLayoutParams();
        layoutParams.width = parentWidth;
        ll_parent.setLayoutParams(layoutParams);

        tv_content = findViewById(R.id.tv_content);
        tv_right = findViewById(R.id.tv_right);
        tv_left = findViewById(R.id.tv_left);

        tv_content.setText(content);
        tv_right.setText(right);
        tv_left.setText(left);
    }

    private void initEvent() {
        tv_right.setOnClickListener(this);
        tv_left.setOnClickListener(this);
    }

    /**
     * 回调接口对象
     */

    public interface ConfirmListener {

        void onClickRight();

        void onClickLeft();
    }

    @Override
    public void onClick(View arg0) {
        int id = arg0.getId();
        if (id == R.id.tv_right) {
            listener.onClickRight();
            dismiss();
        } else if (id == R.id.tv_left) {
            listener.onClickLeft();
            dismiss();
        }
    }
}
