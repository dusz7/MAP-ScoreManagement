package com.hitiot.dusz7.naiverss.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hitiot.dusz7.naiverss.bean.RssFeedBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dusz7 on 2017/7/3.
 */

public class FeedDao {

    Context context;
    MyDBHelper helper;
    SQLiteDatabase db;

    public FeedDao(Context context) {
        this.context = context;
        helper = new MyDBHelper(context);
    }

    public void initFeedTabel() {

        db = helper.getWritableDatabase();
        db.beginTransaction();

        try {
            db.execSQL("insert into " + helper.FEED_TABLE_NAME + " (Id, FeedUrl, Label, Category) values (1, 'http://news.qq.com/newsgn/rss_newsgn.xml', 'QQ新闻','新闻')");
            db.execSQL("insert into " + helper.FEED_TABLE_NAME + " (Id, FeedUrl, Label, Category) values (2, 'http://songshuhui.net/feed', '科学松鼠会','其他')");
            db.execSQL("insert into " + helper.FEED_TABLE_NAME + " (Id, FeedUrl, Label, Category) values (3, 'http://www.geekpark.net/rss', '极客公园','新闻')");
            db.execSQL("insert into " + helper.FEED_TABLE_NAME + " (Id, FeedUrl, Label, Category) values (4, 'http://www.write.org.cn/feed', '读书笔记','生活')");
            db.execSQL("insert into " + helper.FEED_TABLE_NAME + " (Id, FeedUrl, Label, Category) values (5, 'https://www.zhihu.com/rss', '知乎精选','娱乐')");
            db.execSQL("insert into " + helper.FEED_TABLE_NAME + " (Id, FeedUrl, Label, Category) values (6, 'http://feeds2.feedburner.com/jandan', '煎蛋','技术')");
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }


    public void insertOne(String feedUrl, String category, String label) {

        db = helper.getWritableDatabase();
        db.beginTransaction();

        ContentValues contentValues = new ContentValues();
//        contentValues.put("Id", id);
        contentValues.put("FeedUrl", feedUrl);
        contentValues.put("Category", category);
        contentValues.put("Label", label);

        db.insertOrThrow(helper.FEED_TABLE_NAME, null, contentValues);

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

    }

    public void deleteOne(int id) {
        db = helper.getWritableDatabase();
        db.beginTransaction();

        db.delete(helper.FEED_TABLE_NAME, "Id = ?", new String[]{String.valueOf(id)});
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void updateOne(int id, String feedUrl, String category, String label) {
        db = helper.getWritableDatabase();
        db.beginTransaction();

        ContentValues contentValues = new ContentValues();
        contentValues.put("FeedUrl", feedUrl);
        contentValues.put("Category", category);
        contentValues.put("Label", label);

        db.update(helper.FEED_TABLE_NAME,
                contentValues,
                "Id = ?",
                new String[]{String.valueOf(id)});
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public List<RssFeedBean> queryAll() {
        db = helper.getReadableDatabase();

        Cursor cursor = db.query(helper.FEED_TABLE_NAME,
                null,
                null,
                null,
                null, null, null);

        if (cursor.getCount() > 0) {
            List<RssFeedBean> feedList = new ArrayList<RssFeedBean>(cursor.getCount());
            while (cursor.moveToNext()) {
                RssFeedBean feed = parseOrder(cursor);
                feedList.add(feed);
            }
            db.close();
            return feedList;
        }
        db.close();
        return null;
    }

    /**
     * 将查找到的数据转换成FeedBean类
     */
    private RssFeedBean parseOrder(Cursor cursor){
        RssFeedBean feedBean = new RssFeedBean();
        feedBean.setDbId (cursor.getInt(cursor.getColumnIndex("Id")));
        feedBean.setFeedUrl (cursor.getString(cursor.getColumnIndex("FeedUrl")));
        feedBean.setCategory (cursor.getString(cursor.getColumnIndex("Category")));
        feedBean.setLabel (cursor.getString(cursor.getColumnIndex("Label")));
        return feedBean;
    }


}
