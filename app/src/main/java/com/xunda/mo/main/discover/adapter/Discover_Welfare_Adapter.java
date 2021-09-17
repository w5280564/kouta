package com.xunda.mo.main.discover.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xunda.mo.R;
import com.xunda.mo.model.Discover_WelfareCardList_Bean;
import com.xunda.mo.model.NewFriend_Bean;
import com.xunda.mo.staticdata.NoDoubleClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Admin on
 */
public class Discover_Welfare_Adapter extends RecyclerView.Adapter<Discover_Welfare_Adapter.MyViewHolder> {
    List<Discover_WelfareCardList_Bean.DataDTO.ExchangeCardVosDTO> otherList;
    Context context;
    private int myposition;

    public Discover_Welfare_Adapter(Context context, List<Discover_WelfareCardList_Bean.DataDTO.ExchangeCardVosDTO> otherList) {
        this.otherList = otherList;
        this.context = context;
    }

    public void addMoreData(List<NewFriend_Bean.DataDTO.ListDTO> otherList) {
//        this.otherList = otherList;
        notifyDataSetChanged();
    }

    public void removeData(int position) {
        otherList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myview = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.discover_welfare_adapter, null));
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

        if (onItemAddRemoveClickLister != null) {
            holder.change_Btn.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    int pos = holder.getLayoutPosition();
                    if (v instanceof Button) {
                        v.setEnabled(false);
                    }
                    onItemAddRemoveClickLister.onItemRemoveClick(v, position);

                }
            });

        }


        if (otherList.isEmpty()) {
            return;
        }
        final Discover_WelfareCardList_Bean.DataDTO.ExchangeCardVosDTO oneData = otherList.get(position);
        holder.name_Txt.setText(oneData.getCardName());
        holder.fen_Txt.setText(oneData.getRequiredPoints() + "积分兑换");
        int type = oneData.getType();
        int status = oneData.getStatus();
        //1折扣卷 2体验卷 3经验卷
        int avatarId = R.drawable.mo_icon;
        if (type == 1) {
            avatarId = R.mipmap.integral_coupon_icon;
            changeColor(type, status, holder.change_Btn);
        } else if (type == 2) {
            avatarId = R.mipmap.integral_attempt_icon;
            changeColor(type, status, holder.change_Btn);
        } else if (type == 3) {
            avatarId = R.mipmap.integral_exp_icon;
            changeColor(type, status, holder.change_Btn);
        }
        Glide.with(context).load(avatarId).into(holder.head_Simple);

    }

    private void changeColor(int type, Integer status, Button change_Btn) {
        int color = R.color.greyfive;
        if (type == 1) {
            color = R.color.blue;
            change_Btn.setBackgroundResource(R.drawable.welfareadapter_blue_stroke);
            if (status == 1) {
                change_Btn.setBackgroundResource(R.drawable.welfareadapter_blue);
            }
        } else if (type == 2) {
            color = R.color.purpleTxt;
            change_Btn.setBackgroundResource(R.drawable.welfareadapter_purple_stroke);
            if (status == 1) {
                change_Btn.setBackgroundResource(R.drawable.welfareadapter_purple);
            }
        } else if (type == 3) {
            color = R.color.yellowfive;
            change_Btn.setBackgroundResource(R.drawable.welfareadapter_yellow_stroke);
            if (status == 1) {
                change_Btn.setBackgroundResource(R.drawable.welfareadapter_yellow);
            }
        }
        change_Btn.setTextColor(ContextCompat.getColor(context, color));
        String changeStr = "兑换";
        if (status == 1){
             changeStr = "已兑换";
        }
        change_Btn.setText(changeStr);
    }


    @Override
    public int getItemCount() {
        return (otherList == null) ? 0 : otherList.size();//数据加一项
    }

    @Override
    public int getItemViewType(int position) {
        return myposition = position;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private Button change_Btn;
        private ImageView head_Simple;
        private TextView name_Txt, fen_Txt;

        public MyViewHolder(View itemView) {
            super(itemView);
            // 解决view宽和高不显示的问题
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(params);
            head_Simple = itemView.findViewById(R.id.head_Simple);
            name_Txt = itemView.findViewById(R.id.name_Txt);
            fen_Txt = itemView.findViewById(R.id.fen_Txt);
            change_Btn = itemView.findViewById(R.id.change_Btn);

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
