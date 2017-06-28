package com.dyz.gameserver.msg.response.score;

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
public class ScoreResponse extends ServerResponse {
    /**
     *
     * @param status
     */
	public ScoreResponse(int status, int avatarIndex, int score) {
        super(status, ConnectAPI.SCORE_RESPONSE);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("avatarIndex",avatarIndex);
        jsonObject.put("score",score);
        try {
            output.writeUTF(jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            output.close();
        }
    }
}
