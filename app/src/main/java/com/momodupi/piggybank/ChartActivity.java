package com.momodupi.piggybank;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ChartAdapter chartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);


        chartAdapter = new ChartAdapter(this);
        recyclerView = findViewById(R.id.chartframe);
        layoutManager = new LinearLayoutManager(this);

        Robot robot = MainActivity.robot;



        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String h_time = robot.getCurrentTime();
            h_time = h_time.split(" ")[0] + " 00:00:00";

            Date date = simpleDateFormat.parse(h_time);
            //Calendar calendar = Calendar.getInstance();
            //calendar.setTime(date);
            //calendar.add(Calendar.MONTH, 0);
            String[] ymd = h_time.split(" ")[0].split("-");
            String ph_time = ymd[0]+"-"+ymd[1]+"-01 00:00:00";

            h_time = robot.getCurrentTime();
            Log.d("date", h_time + "  " + ph_time);
            List<structure_Database> alldata = robot.getData("ALL", ph_time, h_time);

            String day = ymd[2];
            Log.d("time", ph_time);

            float[] y = new float[Integer.parseInt(day)+1];
            float[] x = new float[Integer.parseInt(day)+1];

            for (int cnt = 0; cnt<Integer.parseInt(day)+1; cnt++) {
                x[cnt] = cnt+1;
            }

            for (structure_Database sdata : alldata) {

                date = simpleDateFormat.parse(sdata.getTime());
                day = (String) DateFormat.format("dd", date);

                y[Integer.parseInt(day)] += sdata.getAmount();
                Log.d("y", String.valueOf(y[Integer.parseInt(day)]));
            }

            ChartData chartData = new ChartData(x,y, ph_time, "Line");
            chartAdapter.addItem(chartData);
        } catch (Exception e) {
            e.printStackTrace();
        }




        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chartAdapter);


        Toolbar toolbar = findViewById(R.id.toolbar_chart);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(ChartActivity.this, MainActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                ChartActivity.this.finish();

                overridePendingTransition(R.anim.leftin, R.anim.rightout);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
