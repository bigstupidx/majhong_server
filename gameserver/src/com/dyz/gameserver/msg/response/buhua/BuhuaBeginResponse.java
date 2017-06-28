package com.dyz.gameserver.msg.response.buhua;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;
import com.dyz.gameserver.pojo.CardVO;
import com.dyz.persist.util.JsonUtilTool;
import net.sf.json.JSONObject;

import java.io.IOException;

/**
 * 
 * @author luck
 *
 */
public class BuhuaBeginResponse extends ServerResponse {
    /**
     *
     * @param status
     * @param cardPoint hua pai
     */
	public BuhuaBeginResponse(int status, int cardPoint, int pickPoint, int avatarIndex) {
        super(status, ConnectAPI.BUHUA_BEGIN_RESPONSE);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cardPoint",cardPoint);
        jsonObject.put("pickPoint",pickPoint);
        jsonObject.put("avatarId",avatarIndex);
        try {
            output.writeUTF(jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            output.close();
        }
    }
}
