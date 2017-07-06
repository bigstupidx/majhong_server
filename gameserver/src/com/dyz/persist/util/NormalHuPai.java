package com.dyz.persist.util;

import java.util.List;
import java.util.ArrayList;

import com.context.NanjingConfig;
import com.context.Rule;
import com.dyz.gameserver.Avatar;

/**
 * Created by kevin on 2016/7/30.
 */
public class NormalHuPai {

    /**
     * //   将牌标志，即牌型“三三三三二”中的“二”
     */
    private int JIANG = 0;


    public static void main(String[] args) {
        int[] pai = new int[]{0, 0, 0, 3, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 1, 1, 1, 1};
        //int [] pai = new int[]{0,0,0,0,0,0,1,1,1,     0,0,2,0,3,1,1,1,0,     0,0,1,1,1,0,0,0,0,   0,0,0,0,0,0,0};
        NormalHuPai normalHuPai = new NormalHuPai();
        boolean flag = normalHuPai.isNanjingPai(pai);
        System.out.println(flag);
    }

    public int checkNanjingHu(Avatar avatar, Avatar avatarShu, List<Avatar> playerList, int[][] paiList, Integer cardIndex, int flag) {
        JIANG = 0;
        int oneCardCount = 0;
        int twoCardCount = 0;
        int threeCardHandCount = 0;
        int fourCardHandCount = 0;
        boolean yadang = false;
        boolean bianzhi = false;
        boolean duzhan = false;

        int oneFengCount = 0;
        int twoFengCount = 0;
        int threeFengHandCount = 0;
        int fourFengHandCount = 0;

        int wanCount = 0;
        int tiaoCount = 0;
        int tongCount = 0;
        int fengCount = 0;

        int pengCount = 0;
        int gangCount = 0;
        int pengFengCount = 0;
        int gangFengCount = 0;

        int huaCount = 0;

        int mingGangCount = 0;
        int anGangCount = 0;
        int pengGangCount = 0;
        int mingGangFengCount = 0;
        int anGangFengCount = 0;

        boolean cardPeng = false;
        boolean cardHandOne = false;

        int[] pai = GlobalUtil.CloneIntList(paiList[0]);
        int result = 0;
        List<Integer> pengGangIndexList = new ArrayList<Integer>();
        for (int i = 0; i < pai.length; i++) {
            int paiCount = pai[i];
            if (i >= NanjingConfig.HUA_INDEX) {
                huaCount += paiCount;
            } else {
                if (paiCount > 0) {
                    if (paiCount == 1) {
                        oneCardCount++;
                        if (i >= 27) {
                            oneFengCount++;
                            result = 0;
                            return 0;
                        }
                        if (cardIndex == i) cardHandOne = true;
                    } else if (paiCount == 2) {
                        twoCardCount++;
                        if (i >= 27) {
                            twoFengCount++;
                        }
                    } else if (paiCount == 3) {
                        if (paiList[1][i] == 0) {
                            threeCardHandCount++;
                            if (i >= 27) {
                                threeFengHandCount++;
                                pai[i] = 0;
                            }
                        } else if (paiList[1][i] == 1) {
                            pengCount++;
                            if (i >= 27) pengFengCount++;
                            pai[i] = 0;
                            pengGangIndexList.add(paiList[2][i]);
                        }
                    } else if (paiCount == 4) {
                        if (paiList[1][i] == 0) {
                            fourCardHandCount++;
                            if (i >= 27) {
                                fourFengHandCount++;
                                pai[i] = 0;
                            }
                        } else if (paiList[1][i] == 1) {
                            pengCount++;
                            if (i >= 27) {
                                pengFengCount++;
                                result = 0;
                                return 0;
                            }
                            if (i == cardIndex) {
                                cardPeng = true;
                                cardHandOne = true;
                            }
                            pai[i] = 1;
                            pengGangIndexList.add(paiList[2][i]);
                        } else if (paiList[1][i] == 2) {
                            gangCount++;
                            if (i >= 27) gangFengCount++;
                            if (paiList[2][i] > -1) {
                                pengGangCount++;
                                mingGangCount++;
                                if (i >= 27) mingGangFengCount++;
                                pengGangIndexList.add(paiList[2][i]);
                            } else if (paiList[3][i] > -1) {
                                mingGangCount++;
                                if (i >= 27) mingGangFengCount++;
                                pengGangIndexList.add(paiList[3][i]);
                            } else {
                                anGangCount++;
                                if (i >= 27) anGangFengCount++;
                            }
                        }
                    }

                    if (i >= 0 && i <= 8) wanCount += paiCount;
                    if (i >= 9 && i <= 17) tiaoCount += paiCount;
                    if (i >= 18 && i <= 26) tongCount += paiCount;
                    if (i >= 27 && i <= 30) fengCount += paiCount;
                }
            }
        }

        // check qidui
        {
            if (twoCardCount == 7) result += Rule.Hu_Type_Qidui;
            else if (fourCardHandCount == 1 && twoCardCount == 5) result += Rule.Hu_Type_Haohuaqidui;
            else if (fourCardHandCount == 2 && twoCardCount == 3) result += Rule.Hu_Type_Chaohaohuaqidui;
            else if (fourCardHandCount == 3 && twoCardCount == 1) result += Rule.Hu_Type_Chaochaohaohuaqidui;
        }

        // check kuaizhao
        {
            if ((pengCount + pengGangCount + mingGangFengCount) >= 3) {
                int index = -1;
                int count = 0;
                for (int i = 0; i < pengGangIndexList.size(); i++) {
                    if (pengGangIndexList.get(i) != index) {
                        count++;
                        index = pengGangIndexList.get(i);
                    }
                }
                if (count == 1) {
                    if (paiList[0][cardIndex] == 3) {
                        int shuIndex = playerList.indexOf(avatarShu);
                        if (shuIndex == index) {
                            result += Rule.Hu_Type_Kuaizhao;
                            result += Rule.Hu_Type_Quanqiududiao;
                        } else {
                            result += Rule.Hu_Type_Kuaizhao;
                            result += Rule.Hu_Type_DuiDuihu;
                        }
                        avatar.baopaiAvatar = playerList.get(pengGangIndexList.get(0));
                    }
                }
            }
        }

        boolean isHu = false;
        if (result > 0) isHu = true;
        if (isHu == false) {
            if (twoFengCount > 1) return 0;
            if (fourFengHandCount > 0) return 0;
            int[] clonePai = GlobalUtil.CloneIntList(pai);
            isHu = isNanjingPai(clonePai);
        }
        if (isHu == false) return 0;

        // check menq ing
        {
            if (result == 0) {
                if ((pengCount + pengGangCount) == 0) result += Rule.Hu_Type_Menqing;
            }
        }

        // check tian di hu
        {
            if ((flag & Rule.Hu_Flag_Tianhu) > 0) result += Rule.Hu_Type_Tianhu;
            if ((flag & Rule.Hu_Flag_Dihu) > 0) result += Rule.Hu_Type_Dihu;
        }

        // check yi se
        if ((result & Rule.Hu_Type_Kuaizhao) == 0 || (result & Rule.Hu_Type_Quanqiududiao) > 0) {
            boolean yise = false;
            if (wanCount > 0 && tiaoCount == 0 && tongCount == 0) yise = true;
            if (tiaoCount > 0 && wanCount == 0 && tongCount == 0) yise = true;
            if (tongCount > 0 && tiaoCount == 0 && wanCount == 0) yise = true;

            if (yise && fengCount > 0) result += Rule.Hu_Type_Hunyise;
            else if (yise && fengCount == 0) result += Rule.Hu_Type_Qingyise;
        }

        // check dui dui hu
        {
            if ((gangCount + pengCount) == 4 && twoCardCount == 1) result += Rule.Hu_Type_Quanqiududiao;
            else if ((gangCount + pengCount + threeCardHandCount) == 4 && twoCardCount == 1)
                result += Rule.Hu_Type_DuiDuihu;
        }

        // check gang kai hua
        {
            if ((flag & Rule.Hu_Flag_Buhua) > 0) result += Rule.Hu_Type_Xiaogangkaihua;
            if ((flag & Rule.Hu_Flag_Gang) > 0) result += Rule.Hu_Type_Dagangkaihua;
        }

        if (result > 0 || huaCount >= 4) result += Rule.Hu_Type_Xiaohu;

        int huCount = getHuCount(pai, cardIndex);
        if (huCount == 1) {
            if (cardIndex < 27) {
                int card = cardIndex % 9;
                // yadang
                if (card > 0 && card < 8) {
                    if (pai[cardIndex] <= 2 && pai[cardIndex - 1] == pai[cardIndex] && pai[cardIndex + 1] == pai[cardIndex])
                        yadang = true;
                }

                // bianzhi
                if (card == 2) {
                    if (pai[cardIndex] <= 2 && pai[cardIndex - 2] == pai[cardIndex] && pai[cardIndex - 1] == pai[cardIndex])
                        bianzhi = true;
                }
                if (card == 6) {
                    if (pai[cardIndex] <= 2 && pai[cardIndex + 1] == pai[cardIndex] && pai[cardIndex + 2] == pai[cardIndex])
                        bianzhi = true;
                }
            }
        }

        // duzhan
        if (huCount == 1 && pai[cardIndex] >= 2) {
            if (cardIndex >= 27) duzhan = true;
            else {
                int card = cardIndex % 9;
                boolean leftCard = false;
                boolean rightCard = false;
                if (card > 0 && pai[cardIndex - 1] > 0) leftCard = true;
                if (card < 8 && pai[cardIndex + 1] > 0) rightCard = true;
                if (leftCard == false && rightCard == false) duzhan = true;
            }
        }

        if (result > 0) {
            // check ya jue
            {
                if (cardPeng == true && (yadang || bianzhi)) result += Rule.Hu_Type_Yajue;
            }

            // check wu hua guo
            {
                if (huaCount == 0) result += Rule.Hu_Type_Wuhuaguo;
            }

            // check qiang gang
            {
                if ((flag & Rule.Hu_Flag_Qiangganghu) > 0) result += Rule.Hu_Type_Qianggang;
            }

            // check hai di lao
            {
                if ((flag & Rule.Hu_Flag_Haidilao) > 0) result += Rule.Hu_Type_Haidilao;
            }

            // check hua
            {
                int ruanHua = 0;
                ruanHua += pengFengCount;
                ruanHua += mingGangFengCount * 2;
                ruanHua += anGangFengCount * 3;
                ruanHua += (mingGangCount - mingGangFengCount);
                ruanHua += (anGangCount - anGangFengCount) * 2;
                if (wanCount == 0) ruanHua++;
                if (tiaoCount == 0) ruanHua++;
                if (tongCount == 0) ruanHua++;
                if ((result & Rule.Hu_Type_Yajue) == 0) {
                    ruanHua += yadang ? 1 : 0;
                    ruanHua += bianzhi ? 1 : 0;
                }

                ruanHua += duzhan ? 1 : 0;

                result += ruanHua << 22;
                result += huaCount << 26;
            }
        }

        return result;
    }

    public boolean isNanjingPai(int[] paiList) {
        if (Remain(paiList) == 0) {
            return true;           //   递归退出条件：如果没有剩牌，则胡牌返回。
        }
        for (int i = 0; i < paiList.length; i++) {//   找到有牌的地方，i就是当前牌,   PAI[i]是个数
            if (i >= NanjingConfig.HUA_INDEX) continue;
            //   跟踪信息
            //   4张组合(杠子)
            if (paiList[i] != 0) {
                if (paiList[i] == 4)                               //   如果当前牌数等于4张
                {
                    paiList[i] = 0;                                     //   除开全部4张牌
                    if (isNanjingPai(paiList)) {
                        return true;             //   如果剩余的牌组合成功，和牌
                    }
                    paiList[i] = 4;                                     //   否则，取消4张组合
                }

                //   3张组合(大对)
                if (paiList[i] >= 3)                               //   如果当前牌不少于3张
                {
                    paiList[i] -= 3;                                   //   减去3张牌
                    if (isNanjingPai(paiList)) {
                        return true;             //   如果剩余的牌组合成功，胡牌
                    }
                    paiList[i] += 3;                                   //   取消3张组合
                }

                //   2张组合(将牌)
                if (JIANG == 0 && paiList[i] >= 2)           //   如果之前没有将牌，且当前牌不少于2张
                {
                    JIANG = 1;                                       //   设置将牌标志
                    paiList[i] -= 2;                                   //   减去2张牌
                    if (isNanjingPai(paiList)) return true;             //   如果剩余的牌组合成功，胡牌
                    paiList[i] += 2;                                   //   取消2张组合
                    JIANG = 0;                                       //   清除将牌标志
                }

                //   顺牌组合，注意是从前往后组合！
                //   排除数值为8,9的牌和风
                if (i < 27 && (i % 9 != 7) && (i % 9 != 8) && paiList[i + 1] != 0 && paiList[i + 2] != 0)             //   如果后面有连续两张牌
                {
                    paiList[i]--;
                    paiList[i + 1]--;
                    paiList[i + 2]--;                                     //   各牌数减1
                    if (isNanjingPai(paiList)) {
                        return true;             //   如果剩余的牌组合成功，胡牌
                    }
                    paiList[i]++;
                    paiList[i + 1]++;
                    paiList[i + 2]++;                                     //   恢复各牌数
                }
            }
        }
        return false;
    }

    //   检查剩余牌数
    int Remain(int[] paiList) {
        int sum = 0;
        for (int i = 0; i < paiList.length; i++) {
            if (i >= NanjingConfig.HUA_INDEX) continue;
            sum += paiList[i];
        }
        return sum;
    }

    int getHuCount(int[] pai, int cardIndex) {
        int[] paiCheck = new int[NanjingConfig.HUA_INDEX];
        for (int i = 0; i < paiCheck.length; i++) {
            paiCheck[i] = 0;
        }

        int huCount = 0;
        for (int i = 0; i < paiCheck.length; i++) {
            if (pai[i] > 0) {
                if (i >= 27) {
                    paiCheck[i] = 1;
                } else {
                    paiCheck[i] = 1;
                    int index = i % 9;
                    if (index > 0) paiCheck[i - 1] = 1;
                    if (index < 8) paiCheck[i + 1] = 1;
                }
            }
        }

        for (int i = 0; i < paiCheck.length; i++) {
            if (paiCheck[i] > 0) {
                int[] cloneList = GlobalUtil.CloneIntList(pai);
                cloneList[cardIndex] -= 1;
                if (cloneList[i] > 3) continue;
                cloneList[i] += 1;
                JIANG = 0;
                if (isNanjingPai(cloneList)) huCount++;
            }
        }
        return huCount;
    }
}
