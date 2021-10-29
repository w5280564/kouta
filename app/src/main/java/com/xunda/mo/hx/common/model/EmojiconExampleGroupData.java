package com.xunda.mo.hx.common.model;

import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojicon.Type;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.xunda.mo.R;

import java.util.Arrays;

public class EmojiconExampleGroupData {
    
    private static int[] icons = new int[]{
        R.drawable.icon_002_cover,
        R.drawable.icon_007_cover,
        R.drawable.icon_010_cover,
        R.drawable.icon_012_cover,
        R.drawable.icon_013_cover,
        R.drawable.icon_018_cover,
        R.drawable.icon_019_cover,
        R.drawable.icon_020_cover,
        R.drawable.icon_021_cover,
        R.drawable.icon_022_cover,
        R.drawable.icon_024_cover,
        R.drawable.icon_027_cover,
        R.drawable.icon_029_cover,
        R.drawable.icon_030_cover,
        R.drawable.icon_035_cover,
        R.drawable.icon_040_cover,
    };
    
    private static int[] bigIcons = new int[]{
        R.drawable.icon_002,
        R.drawable.icon_007,
        R.drawable.icon_010,
        R.drawable.icon_012,
        R.drawable.icon_013,
        R.drawable.icon_018,
        R.drawable.icon_019,
        R.drawable.icon_020,
        R.drawable.icon_021,
        R.drawable.icon_022,
        R.drawable.icon_024,
        R.drawable.icon_027,
        R.drawable.icon_029,
        R.drawable.icon_030,
        R.drawable.icon_035,
        R.drawable.icon_040,
    };

    private static String[] icons_name = new String[]{
            "哭",
            "嘚瑟",
            "跳舞",
            "耸肩",
            "bye",
            "升天",
            "痴迷",
            "晚安",
            "爱你",
            "拜托",
            "no",
            "生日快乐",
            "哼",
            "抓狂",
            "扭一扭",
            "尬舞",
    };
    
    
    private static final EaseEmojiconGroupEntity DATA = createData();
    
    private static EaseEmojiconGroupEntity createData(){
        EaseEmojiconGroupEntity emojiconGroupEntity = new EaseEmojiconGroupEntity();
        EaseEmojicon[] datas = new EaseEmojicon[icons.length];
        for(int i = 0; i < icons.length; i++){
            datas[i] = new EaseEmojicon(icons[i], null, Type.BIG_EXPRESSION);
            datas[i].setBigIcon(bigIcons[i]);
            //you can replace this to any you want
//            datas[i].setName(MyApplication.getInstance().getApplicationContext().getString(R.string.emojicon_test_name)+ (i+1));
            datas[i].setName(icons_name[i]);
            datas[i].setIdentityCode("em"+ (1000+i+1));
        }
        emojiconGroupEntity.setEmojiconList(Arrays.asList(datas));
        emojiconGroupEntity.setIcon(R.drawable.icon_002_cover);
        emojiconGroupEntity.setType(Type.BIG_EXPRESSION);
        return emojiconGroupEntity;
    }
    
    
    public static EaseEmojiconGroupEntity getData(){
        return DATA;
    }
}
