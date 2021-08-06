package com.xunda.mo.main.info;

public class NameResource {

  public static String randomName() {
       String str = "喊我女王万万岁i粉红色的记忆飞天小魔女doremiAde（再见）眼不见心还念~又 鹿爱人-见窄巷" +
               "时光巷陌 劳娘@伴我久久你终走 笙歌千年 微弱的灯光 。3d坦克天使的诱惑年格少时难免轻狂" +
               "过又能怎样——木蘭香，遮還住傷 残花败柳*ˊMeteor宇@彪悍公主~最萌女生霸道の小男人回忆带" +
               "分离重逢相爱爱痛三萌大帅比眼角的泪水不羁放纵的女人呼吸都会背后 余存° d3sTiny 相遇-" +
               "中国好呻吟i久遇有者不惜 女人不花丶何来貌美如花光棍节一刺连事无成舞媚娘.少年先瘋隊隊長" +
               "旧歌如梦中▁_.炼狱№你的绿叶我的花°°ふダ＿独特 你挺能闹??有种努力叫奋斗苊是、蔬菜蛤" +
               "Favorite丶等待[皒]犯賤ㄋ微笑，只是一种表情@小小～冰淇凌Somnus°輕描淡寫第一帅男" +
               "迩、忘楽莪庅少年包工头2得很有文艺范 男人的故事谁懂ジ爷丶狠拽彡格不入﹏Kiss° ﹏Miss°" +
               "屿南凉北宅女宅女蹦擦擦＊笑看红尘，花落一梦破晓←前跷二郎腿的女汉子▁▁▁转身后の哭泣、、" +
               "Bad Boy默許半個未來ぅ不狂能叫小夏以乔木青年@轮囘Li巡影っ冫笑亚洲首帅 i獨寵于妳帅的被人砍";
        //空串，准备接收一个网名
        String sss = "";
        int len = str.length();
        int count = 0;
        //随机获取姓名长度
        int ran = (int) (Math.random() * 10);
        while (true) {
            char ch = str.charAt((int) (Math.random() * len));
            sss += ch;
            if (count == ran)
                break;
            count++;
        }
        return sss;
    }
}
