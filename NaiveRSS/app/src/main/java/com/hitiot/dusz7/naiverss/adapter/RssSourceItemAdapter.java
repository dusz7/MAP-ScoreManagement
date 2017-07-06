package com.hitiot.dusz7.naiverss.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hitiot.dusz7.naiverss.R;
import com.hitiot.dusz7.naiverss.activity.RssSourceActivity;
import com.hitiot.dusz7.naiverss.bean.RssFeedBean;
import com.hitiot.dusz7.naiverss.view.SwipeListLayout;

import java.util.List;

/**
 * Created by dusz7 on 2017/7/3.
 */

public class RssSourceItemAdapter extends ArrayAdapter<RssFeedBean> {

    private int resourceId;

    public RssSourceItemAdapter(Context context, List<RssFeedBean> objects) {
        super(context, R.layout.rss_source_item, objects);
        resourceId = R.layout.rss_source_item;
    }

    /*  由系统调用，获取一个View对象，作为ListView的条目，屏幕上能显示多少个条目，getView方法就会被调用多少次
     *  position：代表该条目在整个ListView中所处的位置，从0开始
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //重写适配器的getItem()方法
        RssFeedBean feedBean = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) { //若没有缓存布局，则加载
            //首先获取布局填充器，然后使用布局填充器填充布局文件
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.sll_main = (SwipeListLayout) view.findViewById(R.id.sll_main);

            //存储子项布局中子控件对象
            viewHolder.labelText = (TextView) view.findViewById(R.id.text_source_label);
            viewHolder.categoryText = (TextView) view.findViewById(R.id.text_source_category);
            viewHolder.updateText = (TextView) view.findViewById(R.id.tv_update);
            viewHolder.deleteText = (TextView)view.findViewById(R.id.tv_delete);

            // 将内部类对象存储到View对象中
            view.setTag(viewHolder);
        } else { //若有缓存布局，则直接用缓存（利用的是缓存的布局，利用的不是缓存布局中的数据）
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.labelText.setText(feedBean.getLabel());
        viewHolder.categoryText.setText(feedBean.getCategory());
        final SwipeListLayout temp = viewHolder.sll_main;
        viewHolder.updateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp.setStatus(SwipeListLayout.Status.Close, true);

            }
        });
        viewHolder.deleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp.setStatus(SwipeListLayout.Status.Close, true);
            }
        });
        return view;
    }

    //内部类，用于存储ListView子项布局中的控件对象
    class ViewHolder {

        SwipeListLayout sll_main;
        TextView labelText;
        TextView categoryText;

        TextView updateText;
        TextView deleteText;

    }
}
