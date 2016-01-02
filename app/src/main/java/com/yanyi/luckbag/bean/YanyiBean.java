package com.yanyi.luckbag.bean;

import com.yanyi.luckbag.util.CommonUtil;

/**
 * Created by amayababy
 * 2015-12-18
 * 下午2:47
 */
public class YanyiBean {
    private String name, time, timeMD, sendBagName;
    private float money;
    private boolean isBest;
    private int luckId;
    private long insertTime;
    private int bagIndex;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSendBagName() {
        return sendBagName;
    }

    public void setSendBagName(String sendBagName) {
        this.sendBagName = sendBagName;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public boolean isBest() {
        return isBest;
    }

    public void setBest(boolean best) {
        isBest = best;
    }

    public int getLuckId() {
        return luckId;
    }

    public void setLuckId(int luckId) {
        this.luckId = luckId;
    }

    public long getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(long insertTime) {
        this.insertTime = insertTime;
    }

    public int getBagIndex() {
        return bagIndex;
    }

    public void setBagIndex(int bagIndex) {
        this.bagIndex = bagIndex;
    }

    public String getTimeMD() {
        return timeMD;
    }

    public void setTimeMD(String timeMD) {
        this.timeMD = timeMD;
    }

    @Override
    public String toString() {
        return "YanyiBean{" +
                "name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", timeMD='" + timeMD + '\'' +
                ", sendBagName='" + sendBagName + '\'' +
                ", money=" + CommonUtil.formatMoney(money) +
                ", isBest=" + isBest +
                ", luckId=" + luckId +
                ", insertTime=" + insertTime +
                '}';
    }
}
