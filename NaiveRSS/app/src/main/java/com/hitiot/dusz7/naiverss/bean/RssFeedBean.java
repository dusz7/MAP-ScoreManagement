package com.hitiot.dusz7.naiverss.bean;

/**
 * Created by dusz7 on 2017/7/3.
 */

public class RssFeedBean {

    private int dbId;
    private String feedUrl;
    private String category;
    private String label;

    public RssFeedBean(int dbId, String feedUrl, String category, String label) {
        this.dbId = dbId;
        this.feedUrl = feedUrl;
        this.category = category;
        this.label = label;
    }

    public RssFeedBean() {

    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
