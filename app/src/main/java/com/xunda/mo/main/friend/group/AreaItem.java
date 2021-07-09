package com.xunda.mo.main.friend.group;


import android.net.Uri;

import androidx.annotation.NonNull;

import com.baozi.treerecyclerview.base.ViewHolder;
import com.baozi.treerecyclerview.item.TreeItem;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xunda.mo.R;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.section.chat.activicy.ChatActivity;
import com.xunda.mo.model.Friend_MyGroupBean;

/**
 *
 */
public class AreaItem extends TreeItem<Friend_MyGroupBean.DataDTO.GroupListDTO> {

    @Override
    public int getLayoutId() {
        return R.layout.item_two;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder) {
        holder.setText(R.id.tv_conten, data.getGroupName());
        SimpleDraweeView my_SimpleView = holder.getView(R.id.my_SimpleView);
        Uri headUri = Uri.parse(data.getGroupHeadImg());
        my_SimpleView.setImageURI(headUri);

    }


    @Override
    public int getSpanSize(int maxSpan) {
        return 0;
    }


    @Override
    public void onClick(ViewHolder viewHolder) {
        super.onClick(viewHolder);

        ChatActivity.actionStart(viewHolder.itemView.getContext(), data.getGroupHxId(), DemoConstant.CHATTYPE_GROUP);
    }
}
