package com.xunda.mo.main.friend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baozi.treerecyclerview.adpater.TreeRecyclerAdapter;
import com.baozi.treerecyclerview.adpater.TreeRecyclerType;
import com.baozi.treerecyclerview.factory.ItemHelperFactory;
import com.baozi.treerecyclerview.item.TreeItem;
import com.google.gson.Gson;
import com.xunda.mo.R;
import com.xunda.mo.main.MainLogin_Register;
import com.xunda.mo.model.Friend_MyGroupBean;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.SetStatusBar;
import com.xunda.mo.staticdata.viewTouchDelegate;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import static com.xunda.mo.network.saveFile.getShareData;

public class MyGroup extends AppCompatActivity {

    private RecyclerView group_Rv;
    private TreeRecyclerAdapter treeRecyclerAdapter;


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MyGroup.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);


        SetStatusBar.StatusBar(this);
        SetStatusBar.MIUISetStatusBarLightMode(this.getWindow(), true);
        SetStatusBar.FlymeSetStatusBarLightMode(this.getWindow(), true);


        initTitle();
        initView();
        initData();
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        title_Include.setElevation(2f);//阴影
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("群聊");
        Button right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.GONE);

        return_Btn.setOnClickListener(new return_Btn());
    }

    private class return_Btn implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            finish();
        }
    }

    private void initView() {

        group_Rv = findViewById(R.id.group_Rv);
        group_Rv.setLayoutManager(new LinearLayoutManager(this));
        group_Rv.setItemAnimator(new DefaultItemAnimator());
        //可折叠
        treeRecyclerAdapter = new TreeRecyclerAdapter(TreeRecyclerType.SHOW_EXPAND);
        group_Rv.setAdapter(treeRecyclerAdapter);
//        treeRecyclerAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(@NonNull @NotNull ViewHolder viewHolder, int position) {
//                ChatActivity.actionStart(MyGroup.this,model.getData().get(position).getGroupList().get(0).getGroupId() , DemoConstant.CHATTYPE_GROUP);
//
//            }
//        });


    }


    private void initData() {
        groupData(MyGroup.this, saveFile.BaseUrl + saveFile.Group_MyGroupList_Url);
    }

    //
    Friend_MyGroupBean model;
    public void groupData(final Context context, String baseUrl) {
        RequestParams params = new RequestParams(baseUrl);
        if (getShareData("JSESSIONID", context) != null) {
            params.setHeader("Authorization", getShareData("JSESSIONID", context));
        }
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
                //{"msg":"成功","code":200,"data":[{"count":0,"groupList":[],"listName":"ownGroupList"},{"count":1,"groupList":[{"groupHeadImg":"https://ahxd-private.obs.cn-east-3.myhuaweicloud.com:443/group%2F852966551628210176%2FheadImg.jpg","groupHxId":"151001522831361","groupId":"852966551628210176","groupName":"风风火火"}],"listName":"manageGroupList"},{"count":13,"groupList":[{"groupHeadImg":"https://ahxd-private.obs.cn-east-3.myhuaweicloud.com:443/group%2F852976212146315264%2FheadImg.jpg","groupHxId":"151003936653313","groupId":"852976212146315264","groupName":"福建"},{"groupHeadImg":"https://ahxd-private.obs.cn-east-3.myhuaweicloud.com:443/group%2F853196600964997120%2FheadImg.jpg","groupHxId":"151059034079235","groupId":"853196600964997120","groupName":"在线"},{"groupHeadImg":"https://ahxd-private.obs.cn-east-3.myhuaweicloud.com:443/group%2F853198508496707584%2FheadImg.jpg","groupHxId":"151059511181313","groupId":"853198508496707584","groupName":"福建"},{"groupHeadImg":"https://ahxd-private.obs.cn-east-3.myhuaweicloud.com:443/group%2F853199565364846592%2FheadImg.jpg","groupHxId":"151059775422466","groupId":"853199565364846592","groupName":"112233"},{"groupHeadImg":"https://ahxd-private.obs.cn-east-3.myhuaweicloud.com:443/group%2F853200057952296960%2FheadImg.jpg","groupHxId":"152173155516418","groupId":"853200057952296960","groupName":"22222"},{"groupHeadImg":"https://ahxd-private.obs.cn-east-3.myhuaweicloud.com:443/group%2F853203033580298240%2FheadImg.jpg","groupHxId":"151060642594817","groupId":"853203033580298240","groupName":"风风火火恍恍惚惚"},{"groupHeadImg":"https://ahxd-private.obs.cn-east-3.myhuaweicloud.com:443/group%2F853203392755326976%2FheadImg.jpg","groupHxId":"151060731723777","groupId":"853203392755326976","groupName":"福建福建"},{"groupHeadImg":"https://ahxd-private.obs.cn-east-3.myhuaweicloud.com:443/group%2F853203988468129792%2FheadImg.jpg","groupHxId":"151060880621569","groupId":"853203988468129792","groupName":"福建你们"},{"groupHeadImg":"https://ahxd-private.obs.cn-east-3.myhuaweicloud.com:443/group%2F857629855864791040%2FheadImg.jpg","groupHxId":"152167348502529","groupId":"857629855864791040","groupName":"测试2"},{"groupHeadImg":"https://ahxd-private.obs.cn-east-3.myhuaweicloud.com:443/group%2F857684043718139904%2FheadImg.jpg","groupHxId":"152180894007298","groupId":"857684043718139904","groupName":"今天下午吃饭"},{"groupHeadImg":"https://ahxd-private.obs.cn-east-3.myhuaweicloud.com:443/group%2F857691565002792960%2FheadImg.jpg","groupHxId":"152182737403905","groupId":"857691565002792960","groupName":"测试创"},{"groupHeadImg":"https://ahxd-private.obs.cn-east-3.myhuaweicloud.com:443/group%2F857693541836988416%2FheadImg.jpg","groupHxId":"152183269031938","groupId":"857693541836988416","groupName":"这首歌曲"},{"groupHeadImg":"https://ahxd-private.obs.cn-east-3.myhuaweicloud.com:443/group%2F853337514542817280%2F20210616201506.jpg","groupHxId":"151094263087105","groupId":"853337514542817280","groupName":"风景秀丽"}],"listName":"joinGroupList"}]}
                if (resultString != null) {
                     model = new Gson().fromJson(resultString, Friend_MyGroupBean.class);
                    if (model.getCode() == -1 || model.getCode() == 500) {
                        Intent intent = new Intent(context, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (model.getCode() == 200) {

                        //创建item
                        //新的
                        List<TreeItem> items = ItemHelperFactory.createItems(model.getData());
                        //添加到adapter
                        treeRecyclerAdapter.getItemManager().replaceAllItem(items);

                    } else {
                        Toast.makeText(context, model.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                String errStr = throwable.getMessage();
                if (errStr.equals("Authorization")) {
                    Intent intent = new Intent(context, MainLogin_Register.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onCancelled(Callback.CancelledException e) {
            }

            @Override
            public void onFinished() {
            }
        });



    }




}