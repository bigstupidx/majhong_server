package com.dyz.gameserver.msg.response.testmsg;

import com.alibaba.fastjson.JSONObject;
import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

import java.io.IOException;

/**
 * Created by Administrator on 2017/5/27.
 */
public class TestSebdMsgResponse extends ServerResponse {
    /**
     * 必须调用此方法设置消息号
     *
     * @param status
     * @param msg
     */
    public TestSebdMsgResponse(int status, String msg) {
       super(status, ConnectAPI.TESTSENDMSG);
       try {
           if(status>0){
               JSONObject mjson = new JSONObject();
                mjson.put("version","2.0");
                mjson.put("name","wwflyme");
                mjson.put("age","25");
                mjson.put("address","昆明");
                mjson.put("phone","110");
               output.writeUTF(mjson.toString());
           }
       }catch (IOException e){
           e.printStackTrace();
       }finally {
           output.close();
       }
    }
}
