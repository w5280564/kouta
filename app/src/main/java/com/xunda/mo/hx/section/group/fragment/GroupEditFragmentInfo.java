package com.xunda.mo.hx.section.group.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import com.hyphenate.easeui.utils.StatusBarCompat;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseActivity;
import com.xunda.mo.hx.section.base.BaseDialogFragment;

public class GroupEditFragmentInfo extends BaseDialogFragment implements EaseTitleBar.OnRightClickListener, EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;
    private EditText etContent;
    private String content;
    private OnSaveClickListener listener;
    private String title;

    public static void showDialog(BaseActivity activity, String title, String content,  OnSaveClickListener listener) {
        GroupEditFragmentInfo fragment = new GroupEditFragmentInfo();
        fragment.setOnSaveClickListener(listener);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("content", content);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragment.show(transaction, null);
    }


    @Override
    public int getLayoutId() {
        return R.layout.demo_fragment_group_edit_info;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.AppTheme);
        StatusBarCompat.setLightStatusBar(mContext, true);
    }

    @Override
    public void onStart() {
        super.onStart();
        setDialogFullParams();
    }

    @Override
    public void initArgument() {
        super.initArgument();
        Bundle bundle = getArguments();
        if(bundle != null) {
            title = bundle.getString("title");
            content = bundle.getString("content");
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        etContent = findViewById(R.id.et_content);


        if (!TextUtils.isEmpty(content)) {
            etContent.setText(content);
        }
        titleBar.setTitle(title);
    }

    @Override
    public void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        titleBar.setOnRightClickListener(this);
    }

    public void setOnSaveClickListener(OnSaveClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onRightClick(View view) {
        String content = etContent.getText().toString().trim();
        if(listener != null) {
            listener.onSaveClick(view, content);
        }
        dismiss();
    }

    @Override
    public void onBackPress(View view) {
        dismiss();
    }

    public interface OnSaveClickListener {
        void onSaveClick(View view, String content);
    }

}
