package com.dyz.gameserver.msg.processor.outroom;

import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.context.GameServerContext;
import com.dyz.gameserver.logic.RoomLogic;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.outroom.OutRoomResponse;
import com.dyz.persist.util.GlobalUtil;

import net.sf.json.JSONObject;

/**
 * 退出房间
 * @author luck
 *
 */
public class OutRoomMsgProcessor extends MsgProcessor implements
		INotAuthProcessor {

	public OutRoomMsgProcessor() {
	}

	@Override
	public void process(GameSession gameSession, ClientRequest request)
			throws Exception {
		if(GlobalUtil.checkIsLogin(gameSession)) {
			JSONObject json = JSONObject.fromObject(request.getString());
			int roomId = (int)json.get("roomId");
			Avatar avatar = gameSession.getRole(Avatar.class);
			if (avatar != null && roomId != 0) {
				RoomLogic roomLogic = RoomManager.getInstance().getRoom(roomId);
				if (roomLogic != null) {
					//退出房间
					roomLogic.exitRoom(avatar);
				} else {
					json = new JSONObject();
					json.put("status_code", "0");
					json.put("type", "-1");
					json.put("uuid", avatar.getUuId());
					json.put("accountName", avatar.avatarVO.getAccount().getNickname());
					avatar.getSession().setLogin(true);
					avatar.getSession().sendMsg(new OutRoomResponse(1, json.toString()));
					GameServerContext.add_onLine_Character(avatar);
				}
			}
		}
	}
}
