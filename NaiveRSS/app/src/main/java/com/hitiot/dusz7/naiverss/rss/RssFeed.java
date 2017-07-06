package com.hitiot.dusz7.naiverss.rss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dusz7 on 2017/7/2.
 */

public class RssFeed {

    // channel信息
    private String title;
    private String pubDate;

    // item列表
    private int itemNum;
    private List<RssItem> rssItems;

    public RssFeed() {
        rssItems = new ArrayList<RssItem>();
    }

    // 添加RssItem条目,返回列表长度
    public int addItem(RssItem item) {
        rssItems.add(item);
        itemNum++;
        return itemNum;
    }

    // 根据下标获取RssItem
    public RssItem getItem(int position) {
        return rssItems.get(position);
    }

    // 获取所有item
//    public List<HashMap<String, Object>> getAllItems() {
//        List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
//        for (int i = 0; i < rssItems.size(); i++) {
//            HashMap<String, Object> item = new HashMap<String, Object>();
//            item.put(RssItem.TITLE, rssItems.get(i).getTitle());
//            item.put(RssItem.PUBDATE, rssItems.get(i).getPubDate());
//            data.add(item);
//        }
//        return data;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public int getItemNum() {
        return itemNum;
    }

    public void setItemNum(int itemNum) {
        this.itemNum = itemNum;
    }

    public List<RssItem> getRssItems() {
        return rssItems;
    }
}
