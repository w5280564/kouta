package com.xunda.mo.pinyin;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xunda.mo.R;
import com.xunda.mo.view.LightningView;

import java.util.List;

/**
 * @author: xp
 * @date: 2017/7/19
 */

public class SortAdapter extends RecyclerView.Adapter<SortAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private List<SortModel> mData;
    private Context mContext;

    public SortAdapter(Context context, List<SortModel> data) {
        mInflater = LayoutInflater.from(context);
        mData = data;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_name, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.tvName = (TextView) view.findViewById(R.id.tvName);
        viewHolder.head_simple = view.findViewById(R.id.head_simple);
        viewHolder.state_txt = view.findViewById(R.id.state_txt);
        viewHolder.vipType_txt = view.findViewById(R.id.vipType_txt);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });

        }
        int nameLength = mData.get(position).getName().length();
        String name = mData.get(position).getName() + " (" + mData.get(position).getUserNum() + ")";
//        String name = mData.get(position).getName();
        long strVip = mData.get(position).getVipType();
        if (strVip == 0) {
            holder.tvName.setText(name);
            holder.state_txt.setVisibility(View.VISIBLE);
            holder.state_txt.setText(String.valueOf(mData.get(position).getLightStatus()));
            holder.vipType_txt.setVisibility(View.VISIBLE);

        } else if (strVip == 1){
            holder.state_txt.setVisibility(View.INVISIBLE);
            holder.vipType_txt.setVisibility(View.INVISIBLE);
            SpannableString spannableString = new SpannableString(name);
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(mContext.getColor(R.color.yellowfive));
            StyleSpan styleSpan_B = new StyleSpan(Typeface.BOLD);//粗体
            RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(1.1f);//字放大
            spannableString.setSpan(styleSpan_B, 0, nameLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(foregroundColorSpan, 0, nameLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(relativeSizeSpan, 0, nameLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            holder.tvName.setText(spannableString);
        }

        if (mData.get(position).getHeadImg() != null) {
            Uri imgUri = Uri.parse(mData.get(position).getHeadImg());
            holder.head_simple.setImageURI(imgUri);
        }


//        holder.tvName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(mContext, mData.get(position).getName(), Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    //**********************itemClick************************
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
    //**************************************************************

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LightningView vipType_txt;
        SimpleDraweeView head_simple;
        TextView tvName, state_txt;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 提供给Activity刷新数据
     *
     * @param list
     */
    public void updateList(List<SortModel> list) {
        this.mData = list;
        notifyDataSetChanged();
    }

    public Object getItem(int position) {
        return mData.get(position);
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    public int getSectionForPosition(int position) {
        return mData.get(position).getLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mData.get(i).getLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

}
