package com.dyz.gameserver.msg.processor.testmsg;

import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.testmsg.TestSebdMsgResponse;

/**
 * Created by Administrator on 2017/5/27.
 */
public class TestSendMsg extends MsgProcessor implements INotAuthProcessor {
    @Override
    public void process(GameSession gameSession, ClientRequest request) throws Exception {
        System.out.println("request:"+request.getString());
        gameSession.sendMsg(new TestSebdMsgResponse(1,"hello"));
//        if(request.getString() != null) {
//            TestMsgVO testMsgVO = JsonUtilTool.fromJson(request.getString(), TestMsgVO.class);
//            gameSession.sendMsg(new TestSebdMsgResponse(1,"hello"));
//        }else {
//            gameSession.sendMsg(new ErrorResponse(ErrorCode.Error_000024));
//        }
    }
}
