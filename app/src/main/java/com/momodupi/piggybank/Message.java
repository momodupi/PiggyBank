package com.momodupi.piggybank;

public class Message {
    private String text;
    private String time;
    private String type;
    private String user;

    public Message(String text, String time, String type, String user) {
        this.text = text;
        this.time = time;
        this.type = type;
        this.user = user;
    }

    public String getText() {
        return this.text;
    }

    public String getTime() {
        return this.time;
    }

    public String getType() {
        return this.type;
    }

    public String getUser() {
        return this.user;
    }
}
