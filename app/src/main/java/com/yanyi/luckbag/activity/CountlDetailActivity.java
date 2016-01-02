package com.yanyi.luckbag.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.yanyi.luckbag.R;
import com.yanyi.luckbag.adapter.YanyiAdapter;
import com.yanyi.luckbag.db.YanyiDB;
import com.yanyi.luckbag.util.AmayaEvent;
import com.yanyi.luckbag.util.UIUtil;

import de.greenrobot.event.EventBus;

/**
 * Created by amayababy
 * 2015-12-18
 * 下午3:03
 */
public class CountlDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgView;
    private ListView listView;
    private YanyiAdapter adapter;

    private int limit, count = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count);
        imgView = (ImageView) findViewById(R.id.count_img);
        imgView.setVisibility(View.GONE);
        listView = (ListView) findViewById(R.id.count_list);
        int w = UIUtil.amayaWidth / 3;
        adapter = new YanyiAdapter(this, w, (UIUtil.amayaWidth - w) / 3);
        listView.setAdapter(adapter);
        EventBus.getDefault().register(this);
        YanyiDB.getInstance().listAll(limit, count);
        YanyiDB.getInstance().closeDB();
        initIndexLayout();
    }

    private void initIndexLayout() {
        View view = findViewById(R.id.item_yanyi_name);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
        lp.width = UIUtil.amayaWidth / 3;

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
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.count_img:
//                if(titleLayout.getVisibility() == View.VISIBLE){
//                    AnimUtil.hideView(titleLayout,AnimUtil.START_RIGHT_TOP,true,null);
//                    AnimUtil.hideView(listView,AnimUtil.START_RIGHT_TOP,true,null);
//                }else{
//                    AnimUtil.showView(titleLayout,AnimUtil.START_RIGHT_TOP,null);
//                    AnimUtil.showView(listView,AnimUtil.START_RIGHT_TOP,null);
//                }
                break;
        }
    }
}
