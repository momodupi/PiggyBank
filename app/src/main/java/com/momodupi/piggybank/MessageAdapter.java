package com.momodupi.piggybank;

import android.app.Activity;
import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends BaseAdapter {

    List<Message> messages = new ArrayList<Message>();
    Context context;

    MessageAdapter(Context context) {
        this.context = context;
    }

    void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged();
    }

    void addtotop(Message message) {
        this.messages.add(0, message);
        //notifyDataSetChanged();
    }

    void addtoppostition(Message message, int pos) {
        this.messages.add(pos, message);
        //notifyDataSetChanged();
    }

    void remove(Message message) {
        int pos = messages.indexOf(message);
        this.messages.remove(pos);

        if (this.messages.get(pos).getUser().equals("bot")) {
            this.messages.remove(pos);
        }

        notifyDataSetChanged();
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

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(i);

        String output_str;
        //a chat bubble on right
        switch (message.getUser()) {
            case "master": {
                convertView = messageInflater.inflate(R.layout.host_msg, null);
                holder.msg = convertView.findViewById(R.id.host_msg);
                holder.time = convertView.findViewById(R.id.host_time);
                convertView.setTag(holder);

                output_str = message.getType() + ": "
                        + context.getResources().getString(R.string.moneyunit) + message.getText();
                holder.msg.setText(output_str);

                String[] time_s = message.getTime().split(" ")[1].split(":");
                output_str = time_s[0]+":"+time_s[1];
                holder.time.setText(output_str);

                holder.msg.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Log.d("press", "true");
                        return false;
                    }
                });
            }
            break;
            case "bot": {
                convertView = messageInflater.inflate(R.layout.bot_msg, null);
                holder.avatar = convertView.findViewById(R.id.avatar);
                holder.name = convertView.findViewById(R.id.name);
                holder.msg = convertView.findViewById(R.id.bot_msg);
                convertView.setTag(holder);

                holder.name.setText(message.getType());
                holder.msg.setText(message.getText());

                AccountTypes accountTypes = new AccountTypes(context);
                if (message.getType().equals("ALL")) {
                    holder.avatar.setImageResource(R.mipmap.moneybank);
                }
                else {
                    holder.avatar.setImageResource(accountTypes.findIconbySring(message.getType()));
                }
            }
            break;
            case "date": {
                convertView = messageInflater.inflate(R.layout.date_msg, null);
                holder.time = convertView.findViewById(R.id.mid_date);
                convertView.setTag(holder);
                //holder.time.setText(message.getText());

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                try {
                    Date transdate = simpleDateFormat.parse(message.getTime());
                    simpleDateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
                    output_str = simpleDateFormat.format(transdate);
                    holder.time.setText(output_str);
                }  catch (Exception e) {
                    Log.d("time", "Bug!");
                }
            }
            break;
            default: {

            }
        }
        return convertView;
    }
}




class MessageViewHolder {
    ImageView avatar;
    TextView time;
    TextView name;
    TextView msg;
}
