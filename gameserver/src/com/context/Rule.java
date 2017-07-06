package com.context;

/**
 * 胡牌规则
 *
 * @author luck
 */
public class Rule {
    //公用
    public static String Hu_zi_common = "zi_common";//自摸普通胡
    public static String Hu_other_common = "other_common";//别人自摸普通胡
    public static String Hu_d_self = "d_self";//别人点自己胡
    public static String Hu_d_other = "d_other";//自己点别人胡
    public static String Hu_d_qingyise = "qingyise";//点炮清一色
    public static String Hu_zi_qingyise = "zi_qingyise";//自摸清一色
    public static String Hu_self_qixiaodui = "self_qixiaodui";
    public static String Hu_other_qixiaodui = "other_qixiaodui";

    //杠牌
    public static String Gang_ming = "ming";
    public static String Gang_an = "an";
    public static String Gang_dian = "diangang";
    public static String Gang_Hua = "huagang";

    public static String Gang_fang = "fanggang";//放杠

    public static String Dasizhang = "dasizhang";//放杠
    public static String Genpai = "genpai";//放杠

    /*
     1：小胡                 需要至少4个硬花
     1<<1：门清              20 城北10 门清可以成小胡
     1<<2：混一色            40 城北30
     1<<3：清一色            60 城北50
     1<<4：对对胡            40 城北30
     1<<5：全球独钓          60 城北50
     1<<6：七对              40 城北50  七对不计门清
     1<<7：豪华七对          160
     1<<8：超豪华七对        240
     1<<9：超超豪华七对      320
     1<<10：小杠开花         20 城北10
     1<<11：大杠开花         40  城北总数乘2 如果是别人打出来刚得，打牌的包3家
     1<<12：天胡             320 城北结束
     1<<13：地胡             280 城北30 可以杠
     1<<14：压绝             40 城北30  压档成牌 压档不再算花 小胡要符合条件
     1<<15：无花果           40 城北30  小胡必须门清
     1<<16：抢杠	         抢杠算自摸，杠的人包3家
     1<<17：海底捞	         10 最后16张牌胡
     18位以后是花的数量
     */

    public static int Hu_Flag_Normal = 0;
    public static int Hu_Flag_Tianhu = 1;
    public static int Hu_Flag_Dihu = 1 << 1;
    public static int Hu_Flag_Gang = 1 << 2;
    public static int Hu_Flag_Buhua = 1 << 3;
    public static int Hu_Flag_Qiangganghu = 1 << 4;
    public static int Hu_Flag_Haidilao = 1 << 5;

    public static int Hu_Type_Dahu = 0x3FFFC;
    public static int Hu_Type_Xiaohu = 1 << 0;
    public static int Hu_Type_Menqing = 1 << 1;
    public static int Hu_Type_Hunyise = 1 << 2;
    public static int Hu_Type_Qingyise = 1 << 3;
    public static int Hu_Type_DuiDuihu = 1 << 4;
    public static int Hu_Type_Quanqiududiao = 1 << 5;
    public static int Hu_Type_Qidui = 1 << 6;
    public static int Hu_Type_Haohuaqidui = 1 << 7;
    public static int Hu_Type_Chaohaohuaqidui = 1 << 8;
    public static int Hu_Type_Chaochaohaohuaqidui = 1 << 9;
    public static int Hu_Type_Xiaogangkaihua = 1 << 10;
    public static int Hu_Type_Dagangkaihua = 1 << 11;
    public static int Hu_Type_Tianhu = 1 << 12;
    public static int Hu_Type_Dihu = 1 << 13;
    public static int Hu_Type_Yajue = 1 << 14;
    public static int Hu_Type_Wuhuaguo = 1 << 15;
    public static int Hu_Type_Qianggang = 1 << 16;
    public static int Hu_Type_Haidilao = 1 << 17;
    public static int Hu_Type_Kuaizhao = 1 << 18;
    public static int Hu_Type_BaoPeng = 1 << 19;
    public static int Hu_Type_Ruanhua = 0x3C00000; // 22
    public static int Hu_Type_Yinghua = 0x7C000000; // 26
}

