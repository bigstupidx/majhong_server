package com.dyz.gameserver.pojo;

import java.util.ArrayList;
import java.util.List;

import com.dyz.myBatis.model.Account;
import com.dyz.persist.util.StringUtil;

/**
 * Created by kevin on 2016/6/23.
 */
public class AvatarVO {
    /**
     * 用户基本信息
     */
    private Account account;
    /**
     * 房间号
     */
    private int roomId;
    /**
     * 是否准备
     */
    private boolean isReady = false;
    /**
     * 是否是庄家
     */
    private boolean isMain = false;

    private int mainCount = 0;
    /**
     * 是否在线
     */
    private boolean isOnLine = false;
    private int scores = 1000;
    private List<Integer>  chupais = new ArrayList<Integer>();
    private int commonCards;
    private int huType = 0;
    private int[][] paiArray;
    private HuReturnObjectVO  huReturnObjectVO;
    
    private String IP;

    private double longitude = 0;
    private double latitude = 0;
    private String address;


    public void setLatitude(double count) {
        latitude = count;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double count) {
        longitude = count;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setAddress(String count) {
        address = count;
    }

    public String getAddress() {
        return address;
    }
   public HuReturnObjectVO getHuReturnObjectVO() {
		return huReturnObjectVO;
	}

	public void setHuReturnObjectVO(HuReturnObjectVO huReturnObjectVO) {
		this.huReturnObjectVO = huReturnObjectVO;
	}
	public Account getAccount() {
        return account;
    }

	public void setAccount(Account account) {
        this.account = account;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public boolean getIsReady() {
        return isReady;
    }

    public void setIsReady(boolean ready) {
        isReady = ready;
    }

    public boolean getMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    public void setMainCount(int count) {
        mainCount = count;
    }

    public int getMainCount() {
       return mainCount;
    }

    public boolean getIsOnLine() {
        return isOnLine;
    }

    public void setIsOnLine(boolean onLine) {
        isOnLine = onLine;
    }

    public int[][] getPaiArray() {
        return paiArray;
    }

    public void setPaiArray(int[][] paiArray) {
        this.paiArray = paiArray;
    }

    public void clearPaiArray() {
       for (int i = 0; i < paiArray.length; i++) {
           for (int j = 0; j < paiArray[i].length; j++) {
               if (i < 2) paiArray[i][j] = 0;
               else paiArray[i][j] = -1;
           }
       }
    }

	public List<Integer> getChupais() {
		return chupais;
	}
	/**
	 * 出了的牌添加到数组中
	 * @param chupai
	 */
	public void updateChupais(Integer chupai) {
		chupais.add(chupai);
 	}
	public void removeLastChupais() {
		int inde = chupais.size();
		chupais.remove(inde-1);
 	}
	
	public int getCommonCards() {
		return commonCards;
	}

	public void setCommonCards(int commonCards) {
		this.commonCards = commonCards;
	}

	public int getScores() {
		return scores;
	}
	public int supdateScores(int score) {
		this.scores = this.scores + score;
		if (this.scores < 0) {
		    int temp = this.scores;
		    this.scores = 0;
		    return temp;
        }
        return 0;
	}

	public void setScores(int socre) {
	    this.scores = socre;
    }

	public int getHuType() {
		return huType;
	}

	public void setHuType(int huType) {
		this.huType = huType;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}
	
	
}
