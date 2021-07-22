package com.xunda.mo.main.group.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.xunda.mo.R;
import com.xunda.mo.model.NewFriend_Bean;

import lombok.SneakyThrows;

/**
 * Created by Admin on
 */
public class GroupDetail_Edit_Adress_Adapter extends RecyclerView.Adapter<GroupDetail_Edit_Adress_Adapter.MyViewHolder> {
    PoiResult otherList;
    private NewFriend_Bean listModel;
    Context context;
    private int myposition;

    public GroupDetail_Edit_Adress_Adapter(Context context, PoiResult poiResult) {
        this.otherList = poiResult;
        this.context = context;
    }

    public void addMoreData(PoiResult otherList) {
//        this.otherList = otherList;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myview = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.groupdetail_edit_adress_adapter, null));
        return myview;
    }

    @SneakyThrows
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
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

        holder.send_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendClickLitener.onClick(v,position);
            }
        });


        if (otherList.getPois().isEmpty()) {
            return;
        }
        PoiItem oneData = otherList.getPois().get(position);
        holder.adress_Txt.setText(oneData.toString());

        String content = oneData.getProvinceName() + oneData.getCityName() + oneData.getAdName() + oneData.getSnippet();
        holder.content_txt.setText(content);

    }

    @Override
    public int getItemCount() {
//        return 10;
        return (otherList == null) ? 0 : otherList.getPois().size();//数据加一项
    }

    @Override
    public int getItemViewType(int position) {
        return myposition = position;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private Button send_Btn;
        private TextView adress_Txt, content_txt;

        public MyViewHolder(View itemView) {
            super(itemView);
//            // 解决view宽和高不显示的问题
//            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            itemView.setLayoutParams(params);
            adress_Txt = itemView.findViewById(R.id.adress_Txt);
            content_txt = itemView.findViewById(R.id.content_txt);
            send_Btn = itemView.findViewById(R.id.send_Btn);

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

    public interface OnSendClickLitener {
        void onClick(View v, int pos);
    }

    private OnSendClickLitener onSendClickLitener;

    public void setOnSendClickLitener(OnSendClickLitener listener) {
        onSendClickLitener = listener;

    }


}
