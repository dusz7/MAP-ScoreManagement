package com.hitiot.dusz7.naiverss.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.hitiot.dusz7.naiverss.R;
import com.hitiot.dusz7.naiverss.adapter.RssItemAdapter;
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
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<RssItem> rssItemList = new ArrayList<RssItem>();

    RecyclerView.Adapter adapter;


    private static final int FEED_FAIL = 0;
    private static final int FEED_SUCCESS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        recyclerView = (RecyclerView)findViewById(R.id.rss_favourite_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RssItemAdapter(rssItemList);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                RssItem item = rssItemList.get(position);
                Intent intent = new Intent(FavouriteActivity.this, RssItemContentActivity.class);
                intent.putExtra("rssItem", item);
                intent.putExtra("itemPosition",position);
                intent.putExtra("isInFavourite",true);
                startActivityForResult(intent,0);
//                startActivityForResult(intent, 0);
            }
        }));

        new GetItemsThread().start();

    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case FEED_FAIL:
                    setTitle("无收藏信息");
                    adapter.notifyDataSetChanged();
                    break;
                case FEED_SUCCESS:
                    adapter.notifyDataSetChanged();
                    break;
            }

            return false;
        }
    });


    private class GetItemsThread extends Thread {
        @Override
        public void run() {

            rssItemList.clear();
            try {
                rssItemList.addAll((List<RssItem>)CacheUtils.readObject("favourite_cache.dat"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(rssItemList.size() != 0) {
                Message message = new Message();
                message.what = FEED_SUCCESS;
                handler.sendMessage(message);
            }else {
                Message message = new Message();
                message.what = FEED_FAIL;
                handler.sendMessage(message);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                rssItemList.get(data.getIntExtra("itemPosition",-1)).setFavourite(data.getBooleanExtra("itemFavourite",false));
                new GetItemsThread().start();
                break;

        }
    }
}
