package com.hitiot.dusz7.naiverss.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hitiot.dusz7.naiverss.R;
import com.hitiot.dusz7.naiverss.activity.RssItemContentActivity;
import com.hitiot.dusz7.naiverss.activity.RssShowActivity;
import com.hitiot.dusz7.naiverss.rss.RssItem;

import java.util.List;

/**
 * Created by dusz7 on 2017/7/2.
 */

public class RssItemAdapter extends RecyclerView.Adapter<RssItemAdapter.ViewHolder> {

   private List<RssItem> rssItemList;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemTitleText;
        TextView itemPubDateText;

        public ViewHolder(View view) {
            super(view);
            itemTitleText = (TextView)view.findViewById(R.id.item_title);
            itemPubDateText = (TextView)view.findViewById(R.id.item_pub_date);
        }

    }

    public RssItemAdapter(List<RssItem> list) {
        rssItemList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rss_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int positon) {
        RssItem item = rssItemList.get(positon);
        holder.itemTitleText.setText(item.getShowTitle());
        holder.itemPubDateText.setText(item.getPubDate());
    }

    @Override
    public int getItemCount() {
        return rssItemList.size();
    }
}
