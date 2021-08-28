package com.xunda.mo.main.friend.myAdapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xunda.mo.R;
import com.xunda.mo.model.AddFriend_FriendGroup_Model;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by Admin on
 */
public class Friend_Seek_GroupList_Adapter extends RecyclerView.Adapter<Friend_Seek_GroupList_Adapter.MyViewHolder> {
    List<AddFriend_FriendGroup_Model.DataDTO.ListDTO> otherList;
    private AddFriend_FriendGroup_Model listModel;
    Context context;
    private int myposition;

    public Friend_Seek_GroupList_Adapter(Context context, List<AddFriend_FriendGroup_Model.DataDTO.ListDTO> otherList, AddFriend_FriendGroup_Model listModel) {
        this.otherList = otherList;
        this.context = context;
        this.listModel = listModel;
    }

    public void addMoreData(List<AddFriend_FriendGroup_Model.DataDTO.ListDTO> otherList) {
//        this.otherList = otherList;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myview = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.friend_seek_friendlist_adapter, null));
        return myview;
    }

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

        final AddFriend_FriendGroup_Model.DataDTO.ListDTO oneData = otherList.get(position);
        if (oneData.getGroupHeadImg() != null) {
            Uri uri = Uri.parse(oneData.getGroupHeadImg());
            holder.head_simple.setImageURI(uri);
        } else {
//            StaticData.lodingheadBg(holder.head_simple);
        }
        holder.name.setText(oneData.getGroupName());
        holder.contentid_txt.setText("ID: " + oneData.getGroupNum());


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
        private SimpleDraweeView head_simple;
        private TextView name, vipType_txt, contentid_txt;

        public MyViewHolder(View itemView) {
            super(itemView);
            head_simple = itemView.findViewById(R.id.head_simple);
            name = itemView.findViewById(R.id.name);
            vipType_txt = itemView.findViewById(R.id.vipType_txt);
            contentid_txt = itemView.findViewById(R.id.contentid_txt);


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


}
