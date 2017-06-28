package com.dyz.gameserver.logic;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.context.ErrorCode;
import com.context.NanjingConfig;
import com.context.Rule;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ResponseMsg;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.response.ErrorResponse;
import com.dyz.gameserver.msg.response.chupai.ChuPaiResponse;
import com.dyz.gameserver.msg.response.common.ReturnInfoResponse;
import com.dyz.gameserver.msg.response.common.ReturnOnLineResponse;
import com.dyz.gameserver.msg.response.gang.GangResponse;
import com.dyz.gameserver.msg.response.gang.OtherGangResponse;
import com.dyz.gameserver.msg.response.hu.HuPaiAllResponse;
import com.dyz.gameserver.msg.response.hu.HuPaiResponse;
import com.dyz.gameserver.msg.response.login.BackLoginResponse;
import com.dyz.gameserver.msg.response.login.OtherBackLoginResonse;
import com.dyz.gameserver.msg.response.peng.PengResponse;
import com.dyz.gameserver.msg.response.pickcard.OtherPickCardResponse;
import com.dyz.gameserver.msg.response.pickcard.PickCardResponse;
import com.dyz.gameserver.msg.response.roomcard.RoomCardChangerResponse;
import com.dyz.gameserver.msg.response.buhua.BuhuaResponse;
import com.dyz.gameserver.msg.response.buhua.HuaGangResponse;
import com.dyz.gameserver.msg.response.buhua.BuhuaBeginResponse;
import com.dyz.gameserver.msg.response.score.ScoreResponse;
import com.dyz.gameserver.msg.response.time.TimeWaitResponse;
import com.dyz.gameserver.pojo.AvatarVO;
import com.dyz.gameserver.pojo.CardVO;
import com.dyz.gameserver.pojo.FinalGameEndItemVo;
import com.dyz.gameserver.pojo.HuReturnObjectVO;
import com.dyz.gameserver.pojo.PlayBehaviedVO;
import com.dyz.gameserver.pojo.PlayRecordGameVO;
import com.dyz.gameserver.pojo.PlayRecordItemVO;
import com.dyz.gameserver.pojo.RoomVO;
import com.dyz.myBatis.model.Account;
import com.dyz.myBatis.model.PlayRecord;
import com.dyz.myBatis.model.Standings;
import com.dyz.myBatis.model.StandingsAccountRelation;
import com.dyz.myBatis.model.StandingsDetail;
import com.dyz.myBatis.model.StandingsRelation;
import com.dyz.myBatis.services.AccountService;
import com.dyz.myBatis.services.PlayRecordService;
import com.dyz.myBatis.services.StandingsAccountRelationService;
import com.dyz.myBatis.services.StandingsDetailService;
import com.dyz.myBatis.services.StandingsRelationService;
import com.dyz.myBatis.services.StandingsService;
import com.dyz.persist.util.DateUtil;
import com.dyz.persist.util.HuPaiType;
import com.dyz.persist.util.Naizi;
import com.dyz.persist.util.NormalHuPai;
import com.dyz.persist.util.StringUtil;
import sun.security.x509.AVA;


/**
 * Created by kevin on 2016/6/18.
 * 玩牌逻辑
 */
public class PlayCardsLogic {

    private int paiCount;
    /**
     * 当前出牌人的索引
     */
    private int curAvatarIndex;
    /**
     * 当前摸牌人的索引(初始值为庄家索引)
     */
    private int pickAvatarIndex;
    /**
     * 整张桌子上所有牌的数组
     */
    private List<Integer> listCard=null;
    /**
     * 有人要胡的數組
     */
    private List<Avatar> huAvatar = new ArrayList<>();
    /**
     *有人要碰的數組
     */
    private List<Avatar> penAvatar = new ArrayList<>();
    /**
     *有人要杠的數組
     */
    private List<Avatar> gangAvatar = new ArrayList<>();
    /**
     *有人要咋吃的數組
     */
    private List<Avatar> chiAvatar = new ArrayList<>();
    /**
     * 起手胡
     */
    private List<Avatar> qishouHuAvatar = new ArrayList<>();

    int[] putOffCount = {0, 0, 0, 0};
    int[][] putOffCard = new int[4][NanjingConfig.HUA_INDEX];
    /**
     * 下张牌的索引
     */
    private int nextCardindex = 0;
    /**
     * 上一家出的牌的点数
     */
    private int putOffCardPoint;
    /**
     * 判断是否是抢胡
     */
    private boolean qianghu = false;
    /**
     * 当前玩家摸的牌的点数
     */
    private int currentCardPoint = -2;
	private int pickCardPoint = -2;
    /**
     * 4家玩家信息集合
     */
    private List<Avatar> playerList;
    /**
     * 判断是否可以同时几个人胡牌
     */
    private int huCount=0;
    /**
     * 庄家
     */
    public Avatar bankerAvatar = null;
    public boolean bixiaHu = false;
    public boolean bixiaHuNext = false;
    public boolean changeMain = false;

    /**
     * 房间信息
     */
    private RoomVO roomVO;
    /**
     * 记录本次游戏是否已经胡了，控制摸牌
     */
    private boolean hasHu;
    /**
     * 记录某个玩家断线时最后一条消息
     */
    //private ResponseMsg responseMsg;
    /**
     * 记录某个玩家断线时发送最后一条消息的玩家
     */
    //private Avatar lastAvtar;

	private NormalHuPai normalHuPai;
    /**
     * String有胡家uuid:码牌1:码牌2  组成
     */
    private String allMas;
    /**
     * 控制胡牌返回次数
     */
    int numb = 1;
	//跟庄牌
	int followPoint = -1;
	//是否跟庄
	Avatar followBanke;
	//跟庄的次数
	int followNumber = 0;
	//是否被跟庄，最后结算的时候用
	boolean isFollow = false;
	//记录抢杠胡 多响情况
	boolean  hasPull = true;
	//单局是否结束，判断能否调用准备接口 10-11新增
	boolean  singleOver = true;
	
	 //游戏回放，
    PlayRecordGameVO playRecordGame;
    /**
     * 和前段握手，判断是否丢包的情况，丢包则继续发送信息
     *Integer为用户uuid
     */
    //private List<Integer> shakeHandsInfo = new  ArrayList<Integer>();
    private Map<Integer , ResponseMsg>  shakeHandsInfo= new  HashMap<Integer,ResponseMsg>();
    

	public void setPickAvatarIndex(int pickAvatarIndex) {
		this.pickAvatarIndex = pickAvatarIndex;
	}
	public Map<Integer , ResponseMsg> getShakeHandsInf() {
		return shakeHandsInfo;
	}
	public void updateShakeHandsInfo(Integer uuid ,  ResponseMsg msg) {
		shakeHandsInfo.put(uuid, msg);
	}

	public String getAllMas() {
		return allMas;
	}
	public List<Avatar> getPlayerList() {
		return playerList;
	}
	/**
	 * 房主ID
	 */
	private int theOwner;
	public void setCreateRoomRoleId(int value){
		theOwner = value;
	}

	public void setPlayerList(List<Avatar> playerList) {
		this.playerList = playerList;
	}

	public PlayCardsLogic(){
		normalHuPai = new NormalHuPai();
	}
	/**
	 * 初始化牌
	 */
	public void initCard(RoomVO value) {
		roomVO = value;
		paiCount = NanjingConfig.PAI_COUNT;
		listCard = new ArrayList<Integer>();
		for (int i = 0; i < paiCount; i++) {
			for (int k = 0; k < 4; k++) {
				if (i >= 34 && k > 0) continue;
				listCard.add(i);
			}
		}

		for(int i=0;i<playerList.size();i++){
			playerList.get(i).avatarVO.setPaiArray(new int[4][paiCount]);
			playerList.get(i).avatarVO.clearPaiArray();
		}

		for (int i = 0; i < putOffCard.length; i++) {
			for (int j = 0; j < putOffCard[i].length; j++) {
			    putOffCard[i][j] = 0;
			}
		}

		//洗牌
		shuffleTheCards();
		//发牌
		dealingTheCards();
	}

	/**
	 * 随机洗牌
	 */
	public void shuffleTheCards() {
		Collections.shuffle(listCard);
		Collections.shuffle(listCard);
	}
	/**
	 * 检测玩家是否胡牌了
	 * @param avatar
	 * @param cardIndex
	 * @param flag 0 normal, 1 tianhu, 1<<1 dihu, 1<<2 gang, 1<<3 buhua, 1<<4 qiangganghu, 1<<5 hai di lao
	 * @param type     当type为""
	 */
	public boolean checkAvatarIsHuPai(Avatar avatar,Avatar avatarShu, int cardIndex,String type, int flag, boolean putCardIn, Avatar fromAvatar){
		if(putCardIn){
			//传入的参数牌索引为100时表示天胡/或是摸牌，不需要再在添加到牌组中
			//System.out.println("检测胡牌的时候------添加别人打的牌："+cardIndex);
			avatar.putCardInList(cardIndex);
		}
		if(checkHu(avatar, avatarShu, cardIndex, flag)){
			//System.out.println("确实胡牌了");
			//System.out.println(avatar.printPaiString() +"  avatar = "+avatar.avatarVO.getAccount().getNickname());
			if(type.equals("chu")){
				//System.out.println("检测胡牌成功的时候------移除别人打的牌："+cardIndex);
				avatar.pullCardFormList(cardIndex);
			}
			if (type.equals("ganghu") && fromAvatar != null) {
				avatar.baopaiAvatar = fromAvatar;
			}
			return true;
		}else{
			//System.out.println("没有胡牌");
			if(type.equals("chu")){
				//System.out.println("检测胡牌失败的时候------移除别人打的牌："+cardIndex);
				avatar.pullCardFormList(cardIndex);
			}
			return false;
		}
	}
	/**
	 * 摸牌
	 *
	 *
	 */
    public void pickCard(){
      	clearAvatar();
        //摸牌
        pickAvatarIndex = getNextAvatarIndex();
        //pickAvatarIndex = nextIndex;
        //本次摸得牌点数，下一张牌的点数，及本次摸的牌点数
        int tempPoint = getNextCardPoint();
    	//System.out.println("摸牌："+tempPoint+"----上一家出牌"+putOffCardPoint+"--摸牌人索引:"+pickAvatarIndex);
        if(tempPoint != -1) {
        	//回放记录
        	PlayRecordOperation(pickAvatarIndex,tempPoint,2,-1,null,null);
        	
        	currentCardPoint = tempPoint;
			pickCardPoint = tempPoint;
        	Avatar avatar = playerList.get(pickAvatarIndex);
        	avatar.qiangHu = true;
        	avatar.canHu = true;
        	avatar.avatarVO.setHuType(0);//
            avatar.neverList.clear();
            //记录摸牌信息
        	 //avatar.canHu = true;
        	avatar.getSession().sendMsg(new PickCardResponse(1, tempPoint));
            for(int i=0;i<playerList.size();i++){
                if(i != pickAvatarIndex){
                    playerList.get(i).getSession().sendMsg(new OtherPickCardResponse(1,pickAvatarIndex));
                }else {
                	playerList.get(i).gangIndex.clear();//每次摸牌就先清除缓存里面的可以杠的牌下标
				}
            }
            //判断自己摸上来的牌自己是否可以胡
            StringBuffer sb = new StringBuffer();
            //摸起来也要判断是否可以杠，胡
            avatar.putCardInList(tempPoint);

            if (avatar.checkSelfGang()) {
            	gangAvatar.add(avatar);
            	sb.append("gang");
            	for (int i : avatar.gangIndex) {
            		sb.append(":"+i);
				}
            	sb.append(",");
            	//avatar.gangIndex.clear();//9-18出牌了才清楚(在杠时断线重连后需要这里面的数据)
            }
			int paiCount = listCard.size() - nextCardindex;
			int flag = Rule.Hu_Flag_Normal;
			if (paiCount < 16) flag |= Rule.Hu_Flag_Haidilao;
			if (avatar.changCard == false) flag |= Rule.Hu_Flag_Dihu;
            if(checkAvatarIsHuPai(avatar, avatar, tempPoint,"mo", flag, false, null)){
            	huAvatar.add(avatar);
            	sb.append("hu,");
            }
            if(sb.length()>2){
				avatar.getSession().sendMsg(new ReturnInfoResponse(1, sb.toString()));
            }

			// check huhua
			if (avatar.checkBuhua(tempPoint)) {
				buhua(avatar, tempPoint, 1);
			}
        }
        else{
            liuju();
        }
    }
    /**
     *
     */
    /**
     * 杠了别人(type)/自己摸杠了自后摸牌起来 然后再检测是否可以胡  可以杠等情况
     * @param avatar
     */
    public void pickCardAfterGang(Avatar avatar, Avatar fromAvatar){
    	
        //本次摸得牌点数，下一张牌的点数，及本次摸的牌点数
        int tempPoint = getNextCardPoint();
        currentCardPoint = tempPoint;
		pickCardPoint = tempPoint;
    	//System.out.println("摸牌!--"+tempPoint);
        if(tempPoint != -1) {
        	//int avatarIndex = playerList.indexOf(avatar); // 2016-8-2注释
        	pickAvatarIndex = playerList.indexOf(avatar);
        	// Avatar avatar = playerList.get(pickAvatarIndex);
            //记录摸牌信息
            for(int i=0;i<playerList.size();i++){
                if(i != pickAvatarIndex){
                    playerList.get(i).getSession().sendMsg(new OtherPickCardResponse(1,pickAvatarIndex));
                }else {
                	playerList.get(i).gangIndex.clear();//每次出牌就先清除缓存里面的可以杠的牌下标
					playerList.get(i).getSession().sendMsg(new PickCardResponse(1, tempPoint));
					//摸牌之后就重置可否胡别人牌的标签
					playerList.get(i).canHu = true;
					playerList.get(i).qiangHu = true;
					playerList.get(i).avatarVO.setHuType(0);
					//System.out.println("摸牌玩家------index"+pickAvatarIndex+"名字"+playerList.get(i).avatarVO.getAccount().getNickname());
				}
            }
            //记录摸牌信息
            PlayRecordOperation(pickAvatarIndex,currentCardPoint,2,-1,null,null);
            
            //判断自己摸上来的牌自己是否可以胡
            StringBuffer sb = new StringBuffer();
            //摸起来也要判断是否可以杠，胡
            avatar.putCardInList(tempPoint);
            if (avatar.checkSelfGang()) {
            	gangAvatar.add(avatar);
            	sb.append("gang");
            	for (int i : avatar.gangIndex) {
            		sb.append(":"+i);
				}
            	sb.append(",");
            	//avatar.gangIndex.clear();
            }
			int paiCount = listCard.size() - nextCardindex;
			int flag = Rule.Hu_Flag_Gang;
			if (paiCount < 16) flag |= Rule.Hu_Flag_Haidilao;
			if (avatar.changCard == false) flag |= Rule.Hu_Flag_Dihu;
            if(checkAvatarIsHuPai(avatar, avatar, tempPoint,"ganghu", flag, false, fromAvatar)){
            	//检测完之后不需要移除
            	huAvatar.add(avatar);
            	sb.append("hu,");
            }
            if(sb.length()>2){
            	//System.out.println(sb);
				avatar.getSession().sendMsg(new ReturnInfoResponse(1, sb.toString()));
            }

			// check huhua
			if (avatar.checkBuhua(tempPoint)) {
				buhua(avatar, tempPoint, 1);
			}
        }
        else{
        	//流局
            liuju();
        }
    }

    public void waitTime(Avatar avatar, int time) {
		avatar.getSession().sendMsg(new TimeWaitResponse(1, time));
	}

	public void buhuaBegin(Avatar avatar, int cardPoint) {
		int avatarIndex = playerList.indexOf(avatar);
		if (avatar.getPaiArray()[1][cardPoint] < 11) {
			avatar.getPaiArray()[1][cardPoint] = 11;
		} else {
			avatar.getPaiArray()[1][cardPoint]++;
		}
		avatar.putResultRelation(avatar.getPaiArray()[1][cardPoint],cardPoint+"");
		PlayRecordOperation(avatarIndex, cardPoint, 15, -1,null,null);

		// check hua gang
		checkHuaGang(avatar, cardPoint);

		int pickPoint = getNextCardPoint();
		PlayRecordOperation(avatarIndex, pickPoint,16,-1,null,null);
		avatar.putCardInList(pickPoint);

		for (int i=0;i<playerList.size();i++) {
			playerList.get(i).getSession().sendMsg(new BuhuaBeginResponse(1, cardPoint, pickPoint, avatarIndex));
		}
	}

	public void buhua(Avatar avatar, int cardPoint, int wait){
		int avatarIndex = playerList.indexOf(avatar);
		if (avatar.getPaiArray()[1][cardPoint] < 11) {
			avatar.getPaiArray()[1][cardPoint] = 11;
		} else {
			avatar.getPaiArray()[1][cardPoint]++;
		}
		avatar.putResultRelation(avatar.getPaiArray()[1][cardPoint],cardPoint+"");
		PlayRecordOperation(avatarIndex, cardPoint, avatar.getPaiArray()[1][cardPoint], -1,null,null);
		for (int i=0;i<playerList.size();i++) {
			if (wait > 0) waitTime(playerList.get(i), wait);
			playerList.get(i).getSession().sendMsg(new BuhuaResponse(1, cardPoint, avatarIndex));
		}

		// check hua gang
		checkHuaGang(avatar, cardPoint);

		pickCardAfterBuhua(avatar);
	}

	public boolean checkHuaGang(Avatar avatar, int cardPoint) {
	    boolean huagang = false;
		if (avatar.getPaiArray()[0][cardPoint] == 4) {
			huagang = true;
		} else if (cardPoint >= 34 && cardPoint <= 37){
			int checkCount = 0;
		    for (int i = 34; i <= 37; i++) {
		        if (avatar.getPaiArray()[0][i] > 0) checkCount++;
			}
			if (checkCount == 4) huagang = true;
		} else if (cardPoint >= 38 && cardPoint <= 41){
			int checkCount = 0;
			for (int i = 38; i <= 41; i++) {
				if (avatar.getPaiArray()[0][i] > 0) checkCount++;
			}
			if (checkCount == 4) huagang = true;
		}
		if (huagang) {
			int score = 20;
			String recordType = "8";
			String endStatisticstype = "huagang";

			//score = score * roomVO.getRealZashu();
			if (bixiaHu) score *= 2;

			int realScore = 0;
			int avatarIndex = playerList.indexOf(avatar);
			for (int i = 0; i < playerList.size(); i++) {
			    Avatar ava = playerList.get(i);
				if (ava.getUuId() != avatar.getUuId()) {
					//修改其他三家的分数
					realScore += score;
                    int result = ava.avatarVO.supdateScores(-score);
                    realScore += result;
					playerList.get(i).avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, -1 * (score + result));
					playerList.get(i).getSession().sendMsg(new HuaGangResponse(1, cardPoint, avatarIndex));
					socreChange(i, -1 * (score + result));
				}
			}

			avatar.avatarVO.supdateScores(realScore);
			avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, realScore);
			//整个房间统计每一局游戏 杠，胡的总次数
			roomVO.updateEndStatistics(avatar.getUuId() + "", endStatisticstype, 1);
			avatar.getSession().sendMsg(new HuaGangResponse(1, cardPoint, avatarIndex));
			socreChange(avatarIndex, realScore);

			String str = "0:" + cardPoint + ":" + Rule.Gang_Hua;
			avatar.avatarVO.getHuReturnObjectVO().updateTotalInfo("huagang", str);
			if (roomVO.getGuozhuangbi()) bixiaHuNext = true;
			if (checkGameover()) gameover();
			return true;
		}

		return false;
	}

	public void pickCardAfterBuhua(Avatar avatar){
		int tempPoint = getNextCardPoint();
		currentCardPoint = tempPoint;
		pickCardPoint = tempPoint;

		if(tempPoint != -1) {
			pickAvatarIndex = playerList.indexOf(avatar);
			//记录摸牌信息
			for(int i=0;i<playerList.size();i++){
				if(i != pickAvatarIndex){
					playerList.get(i).getSession().sendMsg(new OtherPickCardResponse(1,pickAvatarIndex));
				}else {
					playerList.get(i).gangIndex.clear();//每次摸牌就先清除缓存里面的可以杠的牌下标
					playerList.get(i).getSession().sendMsg(new PickCardResponse(1, tempPoint));
					playerList.get(i).canHu = true;
					playerList.get(i).qiangHu = true;
					playerList.get(i).avatarVO.setHuType(0);
				}
			}

			PlayRecordOperation(pickAvatarIndex,currentCardPoint,2,-1,null,null);

			StringBuffer sb = new StringBuffer();
			avatar.putCardInList(tempPoint);
			if (avatar.checkSelfGang()) {
				gangAvatar.add(avatar);
				sb.append("gang");
				for (int i : avatar.gangIndex) {
					sb.append(":"+i);
				}
				sb.append(",");
			}
			int paiCount = listCard.size() - nextCardindex;
			int flag = Rule.Hu_Flag_Buhua;
			if (paiCount < 16) flag |= Rule.Hu_Flag_Haidilao;
			if (avatar.changCard == false) flag |= Rule.Hu_Flag_Dihu;
			if(checkAvatarIsHuPai(avatar, avatar, tempPoint,"buhua", flag, false, null)){
				if (huAvatar.contains(avatar) == false) huAvatar.add(avatar);
				sb.append("hu,");
			}
			if(sb.length()>2){
				avatar.getSession().sendMsg(new ReturnInfoResponse(1, sb.toString()));
			}

			// check huhua
			if (avatar.checkBuhua(tempPoint)) {
				buhua(avatar, tempPoint, 1);
			}
		}
		else{
		    liuju();
		}
	}

    /**
     * 获取下一位摸牌人的索引
     * @return
     */
    public int getNextAvatarIndex(){
        int nextIndex = curAvatarIndex + 1;
        if(nextIndex >= 4){
            nextIndex = 0;
        }
        return nextIndex;
    }

    /**
     * 玩家选择放弃操作
     * @param avatar
     * @param
     *
     */
    public void gaveUpAction(Avatar avatar){
    	//放弃的时候，至少一个数组不为空才行
    	if(validateStatus()){
    		avatar.huAvatarDetailInfo.clear();
    		//System.out.println(JsonUtilTool.toJson(avatar.getRoomVO()));
    		avatar.avatarVO.setHuType(0);//
    		if(pickAvatarIndex == playerList.indexOf(avatar)){
    			//如果是自己摸的过，则 canHu = true；
    			avatar.canHu = true;
    			//system.out.println("自己摸的过");
    			/*if(huAvatar.contains(avatar)){
    				huAvatar.remove(avatar);
    			}
    			if(gangAvatar.contains(avatar)){
    				gangAvatar.remove(avatar);
    			}
    			if(penAvatar.contains(avatar)){
    				penAvatar.remove(avatar);
    			}
    			if(chiAvatar.contains(avatar)){
    				chiAvatar.remove(avatar);
    			}*/
    			clearAvatar();
    		}
    		else{
    			//system.out.println("别人打的过");
    			//如果别人打的牌过，
    			//放弃胡，则检测有没人杠
    			if(huAvatar.contains(avatar)){
    				huAvatar.remove(avatar);
    				avatar.canHu = false;
    				avatar.qiangHu = false;
    			}
    			if(gangAvatar.contains(avatar)){
    				gangAvatar.remove(avatar);
    				avatar.gangIndex.clear();
    			}
    			if(penAvatar.contains(avatar)){
    				penAvatar.remove(avatar);
    			}
    			if(chiAvatar.contains(avatar)){
    				chiAvatar.remove(avatar);
    			}
    			if(huAvatar.size() == 0) {
    				for(Avatar item : gangAvatar){
    					if (item.gangQuest) {
    						avatar.qiangHu = false;
    						//进行这个玩家的杠操作，并且把后面的碰，吃数组置为0;
    						gangCard(item,putOffCardPoint,1);
    						clearArrayAndSetQuest();
    						//system.out.println("********过了但是还有人gang");
    						return;
    					}
    				}
    				for(Avatar item : penAvatar) {
    					if (item.pengQuest) {
    						//进行这个玩家的碰操作，并且把后面的吃数组置为0;
    						pengCard(item, putOffCardPoint);
    						clearArrayAndSetQuest();
    						//system.out.println("********过了但是还有人pen");
    						return;
    					}
    				}
    				for(Avatar item : chiAvatar){
    					if (item.chiQuest) {
    						//进行这个玩家的吃操作
    						CardVO cardVo = new CardVO();
    						cardVo.setCardPoint(putOffCardPoint);
    						chiCard(item,cardVo);
    						clearArrayAndSetQuest();
    						//system.out.println("********过了但是还有人吃");
    						return;
    					}
    				}
    			}
    			//如果都没有人胡，没有人杠，没有人碰，没有人吃的情况下。则下一玩家摸牌
    			chuPaiCallBack();
    		}
    	}
    }

    /**
     * 清理胡杠碰吃数组，并把玩家的请求状态全部设置为false;
     */
    public void clearArrayAndSetQuest(){
        while (gangAvatar.size() >0){
            gangAvatar.remove(0).setQuestToFalse();
        }
        while (penAvatar.size() >0){
            penAvatar.remove(0).setQuestToFalse();
        }
        while (chiAvatar.size() >0){
            chiAvatar.remove(0).setQuestToFalse();
        }
    }

    /**
     * 出牌
     * @param avatar
     * @param cardPoint
     */
    public void putOffCard(Avatar avatar,int cardPoint){
		if(followBanke != null){
			if(followPoint == -1 || followPoint != cardPoint){
				followPoint = cardPoint;
				followNumber = 1;
				followBanke = avatar;
			}else {
				followNumber++;
			}
			if(followNumber == 4){
				followNumber = 0;
				followPoint = -1;
				isFollow = true;
				if (roomVO.getGuozhuangbi()) bixiaHuNext = true;
				if (cardPoint >= 27 && cardPoint <= 30 && roomVO.getFengfa()) {
					int score = 5;
					String recordType = "10";
					//score = score * roomVO.getRealZashu();
					if (bixiaHu) score *= 2;
					if (3 * score <= followBanke.avatarVO.getScores()) {
						int result = followBanke.avatarVO.supdateScores(-score * 3);
						followBanke.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, -(score * 3 + result));
						socreChange(playerList.indexOf(followBanke), -(score * 3 + result));

						int realScore = (score * 3 + result) / 3;
						for (int i = 0; i < playerList.size(); i++) {
							Avatar ava = playerList.get(i);
							if (ava.getUuId() != followBanke.getUuId()) {
								//修改其他三家的分数
								ava.avatarVO.supdateScores(realScore);
								ava.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, realScore);
								socreChange(i, realScore);
							}
						}

						String str = "0:" + cardPoint + ":" + Rule.Genpai;
						avatar.avatarVO.getHuReturnObjectVO().updateTotalInfo(Rule.Genpai, str);

						if (checkGameover()) {
							gameover();
							return;
						}
					}
				}
				followBanke = null;
			}
		} else {
			followPoint = cardPoint;
			followNumber = 1;
			followBanke = avatar;
		}

		//avatar.gangIndex.clear();//每次出牌就清除 缓存里面的可以杠的牌下标
		//System.err.println("出牌："+cardPoint);
		avatar.avatarVO.setHuType(0);//
		//出牌信息放入到缓存中，掉线重连的时候，返回房间信息需要
		avatar.avatarVO.updateChupais(cardPoint);
		//已经出牌就清除所有的吃，碰，杠，胡的数组
		clearAvatar();

		if (pickCardPoint != cardPoint) avatar.changCard = true;

		putOffCardPoint = cardPoint;
		//system.out.println("出牌点数"+putOffCardPoint+"---出牌人索引:"+playerList.indexOf(avatar));
		curAvatarIndex = playerList.indexOf(avatar);
		PlayRecordOperation(curAvatarIndex,cardPoint,1,-1,null,null);
		avatar.pullCardFormList(putOffCardPoint);
		putOffCount[curAvatarIndex]++;
		putOffCard[curAvatarIndex][cardPoint]++;

		// check da si zhang
		if (putOffCard[curAvatarIndex][cardPoint] == 4) {
				int score = 5;
				String recordType = "9";
				//score = score * roomVO.getRealZashu();
				if (bixiaHu) score *= 2;

			if (3 * score <= avatar.avatarVO.getScores()) {
				int result = avatar.avatarVO.supdateScores(-score * 3);
				avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, -(score * 3 + result));
				socreChange(curAvatarIndex, -(score * 3 + result));

				int realScore = (3 * score + result) / 3;
				for (int i = 0; i < playerList.size(); i++) {
					Avatar ava = playerList.get(i);
					if (ava.getUuId() != avatar.getUuId()) {
						//修改其他三家的分数
						ava.avatarVO.supdateScores(realScore);
						ava.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, realScore);
						socreChange(i, realScore);
					}
				}

				String str = "0:" + cardPoint + ":" + Rule.Dasizhang;
				avatar.avatarVO.getHuReturnObjectVO().updateTotalInfo(Rule.Dasizhang, str);

				if (checkGameover()) {
					gameover();
					return;
				}
			}
		}
		for(int i=0;i<playerList.size();i++){
			//不能返回给自己
			if(i != curAvatarIndex) {
				playerList.get(i).getSession().sendMsg(new ChuPaiResponse(1, putOffCardPoint, curAvatarIndex));
				// //system.out.println("发送打牌消息----"+playerList.get(i).avatarVO.getAccount().getNickname());
			}
			else{
				playerList.get(i).gangIndex.clear();//每次出牌就先清除缓存里面的可以杠的牌下标
			}
		}
		//房间为可抢杠胡
		if(avatar.getRoomVO().getZiMo() == 0){
			//出牌时，房间为可抢杠胡并且有癞子时才检测其他玩家有没胡的情况
			Avatar ava;
			StringBuffer sb;
			for(int i=0;i<playerList.size();i++){
				ava = playerList.get(i);
				if(ava.getUuId() != avatar.getUuId()) {
					sb = new StringBuffer();
					//判断吃，碰， 胡 杠的时候需要把以前吃，碰，杠胡的牌踢出再计算
					int flag = Rule.Hu_Flag_Normal;
					if (paiCount < 16) flag |= Rule.Hu_Flag_Haidilao;
					if (ava.changCard == false) flag |= Rule.Hu_Flag_Dihu;
					if(ava.canHu  && checkAvatarIsHuPai(ava, avatar, putOffCardPoint,"chu", flag, true, null)){
						//胡牌状态为可胡的状态时才行
						if (ava.neverList.contains(cardPoint) == false) {
							huAvatar.add(ava);
							sb.append("hu,");
						} else {
							ava.neverList.add(cardPoint);
						}
					}
					if (ava.checkGang(putOffCardPoint)) {
						gangAvatar.add(ava);
						//同时传会杠的牌的点数
						sb.append("gang:"+putOffCardPoint+",");
					}
					if (ava.checkPeng(putOffCardPoint)) {
						if (ava.neverList.contains(cardPoint) == false) {
							penAvatar.add(ava);
							sb.append("peng:" + curAvatarIndex + ":" + putOffCardPoint + ",");
						} else {
							ava.neverList.add(cardPoint);
						}
					}
					if(sb.length()>1){
						ava.getSession().sendMsg(new ReturnInfoResponse(1, sb.toString()));
//        				responseMsg = new ReturnInfoResponse(1, sb.toString());
//        				lastAvtar = ava;
					}
				}
			}
		}
		else{
			Avatar ava;
			StringBuffer sb;
			for(int i=0;i<playerList.size();i++){
				ava = playerList.get(i);
				if(ava.getUuId() != avatar.getUuId()) {
					sb = new StringBuffer();
					if (ava.checkGang(putOffCardPoint)) {
						gangAvatar.add(ava);
						//同时传会杠的牌的点数
						sb.append("gang:"+putOffCardPoint+",");
					}
					if (ava.checkPeng(putOffCardPoint)) {
						penAvatar.add(ava);
						sb.append("peng:"+curAvatarIndex+":"+putOffCardPoint+",");
					}
					if(sb.length()>1){
						//system.out.println(sb);
						/*try {
        		 			Thread.sleep(300);
        		 		} catch (InterruptedException e) {
        		 			e.printStackTrace();
        		 		}*/
						ava.getSession().sendMsg(new ReturnInfoResponse(1, sb.toString()));
//						responseMsg = new ReturnInfoResponse(1, sb.toString());
//						lastAvtar = ava;
					}
				}
			}
		}
		//如果没有吃，碰，杠，胡的情况，则下家自动摸牌
		chuPaiCallBack();
    }
    
    /**
     * 吃牌
     * @param avatar
     * @param
     * @return
     */
    public boolean chiCard(Avatar avatar , CardVO cardVo){
    	//碰，杠都比吃优先
    	boolean flag = false;
    	//int avatarIndex = playerList.indexOf(avatar);
		return flag;
    }
    /**
     *碰牌
     * @param avatar
     * @return
     */
    public boolean pengCard(Avatar avatar , int cardIndex){
    	boolean flag = false;
    	//这里可能是自己能胡能碰能杠 但是选择碰
    	if(cardIndex != putOffCardPoint ){
    		System.out.println("传入错误的牌:传入的牌"+cardIndex+"---上一把出牌："+putOffCardPoint);
    	}
		if (followBanke != null) followBanke = null;
		if(cardIndex < 0){
			try {
				avatar.getSession().sendMsg(new ErrorResponse(ErrorCode.Error_000019));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
    	//if((huAvatar.size() == 0 || huAvatar.contains(avatar))  && penAvatar.size() >= 1)) {
    	if((penAvatar.size() >= 1 && huAvatar.size() == 0) ||
    			( huAvatar.contains(avatar) && huAvatar.size() ==1 && penAvatar.size() ==1)) {
			avatar.changCard = true;
			if(huAvatar.contains(avatar)){
				huAvatar.remove(avatar);
			}
			if(gangAvatar.contains(avatar)){
				gangAvatar.remove(avatar);
			}
			if(penAvatar.contains(avatar)){
				//回放记录
				PlayRecordOperation(playerList.indexOf(avatar),cardIndex,4,-1,null,null);
				//把出的牌从出牌玩家的chupais中移除掉
				playerList.get(curAvatarIndex).avatarVO.removeLastChupais();
				penAvatar.remove(avatar);
				//更新牌组
				flag = avatar.putCardInList(cardIndex);
				avatar.setCardListStatus(cardIndex,1);


				//把各个玩家碰的牌记录到缓存中去,牌的index
				avatar.avatarVO.getHuReturnObjectVO().updateTotalInfo("peng", cardIndex+"");
				//avatar.getResultRelation().put(key, value);
				clearArrayAndSetQuest();
				for (int i=0;i<playerList.size();i++){
					if(playerList.get(i).getUuId() == avatar.getUuId()){
						//碰了的牌放入到avatar的resultRelation  Map中
						playerList.get(i).putResultRelation(1,cardIndex+"");
						playerList.get(i).avatarVO.getPaiArray()[1][cardIndex]=1;
						playerList.get(i).avatarVO.getPaiArray()[2][cardIndex]=pickAvatarIndex;
						avatar.getPaiArray()[1][cardIndex] = 1;
					}
					playerList.get(i).getSession().sendMsg(new PengResponse(1,cardIndex,playerList.indexOf(avatar)));
				}
//    				 responseMsg = new PengResponse(1,cardIndex,playerList.indexOf(avatar));
//    				 lastAvtar = avatar;
				//更新摸牌人信息 2016-8-3
				pickAvatarIndex = playerList.indexOf(avatar);
				curAvatarIndex = playerList.indexOf(avatar);
				currentCardPoint  = -2;//断线重连判断该自己出牌
				// }
    		 }
    		}else{
             if(penAvatar.size() > 0) {
				 avatar.changCard = true;
            	 for (Avatar ava : penAvatar) {
            		 ava.pengQuest = true;
				}
             }
           }
		return flag;
    }
    /**
     *杠牌
     * @param avatar
     * @return
     */
    public boolean gangCard(Avatar avatar , int cardPoint,int gangType){
    	boolean flag = false;
		if (followBanke != null) followBanke = null;
    	int avatarIndex = playerList.indexOf(avatar);
    	//if(gangAvatar.size() > 0 && huAvatar.size() == 0) {//2016-8-1
    	//if(gangAvatar.size() > 0  && huAvatar.size() == 0 || (huAvatar.size() == 1 && huAvatar.contains(avatar) )) {//2016-8-1
    	if(gangAvatar.size() > 0) {
			avatar.changCard = true;
			if((huAvatar.contains(avatar) && huAvatar.size() == 1 ) || huAvatar.size() == 0){
				if(huAvatar.contains(avatar)){
					huAvatar.remove(avatar);
				}
				if(penAvatar.contains(avatar)){
					penAvatar.remove(avatar);
				}
				if(chiAvatar.contains(avatar)){
					chiAvatar.remove(avatar);
				}
				if(gangAvatar.contains(avatar)){
					gangAvatar.remove(avatar);
					//判断杠的类型，自杠，还是点杠

					// gang from
					Avatar fromAvatar = null;
					String str;
					int type;
					int score; //杠牌分数
					String recordType;//暗杠 4 ， 明杠 5(用于统计不同type下的次数和得分)
					String endStatisticstype;
					int playRecordType;//游戏回放 记录杠的类型
					//if(avatar.getUuId() == playerList.get(curAvatarIndex).getUuId()){pickAvatarIndex
					if(avatar.getUuId() == playerList.get(pickAvatarIndex).getUuId()){
						//自杠(明杠或暗杠)，，这里的明杠时需要判断本房间是否是抢杠胡的情况，
						//如果是抢杠胡，则其他玩家有胡牌的情况下，可以胡
						String strs = avatar.getResultRelation().get(1);
						if(strs != null && strs.contains(cardPoint+"")){
							playRecordType = 3;
							if(avatar.getRoomVO().getZiMo() == 0 && checkQiangHu(avatar,cardPoint) ){
								//如果是抢杠胡，则判断其他玩家有胡牌的情况，有则给予提示 //判断其他三家是否能抢杠胡。
								//如果抢胡了，则更新上家出牌的点数为  杠的牌
								putOffCardPoint = cardPoint;
								gangAvatar.add(avatar);
								avatar.gangQuest = true;
								//回放记录
								PlayRecordOperation(avatarIndex,cardPoint,5,4,null,null);
								return false;
							}
							else{
								//存储杠牌的信息，
								avatar.putResultRelation(2,cardPoint+"");
								avatar.avatarVO.getPaiArray()[1][cardPoint] = 2;

								avatar.setCardListStatus(cardPoint,2);//杠牌标记2
								str = playerList.get(avatar.getPaiArray()[2][cardPoint]).getUuId()+":"+cardPoint+":"+Rule.Gang_ming;
								type = 0;
								score = 10;
								recordType ="5";
								endStatisticstype = "minggang";
							}

							fromAvatar = playerList.get(avatar.getPaiArray()[2][cardPoint]);
						}
						else{
							playRecordType = 2;
							//存储杠牌的信息，
							avatar.putResultRelation(2,cardPoint+"");
							avatar.avatarVO.getPaiArray()[1][cardPoint] = 2;
							avatar.getPaiArray()[1][cardPoint] = 2;

							avatar.setCardListStatus(cardPoint,2);//杠牌标记2
							str = "0:"+cardPoint+":"+Rule.Gang_an;
							type = 1;
							score = 5;
							recordType ="4";
							endStatisticstype = "angang";
						}
						//score = score * roomVO.getRealZashu();
						if (bixiaHu) score *= 2;
						int uuidOther = Integer.parseInt(str.split(":")[0]);

						int realScore = 0;
						for (int i = 0; i < playerList.size(); i++) {
							Avatar ava = playerList.get(i);
							if(ava.getUuId() != avatar.getUuId()){
								//修改其他三家的分数
								if (uuidOther != 0) {
									if (ava.getUuId() == uuidOther) {
										realScore += score;
										int result = ava.avatarVO.supdateScores(-score);
										realScore += result;
										ava.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, -1 * (score + result));
										socreChange(i, -1 * (score + result));
									}
								} else {
									realScore += score;
									int result = ava.avatarVO.supdateScores(-score);
									realScore += result;
									ava.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, -1 * (score + result));
									socreChange(i, -1 * (score + result));
								}
							}
						}

						avatar.avatarVO.supdateScores(realScore);
						avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, realScore);
						//整个房间统计每一局游戏 杠，胡的总次数
						roomVO.updateEndStatistics(avatar.getUuId()+"", endStatisticstype, 1);
						socreChange(avatarIndex, realScore);
						flag = true;
					}
					else{
						//存储杠牌的信息，
						playRecordType = 1;

						avatar.putResultRelation(2,cardPoint+"");
						avatar.avatarVO.getPaiArray()[1][cardPoint] = 2;
						avatar.getPaiArray()[1][cardPoint] = 2;
						avatar.getPaiArray()[3][cardPoint] = pickAvatarIndex;

						avatar.setCardListStatus(cardPoint,2);//杠牌标记2
						//把出的牌从出牌玩家的chupais中移除掉
						playerList.get(curAvatarIndex).avatarVO.removeLastChupais();

						//更新牌组(点杠时才需要更新)   自摸时不需要更新
						flag = avatar.putCardInList(cardPoint);
						avatar.avatarVO.getPaiArray()[2][cardPoint]=curAvatarIndex;
						score = 10;
						recordType = "5";
						str = playerList.get(curAvatarIndex).getUuId()+":"+cardPoint+":"+Rule.Gang_ming;
						type = 0;
						endStatisticstype = "minggang";

						//score = score * roomVO.getRealZashu();
						if (bixiaHu) score *= 2;

						//减点杠玩家的分数
						Avatar ava = playerList.get(curAvatarIndex);
						int result = ava.avatarVO.supdateScores(-score);
						ava.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, -1*(score + result));
						socreChange(curAvatarIndex, -1 * (score + result));

						//增加杠家的分数
						avatar.avatarVO.supdateScores(score + result);
						avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, (score + result));
						socreChange(avatarIndex, (score + result));

						//整个房间统计每一局游戏 杠，胡的总次数
						roomVO.updateEndStatistics(avatar.getUuId()+"", endStatisticstype, 1);

						fromAvatar = ava;
					}

					avatar.avatarVO.getHuReturnObjectVO().updateTotalInfo("gang", str);
					//回放记录
					PlayRecordOperation(avatarIndex,cardPoint,5,playRecordType,null,null);


					clearArrayAndSetQuest();
					if(gangType == 0) {
						//可以换牌的情况只补一张牌
						//摸牌并判断自己摸上来的牌自己是否可以胡/可以杠****
						for (int i=0;i<playerList.size();i++){
							if(avatar.getUuId() != playerList.get(i).getUuId()){
								//杠牌返回给其他人只返回杠的类型和杠牌的玩家位置
								playerList.get(i).getSession().sendMsg(new OtherGangResponse(1,cardPoint,avatarIndex,type));
//    							 responseMsg = new OtherGangResponse(1,cardPoint,avatarIndex,type);
//    							 lastAvtar = playerList.get(i);
							}
							else{
								//杠牌返回给其他人只返回杠的类型和杠牌的玩家位置
								playerList.get(i).getSession().sendMsg(new GangResponse(1, 1, 1,type));
//    							 responseMsg = new GangResponse(1, 1, 1,type);
//    							 lastAvtar = playerList.get(i);
							}
						}
						pickCardAfterGang(avatar, fromAvatar);//2016-8-1

						//  }
					}else if(gangType == 1){
						//摸两张  **** 这里需要单独处理摸的两张牌 是否可以胡，可以杠
						//摸牌并判断自己摸上来的牌自己是否可以胡/可以杠****
						for (int i=0;i<playerList.size();i++){
							if(avatar.getUuId() != playerList.get(i).getUuId()){
								//杠牌返回给其他人只返回杠的类型和杠牌的玩家位置
								playerList.get(i).getSession().sendMsg(new OtherGangResponse(1,cardPoint,avatarIndex,type));
//    							 responseMsg = new OtherGangResponse(1,cardPoint,avatarIndex,type);
//    							 lastAvtar = playerList.get(i);
							}
							else{
								//杠牌返回给其他人只返回杠的类型和杠牌的玩家位置
								playerList.get(i).getSession().sendMsg(new GangResponse(1, 1, 1,type));
//    							 responseMsg = new GangResponse(1, 1, 1,type);
//    							 lastAvtar = playerList.get(i);
							}
						}
						pickCardAfterGang(avatar, fromAvatar);//2016-8-1
					}
				}
			}
			else{
				if(gangAvatar.size() > 0) {
					for (Avatar ava : gangAvatar) {
						ava.gangQuest = true;
					}
				}
			}
    	 }else{
             if(gangAvatar.size() > 0) {
				 avatar.changCard = true;
            	 for (Avatar ava : gangAvatar) {
            		 ava.gangQuest = true;
				}
             }
             try {
            	 playerList.get(avatarIndex).getSession().sendMsg(new ErrorResponse(ErrorCode.Error_000016));
			} catch (IOException e) {
				e.printStackTrace();
			}
         }

		if (checkGameover()) {
			gameover();
		}
		return flag;
    }
    /**
     *胡牌
     * @param avatar
     * @return
     */
    public boolean huPai(Avatar avatar , int cardIndex,String type){
		boolean flag = false;
		//胡牌就清除掉存的其他人所有的可以碰 杠 吃的信息
		if(huCount == 0){
			huCount = huAvatar.size();
		}
		avatar.getPaiArray()[1][cardIndex] = 3;
		int playRecordType = 6;//胡牌的分类
		if(huAvatar.size() > 0) {
			if(huAvatar.contains(avatar)){
				//if(playerList.get(pickAvatarIndex).getUuId() != avatar.getUuId()){
				if(StringUtil.isNotEmpty(type) && type.equals("qianghu")){
					//是抢胡，则各更新 出牌人等于摸牌人 , 然后冲被抢玩家的牌里移除此牌
					playRecordType = 7;
					if(hasPull){//两个人都抢胡，只能移除一次牌
						curAvatarIndex = pickAvatarIndex;
						playerList.get(curAvatarIndex).pullCardFormList(cardIndex);//2016-8-9 22:38
						hasPull = false;
					}
				}
				if(pickAvatarIndex == curAvatarIndex){
					//把胡了的牌索引放入到对应赢家的牌组中
					avatar.putCardInList(cardIndex);
					//system.out.println("点炮");
					//当摸牌人的索引等于出牌人的索引时，表示点炮了
					//点炮    别人点炮的时候查看是否可以胡
					if(avatar.canHu){
						//胡牌数组中移除掉胡了的人
						huAvatar.remove(avatar);
						gangAvatar.clear();
						penAvatar.clear();
						chiAvatar.clear();;
						HuPaiType.getInstance().getHuType(playerList.get(curAvatarIndex), avatar,cardIndex ,playerList, bixiaHu);
						//整个房间统计每一局游戏 杠，胡的总次数
						roomVO.updateEndStatistics(avatar.getUuId()+"", "jiepao", 1);
						roomVO.updateEndStatistics(playerList.get(curAvatarIndex).getUuId()+"", "dianpao", 1);
						flag = true;
					}
					else{
						//system.out.println("放过一个人就要等自己摸牌之后才能胡其他人的牌");
						huAvatar.remove(avatar);
					}
				}
				else{
					//自摸,
					//system.out.println("自摸");
					//胡牌数组中移除掉胡了的人
					huAvatar.remove(avatar);
					gangAvatar.clear();
					penAvatar.clear();
					chiAvatar.clear();;
					HuPaiType.getInstance().getHuType(avatar, avatar,cardIndex,playerList, bixiaHu);
					roomVO.updateEndStatistics(avatar.getUuId()+"", "zimo", 1);
					flag = true;
				}
				//本次游戏已经胡，不进行摸牌
				hasHu = true;
				//游戏回放
				PlayRecordOperation(playerList.indexOf(avatar),cardIndex,playRecordType,-1,null,null);
			}
		}
		//所有人胡完
		if (avatar.getUuId() == bankerAvatar.getUuId()) {
			int huType = avatar.avatarVO.getHuType();
			if ((huType & Rule.Hu_Type_Dahu) > 0 || huCount >= 2) {
				if (roomVO.getZhanzhuangbi()) bixiaHuNext = true;
				bankerAvatar.avatarVO.setMainCount(bankerAvatar.avatarVO.getMainCount() + 1);
				changeMain = true;
			}
		}

		if(huAvatar.size()==0 && numb == 1 ){
			numb++;
			if (changeMain == false) {
				int bankerAvatarIndex = playerList.indexOf(bankerAvatar);
				bankerAvatarIndex += 1;
				if (bankerAvatarIndex >= 4) bankerAvatarIndex = 0;

				bankerAvatar.avatarVO.setMainCount(0);
				bankerAvatar.avatarVO.setMain(false);

				bankerAvatar = playerList.get(bankerAvatarIndex);
				bankerAvatar.avatarVO.setMainCount(0);
				bankerAvatar.avatarVO.setMain(true);
				changeMain = true;
			}
			//更新roomlogic的PlayerList信息
			RoomManager.getInstance().getRoom(playerList.get(0).getRoomVO().getRoomId()).setPlayerList(playerList);
			//一局牌胡了，返回这一局的所有数据吃，碰， 杠，胡等信息
			settlementData("0");
		}
		return flag;
	}

	public void liuju() {
		PlayRecordOperation(pickAvatarIndex,-1,9,-1,null,null);
		settlementData("1");

		if (roomVO.getGuozhuangbi()) bixiaHuNext = true;
		bankerAvatar.avatarVO.setMainCount(bankerAvatar.avatarVO.getMainCount() + 1);
	}

	public boolean checkGameover() {
		int zeroCount = 0;
		if (roomVO.getRoundtype() == 0) {
			for (Avatar avatar : playerList) {
				int result = avatar.avatarVO.getScores();
				if (result <= 0) zeroCount++;
			}

			if (zeroCount >= roomVO.getYuanziRule()) return true;
		}

		return false;
	}

	public void gameover() {
		settlementData("2");
	}

	/**
	 * 胡牌/流局/解散房间后返回结算数据信息
	 * 不能多次调用，多次调用，总分会多增加出最近一局的分数    第一局结束扣房卡
	 */
	public void settlementData(String  type){
		int totalCount = roomVO.getRealRoundNumber();
		RoomLogic roomLogic = RoomManager.getInstance().getRoom(roomVO.getRoomId());
		int useCount = roomLogic.getCount();
		if(totalCount == (useCount +1)){
			//第一局结束扣房卡
			deductRoomCard();
		}
		JSONArray array = new JSONArray();
		JSONObject json = new JSONObject();
		if(!type.equals("0")){
			allMas = null;
		}
		StandingsDetail standingsDetail = new StandingsDetail();
		StringBuffer content = new StringBuffer();
		StringBuffer score = new StringBuffer();
		for (Avatar avatar : playerList) {
			HuReturnObjectVO   huReturnObjectVO = avatar.avatarVO.getHuReturnObjectVO();
			//生成战绩内容
			content.append(avatar.avatarVO.getAccount().getNickname()+":"+huReturnObjectVO.getTotalScore()+",");

			//统计本局分数
			huReturnObjectVO.setNickname(avatar.avatarVO.getAccount().getNickname());
			huReturnObjectVO.setPaiArray(avatar.avatarVO.getPaiArray()[0]);
			huReturnObjectVO.setUuid(avatar.getUuId());
			huReturnObjectVO.setHuType(avatar.avatarVO.getHuType());
			array.add(huReturnObjectVO);
			//在整个房间信息中修改总分数(房间次数用完之后的总分数)
			roomVO.updateEndStatistics(avatar.getUuId()+"", "scores", huReturnObjectVO.getTotalScore());
			score.append(avatar.getUuId()+":"+ avatar.avatarVO.getScores()+",");
			//游戏回放 中码消息
			if(avatar.avatarVO.getMain()){
				if(!type.equals("0")){
					PlayRecordOperation(playerList.indexOf(avatar),-1,8,-1,null,null);
				}
				else{
					PlayRecordOperation(playerList.indexOf(avatar),-1,8,-1, null,null);
				}
			}
		}
		json.put("avatarList", array);
		json.put("allMas", allMas);
		json.put("type", type);
		json.put("validMas", new ArrayList<>());
		json.put("currentScore", score.toString());
		//生成战绩content
		standingsDetail.setContent(content.toString());
		try {
			standingsDetail.setCreatetime(DateUtil.toChangeDate(new Date(), DateUtil.maskC));
			int id = StandingsDetailService.getInstance().saveSelective(standingsDetail);
			if(id >0){
				roomLogic.getStandingsDetailsIds().add(standingsDetail.getId());
				//更新游戏回放中的玩家分数
				PlayRecordInitUpdateScore(standingsDetail.getId());
			}
			else{
				System.out.println("分局战绩录入失败："+new Date());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}


		for (Avatar avatar : playerList) {
			//发送消息
			avatar.getSession().sendMsg(new HuPaiResponse(1,json.toString()));

			avatar.overOff = true;
			avatar.oneSettlementInfo = json.toString();

			//清除一些存储数据
			avatar.getResultRelation().clear();
			//avatar.avatarVO.setIsReady(false);10-11注释 在游戏开始之后就已经重置准备属性为false
			avatar.avatarVO.getChupais().clear();
			avatar.avatarVO.setCommonCards(0);
			//清除 hu	ReturnObjectVO 信息
			avatar.avatarVO.setHuReturnObjectVO(new HuReturnObjectVO());

			//没有经过算分  不能开始下一局游戏
			singleOver = true;//10-11新增
		}

		roomLogic.gameOver = (useCount <= 0);
		if (type.equals("2")) roomLogic.gameOver= true;

		if (roomLogic.gameOver == false) {
			int zeroCount = 0;
			if (roomVO.getRoundtype() == 0) {
				for (Avatar avatar : playerList) {
					int result = avatar.avatarVO.getScores();
					if (result <= 0) zeroCount++;
				}

				if (zeroCount >= roomVO.getYuanziRule()) roomLogic.gameOver = true;
			}
		}


		//房间局数用完，返回本局胡牌信息的同时返回整个房间这几局的胡，杠等统计信息
		if(roomLogic.gameOver){
			//总房间战绩
			Standings standings  = new Standings();
			StringBuffer sb = new StringBuffer();
			//standings.setContent(content);
			Map<String, Map<String, Integer>> endStatistics = roomVO.getEndStatistics();
			Map<String,Integer> map = new HashMap<String, Integer>();
			Set<Entry<String, Map<String, Integer>>> set= endStatistics.entrySet();
			JSONObject js = new JSONObject();
			List<FinalGameEndItemVo> list = new ArrayList<FinalGameEndItemVo>();
			FinalGameEndItemVo obj;
			for (Entry<String, Map<String, Integer>>  param : set) {
				obj = new FinalGameEndItemVo();
				obj.setUuid(Integer.parseInt(param.getKey()));
				sb.append(AccountService.getInstance().selectByUUid(Integer.parseInt(param.getKey())).getNickname());
				map = param.getValue();
				for (Entry<String, Integer> entry : map.entrySet()) {
					switch (entry.getKey()) {
						case "zimo":
							obj.setZimo(entry.getValue());
							break;
						case "jiepao":
							obj.setJiepao(entry.getValue());
							break;
						case "dianpao":
							obj.setDianpao(entry.getValue());
							break;
						case "minggang":
							obj.setMinggang(entry.getValue());
							break;
						case "angang":
							obj.setAngang(entry.getValue());
							break;
						case "scores":
							obj.setScores(entry.getValue());
							sb.append(":"+entry.getValue()+",");
							break;
						default:
							break;
					}
				}
				list.add(obj);
			}
			js.put("totalInfo", list);
			js.put("theowner",theOwner);
			//system.out.println("这个房间次数用完：返回数据=="+js.toJSONString());
			//战绩记录存储
			standings.setContent(sb.toString());
			try {
				standings.setCreatetime(DateUtil.toChangeDate(new Date(), DateUtil.maskC));
				standings.setRoomid(roomVO.getId());
				int i = StandingsService.getInstance().saveSelective(standings);
				if(i> 0){
					//存储 房间战绩和每局战绩关联信息
					StandingsRelation standingsRelation;
					List<Integer> standingsDetailsIds =RoomManager.getInstance().getRoom(roomVO.getRoomId()).getStandingsDetailsIds();
					for (Integer standingsDetailsId : standingsDetailsIds) {
						standingsRelation = new StandingsRelation();
						standingsRelation.setStandingsId(standings.getId());
						standingsRelation.setStandingsdetailId(standingsDetailsId);
						StandingsRelationService.getInstance().saveSelective(standingsRelation);
					}
					//存储 房间战绩和每个玩家关联信息
					StandingsAccountRelation standingsAccountRelation;
					for (Avatar avatar : playerList) {
						standingsAccountRelation = new StandingsAccountRelation();
						standingsAccountRelation.setStandingsId(standings.getId());
						standingsAccountRelation.setAccountId(avatar.avatarVO.getAccount().getId());
						StandingsAccountRelationService.getInstance().saveSelective(standingsAccountRelation);
					}
				}
				System.out.println("整个房间战绩"+i);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			//发送消息
			for (Avatar avatar : playerList) {
				avatar.getSession().sendMsg(new HuPaiAllResponse(1,js.toString()));
			}
			//4局完成之后解散房间//销毁
			roomLogic.destoryRoomLogic();
			roomLogic = null;
		}
		//判断该房间还有没有次数。有则清除玩家的准备状态，为下一局开始做准备
		/*else{  10-11注释 在游戏开始之后就已经重置准备属性为false
			//清除当前房间牌的数据信息
			for (Avatar avatar : playerList) {
				avatar.avatarVO.setIsReady(false);
			}
		}*/
	}

	/**
	 * 出牌返回出牌点数和下一家玩家信息
	 * @param
	 *
	 */
	private void chuPaiCallBack(){
		//把出牌点数和下面该谁出牌发送会前端  下一家都还没有摸牌就要出牌了??
		if(!hasHu && checkMsgAndSend()){
			//如果没有吃，碰，杠，胡的情况，则下家自动摸牌
			pickCard();
		}
	}
	/**
	 * 發送吃，碰，杠，胡牌信息
	 * @return
	 */
	private boolean checkMsgAndSend(){
		if(huAvatar.size() > 0){
			return false;
		}
		if(gangAvatar.size() >0){
			return false;
		}
		if(penAvatar.size()>0){
			return false;
		}
		if(chiAvatar.size()>0){
			return false;
		}
		return true;
	}

	/**
	 * 发牌
	 */
	private void dealingTheCards() {
		nextCardindex = 0;
		bankerAvatar = null;
		int bankerIndex = 0;
		for (int i = 0; i < 13; i++) {
			for (int k = 0; k < playerList.size(); k++) {
				if (bankerAvatar == null) {
					if (playerList.get(k).avatarVO.getMain()) {
						bankerAvatar = playerList.get(k);
						bankerIndex = k;
					}
				}
				playerList.get(k).putCardInList(listCard.get(nextCardindex));
				playerList.get(k).oneSettlementInfo = "";
				playerList.get(k).overOff = false;
				nextCardindex++;
			}
		}
		bankerAvatar.putCardInList(listCard.get(nextCardindex));
		//nextCardindex++;
		singleOver = false;

		//游戏回放
		PlayRecordInit();
	}

	public void initCheckBuhua() {
		// check huhua
		for(int i=0;i<playerList.size();i++) {
			Avatar ava = playerList.get(i);
			waitTime(ava, 1);
			int buhuaIndex = ava.checkBuhua();
			while (buhuaIndex != -1) {
				buhuaBegin(ava, buhuaIndex);
				buhuaIndex = ava.checkBuhua();
			}
		}
	}

	public void initCheck() {
		followBanke = bankerAvatar;
		initCheckBuhua();

		//检测一下庄家有没有天胡
		if(checkHu(bankerAvatar,bankerAvatar, listCard.get(nextCardindex), Rule.Hu_Flag_Tianhu)){
			//检查有没有天胡/有则把相关联的信息放入缓存中
			huAvatar.add(bankerAvatar);
			////二期优化注释 pickAvatarIndex = playerList.indexOf(bankerAvatar);//第一个摸牌人就是庄家
			//发送消息
			bankerAvatar.getSession().sendMsg(new HuPaiResponse(1,"hu,"));
			bankerAvatar.huAvatarDetailInfo.add(listCard.get(nextCardindex)+":"+0);
		}

		if(bankerAvatar.checkSelfGang()){
			gangAvatar.add(bankerAvatar);
			//发送消息
			StringBuffer sb = new StringBuffer();
			sb.append("gang");
			for (int i : bankerAvatar.gangIndex) {
				sb.append(":"+i);
			}
			sb.append(",");

			bankerAvatar.getSession().sendMsg(new ReturnInfoResponse(1, sb.toString()));
			// bankerAvatar.huAvatarDetailInfo.add(bankerAvatar.gangIndex.get(0)+":"+2);
			//bankerAvatar.gangIndex.clear();
		}
	}

	/**
	 * 游戏回放，记录 房间信息和初始牌组，玩家信息
	 */
	public void PlayRecordInit(){
		playRecordGame = new PlayRecordGameVO();
		RoomVO roomVo = roomVO.clone();
		roomVo.setEndStatistics(new HashMap<String, Map<String,Integer>>());
		roomVo.setPlayerList(new ArrayList<>());
		playRecordGame.roomvo = roomVo;
		PlayRecordItemVO playRecordItemVO;
		Account account;
		StringBuffer sb;
		for (int i = 0; i < playerList.size(); i++) {
			playRecordItemVO = new PlayRecordItemVO();
			account = playerList.get(i).avatarVO.getAccount();
			playRecordItemVO.setAccountIndex(i);
			playRecordItemVO.setAccountName(account.getNickname());
			sb = new StringBuffer();
			int [] str = playerList.get(i).getPaiArray()[0];
			for (int j = 0; j < str.length; j++) {
				sb.append(str[j]+",");
			}
			playRecordItemVO.setCardList(sb.substring(0,sb.length()-1));
			playRecordItemVO.setHeadIcon(account.getHeadicon());
			playRecordItemVO.setSex(account.getSex());
			playRecordItemVO.setGameRound(roomVO.getCurrentRound());
			playRecordItemVO.setUuid(account.getUuid());
			playRecordGame.playerItems.add(playRecordItemVO);
		}
	}

	/**
	 * 游戏回放，记录打牌操作信息
	 *
	 * @param curAvatarIndex  操作玩家索引
	 * @param cardIndex 操作相关牌索引
	 * @param type 操作相关步骤  1出牌，2摸牌，3吃，4碰，5杠，6胡(自摸/点炮),7抢胡,8抓码,9:流局..... 11 - 14 buhua 15 buhua begin 16 buhua begin pickup
	 * @param gangType  type不为杠时 传入 -1
	 * @param ma  不是抓码操作时 为null
	 */
	public void PlayRecordOperation(Integer curAvatarIndex , Integer cardIndex,Integer type,Integer gangType,String ma,List<Integer> valideMa){

		//System.out.println("记录操作"+type);
		PlayBehaviedVO behaviedvo = new PlayBehaviedVO();
		behaviedvo.setAccountindex_id(curAvatarIndex);
		behaviedvo.setCardIndex(cardIndex+"");
		behaviedvo.setRecordindex(playRecordGame.behavieList.size());
		behaviedvo.setType(type);
		behaviedvo.setGangType(gangType);
		if(StringUtil.isNotEmpty(ma)){
			behaviedvo.setMa(ma);
			behaviedvo.setValideMa(valideMa);
		}
		playRecordGame.behavieList.add(behaviedvo);

	}

	/**
	 * 游戏回放，记录 房间信息和初始牌组，玩家信息 中添加分数
	 * @param standingsDetailId 本局游戏的id
	 */
	public void PlayRecordInitUpdateScore(int standingsDetailId){

		if(!playRecordGame.playerItems.isEmpty()){
			//没有发牌就解散房间
			for (int i = 0; i < playerList.size(); i++) {
				playRecordGame.playerItems.get(i).setSocre(playerList.get(i).avatarVO.getScores());
			}
			//playRecordGame.standingsDetailId = standingsDetailId;
			//信息录入数据库表中
			//String playRecordContent = JsonUtilTool.toJson(playRecordGame);
			String playRecordContent = JSONObject.toJSONString(playRecordGame);
			//System.out.println(playRecordContent);
			PlayRecord playRecord = new PlayRecord();
			playRecord.setPlayrecord(playRecordContent);
			playRecord.setStandingsdetailId(standingsDetailId);
			PlayRecordService.getInstance().saveSelective(playRecord);
			//录入表之后重置 记录
			playRecordGame = new PlayRecordGameVO();
		}

	}

	/**
	 * 获取下一张牌的点数,如果返回为-1 ，则没有牌了
	 * @return
	 */
	public int getNextCardPoint(){
		nextCardindex++;
		if(nextCardindex<listCard.size()){
			return listCard.get(nextCardindex);
		}
		return -1;
	}
	private void checkQiShouFu(){
		for(int i=0;i<playerList.size();i++){
			//判断是否有起手胡，有则加入到集合里面
			if(qiShouFu(playerList.get(i))){
				qishouHuAvatar.add(playerList.get(i));
			}
		}
	}
	/**
	 * 是否是起手胡
	 * @return
	 */
	public boolean qiShouFu(Avatar avatar){
		/**
		 * 起手胡：
		 1 、大四喜：起完牌后，玩家手上已有四张一样的牌，即可胡牌。（四喜计分等同小胡自摸）pai[i] == 4
		 2 、板板胡：起完牌后，玩家手上没有一张 2 、 5 、 8 （将牌），即可胡牌。（等同小胡自摸）
		 3 、缺一色：起完牌后，玩家手上筒、索、万任缺一门，即可胡牌。（等同小胡自摸）
		 4 、六六顺：起完牌后，玩家手上已有 2 个刻子（刻子：三个一样的牌），即可胡牌。（等同小胡自摸）
		 */
		//1:大四喜
		boolean flag = false;
		int[] pai= avatar.avatarVO.getPaiArray()[0];
		boolean flagWan = true;
		boolean flagTiao= true;
		boolean flagTong = true;
		int threeNum = 0;
		boolean dasixi = false;
		boolean banbanhu = false;
		boolean quyise = false;
		boolean liuliushun = false;
		for (int i =0 ; i< pai.length ; i++) {
			if(pai[i] == 4){
				//大四喜
				dasixi = true;
				//胡牌信息放入缓存中****
			}
			if(pai[i] == 3){
				//六六顺
				threeNum++;
				if(threeNum == 2){
					liuliushun = true;
				}
			}
			if(i>=0 && i <=8){
				//缺一色
				if(pai[i] > 0){
					//只要存在一条万子
					flagWan = false;
				}
			}
			else if(i>9 && i<=18){
				//缺一色
				if(pai[i] > 0){
					//只要存在一条条子
					flagTiao = false;
				}
			}
			else{
				//缺一色
				if(pai[i] > 0){
					//只要存在一条筒子
					flagTong = false;
				}
			}
		}
		if(pai[1] ==0 && pai[4] ==0 && pai[7] ==0 &&
				pai[10] ==0 && pai[13] ==0 && pai[16] ==0 &&
				pai[19] ==0 && pai[22] ==0 && pai[25] ==0){
			//板板胡
			banbanhu = true;
		}
		if((flagWan || flagTiao || flagTong)){
			//缺一色
			quyise = true;
		}
		return flag;
	}

	private List<AvatarVO> getAvatarVoList(){
		List<AvatarVO> result = new ArrayList<>();
		for (int m = 0; m < playerList.size(); m++) {
			result.add(playerList.get(m).avatarVO);
		}
		return result;
	}

	// flag 0 normal, 1 tianhu, 1<<1 dihu, 1<<2 gang, 1<<3 buhua, 1<<4 qiangganghu, 1<<5 hai di lao
	public boolean checkHuNanjing(Avatar avatar, Avatar avatarShu, Integer cardIndex, int flag){
		avatar.baopaiAvatar = null;
		int huType = normalHuPai.checkNanjingHu(avatar, avatarShu, playerList, avatar.getPaiArray(), cardIndex, flag);
		avatar.avatarVO.setHuType(huType);
		return huType > 0;
	}

	/**
	 * 检测胡牌算法，其中包含七小对，普通胡牌
	 * @param avatar
	 * @param flag 0 normal, 1 tianhu, 1<<1 dihu, 1<<2 gang, 1<<3 buhua, 1<<4 qiangganghu, 1<<5 hai di lao
	 * @return
	 */
	private boolean checkHu(Avatar avatar,Avatar avatarShu, Integer cardIndex, int flag){
		avatar.baopaiAvatar = null;
		if (avatar != avatarShu && avatarShu.avatarVO.getScores() <= 0) return false;
		return checkHuNanjing(avatar, avatarShu,cardIndex, flag);
	}
	/**
	 *
	 * @param paiList
	 * @return
	 */
	String getString(int[] paiList){
		String result = "int string = ";
		for(int i=0;i<paiList.length;i++){
			result += paiList[i];
		}
		return result;
	}

	/**
	 * 前后端握手消息处理，前段接收到消息则会访问整个握手接口，说明接收到信息了
	 * 然后后台从list里面移除这个用户对应的uuid，
	 * 到最后list里面剩下的就表示前段还没有接收到消息，
	 * 则重新发送消息
	 * @param avatar
	 */
	public void shakeHandsMsg(Avatar  avatar){
		shakeHandsInfo.remove(avatar.getUuId());

	}
	/**
	 * 在可以抢杠胡的情况下，判断其他人有没胡的情况
	 * @return boolean
	 */
	public boolean checkQiangHu(Avatar avatar ,int cardPoint){
		boolean flag = false;

		for (int i = 0;  i < playerList.size(); i++) {
			Avatar ava = playerList.get(i);
			if(ava.getUuId() != avatar.getUuId() && ava.qiangHu){
				//判断其他三家有没抢胡的情况
				ava.putCardInList(cardPoint);
				int huFlag = Rule.Hu_Flag_Qiangganghu;
				if (paiCount < 16) huFlag |= Rule.Hu_Flag_Haidilao;
				if (ava.changCard == false) huFlag |= Rule.Hu_Flag_Dihu;
				if(checkHuNanjing(ava, avatar, cardPoint, huFlag)){
				    ava.baopaiAvatar = avatar;
					huAvatar.add(ava);
					//向玩家发送消息
					ava.getSession().sendMsg(new ReturnInfoResponse(1, "qianghu:"+cardPoint));
					flag = true;

				}
				ava.pullCardFormList(cardPoint);
			}
		}
		if(flag){
			qianghu = true;
			//有人可以抢杠胡的时候，出牌玩家的索引为当前杠牌玩家
			//curAvatarIndex = playerList.indexOf(avatar);//2016-8-9  22:34修改
			//avatar.pullCardFormList(cardPoint);
		}
		return flag;
	}
	/**
	 * 玩家玩游戏时断线重连
	 * @param avatar
	 */
	public void returnBackAction(Avatar avatar){
		RoomVO room = roomVO.clone();
		List<AvatarVO> lists = new ArrayList<AvatarVO>();
		for (int i = 0; i < playerList.size(); i++) {
			if(playerList.get(i).getUuId() != avatar.getUuId()){
				//给其他三个玩家返回重连用户信息
				playerList.get(i).getSession().sendMsg(new OtherBackLoginResonse(1, avatar.getUuId()+""));
			}
			lists.add(playerList.get(i).avatarVO);
		}
		//给自己返回整个房间信息
		AvatarVO avatarVo = null ;
		List<AvatarVO> playerLists = new ArrayList<AvatarVO>();
		for (int j = 0; j < lists.size(); j++) {
			int paiCount = 0;//有多少张普通牌
			avatarVo = lists.get(j);
			if(avatarVo.getAccount().getUuid() != avatar.getUuId()){
				//其他三家的牌组需要处理，不能让重连的玩家知道详细的牌组
				for (int k = 0; k < avatarVo.getPaiArray()[0].length; k++) {
					if(avatarVo.getPaiArray()[0][k] != 0 && avatarVo.getPaiArray()[1][k] == 0){
						paiCount= paiCount +avatarVo.getPaiArray()[0][k];
						//avatarVo.getPaiArray()[0][k] = 0;
					}
				}
				avatarVo.setCommonCards(paiCount);
				playerLists.add(avatarVo);

			}
			else{
				//不需要处理自己的牌组
				playerLists.add(avatarVo);
			}
		}
		if(playerList.size() == 3){
			playerList.add(avatar);
		}
		if(playerLists.size() == 3){
			playerLists.add(avatar.avatarVO);
		}
    		/*else{
    		for (int i = 0; i < playerLists.size(); i++) {
				if(playerLists.get(i).getAccount().getUuid() == avatar.getUuId() ){
					playerLists.remove(i);
					playerLists.add(avatar.avatarVO);;
				}
			}
    	}*/
		room.setPlayerList(playerLists);
		avatar.getSession().sendMsg(new BackLoginResponse(1, room));
		//lastAvtar.getSession().sendMsg(responseMsg);


	}
	/**
	 * 断线重连返回最后操作信息
	 * @param avatar
	 */
	public void LoginReturnInfo(Avatar avatar){
		//断线重连之后，该进行的下一步操作，json存储下一步操作指引
		JSONObject json = new JSONObject();//
		StringBuffer sb = new StringBuffer();
		if(huAvatar.contains(avatar)){
			//这里需要判断是自摸胡，还是别人点炮胡
			if(pickAvatarIndex != curAvatarIndex){
				//自摸
				json.put("currentCardPoint", currentCardPoint);//当前摸的牌点数
				json.put("curAvatarIndex", curAvatarIndex);//当前出牌人的索引
				json.put("putOffCardPoint", putOffCardPoint);//当前出的牌的点数
			}
			else{
				//点炮
				json.put("curAvatarIndex", curAvatarIndex);//当前出牌人的索引
				json.put("putOffCardPoint", putOffCardPoint);//当前出的牌的点数
			}
			if(qianghu){
				sb.append("qianghu:"+putOffCardPoint+",");
				//system.out.println("抢胡");
			}
			else{
				sb.append("hu,");
				//system.out.println("胡");
			}
		}
		if(penAvatar.contains(avatar)){
			sb.append("peng,");
			json.put("curAvatarIndex", curAvatarIndex);//当前出牌人的索引
			json.put("putOffCardPoint", putOffCardPoint);//当前出的牌的点数
			//system.out.println("碰");
		}
		if(gangAvatar.contains(avatar)){
			//这里需要判断是别人打牌杠，还是自己摸牌杠
			StringBuffer gangCardIndex = new StringBuffer();
			List<Integer> gangIndexs = avatar.gangIndex;
			for (int i = 0; i < gangIndexs.size(); i++) {
				gangCardIndex.append(":"+gangIndexs.get(i));
			}
			if(avatar.getUuId() == playerList.get(pickAvatarIndex).getUuId()){
				//自摸杠
				sb.append("gang"+gangCardIndex.toString()+",");
				json.put("currentCardPoint", currentCardPoint);//当前摸的牌点数
				json.put("curAvatarIndex", curAvatarIndex);//当前出牌人的索引
				json.put("putOffCardPoint", putOffCardPoint);//当前出的牌的点数
				//system.out.println("自杠");
			}
			else{
				json.put("curAvatarIndex", curAvatarIndex);//当前出牌人的索引
				json.put("putOffCardPoint", putOffCardPoint);//当前出的牌的点数
				sb.append("gang"+gangCardIndex.toString()+",");
				//system.out.println("点杠");
			}
		}
		if(sb.length()>1){
			////system.out.println(sb);
			//该自己杠/胡/碰
			//游戏轮数
			int roundNum = RoomManager.getInstance().getRoom(avatar.getRoomVO().getRoomId()).getCount();
			json.put("gameRound", roundNum);//游戏轮数
			//桌面剩余牌数
			json.put("surplusCards", listCard.size() - nextCardindex);
			//System.out.println(json.toString());
			avatar.getSession().sendMsg(new ReturnOnLineResponse(1, json.toString()));
			/*try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
			//System.out.println(sb);
			avatar.getSession().sendMsg(new ReturnInfoResponse(1, sb.toString()));
		}
		else{
			if(avatar.getUuId() == playerList.get(pickAvatarIndex).getUuId()){
				//该自己出牌
				//system.out.println("自己出牌");
				json.put("currentCardPoint", currentCardPoint);//当前摸的牌点数，当currentCardPoint = -2时  表示是碰了之后出牌
				json.put("pickAvatarIndex", pickAvatarIndex);//当前摸牌人的索引
				json.put("curAvatarIndex", curAvatarIndex);//当前出牌人的索引
				json.put("putOffCardPoint", putOffCardPoint);//当前出的牌的点数
			}
			else{
				json.put("curAvatarIndex", curAvatarIndex);//当前出牌人的索引
				json.put("pickAvatarIndex", pickAvatarIndex);//当前摸牌人的索引
				json.put("putOffCardPoint", putOffCardPoint);//当前出的牌的点数
			}
			//游戏局数
			int roundNum = RoomManager.getInstance().getRoom(avatar.getRoomVO().getRoomId()).getCount();
			json.put("gameRound", roundNum);
			//桌面剩余牌数
			json.put("surplusCards", listCard.size() - nextCardindex);
			//System.out.println(json.toString());
			avatar.getSession().sendMsg(new ReturnOnLineResponse(1, json.toString()));
		}

	}
	/*
     * 清空所有数组
     */
	public void clearAvatar(){
		huAvatar.clear();
		penAvatar.clear();
		gangAvatar.clear();
		chiAvatar.clear();
		qishouHuAvatar.clear();
	}
	/**
	 * 清空除胡之外的数组
	 */
	public void clearAvatarExceptHu(){
		penAvatar.clear();
		gangAvatar.clear();
		chiAvatar.clear();
		qishouHuAvatar.clear();
	}
	/**
	 * 检测当，缓存数组里全部为空时，放弃操作，则不起作用
	 */
	public boolean validateStatus(){
		if(huAvatar.size() > 0 || penAvatar.size()>0 || gangAvatar.size()>0 || chiAvatar.size()>0 ||qishouHuAvatar.size()>0){
			return true;
		}
		else{
			return false;
		}
	}
	/**
	 * 第一局结束扣房卡
	 */
	public void deductRoomCard(){
		int roomcard = -roomVO.getRoomCardCount();
		Avatar zhuangAvatar = playerList.get(0);
		zhuangAvatar.updateRoomCard(roomcard);//开始游戏，减去房主的房卡,同时更新缓存里面对象的房卡(已经在此方法中修改)
		int roomCard = zhuangAvatar.avatarVO.getAccount().getRoomcard();
		zhuangAvatar.getSession().sendMsg(new RoomCardChangerResponse(1,roomCard));
	}

	public void socreChange(int avatarIndex, int score) {
	    for (int i = 0; i < playerList.size(); i++) {
			playerList.get(i).getSession().sendMsg(new ScoreResponse(1, avatarIndex, score));
		}
	}
}
