package com.yanyi.luckbag.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.yanyi.luckbag.activity.MatrixApplication;

/**
 * Created by yanyilove
 * 2015-12-20
 * 下午 6:03
 */
public class AnimUtil {
    public static final int START_LEFT_TOP = 1;
    public static final int START_LEFT_BOTTOM = 2;
    public static final int START_RIGHT_TOP = 3;
    public static final int START_RIGHT_BOTTOM = 4;
    public static final int START_CENTER = 5;


    /**
     * @param view            目标View
     * @param cornerType      起始点方位方位
     * @param goneOrInvisible true:GONE; false:INVISIBLE
     * @param listener        回调监听
     */
    public static void hideView(final View view, int cornerType, final boolean goneOrInvisible, final AnimatorListenerAdapter listener) {
        hideView(view, cornerType, 0, goneOrInvisible, listener);
    }

    /**
     * @param view
     * @param cornerType
     * @param goneOrInvisible
     * @param listener
     */
    public static void hideView(final View view, final int cornerType, final int animRes, final boolean goneOrInvisible, final AnimatorListenerAdapter listener) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            hideAnim(view, animRes, goneOrInvisible, listener);
        } else {
            try {
                hide(view, cornerType, animRes, goneOrInvisible, listener);
            } catch (Exception e) {
                view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        v.removeOnLayoutChangeListener(this);
                        hide(view, cornerType, animRes, goneOrInvisible, new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationCancel(Animator animation) {
                                super.onAnimationCancel(animation);
                                if (listener != null) listener.onAnimationCancel(animation);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if (listener != null) listener.onAnimationEnd(animation);
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                                super.onAnimationRepeat(animation);
                                if (listener != null) listener.onAnimationRepeat(animation);
                            }

                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                if (listener != null) listener.onAnimationStart(animation);
                            }

                            @Override
                            public void onAnimationPause(Animator animation) {
                                super.onAnimationPause(animation);
                                if (listener != null) listener.onAnimationPause(animation);
                            }

                            @Override
                            public void onAnimationResume(Animator animation) {
                                super.onAnimationResume(animation);
                                if (listener != null) listener.onAnimationResume(animation);
                            }
                        });
                    }
                });
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void hide(final View view, int cornerType, int animRes, final boolean goneOrInvisible, final AnimatorListenerAdapter listener) {
        int[] amaya = calulateCorner(view, cornerType);
//            if(view)
        Animator anim = ViewAnimationUtils.createCircularReveal(view, amaya[0], amaya[1], amaya[2], 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(goneOrInvisible ? View.GONE : View.INVISIBLE);
                if (listener != null) listener.onAnimationEnd(animation);
            }
        });
        anim.setDuration(300);
        anim.start();
    }

    /**
     * @param view       目标View
     * @param cornerType 起始点方位方位
     * @param listener
     */
    public static void showView(final View view, int cornerType, final AnimatorListenerAdapter listener) {
        showView(view, cornerType, 0, listener);
    }

    /**
     * @param view          目标View
     * @param normalAnimRes 低版本动画资源文件,0:不做任何处理
     * @param cornerType    起始点方位方位
     * @param listener
     */
    public static void showView(final View view, final int cornerType, final int normalAnimRes, final AnimatorListenerAdapter listener) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            showAnim(view, normalAnimRes);
        } else if (cornerType < START_LEFT_TOP && cornerType > START_RIGHT_BOTTOM) {
            showAnim(view, normalAnimRes);
        } else {
            try {
                show(view, cornerType, normalAnimRes, listener);
            } catch (Exception e) {
                view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        v.removeOnLayoutChangeListener(this);
                        show(view, cornerType, normalAnimRes, listener);
                    }
                });
            }


        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void show(final View view, final int cornerType, int normalAnimRes, final AnimatorListenerAdapter listener) {
        try {
            int[] amaya = calulateCorner(view, cornerType);
            Animator anim = ViewAnimationUtils.createCircularReveal(view, amaya[0], amaya[1], 0, amaya[2]);
            anim.setInterpolator(new LinearInterpolator());
            anim.setDuration(300);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    view.setVisibility(View.VISIBLE);
                    if (listener != null) {
                        listener.onAnimationStart(animation);
                    }
                }


            });
            anim.start();
        } catch (Exception e) {
            e.printStackTrace();
            view.setVisibility(View.VISIBLE);
        }
    }

    private static void hideAnim(View view, int normalAnimRes, boolean goneOrInvisible, final AnimatorListenerAdapter listener) {
        if (normalAnimRes != 0) {
            Animation animation = AnimationUtils.loadAnimation(MatrixApplication.getContext(), normalAnimRes);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (listener != null) {
                        listener.onAnimationStart(null);
                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (listener != null) {
                        listener.onAnimationEnd(null);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    if (listener != null) {
                        listener.onAnimationRepeat(null);
                    }
                }
            });
            view.startAnimation(animation);
        }
        view.setVisibility(goneOrInvisible ? View.GONE : View.INVISIBLE);
    }

    private static void showAnim(View view, int normalAnimRes) {
        view.setVisibility(View.VISIBLE);
        if (normalAnimRes != 0) {
            view.startAnimation(AnimationUtils.loadAnimation(MatrixApplication.getContext(), normalAnimRes));
        }
    }


    /**
     * 计算起始点的坐标值
     *
     * @param cornerType
     * @return
     */
    private static int[] calulateCorner(View view, int cornerType) {
        int x = (int) view.getX();
        int y = (int) view.getY();
        int w = view.getWidth();
        int h = view.getHeight();
        int[] amaya = new int[3];
        switch (cornerType) {
            default:
            case START_LEFT_TOP:
                amaya[0] = x;
                amaya[1] = y;
                break;
            case START_LEFT_BOTTOM:
                amaya[0] = x;
                amaya[1] = y + h;
                break;
            case START_RIGHT_TOP:
                amaya[0] = x + w;
                amaya[1] = y;
                break;
            case START_RIGHT_BOTTOM:
                amaya[0] = x + w;
                amaya[1] = y + h;
                break;
            case START_CENTER:
                amaya[0] = x + w / 2;
                amaya[1] = y + h / 2;
                break;
        }
        amaya[2] = (int) Math.sqrt(w * w + h * h);
        return amaya;
    }
}
