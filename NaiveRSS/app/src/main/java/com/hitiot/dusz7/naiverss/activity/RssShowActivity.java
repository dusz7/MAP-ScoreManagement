package com.hitiot.dusz7.naiverss.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.solver.Cache;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.hitiot.dusz7.naiverss.MainActivity;
import com.hitiot.dusz7.naiverss.R;
import com.hitiot.dusz7.naiverss.adapter.RssItemAdapter;
import com.hitiot.dusz7.naiverss.application.MyApplication;
import com.hitiot.dusz7.naiverss.bean.RssFeedBean;
import com.hitiot.dusz7.naiverss.cache.CacheUtils;
import com.hitiot.dusz7.naiverss.database.FeedDao;
import com.hitiot.dusz7.naiverss.internet.InternetUtils;
import com.hitiot.dusz7.naiverss.listener.RecyclerItemClickListener;
import com.hitiot.dusz7.naiverss.rss.RssFeed;
import com.hitiot.dusz7.naiverss.rss.RssFeed_SAXParser;
import com.hitiot.dusz7.naiverss.rss.RssItem;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

public class RssShowActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<RssItem> rssItemList = new ArrayList<RssItem>();

    RecyclerView.Adapter adapter;

    Toolbar toolbar;
    final HashMap<String,String> categoryMap = new HashMap<String,String>();

    // 从网络获取RSS地址
    private List<RssFeedBean> rssFeedUrls;
    private RssFeed feed = null;

    private static final int FEED_SUCCESS = 0;
    private static final int FEED_CACHE_ZERO = 1;
    private static final int FEED_CATEGORY_ZERO = 2;

    private String categoryNow = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssshow);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        categoryNow = "all";

        FeedDao feedDao = new FeedDao(this);
        rssFeedUrls = feedDao.queryAll();

        {
            categoryMap.put("技术","tech");
            categoryMap.put("新闻","news");
            categoryMap.put("生活","life");
            categoryMap.put("娱乐","amuse");
            categoryMap.put("其他","others");
            categoryMap.put("all","all");
        }

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Rss订阅列表");
        toolbar.setSubtitle("全部类型");
        toolbar.inflateMenu(R.menu.show_menu);//设置右上角的填充菜单
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                if (menuItemId == R.id.action_category) {
                    showSelectCategoryDialog();

                } else if (menuItemId == R.id.action_sync) {
                    new GetFeedSyncThread(categoryNow).start();

                }
                return true;
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.rss_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RssItemAdapter(rssItemList);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                RssItem item = rssItemList.get(position);
                Intent intent = new Intent(RssShowActivity.this, RssItemContentActivity.class);
                intent.putExtra("rssItem", item);
                intent.putExtra("itemPosition",position);
                startActivityForResult(intent,0);
            }
        }));

        new GetFeedThread(categoryNow).start();

    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case FEED_CACHE_ZERO:
                    setTitle("无有效缓存");
                    adapter.notifyDataSetChanged();
                    Toast.makeText(MyApplication.getContext(),"无有效缓存",Toast.LENGTH_SHORT).show();
                    break;
                case FEED_SUCCESS:
                    Log.d("test","success");
                    adapter.notifyDataSetChanged();
                    break;
                case FEED_CATEGORY_ZERO:
                    setTitle("无相关类别订阅");
                    adapter.notifyDataSetChanged();
                    Toast.makeText(MyApplication.getContext(),"无相关类别订阅",Toast.LENGTH_SHORT).show();
                    break;
            }

            return false;
        }
    });


    private class GetFeedThread extends Thread {
        private String category;
        GetFeedThread(String ca) {
            category = ca;
        }

        @Override
        public void run() {

            rssItemList.clear();
            if(InternetUtils.isNetworkConnected() && CacheUtils.isCacheDataFailure(categoryMap.get(category)+"_cache.dat")) {
                getFeedDataFromNet(category);
            }
            else {
                getDataFromCache(category);
            }
        }
    }

    private class GetFeedSyncThread extends Thread {
        private String category;
        GetFeedSyncThread(String ca) {
            category = ca;
        }

        @Override
        public void run() {

            rssItemList.clear();
            if(InternetUtils.isNetworkConnected()) {
                getFeedDataFromNet(category);
            }

        }
    }


    private void getFeedDataFromNet(String category) {
        rssItemList.clear();
        for(int i = 0; i < rssFeedUrls.size(); i ++) {
            if(category.equals("all") || category.equals(rssFeedUrls.get(i).getCategory())) {
                try {
                    feed = new RssFeed_SAXParser().getFeed(rssFeedUrls.get(i).getFeedUrl());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(feed != null) {
                    rssItemList.addAll(feed.getRssItems());
                    List<RssItem> list = new ArrayList<RssItem>();
                    try {
                        list.addAll((List<RssItem>)CacheUtils.readObject(categoryMap.get(rssFeedUrls.get(i).getCategory())+"_cache.dat"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    list.addAll(feed.getRssItems());
                    CacheUtils.saveObject((Serializable)list,categoryMap.get(rssFeedUrls.get(i).getCategory())+"_cache.dat");
                    Log.d("test0",categoryMap.get(rssFeedUrls.get(i).getCategory())+"_cache.dat");
                    Log.d("test0",((List<RssItem>)CacheUtils.readObject(categoryMap.get(rssFeedUrls.get(i).getCategory())+"_cache.dat")).toString());
                    if(category.equals("all")) {
                        CacheUtils.saveObject((Serializable)rssItemList,"all_cache.dat");
                    }
                    Message message = new Message();
                    message.what = FEED_SUCCESS;
                    handler.sendMessage(message);
                }
            }
        }
        if(rssItemList.size() == 0) {
            Message message = new Message();
            message.what = FEED_CATEGORY_ZERO;
            handler.sendMessage(message);
        }
    }

    private void getDataFromCache(String category) {
        rssItemList.clear();

        try {
            rssItemList.addAll((List<RssItem>)CacheUtils.readObject(categoryMap.get(category)+"_cache.dat"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(rssItemList.size() !=  0) {
            Message message = new Message();
            message.what = FEED_SUCCESS;
            handler.sendMessage(message);
        } else {
            Message message = new Message();
            message.what = FEED_CACHE_ZERO;
            handler.sendMessage(message);
        }
    }

    private void showSelectCategoryDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_select_category, (ViewGroup) findViewById(R.id.dialog_select_category));

        final Spinner spinnerSourceCategory = (Spinner) layout.findViewById(R.id.spinner_select);

        spinnerSourceCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryNow = parent.getItemAtPosition(position).toString();
                Log.d("ca",categoryNow);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(RssShowActivity.this);
        builder.setTitle("选择类型")
                .setView(layout)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new GetFeedThread(categoryNow).start();
                    }
                })
                .setNegativeButton("取消",null)
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                rssItemList.get(data.getIntExtra("itemPosition",-1)).setFavourite(data.getBooleanExtra("itemFavourite",false));
                break;

        }
    }

}
