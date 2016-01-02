package com.yanyi.luckbag.util;

import com.yanyi.luckbag.bean.IncomeBean;

import java.util.ArrayList;

/**
 * Created by amayababy
 * 2015-12-19
 * 下午12:58
 */
public class AmayaEvent {
    public static class UserErrorEvent {
    }

    public static class IncomeBeansEvent {
        public ArrayList<IncomeBean> beans;

        public IncomeBeansEvent(ArrayList<IncomeBean> beans) {
            this.beans = beans;
        }
    }

    public static class AlreadyInsertEvent {
        public boolean alreadyInsert;

        public AlreadyInsertEvent(boolean alreadyInsert) {
            this.alreadyInsert = alreadyInsert;
        }
    }

    public static class LaunchCountUIEvent {
    }
}
