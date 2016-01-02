package com.yanyi.luckbag.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.yanyi.luckbag.activity.MatrixApplication;
import com.yanyi.luckbag.bean.YanyiUser;


public class AmayaSPUtil {

    public static final String KEY_USER_TOKEN = "amaya_user_token";
    public static final String KEY_USER_ID = "amaya_user_id";
    public static final String KEY_USER_IMAGE = "amaya_user_image";
    public static final String KEY_USER_NAME = "amaya_user_name";
    public static SharedPreferences sp;

    public static void initSP(Context context) {
        if (sp == null) {
            sp = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    public static void checkSP() {
        if (sp == null) {
            initSP(MatrixApplication.getContext());
        }
    }

    public static boolean getBoolean(String key, boolean value) {
        checkSP();
        return sp.getBoolean(key, value);
    }

    public static int getInt(String key, int defValue) {
        checkSP();
        return sp.getInt(key, defValue);
    }

    public static String getString(String key, String defValue) {
        checkSP();
        return sp.getString(key, defValue);
    }

    public static long getLong(String key, long defValue) {
        checkSP();
        return sp.getLong(key, defValue);
    }

    public static boolean save(String key, boolean value) {
        checkSP();
        return sp.edit().putBoolean(key, value).commit();
    }

    public static boolean save(String key, int value) {
        checkSP();
        return sp.edit().putInt(key, value).commit();
    }

    public static boolean save(String key, long value) {
        checkSP();
        return sp.edit().putLong(key, value).commit();
    }

    public static boolean save(String key, float value) {
        checkSP();
        return sp.edit().putFloat(key, value).commit();
    }

    public static boolean save(String key, String value) {
        checkSP();
        return sp.edit().putString(key, value).commit();
    }

    public static boolean remove(String key) {
        checkSP();
        return sp.edit().remove(key).commit();
    }

    public static boolean checkUserId() {
        checkSP();
        String uid = sp.getString("matrix_user_id", "");
        if (TextUtils.isEmpty(uid)) {
            return false;
        } else {
            return checkUserToken();
        }

    }

    public static boolean checkUserToken() {
        checkSP();
        String token = sp.getString("matrix_user_token", "");
        if (TextUtils.isEmpty(token)) {
            return false;
        } else {
            return true;
        }
    }

    public static float getFloat(String key) {
        checkSP();
        return sp.getFloat(key, 0f);
    }


    public static void clearUser() {
        checkSP();
        sp.edit().remove(KEY_USER_ID)
                .remove(KEY_USER_IMAGE)
                .remove(KEY_USER_NAME)
                .remove(KEY_USER_TOKEN)
                .commit();
        MatrixApplication.user = new YanyiUser();
    }

}
