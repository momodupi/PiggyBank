package com.momodupi.piggybank;

public class ChartData {
    private String[] X;
    private float[] Y;

    private String time;

    private String charType;
    private String timeType;

    public ChartData(String[] x, float[] y, String time, String chartype, String timetype) {
        this.X = x;
        this.Y = y;
        this.time = time;
        this.charType = chartype;
        this.timeType = timetype;
    }

    public String[] getX() {
        return this.X;
    }

    public float[] getY() {
        return this.Y;
    }

    public String getTime() {
        return this.time;
    }

    public String getCharType() {
        return  this.charType;
    }

    public String getTimeType() {
        return this.timeType;
    }
}