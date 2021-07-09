package com.xunda.mo.hx.section.group.adapter;

import com.xunda.mo.R;
import com.xunda.mo.hx.section.contact.adapter.ContactListAdapter;

public class GroupMemberAuthorityAdapter extends ContactListAdapter {

    @Override
    public int getEmptyLayoutId() {
        return R.layout.ease_layout_default_no_data;
    }
}
