package com.momodupi.piggybank;

public class Message {
    private String text;
    private String time;
    private String type;
    private boolean belongsToCurrentUser; // is this message sent by us?

    public Message(String text, String time, String type, boolean belongsToCurrentUser) {
        this.text = text;
        this.time = time;
        this.type = type;
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    public String getText() {
        if (belongsToCurrentUser) {
            return type + ": $" + text;
        }
        else {
            return "Got it!";
        }
    }

    public String getTime() {
        String datetime[] = time.split(" ");
        String time_s[] = datetime[1].split("-");
        return time_s[0]+":"+time_s[1];
    }

    public  String getType() {
        return type;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }
}