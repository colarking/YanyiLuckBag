package com.yanyi.luckbag.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Created by amayababy
 * 2015-12-19
 * 下午3:36
 */
public class CommonUtil {
    public static DecimalFormat df = new DecimalFormat("#########.##");

    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    public static String formatMoney(float money) {
        if (money == 0) return "0.00";
        return df.format(money);
    }

    public static String formatMMDDTime(long millis) {
        sdf.applyPattern("MM月dd日  ");
        return sdf.format(millis);
    }

    public static String formatTime(long millis) {
        return sdf.format(millis);
    }
}
