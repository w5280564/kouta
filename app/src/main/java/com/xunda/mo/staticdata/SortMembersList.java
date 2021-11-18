package com.xunda.mo.staticdata;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.hyphenate.easeui.domain.EaseUser;
import com.xunda.mo.hx.section.domain.MyEaseUser;
import com.xunda.mo.model.GroupMemberAdd_Bean;
import com.xunda.mo.pinyin.PinyinUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortMembersList {
    //升序列表
    public static List<MyEaseUser> getLastDescList(List<MyEaseUser> list) {
        Collections.sort(list, new Comparator<MyEaseUser>() {
            @Override
            public int compare(MyEaseUser o1, MyEaseUser o2) {
                return o1.getInitialLetter().compareTo(o2.getInitialLetter());
            }
        });
        return list;
    }

        public static List<EaseUser> getLastList(List<EaseUser> list) {
        Collections.sort(list, new Comparator<EaseUser>() {
            @Override
            public int compare(EaseUser o1, EaseUser o2) {
                return o1.getInitialLetter().compareTo(o2.getInitialLetter());
            }
        });
        return list;
    }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public static List<GroupMemberAdd_Bean.DataDTO> getMemberList(List<GroupMemberAdd_Bean.DataDTO> list) {
        Collections.sort(list, Comparator.comparing(o -> PinyinUtils.getFirstSpell(o.getNickname())));
        return list;
    }


}
