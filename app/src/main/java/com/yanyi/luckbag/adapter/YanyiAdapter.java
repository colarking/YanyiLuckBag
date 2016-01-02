package com.yanyi.luckbag.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanyi.luckbag.R;
import com.yanyi.luckbag.bean.IncomeBean;
import com.yanyi.luckbag.util.AmayaSPUtil;
import com.yanyi.luckbag.util.CommonUtil;

/**
 * Created by amayababy
 * 2015-12-20
 * 下午7:05
 */
public class YanyiAdapter extends AmayaAdapter<IncomeBean> {

    private final int inoutOk, inoutBad, nameSelfColor;
    private final String selfName;
    private LinearLayout.LayoutParams nameLP;
    private LinearLayout.LayoutParams otherLP;
    private LayoutInflater inflater;

    public YanyiAdapter(Context context, int nameWidth, int otherWidth) {
        inflater = LayoutInflater.from(context);
        nameLP = new LinearLayout.LayoutParams(nameWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        otherLP = new LinearLayout.LayoutParams(otherWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        otherLP.gravity = Gravity.RIGHT;
        nameLP.gravity = Gravity.LEFT;
        selfName = AmayaSPUtil.getString("yanyi_name", null);
        inoutOk = context.getResources().getColor(R.color.text_color_inout_ok);
        inoutBad = context.getResources().getColor(R.color.text_color_inout_bad);
        nameSelfColor = context.getResources().getColor(R.color.text_color_name_self);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_yanyi_detail, null);
            vh.nameView = (TextView) convertView.findViewById(R.id.item_yanyi_name);
            vh.inoutView = (TextView) convertView.findViewById(R.id.item_yanyi_money_inout);
            vh.inView = (TextView) convertView.findViewById(R.id.item_yanyi_money_in);
            vh.outView = (TextView) convertView.findViewById(R.id.item_yanyi_money_out);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.nameView.setLayoutParams(nameLP);
        vh.inView.setLayoutParams(otherLP);
        vh.inoutView.setLayoutParams(otherLP);
        vh.outView.setLayoutParams(otherLP);
        convertView.setBackgroundResource(position % 2 == 0 ? R.drawable.gray : R.drawable.white);
        IncomeBean bean = getItem(position);
        vh.nameView.setTextColor(bean.getName().equals(selfName) ? nameSelfColor : inoutOk);
        vh.nameView.setText(bean.getName());
        vh.inoutView.setTextColor(bean.getInOutMoney() > 0 ? inoutOk : inoutBad);
        vh.inoutView.setText(CommonUtil.formatMoney(bean.getInOutMoney()));
        vh.inView.setText(CommonUtil.formatMoney(bean.getInMoney()));
        vh.outView.setText(CommonUtil.formatMoney(bean.getOutMoney()));
        return convertView;
    }


    static class ViewHolder {
        TextView nameView, inoutView, inView, outView;
    }
}
