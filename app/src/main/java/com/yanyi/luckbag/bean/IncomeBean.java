package com.yanyi.luckbag.bean;

import com.yanyi.luckbag.util.CommonUtil;

/**
 * Created by amayababy
 * 2015-12-19
 * 下午1:24
 */
public class IncomeBean {
    private String name;
    private float inMoney, outMoney, lastInMoney, inOutMoney;
    private long insertTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getInMoney() {
        return inMoney;
    }

    public void setInMoney(float inMoney) {
        this.inMoney = inMoney;
    }

    public float getOutMoney() {
        return outMoney;
    }

    public void setOutMoney(float outMoney) {
        this.outMoney = outMoney;
    }

    public float getLastInMoney() {
        return lastInMoney;
    }

    public void setLastInMoney(float lastInMoney) {
        this.lastInMoney = lastInMoney;
    }

    public float getInOutMoney() {
        return inOutMoney;
    }

    public void setInOutMoney(float inOutMoney) {
        this.inOutMoney = inOutMoney;
    }

    public long getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(long insertTime) {
        this.insertTime = insertTime;
    }

    @Override
    public String toString() {
        return "IncomeBean{" +
                "name='" + name + '\'' +
                ", inMoney=" + inMoney +
                ", outMoney=" + outMoney +
                ", lastInMoney=" + lastInMoney +
                ", inOutMoney=" + inOutMoney +
                '}';
    }

    public String toToast() {
        return name + "\n净收：" + CommonUtil.formatMoney(inOutMoney) + "元\n抢到：" + CommonUtil.formatMoney(inMoney) + "元\n发出：" + CommonUtil.formatMoney(outMoney);
    }
}
