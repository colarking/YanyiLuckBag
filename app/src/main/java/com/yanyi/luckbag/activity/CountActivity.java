package com.yanyi.luckbag.activity;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.yanyi.luckbag.R;
import com.yanyi.luckbag.adapter.YanyiAdapter;
import com.yanyi.luckbag.bean.IncomeBean;
import com.yanyi.luckbag.db.YanyiDB;
import com.yanyi.luckbag.util.AmayaConstants;
import com.yanyi.luckbag.util.AmayaEvent;
import com.yanyi.luckbag.util.AmayaSPUtil;
import com.yanyi.luckbag.util.AnimUtil;
import com.yanyi.luckbag.util.CommonUtil;
import com.yanyi.luckbag.util.UIUtil;

import java.util.Calendar;

import de.greenrobot.event.EventBus;

/**
 * Created by amayababy
 * 2015-12-18
 * 下午3:03
 */
public class CountActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, View.OnLongClickListener {

    private ImageView imgView;
    private ListView listView;
    private YanyiAdapter adapter;

    private int limit, count = 20;
    private LinearLayout titleLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count);
        imgView = (ImageView) findViewById(R.id.count_img);
        listView = (ListView) findViewById(R.id.count_list);
        int w = UIUtil.amayaWidth / 5;
        adapter = new YanyiAdapter(this, w, (UIUtil.amayaWidth / 2 - w) / 3);
        listView.setAdapter(adapter);


        if (MatrixApplication.TODAY_MILLS == 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.HOUR, 0);
            MatrixApplication.TODAY_MILLS = calendar.getTimeInMillis();

        }
        EventBus.getDefault().register(this);
        YanyiDB.getInstance().listAll(limit, count);
        YanyiDB.getInstance().closeDB();
        imgView.setOnClickListener(this);
        imgView.setOnLongClickListener(this);
        initWindow();
        initIndexLayout();
        MatrixApplication.getImageLoader().displayImageWithCircle(AmayaConstants.PREFIX_DRAWABLE + R.drawable.defaule_yanyi, imgView, 48, R.drawable.default_load_img);
        listView.setOnItemClickListener(this);

    }

    private void initIndexLayout() {
        View view = findViewById(R.id.item_yanyi_name);
        titleLayout = (LinearLayout) view.getParent();
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
        lp.width = UIUtil.amayaWidth / 5;

    }

    private void initWindow() {
        WindowManager.LayoutParams params2 = new WindowManager.LayoutParams();
        params2.width = UIUtil.amayaWidth / 2;
        params2.gravity = Gravity.RIGHT | Gravity.TOP;
        params2.type = WindowManager.LayoutParams.TYPE_PHONE;
        params2.height = UIUtil.amayaWidth;
        params2.type = WindowManager.LayoutParams.TYPE_PHONE;
        params2.format = PixelFormat.RGBA_8888;
        params2.flags = WindowManager.LayoutParams.FLAG_LAYOUT_ATTACHED_IN_DECOR;
        Window window = this.getWindow();
        window.setAttributes(params2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(AmayaEvent.AlreadyInsertEvent event) {
//        MatrixApplication.getImageLoader().displayImageWithCircle(AmayaConstants.PREFIX_DRAWABLE+(event.alreadyInsert?R.drawable.default_sister:R.drawable.defaule_yanyi),imgView,48,R.drawable.default_load_img);
    }

    public void onEventMainThread(AmayaEvent.IncomeBeansEvent beansEvent) {
        if (beansEvent.beans != null && beansEvent.beans.size() > 0) {
            limit += count;
            adapter.addAll(beansEvent.beans);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.count_img:
                if (titleLayout.getVisibility() == View.VISIBLE) {
                    AnimUtil.hideView(titleLayout, AnimUtil.START_RIGHT_TOP, true, null);
                    AnimUtil.hideView(listView, AnimUtil.START_RIGHT_TOP, true, null);
                } else {
                    AnimUtil.showView(titleLayout, AnimUtil.START_RIGHT_TOP, null);
                    AnimUtil.showView(listView, AnimUtil.START_RIGHT_TOP, null);
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (titleLayout.getVisibility() == View.VISIBLE) {
                AnimUtil.hideView(titleLayout, AnimUtil.START_RIGHT_TOP, true, null);
                AnimUtil.hideView(listView, AnimUtil.START_RIGHT_TOP, true, null);
            } else {
                finish();
            }
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        IncomeBean item = adapter.getItem(position);
        Toast.makeText(this, item.toToast(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onLongClick(View v) {

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content(R.string.del_all)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String name = AmayaSPUtil.getString("yanyi_name", "你");
                                IncomeBean bean = YanyiDB.getInstance().findSelfIncomeBean(name, false);
                                int[] deleteIncomeDB = YanyiDB.getInstance().deleteIncomeDB();
                                StringBuilder stringBuilder = new StringBuilder();

                                if (bean != null) {

                                    stringBuilder.append(name).append("\n在此期间")
                                            .append(bean.getInOutMoney() > 0 ? "赚了" : "亏了")
                                            .append(CommonUtil.formatMoney(Math.abs(bean.getInOutMoney())))
                                            .append("元\n")
                                            .append("发出红包 ").append(CommonUtil.formatMoney(bean.getOutMoney())).append("元\n")
                                            .append("抢到红包 ").append(CommonUtil.formatMoney(bean.getInMoney())).append("元\n\n");

                                }

                                stringBuilder.append("共删除人数：")
                                        .append(deleteIncomeDB[0])
                                        .append("人\n")
                                        .append("删除数据：")
                                        .append(deleteIncomeDB[1])
                                        .append("条");
                                Toast.makeText(CountActivity.this, stringBuilder.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .show();
        dialog.show();
        return false;
    }


}
