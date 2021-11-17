package com.xunda.mo.staticdata;

import com.hyphenate.easeui.domain.EaseUser;
import com.xunda.mo.hx.section.domain.MyEaseUser;

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


}
