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
import com.xunda.mo.model.GroupMember_Bean;
import com.xunda.mo.model.NewFriend_Bean;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.view.LightningView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Admin on
 */
public class GroupAllMembers_Manage_SetupManage_Adapter extends RecyclerView.Adapter<GroupAllMembers_Manage_SetupManage_Adapter.MyViewHolder> {
    List<GroupMember_Bean.DataDTO> otherList;
    Context context;
    private int myposition;

    public GroupAllMembers_Manage_SetupManage_Adapter(Context context, List<GroupMember_Bean.DataDTO> otherList) {
        this.otherList = otherList;
        this.context = context;
    }

    public void addMoreData(List<NewFriend_Bean.DataDTO.ListDTO> otherList) {
//        this.otherList = otherList;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeData(int position) {
        otherList.remove(position);
        notifyItemRemoved(position);
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
        // ?????????????????????????????????????????????
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(v -> {
                int pos = holder.getLayoutPosition();
                mOnItemClickLitener.onItemClick(holder.itemView, position);
            });

            holder.itemView.setOnLongClickListener(v -> {
                int pos = holder.getLayoutPosition();
                mOnItemClickLitener.onItemLongClick(holder.itemView, position);
                return false;
            });

        }
        //??????????????????
        if (onItemAddRemoveClickLister != null) {
            holder.remove_Btn.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    int pos = holder.getLayoutPosition();
                    if (v instanceof Button) {
//                        v.setVisibility(View.GONE);
                        v.setEnabled(false);
                    }
                    onItemAddRemoveClickLister.onItemRemoveClick(v, position);

                }
            });

        }

        if (otherList.isEmpty()) {
            return;
        }
        final GroupMember_Bean.DataDTO oneData = otherList.get(position);
        String avatarString = oneData.getHeadImg();
        Glide.with(context).load(avatarString).into(holder.head_Simple);

        if (oneData.getVipType() == 0) {
            holder.vipType_txt.setVisibility(View.GONE);
            holder.name_Txt.setTextColor(context.getColor(R.color.blacktitle));
        } else if (oneData.getVipType() == 1) {
            holder.vipType_txt.setVisibility(View.VISIBLE);
            holder.name_Txt.setTextColor(context.getColor(R.color.yellow));
        }
        holder.name_Txt.setText(oneData.getNickname());
        holder.moID_Txt.setText("Mo ID???" + oneData.getUserNum());


    }

    @Override
    public int getItemCount() {
//        return 10;
        return (otherList == null) ? 0 : otherList.size();//???????????????
    }

    @Override
    public int getItemViewType(int position) {
        return myposition = position;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LightningView vipType_txt;
        private Button remove_Btn;
        private EaseImageView head_Simple;
        private TextView name_Txt, moID_Txt;

        public MyViewHolder(View itemView) {
            super(itemView);
            // ??????view???????????????????????????
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
