package com.hitiot.dusz7.naiverss.activity;

import android.content.Intent;
import android.support.constraint.solver.Cache;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.hitiot.dusz7.naiverss.R;
import com.hitiot.dusz7.naiverss.adapter.RssItemAdapter;
import com.hitiot.dusz7.naiverss.cache.CacheUtils;
import com.hitiot.dusz7.naiverss.database.FeedDao;
import com.hitiot.dusz7.naiverss.rss.RssFeed;
import com.hitiot.dusz7.naiverss.rss.RssFeed_SAXParser;
import com.hitiot.dusz7.naiverss.rss.RssItem;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

public class RssItemContentActivity extends AppCompatActivity {

    private RssItem rssItem;
    private boolean isFavourite;
    private int itemPosition;

    CoordinatorLayout container;

    private String title;
    private String description;
    private String link;
    private String pubDate;

    private String testContent;

    private FloatingActionButton fabFavourite;
    private TextView contentText;
    private TextView titleText;
    private TextView pubText;
    private TextView desText;
    private TextView linkText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssitem_content);

        fabFavourite = (FloatingActionButton)findViewById(R.id.fab_favourite);
        titleText = (TextView)findViewById(R.id.text_rss_title);
        pubText = (TextView)findViewById(R.id.text_rss_pubdate);
        desText = (TextView)findViewById(R.id.text_rss_description);
        contentText = (TextView)findViewById(R.id.text_rss_content);
        linkText = (TextView)findViewById(R.id.text_rss_link);

        container = (CoordinatorLayout)findViewById(R.id.container1);

        Intent intent = getIntent();
        rssItem = (RssItem) intent.getSerializableExtra("rssItem");
        itemPosition = intent.getIntExtra("itemPosition",-1);

        title = rssItem.getTitle();
        description = rssItem.getDescription();
        link = rssItem.getLink();
        pubDate = rssItem.getPubDate();
        isFavourite = rssItem.isFavourite();
        Log.d("isFavourite","s"+isFavourite);

        titleText.setText(title);
        pubText.setText(pubDate);
        desText.setText(description);
        linkText.setText("详细信息请访问以下网址:\n" + link);


        fabFavourite = (FloatingActionButton)findViewById(R.id.fab_favourite);
        fabFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rssItem.setFavourite(!isFavourite);
                if(rssItem.isFavourite()) {
                    Snackbar.make(container, "收藏成功", Snackbar.LENGTH_LONG).show();
                    isFavourite = true;
                    List<RssItem> list = (List<RssItem>)CacheUtils.readObject("favourite_cache.dat");
                    if (list == null) {
                        list = new ArrayList<RssItem>();
                    }
                    for(RssItem item : list) {
                        if (item.getTitle().equals(rssItem.getTitle())) {
                            return;
                        }
                    }
                    list.add(rssItem);
                    CacheUtils.saveObject((Serializable)list,"favourite_cache.dat");
                } else {
                    isFavourite = false;
                    Snackbar.make(container, "取消收藏", Snackbar.LENGTH_LONG).show();
                    List<RssItem> list = (List<RssItem>)CacheUtils.readObject("favourite_cache.dat");
                    for(RssItem item : list) {
                        if (item.getTitle().equals(rssItem.getTitle())) {
                            list.remove(item);
                            break;
                        }
                    }

                    CacheUtils.saveObject((Serializable)list,"favourite_cache.dat");
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("itemPosition",itemPosition);
        intent.putExtra("itemFavourite",rssItem.isFavourite());
        setResult(RESULT_OK,intent);
        finish();
    }

}
