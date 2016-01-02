package com.yanyi.luckbag.activity;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.yanyi.luckbag.bean.YanyiUser;
import com.yanyi.luckbag.util.AmayaImageLoader;
import com.yanyi.luckbag.util.UIUtil;

import java.util.Calendar;

/**
 * Created by amayababy
 * 2015-12-18
 * 下午2:57
 */
public class MatrixApplication extends Application {
    public static Context mContext;
    public static YanyiUser user;
    public static long TODAY_MILLS;
    private static AmayaImageLoader imageLoader;

    public static Context getContext() {
        return mContext;
    }

    public static AmayaImageLoader getImageLoader() {
        if (imageLoader == null) {
            imageLoader = AmayaImageLoader.getInstance();
        }
        return imageLoader;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        initImageLoader();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.HOUR, 0);
        TODAY_MILLS = calendar.getTimeInMillis();

        DisplayMetrics dp = getResources().getDisplayMetrics();
        UIUtil.initAmayaParams(dp.widthPixels, dp.heightPixels);
        UIUtil.initSystemParam(dp.density, dp.scaledDensity);
    }

    public void initImageLoader() {
        DisplayImageOptions dio = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app
        config.defaultDisplayImageOptions(dio);
        // Initialize ImageLoader with configuration.
        getImageLoader().init(config.build());
    }
}
