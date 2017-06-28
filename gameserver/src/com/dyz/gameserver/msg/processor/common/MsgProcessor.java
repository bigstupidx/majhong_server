package com.dyz.gameserver.msg.processor.common;

import com.context.ConnectAPI;
import com.sun.corba.se.pept.transport.ConnectionCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;

public abstract class MsgProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(MsgProcessor.class);
	
	public void handle(GameSession gameSession,ClientRequest request){
		try {
			process(gameSession,request);
			int msgCode = request.getMsgCode();
			if (msgCode != ConnectAPI.head) {
				String logMsg = String.format("msg process success, msg code: %x, address: %s.", msgCode, gameSession.getAddress());
				logger.info(logMsg);
			}
		} catch (Exception e) {
			String logMsg = String.format("msg process failed, msg code: %x, address: %s.", request.getMsgCode(), gameSession.getAddress());
			logger.info(logMsg);
			e.printStackTrace();
		}
	}
	
	public abstract void process(GameSession gameSession,ClientRequest request)throws Exception;
}
