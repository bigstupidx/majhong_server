package com.dyz.persist.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.context.NanjingConfig;
import org.apache.commons.lang.math.Fraction;

import java.util.Set;

import com.context.Rule;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.logic.PlayCardsLogic;

/**
 * 判断胡牌类型
 *
 * @author luck
 */
public class HuPaiType {
    private static HuPaiType huPaiType;

    private HuPaiType() {

    }

    public static HuPaiType getInstance() {
        if (huPaiType == null) {
            huPaiType = new HuPaiType();
        }
        return huPaiType;
    }

    public void getHuType(Avatar avatarShu, Avatar avatar, int cardIndex,
                          List<Avatar> playerList, boolean bixiahu) {
        nanjing(avatarShu, avatar, cardIndex, playerList, bixiahu);
    }

    private static void nanjing(Avatar avatarShu, Avatar avatar, int cardIndex,
                                List<Avatar> playerList, boolean bixiahu) {
        String str;
        int huType = avatar.avatarVO.getHuType();
        boolean chengbei = avatar.getRoomVO().getChengbei();
        boolean isYuanzi = (avatar.getRoomVO().getRoundtype() == 0);
        int yuanziScore = avatar.getRoomVO().getYuanzishu();
        int zashu = avatar.getRoomVO().getRealZashu();
        int paofen = avatar.getRoomVO().getRealPaofen();
        int kaihuaFactor = 1;

        int pai_score = 0;
        if ((huType & Rule.Hu_Type_Menqing) > 0) pai_score += chengbei ? 10 : 10;
        if ((huType & Rule.Hu_Type_Hunyise) > 0) pai_score += chengbei ? 30 : 40;
        if ((huType & Rule.Hu_Type_Qingyise) > 0) pai_score += chengbei ? 50 : 60;
        if ((huType & Rule.Hu_Type_DuiDuihu) > 0) pai_score += chengbei ? 30 : 40;
        if ((huType & Rule.Hu_Type_Quanqiududiao) > 0) pai_score += chengbei ? 50 : 60;
        if ((huType & Rule.Hu_Type_Qidui) > 0) pai_score += chengbei ? 50 : 40;
        if ((huType & Rule.Hu_Type_Haohuaqidui) > 0) pai_score += chengbei ? 150 : 160;
        if ((huType & Rule.Hu_Type_Chaohaohuaqidui) > 0) pai_score += chengbei ? 230 : 240;
        if ((huType & Rule.Hu_Type_Chaochaohaohuaqidui) > 0) pai_score += chengbei ? 310 : 320;
        if ((huType & Rule.Hu_Type_Xiaogangkaihua) > 0) pai_score += chengbei ? 10 : 20;
        if ((huType & Rule.Hu_Type_Dagangkaihua) > 0) {
            if (chengbei) kaihuaFactor = 2;
            else pai_score += 40;
        }
        if ((huType & Rule.Hu_Type_Tianhu) > 0) pai_score += 320;
        if ((huType & Rule.Hu_Type_Dihu) > 0) pai_score += chengbei ? 30 : 280;
        if ((huType & Rule.Hu_Type_Yajue) > 0) pai_score += chengbei ? 30 : 40;
        if ((huType & Rule.Hu_Type_Wuhuaguo) > 0) pai_score += chengbei ? 30 : 40;
        if ((huType & Rule.Hu_Type_Haidilao) > 0) pai_score += 10;
        int hua_score = ((huType & Rule.Hu_Type_Ruanhua) >> 22) + ((huType & Rule.Hu_Type_Yinghua) >> 26);

        int score = hua_score * Integer.max(zashu, 1) + paofen + pai_score;
        score *= kaihuaFactor;

        if (bixiahu) {
            score = (score/* + 10 * avatar.avatarVO.getMainCount()*/) * 2;
        }

        // qianggang dagangkaihua baopai
        if (((huType & Rule.Hu_Type_Qianggang)
                | (huType & Rule.Hu_Type_Dagangkaihua)
                | (huType & Rule.Hu_Type_Quanqiududiao)
                | (huType & Rule.Hu_Type_DuiDuihu)) > 0) {
            if (avatar.baopaiAvatar != null) {
                avatarShu = avatar.baopaiAvatar;
                if (isYuanzi) {
                    score = bixiahu ? yuanziScore : (yuanziScore / 2);
                } else {
                    score = 3 * score;
                }
            }
        }

        if (avatarShu.getUuId() == avatar.getUuId()) {
            int realScore = 0;
            for (int i = 0; i < playerList.size(); i++) {
                if (playerList.get(i).getUuId() != avatar.getUuId()) {
                    str = avatar.getUuId() + ":" + cardIndex + ":" + Rule.Hu_other_common;
                    playerList.get(i).avatarVO.getHuReturnObjectVO().updateTotalInfo("hu", str);
                    realScore += score;
                    int result = playerList.get(i).avatarVO.supdateScores(-score);
                    realScore += result;
                    playerList.get(i).avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("1", -1 * (score + result));
                }
            }

            str = "0:" + cardIndex + ":" + Rule.Hu_zi_common;
            avatar.avatarVO.getHuReturnObjectVO().updateTotalInfo("hu", str);
            avatar.avatarVO.supdateScores(realScore);
            avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("1", realScore);
        } else {
            str = avatar.getUuId() + ":" + cardIndex + ":" + Rule.Hu_d_other;
            avatarShu.avatarVO.getHuReturnObjectVO().updateTotalInfo("hu", str);
            int result = avatarShu.avatarVO.supdateScores(-score);
            avatarShu.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("3", -1 * (score + result));

            str = avatarShu.getUuId() + ":" + cardIndex + ":" + Rule.Hu_d_self;
            avatar.avatarVO.getHuReturnObjectVO().updateTotalInfo("hu", str);
            avatar.avatarVO.supdateScores(score + result);
            avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("2", score + result);
        }
    }
}
