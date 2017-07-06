package com.dyz.gameserver.msg.response.time;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;
import com.dyz.gameserver.pojo.CardVO;
import com.dyz.persist.util.JsonUtilTool;
import net.sf.json.JSONObject;

import java.io.IOException;

/**
 * @author luck
 */
public class TimeWaitResponse extends ServerResponse {
    /**
     * @param status
     */
    public TimeWaitResponse(int status, int time) {
        super(status, ConnectAPI.TIME_WAIT_RESPONSE);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("time", time);
        try {
            output.writeUTF(jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            output.close();
        }
    }
}
