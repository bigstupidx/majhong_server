package com.dyz.gameserver.msg.response.gang;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;
import com.dyz.gameserver.pojo.GangBackVO;
import com.dyz.persist.util.JsonUtilTool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luck
 */
public class GangResponse extends ServerResponse {
    /**
     * @param status
     * @param fristPoint 第一张牌
     * @param nextPoint  第二张牌
     * @param type       明杠0 ， 暗杠 1
     */
    public GangResponse(int status, int fristPoint, int nextPoint, int type) {
        super(status, ConnectAPI.GANGPAI_RESPONSE);
        if (status > 0) {
            GangBackVO gangBackVO = new GangBackVO();
            gangBackVO.setType(type);
            try {
                output.writeUTF(JsonUtilTool.toJson(gangBackVO));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                output.close();
            }
        }
    }
}
