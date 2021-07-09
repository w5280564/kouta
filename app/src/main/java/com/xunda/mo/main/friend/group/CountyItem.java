package com.xunda.mo.main.friend.group;

import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baozi.treerecyclerview.base.ViewHolder;
import com.baozi.treerecyclerview.factory.ItemHelperFactory;
import com.baozi.treerecyclerview.item.TreeItem;
import com.baozi.treerecyclerview.item.TreeItemGroup;
import com.xunda.mo.R;
import com.xunda.mo.model.Friend_MyGroupBean;

import java.util.List;

/**
 *
 */
public class CountyItem extends TreeItemGroup<Friend_MyGroupBean.DataDTO.GroupListDTO> {

    @Override
    public List<TreeItem> initChild(Friend_MyGroupBean.DataDTO.GroupListDTO data) {
        return ItemHelperFactory.createItems((List) data, this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_two;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder) {
        holder.setText(R.id.tv_conten, data.getGroupName());
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, RecyclerView.LayoutParams layoutParams, int position) {
        super.getItemOffsets(outRect, layoutParams, position);
        outRect.top = 1;
    }

    @Override
    public boolean onInterceptClick(TreeItem child) {
        return super.onInterceptClick(child);
    }
}
