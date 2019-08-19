package com.momodupi.piggybank;

public class ChartData {
    private float[] X;
    private float[] Y;

    public ChartData(float[] x, float[] y) {
        this.X = x;
        this.Y = y;
    }

    public float[] getX() {
        return this.X;
    }

    public float[] getY() {
        return this.Y;
    }
}
