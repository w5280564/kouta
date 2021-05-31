package com.moying.energyring.xrecycle;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;

/**
 * Created by expect_xh on 2016/3/22.
 */
public class XRecyclerView extends RecyclerView {
    private static final String TAG = "XRecyclerView";
    private Context mContext;//上下文
    private ArrayList<View> mHeaderViews = new ArrayList<View>();//头文件列表
    private com.moying.energyring.xrecycle.XRecyclerViewHeader mHeader;
    private com.moying.energyring.xrecycle.XRecyclerViewFooter mFooter;
    private Adapter mAdapter;//传入的Adapter
    private Adapter mWrapAdapter;//组合的Adapter
    private float mLastY = -1; //记录的Y坐标
    private static final float DRAG_RATE = 3;//阻力率
    private LoadingListener mLoadingListener;//滑动监听
    private boolean pullRefreshEnabled = true;   //刷新状态
    private boolean loadingMoreEnabled = true;   //上拉状态

    private static final int TYPE_REFRESH_HEADER = -5; //添加刷新头
    private static final int TYPE_HEADER = -4;  //添加头部
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FOOTER = -3;//添加上拉加载布局
    private int previousTotal = 0;//  记录ITEM的条数


    public XRecyclerView(Context context) {
        this(context, null);
    }

    public XRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        if (pullRefreshEnabled) {
            com.moying.energyring.xrecycle.XRecyclerViewHeader xRecyclerViewHeader = new com.moying.energyring.xrecycle.XRecyclerViewHeader(mContext);
            mHeaderViews.add(0, xRecyclerViewHeader);
            mHeader = xRecyclerViewHeader;
        }
        mFooter = new com.moying.energyring.xrecycle.XRecyclerViewFooter(mContext);
        mFooter.setVisibility(GONE);
    }

    //添加头部文件的时候 判断有没有刷新头
    public void addHeaderView(View view) {
        if (pullRefreshEnabled && !(mHeaderViews.get(0) instanceof com.moying.energyring.xrecycle.XRecyclerViewHeader)) {
            com.moying.energyring.xrecycle.XRecyclerViewHeader xRecyclerViewHeader = new com.moying.energyring.xrecycle.XRecyclerViewHeader(mContext);
            mHeaderViews.add(0, xRecyclerViewHeader);
            mHeader = xRecyclerViewHeader;
        }
        mHeaderViews.add(view);
    }

    public void loadMoreComplete() {//上拉加载完成后的   隐藏上拉加载布局
        if (mFooter instanceof com.moying.energyring.xrecycle.XRecyclerViewFooter) {
            mFooter.setState(com.moying.energyring.xrecycle.XRecyclerViewFooter.STATE_COMPLETE);
        }
//        if (previousTotal < getLayoutManager().getItemCount()) {
//        if (previousTotal <= getLayoutManager().getItemCount()) {//修改比对条件
////            Log.d(TAG, "loadMoreComplete: ");
//            mFooter.setState(XRecyclerViewFooter.STATE_COMPLETE);
//        }
        previousTotal = getLayoutManager().getItemCount();
    }

    public void refreshComplete() {//下拉刷新完成后的  隐藏下拉加载 布局
        mHeader.refreshComplate();
    }


    public void setPullRefreshEnabled(boolean enabled) {//设置是否可以刷新
        pullRefreshEnabled = enabled;
    }

    public void setLoadingMoreEnabled(boolean enabled) {//设置是否可以上拉
        loadingMoreEnabled = enabled;
        if (!enabled) {
            mFooter.setVisibility(View.GONE);
        }
//        if (enabled){
//            mFooter.setVisibility(VISIBLE);
//        }else {
//            mFooter.setVisibility(GONE);
//        }
//        mWrapAdapter.notifyDataSetChanged();
    }

    public void refresState(int index) {
        mHeader.setState(index);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
        mWrapAdapter = new WrapAdapter(mHeaderViews, mFooter, adapter);
        super.setAdapter(mWrapAdapter);
        mAdapter.registerAdapterDataObserver(mDataObserver);
    }


    /**
     * 活动监听  判断是否到底，用于加载 添加滑动时暂停图片加载
     */
    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE && mLoadingListener != null && loadingMoreEnabled) {//空闲状态
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition;  //最后可见的Item的position的值
            if (layoutManager instanceof GridLayoutManager) {   //网格布局的中lastVisibleItemPosition的取值
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {//瀑布流布局中lastVisibleItemPosition的取值
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            } else {   //剩下只有线性布局（listview）中lastVisibleItemPosition的取值
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
            if (layoutManager.getChildCount() > 0
                    && lastVisibleItemPosition >= layoutManager.getItemCount() - 1 && layoutManager.getItemCount() > layoutManager.getChildCount()
                    && mHeader.getState() < XRecyclerViewHeader.STATE_REFRESHING) {

                mFooter.setState(XRecyclerViewFooter.STATE_LOADING);

                mLoadingListener.onLoadMore();
            }

            if (Fresco.getImagePipeline().isPaused()) {//加载
                Fresco.getImagePipeline().resume();
            }
        } else if (state == RecyclerView.SCROLL_STATE_SETTLING) { //滚动状态
            if (!Fresco.getImagePipeline().isPaused()) {
                Fresco.getImagePipeline().pause();
            }
        } else if (state == RecyclerView.SCROLL_STATE_IDLE) {
            if (Fresco.getImagePipeline().isPaused()) {//加载
                Fresco.getImagePipeline().resume();
            }
        }
    }

    /**
     * 监听手势活动  判断有没有到顶，用于刷新
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {

        if (mLastY == -1) {
            mLastY = e.getRawY();
        }
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = e.getRawY();

//                xDistance = yDistance = 0f;
//                xLast = e.getX();
//                yLast = e.getY();

                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = e.getRawY() - mLastY;
                mLastY = e.getRawY();
                if (isOnTop() && pullRefreshEnabled) {
                    mHeader.onMove(deltaY / DRAG_RATE);
                    if (mHeader.getVisiableHeight() > 0 && mHeader.getState() < XRecyclerViewHeader.STATE_REFRESHING) {
                        Log.i("getVisiableHeight", "getVisiableHeight = " + mHeader.getVisiableHeight());
                        Log.i("getVisiableHeight", " mHeader.getState() = " + mHeader.getState());
                        return false;
                    }
                }


//                final float curX = e.getX();
//                final float curY = e.getY();
//                xDistance += Math.abs(curX - xLast);
//                yDistance += Math.abs(curY - yLast);
//                xLast = curX;
//                yLast = curY;
//                if (xDistance > yDistance) {
//                    return false;
//                }

                break;
            default:
                mLastY = -1; // reset
                if (isOnTop() && pullRefreshEnabled) {
                    if (mHeader.releaseAction()) {
                        if (mLoadingListener != null) {
                            mLoadingListener.onRefresh();
                            previousTotal = 0;
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(e);

    }

    //判断是不是在顶部
    public boolean isOnTop() {
        if (mHeaderViews == null || mHeaderViews.isEmpty()) {
            return false;
        }

        View view = mHeaderViews.get(0);
        if (view.getParent() != null) {
            return true;
        } else {
            return false;
        }
    }

    //瀑布流里面用到的计算公式
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    // 滑动距离及坐标
    private float xDistance, yDistance, xLast, yLast;

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//// TODO Auto-generated method stub
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                xDistance = yDistance = 0f;
//                xLast = ev.getX();
//                yLast = ev.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                final float curX = ev.getX();
//                final float curY = ev.getY();
//
//
//                xDistance += Math.abs(curX - xLast);
//                yDistance += Math.abs(curY - yLast);
//                xLast = curX;
//                yLast = curY;
//
//
//                if (xDistance > yDistance) {
//                    return false;
//                }
//        }
//
//
//        return super.onInterceptTouchEvent(ev);
//    }


    /**
     * adapter数据观察者
     */
    private final AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            mWrapAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    };

    /**
     * 设配器重组
     */
    private class WrapAdapter extends Adapter<ViewHolder> {

        private Adapter adapter;

        private ArrayList<View> mHeaderViews;

        private XRecyclerViewFooter mFootView;

        private int headerPosition = 1;

        public WrapAdapter(ArrayList<View> headerViews, XRecyclerViewFooter footView, Adapter adapter) {
            this.adapter = adapter;
            this.mHeaderViews = headerViews;
            this.mFootView = footView;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (isHeader(position) || isFooter(position)) ? gridManager.getSpanCount() : 1;
                    }
                });
            }
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && (isHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }

        public boolean isHeader(int position) {
            return position >= 0 && position < mHeaderViews.size();
        }

        public boolean isFooter(int position) {
            return position < getItemCount() && position >= getItemCount() - 1;
        }

        public boolean isRefreshHeader(int position) {
            return position == 0;
        }

        public int getHeadersCount() {
            return mHeaderViews.size();
        }

        public int getFootersCount() {
            return 1;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_REFRESH_HEADER) {
                return new SimpleViewHolder(mHeaderViews.get(0));
            } else if (viewType == TYPE_HEADER) {
                return new SimpleViewHolder(mHeaderViews.get(headerPosition++));
            } else if (viewType == TYPE_FOOTER) {
                return new SimpleViewHolder(mFootView);
            }
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (isHeader(position)) {
                return;
            }
            int adjPosition = position - getHeadersCount();
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    adapter.onBindViewHolder(holder, adjPosition);
                    return;
                }
            }
        }

        @Override
        public int getItemCount() {
            if (adapter != null) {
                return getHeadersCount() + getFootersCount() + adapter.getItemCount();
            } else {
                return getHeadersCount() + getFootersCount();
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (isRefreshHeader(position)) {
                return TYPE_REFRESH_HEADER;
            }
            if (isHeader(position)) {
                return TYPE_HEADER;
            }
            if (isFooter(position)) {
                return TYPE_FOOTER;
            }
            int adjPosition = position - getHeadersCount();
            ;
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return adapter.getItemViewType(adjPosition);
                }
            }
            return TYPE_NORMAL;
        }

        @Override
        public long getItemId(int position) {
            if (adapter != null && position >= getHeadersCount()) {
                int adjPosition = position - getHeadersCount();
                int adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return adapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            if (adapter != null) {
                adapter.unregisterAdapterDataObserver(observer);
            }
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            if (adapter != null) {
                adapter.registerAdapterDataObserver(observer);
            }
        }

        private class SimpleViewHolder extends ViewHolder {
            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    /**
     * 监听接口
     */
    public void setLoadingListener(LoadingListener listener) {
        mLoadingListener = listener;
    }

    public interface LoadingListener {

        void onRefresh();

        void onLoadMore();
    }

    public void addGif(Uri uri, int width, int height) {
        mHeader.addGif(uri, width, height);
    }

}
