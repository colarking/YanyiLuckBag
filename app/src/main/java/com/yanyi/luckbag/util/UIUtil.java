package com.yanyi.luckbag.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import java.lang.reflect.Field;

/**
 * Created by amayababy
 * 2015-07-08
 * 上午11:45
 */
public class UIUtil {
    private static final float AMAYA_WIDTH = 720;
    private static final float AMAYA_HEIGHT = 1280;
    public static int amayaWidth = (int) AMAYA_WIDTH, amayaHeight = (int) AMAYA_HEIGHT;


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    static float scale;
    static float fontScale;

    public static int dip2px(float dpValue) {
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(float pxValue) {
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int dip2sp(float dipValue) {
        return (int) (dip2px(dipValue) / fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        return (int) (pxValue / scale + 0.5f);
    }

    public static void initSystemParam(float density, float scaledDensity) {
        scale = density;
        fontScale = scaledDensity;

    }

    public static void initAmayaParams(int width, int height) {
        if (width > height) {
            amayaWidth = height;
            amayaHeight = width;
        } else {
            amayaWidth = width;
            amayaHeight = height;
        }
    }

    public static int getHeight(int height) {
        return (int) (AMAYA_HEIGHT / amayaHeight * height);
    }

    public static int getCommonWidth(int width) {
        return dip2px(AMAYA_WIDTH / amayaWidth * width);
    }

    public static int getCommonHeight(int height) {
        if (AMAYA_HEIGHT > amayaHeight) {

            return dip2px(amayaHeight / AMAYA_HEIGHT * height);
        } else {

            return dip2px(AMAYA_HEIGHT / amayaHeight * height);
        }
    }


    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 设置Selector。
     */
    public static StateListDrawable generateSelector(Context context, int idNormal, int idPressed, int idFocused,
                                                     int idUnable) {

        StateListDrawable bg = new StateListDrawable();
        Drawable normal = idNormal == -1 ? null : context.getResources().getDrawable(idNormal);
        Drawable pressed = idPressed == -1 ? null : context.getResources().getDrawable(idPressed);
        Drawable focused = idFocused == -1 ? null : context.getResources().getDrawable(idFocused);
        Drawable unable = idUnable == -1 ? null : context.getResources().getDrawable(idUnable);
        // View.PRESSED_ENABLED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
        // View.ENABLED_FOCUSED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, focused);
        // View.ENABLED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_enabled}, normal);
        // View.FOCUSED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_focused}, focused);
        // View.WINDOW_FOCUSED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_window_focused}, unable);
        // View.EMPTY_STATE_SET
        bg.addState(new int[]{}, normal);
        return bg;
    }


    public static ColorStateList createColorStateList(int normal, int pressed, int focused, int unable) {
        int[] colors = new int[]{pressed, focused, normal, focused, unable, normal};
        int[][] states = new int[6][];
        states[0] = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_enabled, android.R.attr.state_focused};
        states[2] = new int[]{android.R.attr.state_enabled};
        states[3] = new int[]{android.R.attr.state_focused};
        states[4] = new int[]{android.R.attr.state_window_focused};
        states[5] = new int[]{};
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }

}
