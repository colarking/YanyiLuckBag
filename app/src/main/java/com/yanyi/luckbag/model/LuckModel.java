package com.yanyi.luckbag.model;

import com.yanyi.luckbag.bean.YanyiBean;
import com.yanyi.luckbag.db.YanyiDB;
import com.yanyi.luckbag.util.AmayaEvent;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by amayababy
 * 2015-12-19
 * 下午1:02
 */
public class LuckModel extends YanyiModel {


    private static LuckModel luckModel = new LuckModel();

    private LuckModel() {
    }

    public static LuckModel instance() {
        return luckModel;
    }

    public void insertBeans(final ArrayList<YanyiBean> beans, final YanyiBean sendUser, final boolean equals) {
        if (beans != null && beans.size() > 0) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    YanyiDB.getInstance().insert(beans, sendUser, true, equals);
                    EventBus.getDefault().post(new AmayaEvent.LaunchCountUIEvent());
                }
            };
            submit(runnable);
        }
    }

}
