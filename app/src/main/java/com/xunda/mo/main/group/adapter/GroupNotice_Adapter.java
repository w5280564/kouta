package com.xunda.mo.main.group.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xunda.mo.R;
import com.xunda.mo.model.Group_notices_Bean;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.StaticData;
import com.xunda.mo.view.LightningView;

import java.util.List;

/**
 * Created by Admin on
 */
public class GroupNotice_Adapter extends RecyclerView.Adapter<GroupNotice_Adapter.MyViewHolder> {
    List<Group_notices_Bean.DataDTO> otherList;
    private Group_notices_Bean listModel;
    Context context;
    private int myposition;

    public GroupNotice_Adapter(Context context, List<Group_notices_Bean.DataDTO> otherList, Group_notices_Bean listModel) {
        this.otherList = otherList;
        this.context = context;
        this.listModel = listModel;
    }

    public void addMoreData(List<Group_notices_Bean.DataDTO> otherList) {
        this.otherList = otherList;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myview = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.group_applylist_adapter, null));
        return myview;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
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

            holder.add_Btn.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    int pos = holder.getLayoutPosition();
                    if (v instanceof Button) {
                        v.setEnabled(false);
                        holder.isAgree_Group.setVisibility(View.GONE);
                        holder.time_Txt.setVisibility(View.VISIBLE);
                        holder.result_Txt.setText("已通过");
                    }
                    onItemAddRemoveClickLister.onItemAddClick(v, position);

                }
            });

            holder.refuse_Btn.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    int pos = holder.getLayoutPosition();
                    if (v instanceof Button) {
                        v.setEnabled(false);
                        holder.isAgree_Group.setVisibility(View.GONE);
                        holder.time_Txt.setVisibility(View.VISIBLE);
                        holder.result_Txt.setText("已拒绝");
                    }
                    onItemAddRemoveClickLister.onItemRemoveClick(v, position);
                }
            });

            holder.black_Btn.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    if (v instanceof Button) {
                        v.setEnabled(false);
                        holder.isAgree_Group.setVisibility(View.GONE);
                        holder.time_Txt.setVisibility(View.VISIBLE);
                        holder.result_Txt.setText("已拉黑");
                    }
                    onItemAddRemoveClickLister.onItemBlackClick(v,position);

                }
            });


        }

        if (otherList.isEmpty()) {
            return;
        }
        final Group_notices_Bean.DataDTO oneData = otherList.get(position);
        if (oneData.getHeadImg() != null) {
            Uri uri = Uri.parse(oneData.getHeadImg());
            holder.head_Simple.setImageURI(uri);
        } else {
//            StaticData.lodingheadBg(holder.head_simple);
        }
        holder.name_Txt.setTextColor(ContextCompat.getColor(context,R.color.blacktitle));
        holder.name_Txt.setText(oneData.getTitleName());
        holder.friend_Txt.setText(oneData.getEvent());
        holder.time_Txt.setText(StaticData.stampToDate(oneData.getCreateTime()));

        holder.vipType_txt.setVisibility(View.GONE);
        holder.isAgree_Group.setVisibility(View.GONE);
        holder.time_Txt.setVisibility(View.VISIBLE);
        if (oneData.getNotifyType() == 1){
            holder.result_Txt.setVisibility(View.INVISIBLE);
        }else {
            //1申请中2已通过3被拒绝4拉黑5已过期
            if (oneData.getApplyStatus() == 1) {
                holder.isAgree_Group.setVisibility(View.VISIBLE);
                holder.time_Txt.setVisibility(View.INVISIBLE);
            } else if (oneData.getApplyStatus() == 2) {
                holder.result_Txt.setText("已通过");
            } else if (oneData.getApplyStatus() == 3) {
                holder.result_Txt.setText("被拒绝");
            } else if (oneData.getApplyStatus() == 5) {
                holder.result_Txt.setText("已拉黑");
            }else if (oneData.getApplyStatus() == 4) {
                holder.result_Txt.setText("已过期");
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
        private final Group isAgree_Group;
        private  LightningView vipType_txt;
        private Button add_Btn, refuse_Btn, black_Btn;
        private SimpleDraweeView head_Simple;
        private TextView name_Txt;
        private TextView friend_Txt;
        private TextView result_Txt;
        private TextView time_Txt;

        public MyViewHolder(View itemView) {
            super(itemView);
            // 解决view宽和高不显示的问题
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(params);
            head_Simple = itemView.findViewById(R.id.head_Simple);
            name_Txt = itemView.findViewById(R.id.name_Txt);
            vipType_txt = itemView.findViewById(R.id.vipType_txt);
            friend_Txt = itemView.findViewById(R.id.friend_Txt);
            TextView apply_Txt = itemView.findViewById(R.id.apply_Txt);
            add_Btn = itemView.findViewById(R.id.add_Btn);
            refuse_Btn = itemView.findViewById(R.id.refuse_Btn);
            black_Btn = itemView.findViewById(R.id.black_Btn);
            result_Txt = itemView.findViewById(R.id.result_Txt);
            time_Txt = itemView.findViewById(R.id.time_Txt);
            isAgree_Group = itemView.findViewById(R.id.isAgree_Group);

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
        void onItemAddClick(View view, int position);

        void onItemRemoveClick(View view, int position);

        void onItemBlackClick(View view, int position);
    }

    private OnItemAddRemoveClickLister onItemAddRemoveClickLister;

    public void setOnItemAddRemoveClickLister(OnItemAddRemoveClickLister ItemListener) {
        onItemAddRemoveClickLister = ItemListener;
    }


}
