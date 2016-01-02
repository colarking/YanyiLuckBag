package com.yanyi.luckbag.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.yanyi.luckbag.R;
import com.yanyi.luckbag.activity.CountActivity;
import com.yanyi.luckbag.bean.YanyiBean;
import com.yanyi.luckbag.model.LuckModel;
import com.yanyi.luckbag.util.AmayaConstants;
import com.yanyi.luckbag.util.AmayaEvent;
import com.yanyi.luckbag.util.AmayaLog;
import com.yanyi.luckbag.util.AmayaSPUtil;
import com.yanyi.luckbag.util.CommonUtil;

import java.util.ArrayList;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 * Created by amayababy
 * 2015-12-18
 * 下午2:10
 */
public class YanYiLuckService extends AccessibilityService {

    static final String TAG = YanYiLuckService.class.getSimpleName();
    /**
     * 红包个数最小数
     */
    private static final int LUCK_BAG_MIN = 4;
    /**
     * 红包个数最大数
     */
    private static final int LUCK_BAG_MAX = 6;
    private int totalCount;
    private boolean valid;
    private boolean isBestThisTime;
    private float totalMoney;
    private YanyiBean bean;
    private ArrayList<YanyiBean> beans = new ArrayList<>();
    ;
    private YanyiBean sendUser = new YanyiBean();
    private boolean isEnglish;
    private Pattern timePatternEN, timePatternCN;
    private boolean canJump;
    private boolean mineGetMoney; //是否自己抢到了钱包 抢到后判断红包个数的totalCount=6，默认为3
    private boolean quitCount;
    private String lastTime;
    private String selfName;
    private CharSequence todayMMDD;//格式化今天日期

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();
        AmayaLog.e(TAG, "onAccessibilityEvent()..." + event.getClassName());
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(event.getClassName())) {
                //拆完红包后看详细的纪录界面
                //nonething
                if (canJump) findUsers();
            } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(event.getClassName())) {
                AmayaConstants.COUNT_FINISH = false;
                canJump = true;
            } else if ("com.tencent.mm.ui.LauncherUI".equals(event.getClassName())) {
                //在聊天界面,去点中红包
                AmayaConstants.COUNT_FINISH = false;
                canJump = true;
//            checkKey2();
            }
        }
    }

    @Override
    public void onInterrupt() {
        Toast.makeText(this, R.string.service_bind_failed, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        Toast.makeText(this, R.string.service_bind_success, Toast.LENGTH_SHORT).show();

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void findUsers() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }
        int count = nodeInfo.getChildCount();

        clearAll();
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo n = nodeInfo.getChild(i);
            boolean end = listNode(0, n);
            if (end) {
                break;
            }
        }
        if (quitCount) {
            Toast.makeText(this, "不是今天红包,", Toast.LENGTH_SHORT).show();
            return;
        }

        long millis = System.currentTimeMillis();
        int firstIndex = beans.size() - 1;
        boolean equals = false;
        for (int i = firstIndex; i > -1; i--) {
            YanyiBean bean = beans.get(i);
            if (i == firstIndex) {
                sendUser.setTime(bean.getTime());
                sendUser.setTimeMD(bean.getTimeMD());
            }
            bean.setInsertTime(millis);
            if (i == 0 && TextUtils.isEmpty(bean.getTime())) {
                bean.setTime(lastTime);
            }
            bean.setBagIndex(i);
            if (!equals) equals = bean.getName().equals(sendUser.getName());
            bean.setSendBagName(sendUser.getName());
        }

        sendUser.setMoney(Math.round(totalMoney));
        sendUser.setInsertTime(millis);
        LuckModel.instance().insertBeans(beans, sendUser, equals);
//        Log.e(TAG,"listBeans()...sendUser="+sendUser.toString());
    }

    private void clearAll() {
        if (timePatternCN == null) {
            timePatternCN = Pattern.compile("^(([0-1]?[0-9])|([2][0-3])):([0-5]?[0-9])(:([0-5]?[0-9]))?$");
        }
        if (timePatternEN == null) {
            timePatternEN = Pattern.compile("^(([0-1]?[0-9])|([2][0-3])):([0-5]?[0-9])(:([0-5]?[0-9]))?$");
        }
        if (TextUtils.isEmpty(todayMMDD)) {
            todayMMDD = CommonUtil.formatMMDDTime(System.currentTimeMillis());
        }
        lastTime = null;
        sendUser.setName(null);
        sendUser.setInsertTime(0);
        sendUser.setMoney(0);
        sendUser.setBest(false);
        sendUser.setLuckId(0);
        sendUser.setSendBagName(null);
        sendUser.setTime(null);

        totalCount = 0;
        totalMoney = 0;
        if (beans != null) beans.clear();
        quitCount = mineGetMoney = isBestThisTime = false;
        if (TextUtils.isEmpty(selfName)) {
            selfName = AmayaSPUtil.getString("yanyi_name", null);
        }
    }

    public boolean listNode(int level, AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            totalCount++;
            CharSequence text2 = info.getText();
            if (text2 == null) return true;
//            AmayaLog.e(TAG,"totalCount="+totalCount+"--info="+text2);
            if (mineGetMoney) {
                if (totalCount < 6) {
                    return false;
                } else if (totalCount == 6) {
//                    AmayaLog.e(TAG,"mineGetMoney="+mineGetMoney+"totalCount="+totalCount+"--info"+info.getText());
                    try {
                        valid = checkBagCount(info);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            }
            switch (totalCount) {
                default:
                    //大于3以后的都走这里
                    if (valid) {
                        int index = (totalCount + (mineGetMoney ? 3 : 0) + (isBestThisTime ? -1 : 0)) % 3;
                        if (text2 == null) {
                            return true;
                        }
                        String text = text2.toString();
//                        AmayaLog.e(TAG,"isBestThisTime="+isBestThisTime+"--index="+index+"--totalCount="+totalCount+"--info="+text2);
                        switch (index) {
                            case 0:
                                float money;
                                if (isEnglish) {
                                    money = Float.parseFloat(text.substring(1, text.length()));
                                } else {
                                    money = Float.parseFloat(text.substring(0, text.length() - 1));
                                }
                                totalMoney += money;
                                bean.setMoney(money);
                                beans.add(bean);
                                break;
                            case 1:
                                if (getString(R.string.yanyi_best).equals(text)) {
                                    isBestThisTime = true;
                                    bean.setBest(true);
                                } else {
                                    bean = new YanyiBean();
                                    bean.setName(text.trim());
                                }
                                break;
                            case 2:
                                boolean find = timePatternEN.matcher(text).find();
                                if (!TextUtils.isEmpty(text) && text.length() == 8 && find) {
                                    bean.setTime(text);
                                    bean.setTimeMD(todayMMDD + text.substring(0, 5));
                                    lastTime = text;
                                } else if (text.length() == 13 && text.contains("月")) {
                                    bean.setTimeMD(text);
                                    lastTime = text;
                                } else if (mineGetMoney && (isEnglish ? "Message" : "留言").equals(text)) {
                                    if (TextUtils.isEmpty(selfName)) {
                                        selfName = bean.getName();
                                        AmayaSPUtil.save("yanyi_name", selfName);
                                    }
                                    bean.setTime(lastTime);
                                } else {
                                    quitCount = true;
                                }
                                break;
                        }
                    } else {
//                        Log.e(TAG,"listNode()...totalCount="+ totalCount+"--text="+ text2);
                    }
                    break;
                case 1:
                    //识别语言
                    String name = text2.toString();
                    isEnglish = name.startsWith("Lucky Money from");
                    String name2 = isEnglish ? name.substring(name.lastIndexOf(" ") + 1) : name.substring(0, name.length() - 3);
                    sendUser.setName(name2.trim());
                    break;
                case 3:
                    //识别红包个数
                    try {
                        if (text2.length() < 6) {
                            mineGetMoney = true;
                        } else {
                            valid = checkBagCount(info);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        valid = true;
                    }
                    break;
                case 2:
                    break;
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                AccessibilityNodeInfo child = info.getChild(i);
                if (child != null) {
                    boolean end = listNode(level + 1, child);
                    if (end) {
                        return end;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 检查红包个数以及抢完耗时时间
     *
     * @param info
     */
    private boolean checkBagCount(AccessibilityNodeInfo info) {
        String text = info.getText().toString();
        if (text.contains("已领取")) {
            return true;
        }
        int end = text.indexOf(isEnglish ? " " : "个", isEnglish ? 7 : 0);
        int start = isEnglish ? 7 : 0;

        String s = text.substring(start, end);
        int anInt = Integer.parseInt(s);
        return anInt >= LUCK_BAG_MIN && anInt <= LUCK_BAG_MAX;
    }

    public void onEventMainThread(AmayaEvent.LaunchCountUIEvent event) {
        AmayaLog.e(TAG, "LaunchCountUIEvent ...canJump=" + canJump);
        Intent intent = new Intent(this, CountActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        canJump = false;

    }

}
