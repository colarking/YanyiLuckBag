package com.yanyi.luckbag.util;

import android.util.Log;

import com.yanyi.luckbag.BuildConfig;


/**
 * Created by amayababy on 15-1-25.
 */
public class AmayaLog {
    public static void e(String clazz, String msg) {
        if (BuildConfig.DEBUG)
            Log.e("amaya", clazz + "--->" + msg);
    }

    public static void i(String clazz, String msg) {
        if (BuildConfig.DEBUG)
            Log.i("amaya", clazz + "--->" + msg);
    }
}
