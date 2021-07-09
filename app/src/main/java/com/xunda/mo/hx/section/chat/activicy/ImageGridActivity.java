package com.xunda.mo.hx.section.chat.activicy;

import androidx.fragment.app.FragmentTransaction;

import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.chat.fragment.ImageGridFragment;


public class ImageGridActivity extends BaseInitActivity {

	private static final String TAG = "ImageGridActivity";

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_image_grid;
    }

    @Override
    protected void initData() {
        super.initData();
        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fl_fragment, new ImageGridFragment(), TAG);
            ft.commit();
        }
    }
}
