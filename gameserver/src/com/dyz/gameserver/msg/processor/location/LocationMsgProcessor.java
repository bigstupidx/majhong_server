package com.dyz.gameserver.msg.processor.location;

import com.context.ErrorCode;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.logic.RoomLogic;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.ErrorResponse;
import com.dyz.gameserver.msg.response.messageBox.MessageBoxResponse;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * Created by kevin on 2016/8/2.
 */
public class LocationMsgProcessor extends MsgProcessor implements INotAuthProcessor {
    @Override
    public void process(GameSession gameSession, ClientRequest request) throws Exception {
        Avatar avatar = gameSession.getRole(Avatar.class);
        if(avatar != null){
            JSONObject json = JSONObject.fromObject(request.getString());
            avatar.avatarVO.setLatitude((double)json.get("latitude"));
            avatar.avatarVO.setLongitude((double)json.get("longitude"));
            avatar.avatarVO.setAddress(json.get("address").toString());
        }else{
            gameSession.sendMsg(new ErrorResponse(ErrorCode.Error_000024));
        }
    }
}
