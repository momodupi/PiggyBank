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


    ChartData(LineChartData l, PieChartData p, BarChartData b, String time, String timetype) {
        this.lineChartData = l;
        this.pieChartData = p;
        this.barChartData = b;
        this.time = time;
        this.timeType = timetype;
    }

    void setTotalIncome(int income) {
        this.income = income;
    }

    float getTotalIncome() {
        return this.income;
    }

    int[] getLineX() {
        return this.lineChartData.X;
    }

    float[] getLineY() {
        return this.lineChartData.Y;
    }

    String[] getPieX() {
        return this.pieChartData.X;
    }

    float[] getPieY() {
        return this.pieChartData.Y;
    }

    public int[] getBarX() {
        return this.barChartData.X;
    }

    float[] getBarY() {
        return this.barChartData.Y;
    }

    String getTime() {
        return this.time;
    }


    String getTimeType() {
        return this.timeType;
    }
}


class LineChartData {
    int[] X;
    float[] Y;
}

class PieChartData {
    String[] X;
    float[] Y;
}

class BarChartData {
    int[] X;
    float[] Y;
}