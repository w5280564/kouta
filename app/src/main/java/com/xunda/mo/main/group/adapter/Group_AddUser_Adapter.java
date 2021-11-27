package com.xunda.mo.main.group.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.easeui.domain.EaseUser;
import com.xunda.mo.R;
import com.xunda.mo.model.NewFriend_Bean;
import com.xunda.mo.view.LightningView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import lombok.SneakyThrows;

/**
 * Created by Admin on
 */
public class Group_AddUser_Adapter extends RecyclerView.Adapter<Group_AddUser_Adapter.MyViewHolder> {
    List<EaseUser> otherList;
    private NewFriend_Bean listModel;
    Context context;
    private int myposition;

    public Group_AddUser_Adapter(Context context, List<EaseUser> otherList) {
        this.otherList = otherList;
        this.context = context;
    }

    public void addMoreData(List<EaseUser> otherList) {
//        this.otherList = otherList;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myview = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.group_adduser_adapter, null));
        return myview;
    }

    @SneakyThrows
    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(holder.itemView, position);
                    return false;
                }
            });

        }

        if (otherList.isEmpty()) {
            return;
        }
        final EaseUser oneData = otherList.get(position);
        if (oneData.getAvatar() != null) {
            Uri uri = Uri.parse(oneData.getAvatar());
            holder.head_Simple.setImageURI(uri);
        } else {
//            StaticData.lodingheadBg(holder.head_simple);
        }
//        try {
//            String selectInfoExt = oneData.getExt();
//            JSONObject jsonObject = new JSONObject(selectInfoExt);//用户资料扩展属性
//            String name = TextUtils.isEmpty(jsonObject.getString("remarkName")) ? oneData.getNickname() : jsonObject.getString("remarkName");
//            int nameLength = name.length();
//            String nameAndNum = name + " (" + jsonObject.getString("userNum") + ")";
//            setName(nameAndNum, nameLength, holder.name_Txt);
//
//            String vipTypeString = jsonObject.getString("vipType");
//            if (TextUtils.equals(vipTypeString, "0")) {
//                holder.vipType_txt.setVisibility(View.GONE);
//            } else {
//                holder.vipType_txt.setVisibility(View.VISIBLE);
//            }
//        } catch (
//                JSONException e) {
//            e.printStackTrace();
//        }


        JSONObject myobJson = getExtStr(oneData);
        if (myobJson != null) {
            String name = TextUtils.isEmpty(myobJson.getString("remarkName")) ? oneData.getNickname() : myobJson.getString("remarkName");
            int nameLength = name.length();
            String nameAndNum = name + " (" + myobJson.getString("userNum") + ")";
            setName(nameAndNum, nameLength, holder.name_Txt);

            String vipTypeString = myobJson.getString("vipType");
            if (TextUtils.equals(vipTypeString, "0")) {
                holder.vipType_txt.setVisibility(View.GONE);
            } else {
                holder.vipType_txt.setVisibility(View.VISIBLE);
            }
        }


    }

    @Override
    public int getItemCount() {
//        return 10;
        return (otherList == null) ? 0 : otherList.size();//数据加一项
    }

    @Override
    public int getItemViewType(int position) {
        return myposition = position;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private  LightningView vipType_txt;
        private SimpleDraweeView head_Simple;
        private TextView name_Txt;

        public MyViewHolder(View itemView) {
            super(itemView);
            // 解决view宽和高不显示的问题
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(params);
            head_Simple = itemView.findViewById(R.id.head_Simple);
            name_Txt = itemView.findViewById(R.id.name_Txt);
            vipType_txt = itemView.findViewById(R.id.vipType_txt);
            TextView friend_Txt = itemView.findViewById(R.id.friend_Txt);

        }
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);

    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener listener) {
        mOnItemClickLitener = listener;
    }


    /**
     * @param name       要显示的数据
     * @param nameLength 要放大改颜色的字体长度
     * @param viewName
     */
    private void setName(String name, int nameLength, TextView viewName) {
        SpannableString spannableString = new SpannableString(name);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(context.getColor(R.color.yellowfive));
//        StyleSpan styleSpan_B = new StyleSpan(Typeface.BOLD);//粗体
        RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(1.1f);//字放大
//        spannableString.setSpan(styleS
//        pan_B, 0, nameLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(foregroundColorSpan, 0, nameLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(relativeSizeSpan, 0, nameLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        viewName.setText(spannableString);
    }

    private JSONObject getExtStr(EaseUser myEaseUser) {
        try {
            String selectInfoExt = myEaseUser.getExt();
            JSONObject jsonObject = new JSONObject(selectInfoExt);//用户资料扩展属性

            return jsonObject;
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
