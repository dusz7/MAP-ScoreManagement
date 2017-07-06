package com.hitiot.dusz7.naiverss.rss;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by dusz7 on 2017/7/2.
 */

public class RssHandler extends DefaultHandler {

    RssFeed feed;
    RssItem item;

    String lastElementName = "";// 标记变量，用于标记在解析过程中我们关心的几个标签，若不是我们关心的标签记做0

    final int RSS_TITLE = 1;// 若是 title 标签，记做1，注意有两个title，但我们都保存在item的成员变量中
    final int RSS_LINK = 2;// 若是 link 标签，记做2
    final int RSS_AUTHOR = 3;
    final int RSS_CATEGORY = 4;// 若是category标签,记做 4
    final int RSS_PUBDATE = 5; // 若是pubdate标签,记做5,注意有两个pubdate,但我们都保存在item的pubdate成员变量中
    final int RSS_COMMENTS = 6;
    final int RSS_DESCRIPTION = 7;// 若是 description 标签，记做7

    int currentFlag = 0;

    public RssHandler() {

    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        feed = new RssFeed();
        item = new RssItem();

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        super.characters(ch, start, length);
        // 获取字符串
        String text = new String(ch, start, length);
//        Log.d("text", "要获取的内容：" + text);

        switch (currentFlag) {
            case RSS_TITLE:
                item.setTitle(text);
                currentFlag = 0;// 设置完后，重置为开始状态
                break;
            case RSS_PUBDATE:
                item.setPubDate(text);
                currentFlag = 0;// 设置完后，重置为开始状态
                break;
            case RSS_CATEGORY:
                item.setCategory(text);
                currentFlag = 0;// 设置完后，重置为开始状态
                break;
            case RSS_LINK:
                item.setLink(text);
                currentFlag = 0;// 设置完后，重置为开始状态
                break;
            case RSS_AUTHOR:
                item.setAuthor(text);
                currentFlag = 0;// 设置完后，重置为开始状态
                break;
            case RSS_DESCRIPTION:
                item.setDescription(text);
                currentFlag = 0;// 设置完后，重置为开始状态
                break;
            case RSS_COMMENTS:
                item.setComments(text);
                currentFlag = 0;// 设置完后，重置为开始状态
                break;
            default:
                break;
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        super.startElement(uri, localName, qName, attributes);

        if ("chanel".equals(localName)) {
            // 这个标签内没有我们关心的内容，所以不作处理，currentFlag=0
            currentFlag = 0;
            return;
        }
        if ("item".equals(localName)) {
            item = new RssItem();
            return;
        }
        if ("title".equals(localName)) {
            currentFlag = RSS_TITLE;
            return;
        }
        if ("description".equals(localName)) {
            currentFlag = RSS_DESCRIPTION;
            return;
        }
        if ("link".equals(localName)) {
            currentFlag = RSS_LINK;
            return;
        }
        if ("pubDate".equals(localName)) {
            currentFlag = RSS_PUBDATE;
            return;
        }
        if ("category".equals(localName)) {
            currentFlag = RSS_CATEGORY;
            return;
        }

        if ("author".equals(localName)) {
            currentFlag = RSS_AUTHOR;
            return;
        }

        if ("comments".equals(localName)) {
            currentFlag = RSS_COMMENTS;
            return;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        super.endElement(uri, localName, qName);
        // 如果解析一个item节点结束，就将rssItem添加到rssFeed中。
        if ("item".equals(localName)) {

            feed.addItem(item);
            return;
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    public RssFeed getRssFeed() {
        return feed;
    }

}
