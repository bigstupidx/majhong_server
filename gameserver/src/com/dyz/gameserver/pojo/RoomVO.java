package com.dyz.gameserver.pojo;

import com.context.NanjingConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kevin on 2016/6/22.
 */
public class RoomVO {
    /**
     * 房间ID
     */
    private int roomId;
    /**
     * 数据库表ID
     */
    private int id;
    /**
     * 房间的使用总次数
     */
    private int roundNumber;
    /**
     * 房间当前轮数
     */
    private int currentRound = 0;
    private boolean hong;
    private int roomType;
    private boolean sevenDouble;
    private int ma;
    private int ziMo;
    private int xiaYu;
    private boolean addWordCard;

    // nanjing
    private boolean chengbei;
    private boolean aa;
    private int zashu;
    private int paofen;
    private int roundtype;
    private int yuanzishu;
    private int yuanzijiesu;
    private boolean zhanzhuangbi;
    private boolean guozhuangbi;
    private boolean fengfa;

    /**
     * 房间名
     */
    public String name;
    /**
     * 整个房间对应的所有人的牌组
     */
    private List<AvatarVO> playerList;
    /**
     * 开一个房间几局游戏完后，统计所有玩家的杠，胡次数
     * 第一个key：用户uuid
     *
     *  南京麻将
     */
    private Map<String , Map<String,Integer>> endStatistics = new HashMap<String, Map<String,Integer>>();

	public Map<String, Map<String, Integer>> updateEndStatistics(String uuid , String type ,int roundScore) {
    		if(endStatistics.get(uuid) == null){
    			Map<String,Integer > map = new HashMap<String , Integer>();
        		map.put(type,roundScore);
        		endStatistics.put(uuid, map);
    		}
    		else{
    			if(endStatistics.get(uuid).get(type) != null){
    				endStatistics.get(uuid).put(type, endStatistics.get(uuid).get(type)+roundScore);
    			}
    			else{
    				endStatistics.get(uuid).put(type, roundScore);
    			}
    		}

		return endStatistics;
	}


    public Map<String, Map<String, Integer>> getEndStatistics() {
		return endStatistics;
	}




	public int getRoomId() {
        return roomId;
    }

	public void setEndStatistics(Map<String, Map<String, Integer>> endStatistics) {
		this.endStatistics = endStatistics;
	}

	public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public int getCurrentRound() {
		return currentRound;
	}

	public void setCurrentRound(int currentRound) {
		this.currentRound = currentRound;
	}


	public int getRoomType() {
        return roomType;
    }

    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public int getMa() {
        return ma;
    }

    public void setMa(int ma) {
        this.ma = ma;
    }

    public int getZiMo() {
        return ziMo;
    }

    public void setZiMo(int ziMo) {
        this.ziMo = ziMo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getHong() {
        return hong;
    }

    public void setHong(boolean hong) {
        this.hong = hong;
    }

    public boolean getSevenDouble() {
        return sevenDouble;
    }

    public void setSevenDouble(boolean sevenDouble) {
        this.sevenDouble = sevenDouble;
    }

    public int getXiaYu() {
        return xiaYu;
    }

    public void setXiaYu(int xiaYu) {
        this.xiaYu = xiaYu;
    }
    public boolean getAa() {
        return aa;
    }

    public void setAa(boolean value) {
        this.aa = value;
    }

    public boolean getChengbei() {
        return chengbei;
    }

    public void setChengbei(boolean value) {
        this.chengbei = value;
    }

    public int getZashu() {
        return zashu;
    }
    public int getRealZashu() {
        return NanjingConfig.ZASHU_SET[zashu];
    }
    public void setZashu(int value) {
        this.zashu = value;
    }

    public int getPaofen() {
        return paofen;
    }
    public int getRealPaofen() {
	    return NanjingConfig.PAOFEN_SET[paofen];
    }
    public void setPaofen(int value) {
        this.paofen = value;
    }

    public int getRoundtype() {
        return roundtype;
    }
    public void setRoundtype(int value) {
        this.roundtype = value;
    }
    public int getYuanzishu() {
        return yuanzishu;
    }

    public void setYuanzishu(int value) {
        this.yuanzishu = value;
    }

    public int getYuanzijiesu() {
        return yuanzijiesu;
    }
    public void setYuanzijiesu(int value) {
        this.yuanzijiesu = value;
    }

    public boolean getZhanzhuangbi() {
        return zhanzhuangbi;
    }
    public void setZhanzhuangbi(boolean value) {
        this.zhanzhuangbi = value;
    }

    public boolean getGuozhuangbi() {
        return guozhuangbi;
    }
    public void setGuozhuangbi(boolean value) {
        this.guozhuangbi = value;
    }

    public boolean getFengfa() {
        return fengfa;
    }
    public void setFengfa(boolean value) {
        this.fengfa = value;
    }

    public List<AvatarVO> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<AvatarVO> playerList) {
        this.playerList = playerList;
    }

    public boolean isAddWordCard() {
        return addWordCard;
    }

    public void setAddWordCard(boolean addWordCard) {
        this.addWordCard = addWordCard;
    }

    public int getRealRoundNumber() {
        if (roundtype == 0) {
            return NanjingConfig.ROUND_DEFAULT;
        } else {
            return NanjingConfig.ROUND_SET[roundNumber];
        }
    }

    public int getRoomCardCount() {
        if (roundtype == 0) {
            return NanjingConfig.YUANZI_ROOMCARD[yuanzishu];
        } else {
            return NanjingConfig.ROOMCARD[roundNumber];
        }
    }

    public int getScore() {
        if (roundtype == 0) {
            return NanjingConfig.YUANZI_COUNT_SET[yuanzishu];
        } else {
            return NanjingConfig.SCORE_DEFAULT;
        }
    }

    public int getYuanziRule() {
        if (roundtype == 0) {
            return NanjingConfig.YUANZI_RULE_SET[yuanzijiesu];
        }
        return 1;
    }

    public RoomVO clone(){
    	RoomVO result = new RoomVO();
    	result.roomId = roomId;
        result.roundNumber = roundNumber;
        result.currentRound = currentRound;
        result.hong = hong;
        result.roomType = roomType;
        result.sevenDouble = sevenDouble;
        result.ma = ma;
        result.ziMo = ziMo;
        result.xiaYu = xiaYu;
        result.addWordCard = addWordCard;
        result.name = name;
        result.playerList = playerList;
        result.endStatistics = endStatistics;
        result.id = id;

        result.chengbei = chengbei;
        result.aa = aa;
        result.zashu = zashu;
        result.paofen = paofen;
        result.roundtype = roundtype;
        result.yuanzishu = yuanzishu;
        result.yuanzijiesu = yuanzijiesu;
        result.zhanzhuangbi = zhanzhuangbi;
        result.guozhuangbi = guozhuangbi;
        result.fengfa = fengfa;
        return result;
    }


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}

    
}
