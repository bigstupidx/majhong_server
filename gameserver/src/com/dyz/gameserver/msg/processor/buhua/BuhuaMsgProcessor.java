package com.dyz.gameserver.msg.processor.buhua;

import com.context.ErrorCode;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.logic.RoomLogic;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.ErrorResponse;

import net.sf.json.JSONObject;

/**
 * @author luck
 */
public class BuhuaMsgProcessor extends MsgProcessor implements
        INotAuthProcessor {
    @Override
    public void process(GameSession gameSession, ClientRequest request) throws Exception {
        RoomLogic roomLogic = RoomManager.getInstance().getRoom(gameSession.getRole(Avatar.class).getRoomVO().getRoomId());
        if (roomLogic != null) {
            JSONObject json = JSONObject.fromObject(request.getString());
            int cardPoint = (int) json.get("cardPoint");
            roomLogic.buhua(gameSession.getRole(Avatar.class), cardPoint, 0);
        } else {
            gameSession.sendMsg(new ErrorResponse(ErrorCode.Error_000005));
        }
    }
}
