package com.yanyi.luckbag.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yanyi.luckbag.R;
import com.yanyi.luckbag.util.AmayaAnimUtil;
import com.yanyi.luckbag.util.AmayaConstants;

/**
 * Created by amayababy
 * 2015-12-18
 * 下午2:09
 */
public class YanyiActivity extends AppCompatActivity implements View.OnClickListener, ImageLoadingListener {

    private static final String TAG = YanyiActivity.class.getSimpleName();
    private int[] yanyis;
    private int[] yanyiAnims = new int[]{AmayaAnimUtil.START_CENTER, AmayaAnimUtil.START_LEFT_BOTTOM, AmayaAnimUtil.START_LEFT_TOP, AmayaAnimUtil.START_RIGHT_BOTTOM, AmayaAnimUtil.START_RIGHT_TOP};
    private int yanyiIndex, animIndex;
    private ImageView img;
    private AnimatorListenerAdapter listenerAdapter;
    private boolean hideView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.default_yanyi_2);
        actionBar.setDisplayUseLogoEnabled(true);

        findViewById(R.id.yy_start).setOnClickListener(this);
        findViewById(R.id.yy_check).setOnClickListener(this);

        yanyis = new int[]{R.drawable.default_yanyi_10, R.drawable.default_yanyi_3, R.drawable.default_yanyi_4, R.drawable.default_yanyi_5,
                R.drawable.default_yanyi_6, R.drawable.default_yanyi_7, R.drawable.default_yanyi_8, R.drawable.default_yanyi_9, R.drawable.default_yanyi_1
        };
        img = (ImageView) findViewById(R.id.yy_image);
        img.setOnClickListener(this);
        MatrixApplication.getImageLoader().displayImage(AmayaConstants.PREFIX_DRAWABLE + yanyis[yanyiIndex++], img);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yy_image:
                if (img != null && yanyis != null && !hideView) {
                    hideView = true;

                    if (listenerAdapter == null) {
                        listenerAdapter = new AnimatorListenerAdapter() {

                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                super.onAnimationCancel(animation);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if (hideView) {
                                    MatrixApplication.getImageLoader().loadImage(AmayaConstants.PREFIX_DRAWABLE + yanyis[yanyiIndex++], YanyiActivity.this);
                                    hideView = false;
                                    AmayaAnimUtil.showView(img, yanyiAnims[animIndex++], null);
                                    if (animIndex == yanyiAnims.length) animIndex = 0;
                                }
                            }
                        };
                    }
                    AmayaAnimUtil.hideView(img, yanyiAnims[animIndex], android.R.anim.fade_in, false, listenerAdapter);
                    if (yanyiIndex == yanyis.length) {
                        yanyiIndex = 0;
                    }
                }
                break;
            case R.id.yy_start:
                enableService();
                break;
            case R.id.yy_check:
                Intent intent = new Intent(this, CountlDetailActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void enableService() {
        try {
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, "找到颜依的红包选项，然后开启服务即可", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {

    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        img.setImageBitmap(loadedImage);

    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {

    }
}
