package com.xunda.mo.main.friend.group;

import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.baozi.treerecyclerview.base.ViewHolder;
import com.baozi.treerecyclerview.factory.ItemHelperFactory;
import com.baozi.treerecyclerview.item.TreeItem;
import com.baozi.treerecyclerview.item.TreeItemGroup;
import com.xunda.mo.R;
import com.xunda.mo.model.Friend_MyGroupBean;

import java.util.List;

/**
 * Created by baozi on 2016/12/8.
 */
public class ProvinceItem extends TreeItemGroup<Friend_MyGroupBean.DataDTO> {
//    @Override
//    protected void init() {
//        super.init();
//        setExpand(false);
//    }


    @Override
    public int getLayoutId() {
        return R.layout.itme_one;
    }

    @Override
    public boolean isCanExpand() {
        return true;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    protected List<TreeItem> initChild(Friend_MyGroupBean.DataDTO data) {
        List<TreeItem> items = ItemHelperFactory.createItems(data.getGroupList(), this);
//        for (int i = 0; i < items.size(); i++) {
//            TreeItemGroup treeItem = (TreeItemGroup) items.get(i);
//            treeItem.setExpand(false);
//        }
        return items;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder) {
        if (isExpand()) {
            holder.setImageResource(R.id.iv_right, R.drawable.ic_keyboard_arrow_down_black_24dp);
        } else {
            holder.setImageResource(R.id.iv_right, R.drawable.ic_keyboard_arrow_right_black_24dp);
        }
        String listName = "群聊";
        if (data.getListName().equals("ownGroupList")){
            listName = "我创建的群聊";
        }else if (data.getListName().equals("manageGroupList")){
            listName = "我管理的群聊";
        }else if (data.getListName().equals("joinGroupList")){
            listName = "我加入的群聊";
        }
        holder.setText(R.id.tv_name, listName);
        holder.setText(R.id.tv_content, data.getCount()+"");
    }

    @Override
    public void onClick(ViewHolder viewHolder) {
        super.onClick(viewHolder);
        if (isExpand()) {
            viewHolder.setImageResource(R.id.iv_right, R.drawable.ic_keyboard_arrow_down_black_24dp);
        } else {
            viewHolder.setImageResource(R.id.iv_right, R.drawable.ic_keyboard_arrow_right_black_24dp);
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, RecyclerView.LayoutParams layoutParams, int position) {
        super.getItemOffsets(outRect, layoutParams, position);
        outRect.bottom = 1;
    }
}
