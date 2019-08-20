package com.momodupi.piggybank;

public class ChartData {
    private float[] X;
    private float[] Y;

    private String time;

    private String chartype;

    public ChartData(float[] x, float[] y, String time, String chartype) {
        this.X = x;
        this.Y = y;
        this.time = time;
        this.chartype = chartype;
    }

    public float[] getX() {
        return this.X;
    }

    public float[] getY() {
        return this.Y;
    }

    public String getTime() {
        return this.time;
    }

    public String getChartype() {
        return  this.chartype;
    }
}