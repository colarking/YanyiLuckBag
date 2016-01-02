package com.yanyi.luckbag.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yanyi.luckbag.R;
import com.yanyi.luckbag.util.UIUtil;


/**
 * Created by xuewen on 15/10/29.
 */
public class AmayaEmptyView extends LinearLayout {

    private TextView showText;
    private boolean isTextTop;
    private int resourceId;
    private String textRes;
    private int textColor;
    private ImageView amayaImg;
    private ProgressBar amayaBar;

    public AmayaEmptyView(Context context) {
        super(context);
        init(context);
    }

    public AmayaEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        init(context);
    }

    public AmayaEmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AmayaEmptyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(context, attrs);
        init(context);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AmayaEmptyView);
        textRes = a.getString(R.styleable.AmayaEmptyView_emptyText);
        resourceId = a.getResourceId(R.styleable.AmayaEmptyView_emptyImage, R.drawable.defaule_yanyi);
        isTextTop = a.getBoolean(R.styleable.AmayaEmptyView_isTextTop, false);
        textColor = a.getColor(R.styleable.AmayaEmptyView_emptyTextColor, getResources().getColor(R.color.black));

        a.recycle();
    }

    public void showResultText(String text) {
        hideLoading();
//        showText.setText(text);
        showText.setVisibility(GONE);
    }

    public void showResultText(int textRes) {
        hideLoading();
        showText.setVisibility(GONE);
//        showText.setText(textRes);
    }

    private void init(Context context) {

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        amayaImg = new ImageView(context);
        showText = new TextView(context);


        amayaBar = new ProgressBar(context);
        amayaBar.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.abs__progress_medium_holo));
        int dp20 = UIUtil.dip2px(15);
        LayoutParams lp = new LayoutParams(dp20, dp20);
        FrameLayout frame = new FrameLayout(context);
        frame.addView(amayaImg);
        frame.addView(amayaBar);
        if (isTextTop) {
            addView(showText);
            addView(frame);
        } else {
            addView(frame);
            addView(showText);
        }

        showText.setPadding(0, 20, 0, 0);
        showText.setTextColor(textColor);
        showText.setGravity(Gravity.CENTER);
        showText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        if (TextUtils.isEmpty(textRes)) {
            showText.setText(R.string.loading);
        } else {
            showText.setText(textRes);
        }
        setEmptyResource(resourceId);
        amayaImg.setVisibility(GONE);
    }

    public void setEmptyResource(int emptyResource) {
        amayaImg.setImageResource(emptyResource);
    }

    public void setEmptyText(String emptyText) {
        textRes = emptyText;
    }

    public void setEmptyText(int emptyText) {
        textRes = getResources().getString(emptyText);
    }

    public boolean isLoading() {
        return amayaBar.getVisibility() == View.VISIBLE;
    }

    public void startLoading() {
        amayaBar.setVisibility(View.VISIBLE);
        amayaImg.setVisibility(GONE);
        showText.setVisibility(VISIBLE);
        showText.setText(R.string.loading);
    }

    public void startLoading(boolean hideText) {
        amayaBar.setVisibility(View.VISIBLE);
        amayaImg.setVisibility(GONE);
        if (hideText) {
            showText.setVisibility(GONE);
        }
    }

    public void hideLoading(boolean show) {
        amayaBar.setVisibility(View.GONE);
        amayaImg.setVisibility(VISIBLE);
        if (show) {
            showText.setVisibility(VISIBLE);
        }
    }

    public void hideLoading() {
        amayaBar.setVisibility(View.GONE);
        amayaImg.setVisibility(VISIBLE);
        showText.setText(textRes);
    }
}
