package com.xunda.mo.main.friend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.dialog.DemoDialogFragment;
import com.xunda.mo.hx.section.dialog.SimpleDialogFragment;
import com.xunda.mo.main.MainLogin_Register;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.main.myAdapter.Friend_NewFriendList_Adapter;
import com.xunda.mo.model.NewFriend_Bean;
import com.xunda.mo.model.baseModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.xrecycle.XRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import static com.xunda.mo.network.saveFile.getShareData;

public class Friend_NewFriends extends BaseInitActivity {

    private XRecyclerView list_xrecycler;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Friend_NewFriends.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_friend_newfriends;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();
        list_xrecycler = findViewById(R.id.list_xrecycler);
        list_xrecycler.setLoadingListener(new listLoadingLister());
    }

    private int PageIndex;
    private int pageSize;

    @Override
    protected void initData() {
        super.initData();
        PageIndex = 1;
        pageSize = 10;
        NewFriendMethod(Friend_NewFriends.this, saveFile.BaseUrl + saveFile.Friend_ApplyList_Url + "?pageNum=" + PageIndex + "&pageSize=" + pageSize);
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setElevation(2f);//阴影
        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("新朋友");
        Button right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.VISIBLE);
        right_Btn.setBackgroundResource(R.mipmap.adress_head_more);
        viewTouchDelegate.expandViewTouchDelegate(right_Btn, 50, 50, 50, 50);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        int margisright = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
        layoutParams.setMargins(0, 0, margisright, 0);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        right_Btn.setLayoutParams(layoutParams);

        return_Btn.setOnClickListener(new return_Btn());
        right_Btn.setOnClickListener(new right_BtnClick());
    }


    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }

    private class right_BtnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            showMore(Friend_NewFriends.this, v, 0);
        }
    }


    private class listLoadingLister implements XRecyclerView.LoadingListener {
        @Override
        public void onRefresh() {
            initData();
        }

        @Override
        public void onLoadMore() {
            PageIndex = PageIndex + 1;
            pageSize = 10;
            NewFriendMethod(Friend_NewFriends.this, saveFile.BaseUrl + saveFile.Friend_ApplyList_Url + "?pageNum=" + PageIndex + "&pageSize=" + pageSize);
        }
    }

    Friend_NewFriendList_Adapter mAdapter;

    public void initlist(final Context context) {
        LinearLayoutManager mMangaer = new LinearLayoutManager(context);
        list_xrecycler.setLayoutManager(mMangaer);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        list_xrecycler.setHasFixedSize(true);
        mAdapter = new Friend_NewFriendList_Adapter(context, baseModel, Model);
        list_xrecycler.setAdapter(mAdapter);
        mAdapter.setOnItemClickLitener(new Friend_NewFriendList_Adapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
//                String addType = "8";
//                String friendUserId = baseModel.get(position).
//                ChatFriend_Detail.actionStart(mContext, friendUserId, addType);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }

        });
        //接受、拒绝好友请求
        mAdapter.setOnItemAddRemoveClickLister(new Friend_NewFriendList_Adapter.OnItemAddRemoveClickLister() {
            @Override
            public void onItemAddClick(View view, int position) {
                String agreeType = "1";
                String friendApplyId = baseModel.get(position).getFriendApplyId();
                AddRemoveMethod(Friend_NewFriends.this, saveFile.BaseUrl + saveFile.Friend_Agree_Url, agreeType, friendApplyId);
                try {
//                    String name = baseModel.get(position).getUserNum().toString();
//                    DemoHelper.getInstance().getContactManager().acceptInvitation(name);
                } catch (Exception e) {
                }
            }

            @Override
            public void onItemRemoveClick(View view, int position) {
                String agreeType = "2";
                String friendApplyId = baseModel.get(position).getFriendApplyId();
                AddRemoveMethod(Friend_NewFriends.this, saveFile.BaseUrl + saveFile.Friend_Agree_Url, agreeType, friendApplyId);
            }
        });
    }

    //更多
    private void showMore(final Context mContext, final View view, final int pos) {
        View contentView = View.inflate(mContext, R.layout.popup_morechoice, null);
        PopupWindow MorePopup = new BasePopupWindow(mContext);
        MorePopup.setWidth(RadioGroup.LayoutParams.MATCH_PARENT);
        MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setTouchable(true);
        MorePopup.setContentView(contentView);
        MorePopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        TextView change_txt = contentView.findViewById(R.id.change_txt);
        change_txt.setText("全部拒绝");
        TextView newregistr_txt = contentView.findViewById(R.id.newregistr_txt);
        newregistr_txt.setText("全部清除");
        TextView cancel_txt = contentView.findViewById(R.id.cancel_txt);
        change_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                MorePopup.dismiss();
                rejectAll_AndClearMethod(Friend_NewFriends.this, saveFile.BaseUrl + saveFile.Friend_RejectAll_Url);
            }
        });
        newregistr_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                MorePopup.dismiss();
                clearHistory();
            }
        });
        cancel_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                MorePopup.dismiss();
            }
        });
    }

    //清除申请记录
    private void clearHistory() {
        new SimpleDialogFragment.Builder(mContext)
                .setTitle("是否清空全部历史记录？")
                .setOnConfirmClickListener(new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        rejectAll_AndClearMethod(Friend_NewFriends.this, saveFile.BaseUrl + saveFile.Friend_ClearApplyList_Url);
                    }
                })
                .showCancelButton(true)
                .show();
    }


    private List<NewFriend_Bean.DataDTO.ListDTO> baseModel;
    private NewFriend_Bean Model;

    public void NewFriendMethod(Context context, String baseUrl) {
        RequestParams params = new RequestParams(baseUrl);
        if (getShareData("JSESSIONID", context) != null) {
            params.setHeader("Authorization", getShareData("JSESSIONID", context));
        }
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
                if (resultString != null) {
                    Model = new Gson().fromJson(resultString, NewFriend_Bean.class);
                    if (Model.getCode() == -1 || Model.getCode() == 500) {
                        Intent intent = new Intent(context, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (Model.getCode() == 200) {

                        if (PageIndex == 1) {
                            if (baseModel != null) {
                                baseModel.clear();
                            }
                            baseModel = new ArrayList<>();
                        }

                        if (Model.getData() != null) {
                            baseModel.addAll(Model.getData().getList());
                            if (PageIndex == 1) {
                                list_xrecycler.refreshComplete();
                                initlist(context);
                            } else {
                                list_xrecycler.loadMoreComplete();
                                mAdapter.addMoreData(baseModel);
                            }

                        } else {
                            list_xrecycler.removeAllViews();
                            list_xrecycler.refreshComplete();
                            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(context, Model.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {
            }
        });
    }


    /**
     * 添加拒绝好友请求
     *
     * @param context
     * @param baseUrl
     * @param agreeType     1通过2拒绝
     * @param friendApplyId 好友申请ID
     */
    public void AddRemoveMethod(Context context, String baseUrl, String agreeType, String friendApplyId) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("agreeType", agreeType);
            obj.put("friendApplyId", friendApplyId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(baseUrl);
        if (saveFile.getShareData("JSESSIONID", context) != null) {
            params.setHeader("Authorization", saveFile.getShareData("JSESSIONID", context));
        }
        params.setAsJsonContent(true);
        params.setBodyContent(obj.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
                if (!TextUtils.isEmpty(resultString)) {
                    baseModel baseBean = new Gson().fromJson(resultString, baseModel.class);
                    if (baseBean.getCode() == -1) {
                        Intent intent = new Intent(context, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (baseBean.getCode() == 200) {
                        if (TextUtils.equals(agreeType, "1")) {
//                        Toast.makeText(context, baseBean.getMsg(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(mContext, "已通过", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "已拒绝", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, baseBean.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 拒绝与清除申请记录
     *
     * @param context
     * @param baseUrl
     */
    public void rejectAll_AndClearMethod(Context context, String baseUrl) {
        RequestParams params = new RequestParams(baseUrl);
        if (saveFile.getShareData("JSESSIONID", context) != null) {
            params.setHeader("Authorization", saveFile.getShareData("JSESSIONID", context));
        }
        params.setAsJsonContent(true);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
                if (!TextUtils.isEmpty(resultString)) {
                    baseModel baseBean = new Gson().fromJson(resultString, baseModel.class);
                    if (baseBean.getCode() == -1) {
                        Intent intent = new Intent(context, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (baseBean.getCode() == 200) {
                        list_xrecycler.refreshComplete();
                        Toast.makeText(context, baseBean.getMsg(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, baseBean.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

        });
    }

}