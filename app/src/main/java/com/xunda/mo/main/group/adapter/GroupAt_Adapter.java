package com.xunda.mo.main.group.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.widget.EaseImageView;
import com.xunda.mo.R;
import com.xunda.mo.hx.section.domain.MyEaseUser;

import java.util.ArrayList;
import java.util.List;

public class GroupAt_Adapter extends EaseBaseRecyclerViewAdapter<MyEaseUser> {
    private List<String> existMembers;
    private List<String> selectedMembers;
    private List<EaseUser> MyEaseUserList;
    private boolean isCreateGroup;
    private OnSelectListener listener;

    public GroupAt_Adapter() {
        this.isCreateGroup = false;
        selectedMembers = new ArrayList<>();
        MyEaseUserList = new ArrayList<>();
    }

    public GroupAt_Adapter(boolean isCreateGroup) {
        this.isCreateGroup = isCreateGroup;
        selectedMembers = new ArrayList<>();
        MyEaseUserList = new ArrayList<>();
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater.from(mContext).inflate(R.layout.base_contact_check_item, parent, false));
    }

    @Override
    public int getEmptyLayoutId() {
        return R.layout.ease_layout_no_data_show_nothing;
    }

    /**
     * 不设置条目点击事件
     *
     * @return
     */
    @Override
    public boolean isItemClickEnable() {
        return false;
    }

    public void setExistMember(List<String> existMembers) {
        this.existMembers = existMembers;
        if (isCreateGroup) {
            selectedMembers.clear();
            selectedMembers.addAll(existMembers);
        }
        notifyDataSetChanged();
    }

    public List<String> getSelectedMembers() {
        return selectedMembers;
    }

    public List<EaseUser> getUserList() {
        return MyEaseUserList;
    }

    public class ContactViewHolder extends ViewHolder<MyEaseUser> {
        private TextView headerView;
        private CheckBox checkbox;
        private EaseImageView avatar;
        private TextView name;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            headerView = findViewById(R.id.header);
            checkbox = findViewById(R.id.checkbox);
            avatar = findViewById(R.id.avatar);
            name = findViewById(R.id.name);
//            avatar.setShapeType(DemoHelper.getInstance().getEaseAvatarOptions().getAvatarShape());
        }

        @Override
        public void setData(MyEaseUser item, int position) {
            String username = "";
            name.setText(item.getNickname());
            Glide.with(mContext).load(R.drawable.ease_groups_icon).into(avatar);

            headerView.setVisibility(View.GONE);
            checkbox.setVisibility(View.GONE);

            if (checkIfContains(username) || (!selectedMembers.isEmpty() && selectedMembers.contains(username))) {
                checkbox.setChecked(true);
                if (isCreateGroup) {
                    checkbox.setBackgroundResource(R.drawable.demo_selector_bg_check);
                    itemView.setEnabled(true);
                } else {
                    checkbox.setBackgroundResource(R.drawable.demo_selector_bg_gray_check);
                    itemView.setEnabled(true);
                }
            } else {
                checkbox.setBackgroundResource(R.drawable.demo_selector_bg_check);
                checkbox.setChecked(false);
                itemView.setEnabled(true);
            }

            String finalUsername = username;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkbox.setChecked(!checkbox.isChecked());
                    boolean checked = checkbox.isChecked();
                    if (isCreateGroup || !checkIfContains(finalUsername)) {
                        if (checked) {
                            if (!selectedMembers.contains(finalUsername)) {
                                selectedMembers.add(finalUsername);
                                MyEaseUserList.add(item);
                            }
                        } else {
                            if (selectedMembers.contains(finalUsername)) {
                                selectedMembers.remove(finalUsername);
                                MyEaseUserList.remove(item);
                            }
                        }
                    }
                    if (listener != null) {
                        listener.onSelected(position, selectedMembers);
                    }
                }
            });
        }
    }

    /**
     * 检查是否已存在
     *
     * @param username
     * @return
     */
    private boolean checkIfContains(String username) {
        if (existMembers == null) {
            return false;
        }
        return existMembers.contains(username);
    }

    /**
     * 因为环信id只能由字母和数字组成，如果含有“/”就可以认为是多端登录用户
     *
     * @param username
     * @return
     */
    private String getRealUsername(String username) {
        if (!username.contains("/")) {
            return username;
        }
        String[] multipleUser = username.split("/");
        return multipleUser[0];
    }

    public void setOnSelectListener(OnSelectListener listener) {
        this.listener = listener;
    }

    public interface OnSelectListener {
        void onSelected(int pos, List<String> selectedMembers);
    }

}
