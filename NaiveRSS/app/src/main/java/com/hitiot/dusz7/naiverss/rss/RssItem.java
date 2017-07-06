package com.hitiot.dusz7.naiverss.rss;

import java.io.Serializable;

/**
 * Created by dusz7 on 2017/7/2.
 */

public class RssItem implements Serializable {

    private String title;
    private String link;
    private String author;
    private String category;
    private String pubDate;
    private String comments;
    private String description;

    private boolean isFavourite;
    private int dbID;

//    public static final String TITLE = "title";
//    public static final String PUBDATE = "pubDate";

    public RssItem() {

    }

    public RssItem(String title, String pubDate) {
        this.title = title;
        this.pubDate = pubDate;
        this.isFavourite = false;
    }

    public String getTitle() {
        return title;
    }
    public String getShowTitle() {
        if(title.length() > 20) {
            return title.substring(0,19)+"...";
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public int getDbID() {
        return dbID;
    }

    public void setDbID(int dbID) {
        this.dbID = dbID;
    }

    @Override
    public String toString() {
        return "RssItem [title=" + title + ", description=" + description
                + ", link=" + link + ", category=" + category + ", pubdate="
                + pubDate + "]";
    }
}
