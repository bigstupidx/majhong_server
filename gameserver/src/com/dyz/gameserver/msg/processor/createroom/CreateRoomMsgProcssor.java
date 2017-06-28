package com.dyz.gameserver.msg.processor.createroom;

import com.context.ErrorCode;
import com.context.NanjingConfig;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.ErrorResponse;
import com.dyz.gameserver.msg.response.createroom.CreateRoomResponse;
import com.dyz.gameserver.pojo.AvatarVO;
import com.dyz.gameserver.pojo.RoomVO;
import com.dyz.persist.util.JsonUtilTool;

/**
 * Created by kevin on 2016/6/21.
 */
public class CreateRoomMsgProcssor extends MsgProcessor implements
        INotAuthProcessor {

    public CreateRoomMsgProcssor(){

    }

    @Override
    public void process(GameSession gameSession, ClientRequest request) throws Exception {
        String message = request.getString();
        RoomVO roomVO = (RoomVO) JsonUtilTool.fromJson(message, RoomVO.class);
        if(gameSession.isLogin()) {
            Avatar avatar = gameSession.getRole(Avatar.class);
            AvatarVO avatarVo = avatar.avatarVO;
            int roomcard = roomVO.getRoomCardCount();
            // check room right

            if(avatarVo.getAccount().getRoomcard() >= roomcard) {
                if(avatarVo.getRoomId() == 0) {
                    RoomManager.getInstance().createRoom(avatar,roomVO);
                    gameSession.sendMsg(new CreateRoomResponse(1,roomVO.getRoomId()+""));
                }else{
                    gameSession.sendMsg(new CreateRoomResponse(1,avatarVo.getRoomId()+""));
                }
            }else{
                gameSession.sendMsg(new ErrorResponse(ErrorCode.Error_000014));
            }
        }
    }
}
