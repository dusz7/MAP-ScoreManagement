package com.hitiot.dusz7.mtdex.ex3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hitiot.dusz7.mtdex.R;

import java.util.List;

/**
 * 自定义ListView适配器类
 * Created by dusz7 on 2017/6/22.
 */

public class MsgAdapter extends ArrayAdapter<Msg> {
    private int resourceId;

    public MsgAdapter(Context context, int textViewResourceId, List<Msg> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Msg msg = getItem(position);
        View view;
        ViewHolder holder;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            holder = new ViewHolder();
            holder.leftLayout = (LinearLayout)view.findViewById(R.id.left_layout);
            holder.rightLayout = (LinearLayout)view.findViewById(R.id.right_layout);
            holder.leftMsg = (TextView)view.findViewById(R.id.left_msg);
            holder.rightMsg = (TextView)view.findViewById(R.id.right_msg);
            view.setTag(holder);
        }else {
            view = convertView;
            holder = (ViewHolder)view.getTag();
        }
        if(msg.getType() == Msg.TYPE_RECEIVED) {
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
        } else if (msg.getType() == Msg.TYPE_SENT) {
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
        }
        return view;
    }


    class ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
    }

}
