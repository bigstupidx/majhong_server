package com.dyz.myBatis.model;

import java.util.Date;

public class RoomInfo {
    private Integer id;

    private String gametype;

    private String ishong;

    private Integer roomid;

    private String sevendouble;

    private Integer ma;

    private String zimo;

    private Integer xiayu;

    private String addwordcard;

    private String name;

    private Date createTime;

    private int cardNumb;//创建房间 消耗房卡数量

    private String chengbei;
    private String aa;
    private Integer zushu;
    private Integer paofen;
    private Integer roundtype;
    private Integer yuanzishu;
    private Integer yuanzijiesu;
    private String zhanzhuangbi;
    private String guozhuangbi;
    private String fengfa;

    public int getCardNumb() {
        return cardNumb;
    }

    public void setCardNumb(int cardNumb) {
        this.cardNumb = cardNumb;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGametype() {
        return gametype;
    }

    public void setGametype(String gametype) {
        this.gametype = gametype == null ? null : gametype.trim();
    }

    public String getIshong() {
        return ishong;
    }

    public void setIshong(String ishong) {
        this.ishong = ishong == null ? null : ishong.trim();
    }

    public Integer getRoomid() {
        return roomid;
    }

    public void setRoomid(Integer roomid) {
        this.roomid = roomid;
    }

    public String getSevendouble() {
        return sevendouble;
    }

    public void setSevendouble(String sevendouble) {
        this.sevendouble = sevendouble == null ? null : sevendouble.trim();
    }

    public Integer getMa() {
        return ma;
    }

    public void setMa(Integer ma) {
        this.ma = ma;
    }

    public String getZimo() {
        return zimo;
    }

    public void setZimo(String zimo) {
        this.zimo = zimo == null ? null : zimo.trim();
    }

    public Integer getXiayu() {
        return xiayu;
    }

    public void setXiayu(Integer xiayu) {
        this.xiayu = xiayu;
    }

    public String getAddwordcard() {
        return addwordcard;
    }

    public void setAddwordcard(String addwordcard) {
        this.addwordcard = addwordcard == null ? null : addwordcard.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getChengbei() {
        return chengbei;
    }

    public void setChengbei(String chengbei) {
        this.chengbei = chengbei == null ? null : chengbei.trim();
    }

    public String getAa() {
        return aa;
    }

    public void setAa(String aa) {
        this.aa = aa == null ? null : aa.trim();
    }

    public Integer getZushu() {
        return zushu;
    }

    public void setZushu(Integer zushu) {
        this.zushu = zushu;
    }

    public Integer getPaofen() {
        return paofen;
    }

    public void setPaofen(Integer paofen) {
        this.paofen = paofen;
    }

    public Integer getRoundtype() {
        return roundtype;
    }

    public void setRoundtype(Integer roundtype) {
        this.roundtype = roundtype;
    }

    public Integer getYuanzishu() {
        return yuanzishu;
    }

    public void setYuanzishu(Integer yuanzishu) {
        this.yuanzishu = yuanzishu;
    }

    public Integer getYuanzijiesu() {
        return yuanzijiesu;
    }

    public void setYuanzijiesu(Integer yuanzijiesu) {
        this.yuanzijiesu = yuanzijiesu;
    }

    public String getZhanzhuangbi() {
        return zhanzhuangbi;
    }

    public void setZhanzhuangbi(String zhanzhuangbi) {
        this.zhanzhuangbi = zhanzhuangbi == null ? null : zhanzhuangbi.trim();
    }

    public String getGuozhuangbi() {
        return guozhuangbi;
    }

    public void setGuozhuangbi(String guozhuangbi) {
        this.guozhuangbi = guozhuangbi == null ? null : guozhuangbi.trim();
    }

    public String getFengfa() {
        return fengfa;
    }

    public void setFengfa(String fengfa) {
        this.fengfa = fengfa == null ? null : fengfa.trim();
    }
}