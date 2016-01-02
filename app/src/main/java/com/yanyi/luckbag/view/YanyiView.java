package com.yanyi.luckbag.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by amayababy
 * 2015-12-20
 * 下午6:42
 */
public class YanyiView extends LinearLayout {

    public YanyiView(Context context) {
        super(context);
        init(context);
    }

    public YanyiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public YanyiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public YanyiView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {

    }
}
