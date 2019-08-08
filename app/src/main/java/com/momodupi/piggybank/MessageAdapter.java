package com.momodupi.piggybank;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends BaseAdapter {

    List<Message> messages = new ArrayList<Message>();
    Context context;

    public MessageAdapter(Context context) {
        this.context = context;
    }

    public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged(); // to render the list we need to notify
    }

    public void addtotop(Message message) {
        this.messages.add(0, message);
        notifyDataSetChanged(); // to render the list we need to notify
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(i);

        //a chat bubble on right
        if (message.isBelongsToCurrentUser()) {
            convertView = messageInflater.inflate(R.layout.host_msg, null);
            holder.msg = (TextView) convertView.findViewById(R.id.host_msg);
            holder.time = (TextView) convertView.findViewById(R.id.host_time);
            convertView.setTag(holder);
            holder.msg.setText(message.getText());
            holder.time.setText(message.getTime());
        }
        // a chat bubble on left
        else {
            convertView = messageInflater.inflate(R.layout.bot_msg, null);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.msg = (TextView) convertView.findViewById(R.id.bot_msg);
            convertView.setTag(holder);

            holder.name.setText(message.getType());
            holder.msg.setText(message.getText());

            AccountTypes accountTypes = new AccountTypes();
            holder.avatar.setImageResource(accountTypes.findIconbySring(message.getType()));
        }

        return convertView;
    }
}




class MessageViewHolder {
    public ImageView avatar;
    public TextView time;
    public TextView name;
    public TextView msg;
}
