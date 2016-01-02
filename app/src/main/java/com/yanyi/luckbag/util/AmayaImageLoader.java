package com.yanyi.luckbag.util;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.HashMap;

/**
 * Created by amayababy
 * 2015-12-20
 * 下午5:54
 */
public class AmayaImageLoader extends ImageLoader {

    private static AmayaImageLoader amayaImageLoader = new AmayaImageLoader();
    private HashMap<Float, DisplayImageOptions> cacheDios = new HashMap<>();

    private AmayaImageLoader() {
    }

    public static AmayaImageLoader getInstance() {
        return amayaImageLoader;
    }

    public void displayImageWithCircle(String uri, ImageView imageView, float radios, int loadResource) {
        if (cacheDios == null) cacheDios = new HashMap<>();
        DisplayImageOptions dio = cacheDios.get(radios + loadResource);

        if (dio == null) {
            dio = getCicleDIO(radios, loadResource);
            cacheDios.put(radios + loadResource, dio);
        }
        displayImage(uri, imageView, dio);
    }


    public DisplayImageOptions getCicleDIO(float radius, int res) {
        return new DisplayImageOptions.Builder().displayer(new RoundedBitmapDisplayer(UIUtil.dip2px(radius)))
                .showImageOnFail(res)
                .considerExifParams(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageForEmptyUri(res)
                .showImageOnLoading(res)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

    }
}
