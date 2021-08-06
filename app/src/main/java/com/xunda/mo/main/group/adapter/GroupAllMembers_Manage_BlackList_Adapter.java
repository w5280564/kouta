package com.xunda.mo.main.group.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hyphenate.easeui.widget.EaseImageView;
import com.xunda.mo.R;
import com.xunda.mo.model.GroupBlackList_Bean;
import com.xunda.mo.staticdata.NoDoubleClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Admin on
 */
public class GroupAllMembers_Manage_BlackList_Adapter extends RecyclerView.Adapter<GroupAllMembers_Manage_BlackList_Adapter.MyViewHolder> {
    List<GroupBlackList_Bean.DataDTO> otherList;
    Context context;
    private int myposition;

    public GroupAllMembers_Manage_BlackList_Adapter(Context context, List<GroupBlackList_Bean.DataDTO> otherList) {
        this.otherList = otherList;
        this.context = context;
    }

    public void addMoreData(List<GroupBlackList_Bean.DataDTO> otherList) {
//        this.otherList = otherList;
        notifyDataSetChanged();
    }

    public void setData(List<GroupBlackList_Bean.DataDTO> otherList) {
        this.otherList = otherList;
        notifyDataSetChanged();
    }

    public void removeData(int position) {
        otherList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myview = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.group_setupmanage_adapter, null));
        return myview;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(@NotNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
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
        //接受拒绝请求
        if (onItemAddRemoveClickLister != null) {
            holder.remove_Btn.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    int pos = holder.getLayoutPosition();
                    if (v instanceof Button) {
                        v.setVisibility(View.GONE);
                        v.setEnabled(false);
                    }
                    onItemAddRemoveClickLister.onItemRemoveClick(v, position);

                }
            });

        }

        if (otherList.isEmpty()) {
            return;
        }
        final GroupBlackList_Bean.DataDTO oneData = otherList.get(position);
        String avatarString = oneData.getHeadImg();
        Glide.with(context).load(avatarString).into(holder.head_Simple);

        holder.name_Txt.setText(oneData.getNickname());

        String moIDStr = String.format("Mo ID：%1$s·禁止加入", oneData.getUserNum());
        holder.moID_Txt.setText(moIDStr);


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
        private Button remove_Btn;
        private EaseImageView head_Simple;
        private TextView name_Txt, vipType_txt, moID_Txt;

        public MyViewHolder(View itemView) {
            super(itemView);
            // 解决view宽和高不显示的问题
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(params);
            head_Simple = itemView.findViewById(R.id.head_Simple);
            name_Txt = itemView.findViewById(R.id.name_Txt);
            vipType_txt = itemView.findViewById(R.id.vipType_txt);
            moID_Txt = itemView.findViewById(R.id.moID_Txt);
            remove_Btn = itemView.findViewById(R.id.remove_Btn);

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


    public interface OnItemAddRemoveClickLister {

        void onItemRemoveClick(View view, int position);
    }

    private OnItemAddRemoveClickLister onItemAddRemoveClickLister;

    public void setOnItemAddRemoveClickLister(OnItemAddRemoveClickLister ItemListener) {
        onItemAddRemoveClickLister = ItemListener;
    }


}
