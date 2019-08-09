package com.momodupi.piggybank;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private String text;
    private String time;
    private String type;
    private String user; // is this message sent by us?

    public Message(String text, String time, String type, String user) {
        this.text = text;
        this.time = time;
        this.type = type;
        this.user = user;
    }

    public String getText() {
        /**/
        switch (this.user) {
            case "master": {
                return this.type + ": $" + this.text;
            }
            case "bot": {
                return this.text;
            }
            case "date": {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try {
                    Date transdate = simpleDateFormat.parse(this.time);
                    simpleDateFormat = new SimpleDateFormat("MMM dd");
                    this.text = simpleDateFormat.format(transdate);
                    Log.d("time", this.text + "   " + this.time);
                }  catch (Exception e) {
                    Log.d("time", "Bug!");
                }

                return this.text;
            }
            default: {
                return this.text;
            }
        }
    }

    public String getTime() {
        String datetime[] = this.time.split(" ");
        String time_s[] = datetime[1].split(":");
        return time_s[0]+":"+time_s[1];
    }

    public String getType() {
        return this.type;
    }

    public String getUser() {
        return this.user;
    }
}
