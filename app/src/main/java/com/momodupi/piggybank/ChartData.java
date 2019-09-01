package com.momodupi.piggybank;

import java.lang.reflect.Array;

public class ChartData {
    //private String[] X;
    //private float[] Y;
    private LineChartData lineChartData;
    private PieChartData pieChartData;
    private BarChartData barChartData;

    private String time;

    private String timeType;

    private float income = 0;


    public ChartData(LineChartData l, PieChartData p, BarChartData b, String time, String timetype) {
        this.lineChartData = l;
        this.pieChartData = p;
        this.barChartData = b;
        this.time = time;
        this.timeType = timetype;
    }

    public void setTotalIncome(int income) {
        this.income = income;
    }

    public float getTotalIncome() {
        return this.income;
    }

    public int[] getLineX() {
        return this.lineChartData.X;
    }

    public float[] getLineY() {
        return this.lineChartData.Y;
    }

    public String[] getPieX() {
        return this.pieChartData.X;
    }

    public float[] getPieY() {
        return this.pieChartData.Y;
    }

    public int[] getBarX() {
        return this.barChartData.X;
    }

    public float[] getBarY() {
        return this.barChartData.Y;
    }

    public String getTime() {
        return this.time;
    }


    public String getTimeType() {
        return this.timeType;
    }
}


class LineChartData {
    public int[] X;
    public float[] Y;
}

class PieChartData {
    public String[] X;
    public float[] Y;
}

class BarChartData {
    public int[] X;
    public float[] Y;
}