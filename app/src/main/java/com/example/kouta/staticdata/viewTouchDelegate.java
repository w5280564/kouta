package com.example.kouta.staticdata;

import android.graphics.Rect;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;

import static android.content.ContentValues.TAG;

/**
 * Created by waylen on 2017/9/29.
 */

public class viewTouchDelegate {
    /**
     * 扩大View的触摸和点击响应范围,最大不超过其父View范围
     *
     * @param view
     * @param top
     * @param bottom
     * @param left
     * @param right
     */
    public static void expandViewTouchDelegate(final View view, final int top,
                                               final int bottom, final int left, final int right) {
        ((View) view.getParent()).post(new Runnable() {
            @Override
            public void run() {
                Rect bounds = new Rect();
                view.setEnabled(true);
                view.getHitRect(bounds);
                Log.d(TAG, "width: "+bounds.width()+" | height: "+bounds.height());
                Log.d(TAG, "left: "+bounds.left + " | Top: "+bounds.top +
                        " | right: "+bounds.right+" | bottom: "+bounds.bottom);
                bounds.top -= top;
                bounds.bottom += bottom;
                bounds.left -= left;
                bounds.right += right;
                Log.d(TAG, "——————————扩大触摸区域后  矩阵区域————————");
                Log.d(TAG, "width: "+bounds.width()+" | height: "+bounds.height());
                Log.d(TAG, "left: "+bounds.left + " | Top: "+bounds.top +
                        " | right: "+bounds.right+" | bottom: "+bounds.bottom);
                TouchDelegate touchDelegate = new TouchDelegate(bounds, view);
                if (View.class.isInstance(view.getParent())) {
                    ((View) view.getParent()).setTouchDelegate(touchDelegate);
                }
            }
        });
    }
    /**
     * 还原View的触摸和点击响应范围,最小不小于View自身范围
     *
     * @param view
     */
    public static void restoreViewTouchDelegate(final View view) {
        ((View) view.getParent()).post(new Runnable() {
            @Override
            public void run() {
                Rect bounds = new Rect();
                bounds.setEmpty();
                TouchDelegate touchDelegate = new TouchDelegate(bounds, view);
                if (View.class.isInstance(view.getParent())) {
                    ((View) view.getParent()).setTouchDelegate(touchDelegate);
                }
            }
        });
    }
}
