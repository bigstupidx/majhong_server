package com.dyz.gameserver.msg.processor.outroom;

import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.logic.RoomLogic;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.outroom.OutRoomResponse;
import com.dyz.gameserver.context.GameServerContext;
import com.dyz.persist.util.GlobalUtil;

import net.sf.json.JSONObject;

/**
 * 申请解散房间
 *
 * @author luck
 */
public class DissolveRoomMsgProcessor extends MsgProcessor implements
        INotAuthProcessor {

    @Override
    public void process(GameSession gameSession, ClientRequest request) throws Exception {
        if (GlobalUtil.checkIsLogin(gameSession)) {
            JSONObject json = JSONObject.fromObject(request.getString());
            int roomId = Integer.parseInt(json.get("roomId").toString());
            String type = json.get("type").toString();
            Avatar avatar = gameSession.getRole(Avatar.class);
            //system.out.println("申请解散房间");
            if (avatar != null && roomId != 0) {
                RoomLogic roomLogic = RoomManager.getInstance().getRoom(roomId);
                if (roomLogic != null) {
                    if (type.equals("0")) {
                        //申请解散房间
                        if (roomLogic.isDissolve()) {
                            roomLogic.setDissolveCount(1);
                            //申请解散
                            roomLogic.dissolveRoom(avatar, roomId, type);
                        } else {
                            //system.out.println("已经有人申请解散房间");
                        }
                    } else {
                        //同意/拒绝解散房间
                        roomLogic.dissolveRoom(avatar, roomId, type);
                    }
                } else {
                    //system.out.println("房间不能为空");
                    json = new JSONObject();
                    json.put("type", "-1");
                    json.put("status_code", "0");
                    json.put("uuid", avatar.getUuId());
                    json.put("accountName", avatar.avatarVO.getAccount().getNickname());
                    avatar.getSession().setLogin(true);
                    avatar.getSession().sendMsg(new OutRoomResponse(1, json.toString()));
                    GameServerContext.add_onLine_Character(avatar);
                }
            } else {
                //system.out.println("房间号不能为0");
            }
        }
    }

}
