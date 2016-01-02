package com.yanyi.luckbag.util;

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

}
