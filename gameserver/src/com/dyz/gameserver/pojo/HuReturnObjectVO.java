package com.dyz.gameserver.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.dyz.persist.util.StringUtil;
import com.context.Rule;

/**
 * 胡牌时返回信息组成对象
 * 牌组，杠的详细信息，胡的详细信息，昵称，uuid
 *
 * @author luck
 */
public class HuReturnObjectVO {
    /**
     * 牌数组
     */
    private int[] paiArray;
    /**
     * key:type:游戏自摸1，接炮2，点炮3，暗杠4，明杠5 ，胡6记录(key),
     * value:list里面，第一个为点炮/杠/胡次数，第二个元素为点炮/杠/胡分数总和
     */
    private Map<String, ArrayList<Integer>> gangAndHuInfos = new HashMap<String, ArrayList<Integer>>();
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 玩家uuid
     */
    private Integer uuid;
    /**
     * 杠的总分
     */
    private int gangScore = 0;
    /**
     * 总分
     */
    private int totalScore = 0;

    private int huScore = 0;

    private String huType;
    /**
     * 存放吃，碰，杠，胡的信息
     */
    private Map<String, String> totalInfo = new HashMap<String, String>();


    public Map<String, String> getTotalInfo() {
        return totalInfo;
    }

    /**
     * 统计吃，碰，杠，胡的详细信息， 比如 谁杠了谁的什么牌，明杠，还是暗杠等
     *
     * @param type 信息类型  "chi","peng","gang","hu", "gengzhuang", "huagang", "genpai"
     * @param str  信息内容
     */
    public synchronized void updateTotalInfo(String type, String str) {
        if (type.equals("chi")) {
            System.out.println("chi");
        }
        if (StringUtil.isNotEmpty(str)) {
            if (totalInfo.get(type) == null) {
                totalInfo.put(type, str);
            } else {
                totalInfo.put(type, totalInfo.get(type) + "," + str);
            }
        } else {
            System.out.println("HuReturnObjectVO里面的updateTotalInfo--传入的str不呢为空");
        }
    }

    /**
     * 自摸1，接炮2，点炮3，暗杠4，明杠5 ，胡6记录(type),加码7 花杠8 打4张9 跟牌10
     *
     * @param type 类型
     */
    public synchronized void updateGangAndHuInfos(String type, int score) {
        ArrayList<Integer> listNew = new ArrayList<Integer>();
        ArrayList<Integer> list = gangAndHuInfos.get(type);
        if (list == null || list.size() <= 0) {
            listNew.add(1);
            listNew.add(score);
        } else {
            //在原来的基础上修改信息
            listNew.add(list.get(0) + 1);
            listNew.add(list.get(1) + score);
        }

        switch (type) {
            case "4":
            case "5":
                updateGangScore(score);
                break;
            case "8":
                break;
            case "9":
            case "10":
                break;
            default:
                updateHuScore(score);
                break;
        }
        updateTotalScore(score);
        gangAndHuInfos.put(type, listNew);
    }

    public int[] getPaiArray() {
        return paiArray;
    }

    public void setPaiArray(int[] paiArray) {
        this.paiArray = paiArray;
    }

    public Map<String, ArrayList<Integer>> getGangAndHuInfos() {
        return gangAndHuInfos;
    }

    public int getGangScore() {
        return gangScore;
    }

    private void updateGangScore(int score) {
        gangScore = gangScore + score;
    }

    public int getHuScore() {
        return huScore;
    }

    private void updateHuScore(int score) {
        huScore = huScore + score;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getUuid() {
        return uuid;
    }

    public void setUuid(Integer uuid) {
        this.uuid = uuid;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setHuType(int huType) {
        if (huType > 0) {
            this.huType = "";
            int yinghua = (huType & Rule.Hu_Type_Yinghua) >> 26;
            if (yinghua > 0) this.huType += "硬花 " + yinghua + "      ";
            int ruanhua = (huType & Rule.Hu_Type_Ruanhua) >> 22;
            if (ruanhua > 0) this.huType += "软花 " + ruanhua + "      ";
            if ((huType & Rule.Hu_Type_Menqing) > 0) this.huType += "门清 10      ";
            if ((huType & Rule.Hu_Type_Hunyise) > 0) this.huType += "混一色 40      ";
            if ((huType & Rule.Hu_Type_Qingyise) > 0) this.huType += "清一色 60      ";
            if ((huType & Rule.Hu_Type_DuiDuihu) > 0) this.huType += "对对胡 40      ";
            if ((huType & Rule.Hu_Type_Quanqiududiao) > 0) this.huType += "全球独钓 60      ";
            if ((huType & Rule.Hu_Type_Qidui) > 0) this.huType += "七对 40      ";
            if ((huType & Rule.Hu_Type_Haohuaqidui) > 0) this.huType += "豪华七对 160      ";
            if ((huType & Rule.Hu_Type_Chaohaohuaqidui) > 0) this.huType += "超豪华七对 240      ";
            if ((huType & Rule.Hu_Type_Chaochaohaohuaqidui) > 0) this.huType += "超超豪华七对 320      ";
            if ((huType & Rule.Hu_Type_Xiaogangkaihua) > 0) this.huType += "小杠开花 20      ";
            if ((huType & Rule.Hu_Type_Dagangkaihua) > 0) this.huType += "大杠开花 40      ";
            if ((huType & Rule.Hu_Type_Tianhu) > 0) this.huType += "天胡 320      ";
            if ((huType & Rule.Hu_Type_Dihu) > 0) this.huType += "地胡 280      ";
            if ((huType & Rule.Hu_Type_Yajue) > 0) this.huType += "压绝 40      ";
            if ((huType & Rule.Hu_Type_Wuhuaguo) > 0) this.huType += "无花果 40      ";
            if ((huType & Rule.Hu_Type_Haidilao) > 0) this.huType += "海底捞 10      ";
        } else {
            this.huType = "";
        }
    }

    public String getHuType() {
        return huType;
    }

    //更新总分数
    private void updateTotalScore(int score) {
        totalScore = totalScore + score;
    }


}
