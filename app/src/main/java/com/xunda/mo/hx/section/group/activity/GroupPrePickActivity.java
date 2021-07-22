package com.xunda.mo.hx.section.group.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.hyphenate.easeui.domain.EaseUser;
import com.xunda.mo.main.group.activity.newGroup;

import java.util.List;

public class GroupPrePickActivity extends GroupPickContactsActivity {

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, GroupPrePickActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onRightClick(View view) {

        List<EaseUser> selectUserList = adapter.getUserList();
        List<String> selectedMembers = adapter.getSelectedMembers();
        String[] newMembers = null;
        if (selectedMembers.isEmpty() || selectedMembers.size() < 2) {
            Toast.makeText(GroupPrePickActivity.this, "群成员不能少于两人", Toast.LENGTH_SHORT).show();
            return;
        }
//        if (selectedMembers != null && !selectedMembers.isEmpty()) {
//        newMembers = selectedMembers.toArray(new String[0]);
//        }
        newGroup.actionStart(mContext,selectedMembers,selectUserList);
        finish();
    }
}

