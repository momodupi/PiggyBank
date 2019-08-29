package com.momodupi.piggybank;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
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
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ChartAdapter chartAdapter;
    private Robot robot;

    private String charttype = "month";
    private String historytime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);


        chartAdapter = new ChartAdapter(this);
        layoutManager = new LinearLayoutManager(this);


        robot = MainActivity.robot;


        recyclerView = findViewById(R.id.chartframe);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chartAdapter);

        //historytime = robot.getBotHistoryTime();
        this.showAllMonthTab();


        //setMonthTab(chartAdapter, robot.getCurrentTime());


        Toolbar toolbar = findViewById(R.id.toolbar_chart);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chartmain, menu);
        return true;
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
            case R.id.chart_month:
                Log.d("menu", "month");
                chartAdapter.deleteAll();

                this.showAllMonthTab();

                charttype = "month";
                return true;

            case R.id.chart_year:
                Log.d("menu", "year");
                chartAdapter.deleteAll();

                charttype = "year";
                return true;

            case R.id.chart_type:
                Log.d("menu", "type");
                chartAdapter.deleteAll();

                charttype = "type";
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChartActivity.this, MainActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        ChartActivity.this.finish();

        overridePendingTransition(R.anim.leftin, R.anim.rightout);
    }



    private void setMonthTab(ChartAdapter chartAdapter, String h_time, boolean isCurrentMonth) {
        LineChartData lineChartData = new LineChartData();
        PieChartData pieChartData = new PieChartData();
        BarChartData barChartData = new BarChartData();

        String ph_time = "";
        boolean isDateSetNull = true;

        try {
            //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //String h_time = robot.getCurrentTime();
            h_time = h_time.split(" ")[0] + " 00:00:00";

            String[] ymd = h_time.split(" ")[0].split("-");
            ph_time = ymd[0]+"-"+ymd[1]+"-01 00:00:00";

            String day = ymd[2];

            int daysInMonth;
            if (Build.VERSION.SDK_INT >= 26) {
                YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(ymd[0]), Integer.parseInt(ymd[1]));
                daysInMonth = yearMonthObject.lengthOfMonth();
            }
            else {
                Calendar cal = new GregorianCalendar(Integer.parseInt(ymd[0]), Integer.parseInt(ymd[1]), Integer.parseInt(ymd[2]));
                daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            }


            float[] liney;
            int[] linex;

            if (isCurrentMonth) {
                h_time = robot.getCurrentTime();
                liney = new float[Integer.parseInt(day)+1];
                linex = new int[Integer.parseInt(day)+1];

                for (int cnt = 1; cnt<Integer.parseInt(day)+1; cnt++) {
                    linex[cnt] = cnt;
                }
            }
            else {
                h_time = ymd[0] + "-" + ymd[1] + "-"
                        + String.format("%02d", daysInMonth) + " 23:59:59";
                liney = new float[daysInMonth+1];
                linex = new int[daysInMonth+1];

                for (int cnt = 1; cnt<daysInMonth+1; cnt++) {
                    linex[cnt] = cnt;
                }
            }

            Log.d("date", h_time + "  " + ph_time);
            List<structure_Database> alldata = robot.getData("ALL", ph_time, h_time);

            if (alldata.size() == 0) {
                isDateSetNull = true;
            }
            else {
                isDateSetNull = false;
            }

            for (structure_Database sdata : alldata) {
                day = sdata.getTime().split(" ")[0].split("-")[2];
                liney[Integer.parseInt(day)] += sdata.getAmount();
            }

            lineChartData.X = linex;
            lineChartData.Y = liney;

            AccountTypes accountTypes = new AccountTypes(this);
            String[] piex = accountTypes.getGeneralTypeString();
            float[] piey = new float[piex.length];

            ArrayList<String> type_index = new ArrayList<String>(Arrays.asList(piex));

            for (structure_Database sdata : alldata) {
                int pos = type_index.indexOf(accountTypes.getGeneralType(sdata.getType()));
                if (pos >= 0 && pos < piex.length) {
                    piey[pos] += sdata.getAmount();
                }
            }

            pieChartData.X = piex;
            pieChartData.Y = piey;

            barChartData.X = linex;
            barChartData.Y = liney;

        } catch (Exception e) {
            e.printStackTrace();
        }

        ChartData chartData = new ChartData(lineChartData, pieChartData, barChartData, ph_time, "month");

        if (!isDateSetNull) {
            chartAdapter.addItemToTop(chartData);
        }

    }

    private void showAllMonthTab() {
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String datetime[] = robot.getBotHistoryTime().split(" ");
        String ymd[] = datetime[0].split("-");
        int month = Integer.parseInt(ymd[1]);

        String ph_time = ymd[0] + "-" + String.format("%02d", month) + "-" + ymd[2] + " " + datetime[1];
        setMonthTab(chartAdapter, ph_time, true);

        while (month > 1) {
            month --;
            ph_time = ymd[0] + "-" + String.format("%02d", month) + "-" + ymd[2] + " " + datetime[1];
            //Log.d("time", ph_time);

            setMonthTab(chartAdapter, ph_time, false);
        }
    }

}
