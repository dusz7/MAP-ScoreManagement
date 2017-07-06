package com.hitiot.dusz7.naiverss.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.hitiot.dusz7.naiverss.MainActivity;
import com.hitiot.dusz7.naiverss.R;

import com.hitiot.dusz7.naiverss.bean.RssFeedBean;
import com.hitiot.dusz7.naiverss.database.FeedDao;
import com.hitiot.dusz7.naiverss.view.SwipeListLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RssSourceActivity extends AppCompatActivity {

    List<RssFeedBean> feedBeanList;
    ListView listView;

    private static Set<SwipeListLayout> sets = new HashSet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsssource);

        listView = (ListView)findViewById(R.id.rss_source_list);
        feedBeanList = new ArrayList<RssFeedBean>();
        FeedDao feedDao = new FeedDao(this);
        feedBeanList = feedDao.queryAll();

        RssSourceItemInnerAdapter adapter = new RssSourceItemInnerAdapter(this,feedBeanList);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    //当listview开始滑动时，若有item的状态为Open，则Close，然后移除
                    case SCROLL_STATE_TOUCH_SCROLL:
                        if (sets.size() > 0) {
                            for (SwipeListLayout s : sets) {
                                s.setStatus(SwipeListLayout.Status.Close, true);
                                sets.remove(s);
                            }
                        }
                        break;

                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }

    String sourceCategory;
    private void showUpdateSourceDialog(final RssFeedBean feedBean) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_source, (ViewGroup) findViewById(R.id.dialog_add_source));
        final EditText etSourceUrl = (EditText)layout.findViewById(R.id.edit_source_url);
        final EditText etSourceLabel = (EditText)layout.findViewById(R.id.edit_source_label);

        etSourceUrl.setText(feedBean.getFeedUrl());
        etSourceLabel.setText(feedBean.getLabel());

        final Spinner spinnerSourceCategory = (Spinner) layout.findViewById(R.id.spinner_source_category);

        spinnerSourceCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sourceCategory = parent.getItemAtPosition(position).toString();

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(RssSourceActivity.this);
        builder.setTitle("添加订阅源")
                .setView(layout)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String sourceUrl = (etSourceUrl.getText().toString());
                        String sourceLabel = (etSourceLabel.getText().toString());


                        if(!"".equals(sourceUrl) && sourceUrl!=null) {

                            FeedDao feedDao = new FeedDao(RssSourceActivity.this);
                            feedDao.updateOne(feedBean.getDbId(),sourceUrl,sourceCategory,sourceLabel);
                        }

                    }
                })
                .setNegativeButton("取消",null)
                .create()
                .show();
    }

    class MyOnSlipStatusListener implements SwipeListLayout.OnSwipeStatusListener {

        private SwipeListLayout slipListLayout;

        public MyOnSlipStatusListener(SwipeListLayout slipListLayout) {
            this.slipListLayout = slipListLayout;
        }

        @Override
        public void onStatusChanged(SwipeListLayout.Status status) {
            if (status == SwipeListLayout.Status.Open) {
                //若有其他的item的状态为Open，则Close，然后移除
                if (sets.size() > 0) {
                    for (SwipeListLayout s : sets) {
                        s.setStatus(SwipeListLayout.Status.Close, true);
                        sets.remove(s);
                    }
                }
                sets.add(slipListLayout);
            } else {
                if (sets.contains(slipListLayout))
                    sets.remove(slipListLayout);
            }
        }

        @Override
        public void onStartCloseAnimation() {

        }

        @Override
        public void onStartOpenAnimation() {

        }

    }


    class RssSourceItemInnerAdapter extends ArrayAdapter<RssFeedBean> {

        private int resourceId;

        public RssSourceItemInnerAdapter(Context context, List<RssFeedBean> objects) {
            super(context, R.layout.rss_source_item, objects);
            resourceId = R.layout.rss_source_item;
        }

        /*  由系统调用，获取一个View对象，作为ListView的条目，屏幕上能显示多少个条目，getView方法就会被调用多少次
         *  position：代表该条目在整个ListView中所处的位置，从0开始
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //重写适配器的getItem()方法
            final RssFeedBean feedBean = getItem(position);
            View view;
            ViewHolder viewHolder;
            if (convertView == null) { //若没有缓存布局，则加载
                //首先获取布局填充器，然后使用布局填充器填充布局文件
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.sll_main = (SwipeListLayout) view.findViewById(R.id.sll_main);
                viewHolder.sll_main.setOnSwipeStatusListener(new MyOnSlipStatusListener(viewHolder.sll_main));
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
                    showUpdateSourceDialog(feedBean);
                }
            });
            viewHolder.deleteText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    temp.setStatus(SwipeListLayout.Status.Close, true);

                    FeedDao feedDao = new FeedDao(RssSourceActivity.this);
                    feedDao.deleteOne(feedBean.getDbId());
                    feedBeanList.remove(feedBean);
                    notifyDataSetChanged();
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
}
