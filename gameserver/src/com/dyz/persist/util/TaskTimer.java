package com.dyz.persist.util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.manager.GameSessionManager;
import com.dyz.gameserver.msg.response.HeadResponse;
import com.dyz.gameserver.sprite.tool.AsyncTaskQueue;

/**
 * Created by kevin on 2016/8/15.
 * 每天固定时间执行任务的定时器
 */
public class TaskTimer {
    static int count = 0;
    AsyncTaskQueue asyncTaskQueue = new AsyncTaskQueue();
    public static void showTimer() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                ++count;
                System.out.println("时间=" + new Date() + " 执行了" + count + "次"); // 1次
                Map<String, GameSession> gamessions = GameSessionManager.getInstance().sessionMap;
                if(!gamessions.isEmpty()){
                	for (Entry<String, GameSession> set :gamessions.entrySet()) {
                		set.getValue().getRole(Avatar.class).avatarVO.getAccount().setIsGame("0");
					}
                }
            }
        };

        //设置执行时间
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);//每天
        //定制每天的21:09:00执行，
        calendar.set(year, month, day, 1, 0, 0);
        Date date = calendar.getTime();
        Timer timer = new Timer();
        System.out.println(date);
        timer.schedule(task, date,24*60*60*1000);
    }

    public static void headBag(){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                List<GameSession> gameSessionList = GameSessionManager.getInstance().getAllSession();
                if(gameSessionList != null){
                    for(int i=0;i<gameSessionList.size();i++){
                        gameSessionList.get(i).addTime(1);
                        if(gameSessionList.get(i).getTime() > 15){
                            gameSessionList.get(i).destroyObj();
                            gameSessionList.get(i).sendMsg(new HeadResponse(1,"1"));
                        }
                        else{
                        	try {
                        		gameSessionList.get(i).sendMsg(new HeadResponse(1,"0"));
                        	}catch (Exception e){
                        		System.out.println(e.getMessage());
                        	}
                        }
                    }
                }
            }
        };
        //设置执行时间
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);//每天
        //定制每天的21:09:00执行，
        calendar.set(year, month, day, 0, 0, 1);
        Date date = calendar.getTime();
        Timer timer = new Timer();
        System.out.println(date);
        //20秒一次心跳包
        timer.schedule(task, date,20000);
    }
}
