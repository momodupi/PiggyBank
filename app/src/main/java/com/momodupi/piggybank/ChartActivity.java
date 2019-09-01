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
import java.util.Locale;

public class ChartActivity extends AppCompatActivity {

    private TabLayout chartTabLayout;
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

        robot = new Robot(this, DatabaseHelper.BOOKNAME);

        chartTabLayout = findViewById(R.id.charttab);
        recyclerView = findViewById(R.id.chartframe);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chartAdapter);

        //historytime = robot.getBotHistoryTime();
        this.showAllMonthTab();


        //setMonthTab(chartAdapter, robot.getCurrentTime());
        chartTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("tab", tab.getPosition()+"");

                switch (tab.getPosition()) {
                    case 0:
                        //Log.d("menu", "month");
                        chartAdapter.deleteAll();
                        showAllMonthTab();
                        charttype = "month";
                        break;
                    case 1:
                        //Log.d("menu", "year");
                        chartAdapter.deleteAll();
                        showAllYearTab();
                        charttype = "year";
                        break;
                    case 2:
                        //Log.d("menu", "type");
                        chartAdapter.deleteAll();

                        charttype = "type";
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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
        int incomem = 0;

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
                        + String.format(Locale.getDefault(), "%02d", daysInMonth) + " 23:59:59";
                liney = new float[daysInMonth+1];
                linex = new int[daysInMonth+1];

                for (int cnt = 1; cnt<daysInMonth+1; cnt++) {
                    linex[cnt] = cnt;
                }
            }

            //Log.d("date", h_time + "  " + ph_time);
            List<structure_Database> alldata = robot.getData("ALL", ph_time, h_time);

            if (alldata.size() == 0) {
                isDateSetNull = true;
            }
            else {
                isDateSetNull = false;
            }

            AccountTypes accountTypes = new AccountTypes(this);
            for (structure_Database sdata : alldata) {
                day = sdata.getTime().split(" ")[0].split("-")[2];
                if (!accountTypes.getGeneralType(sdata.getType()).equals("Income")) {
                    liney[Integer.parseInt(day)] += sdata.getAmount();
                }
                else {
                    incomem += sdata.getAmount();
                }
            }

            lineChartData.X = linex;
            lineChartData.Y = liney;


            String[] piex = accountTypes.getGeneralTypeString();
            float[] piey = new float[piex.length];

            ArrayList<String> type_index = new ArrayList<String>(Arrays.asList(piex));

            for (structure_Database sdata : alldata) {
                int pos = type_index.indexOf(accountTypes.getGeneralType(sdata.getType()));
                if (pos >= 0 && pos < piex.length) {
                    if (!accountTypes.getGeneralType(sdata.getType()).equals("Income")) {
                        piey[pos] += sdata.getAmount();
                    }
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
        chartData.setTotalIncome(incomem);

        if (!isDateSetNull) {
            chartAdapter.addItem(chartData);
        }

    }

    private void showAllMonthTab() {
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String datetime[] = robot.getCurrentTime().split(" ");
        String ymd[] = datetime[0].split("-");
        //int month = Integer.parseInt(ymd[1]);

        String ph_time;
        for(int month=1; month<Integer.parseInt(ymd[1]); month++) {

            ph_time = ymd[0] + "-" + String.format(Locale.getDefault(), "%02d", month) + "-" + ymd[2] + " " + datetime[1];
            //Log.d("time", ph_time);

            setMonthTab(chartAdapter, ph_time, false);
        }
        ph_time = ymd[0] + "-" + String.format(Locale.getDefault(), "%02d", Integer.parseInt(ymd[1])) + "-" + ymd[2] + " " + datetime[1];
        setMonthTab(chartAdapter, ph_time, true);

        recyclerView.smoothScrollToPosition(chartAdapter.getItemCount());
    }



    private void setYearTab(ChartAdapter chartAdapter, String h_time, boolean isCurrentYear) {
        LineChartData lineChartData = new LineChartData();
        PieChartData pieChartData = new PieChartData();
        BarChartData barChartData = new BarChartData();

        String ph_time = "";
        boolean isDateSetNull = true;
        int incomey = 0;

        try {
            //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //String h_time = robot.getCurrentTime();
            h_time = h_time.split(" ")[0] + " 00:00:00";

            String[] ymd = h_time.split(" ")[0].split("-");
            ph_time = ymd[0]+"-01-01 00:00:00";

            String month = ymd[1];

            float[] liney;
            float[] bary;
            int[] linex;

            if (isCurrentYear) {
                h_time = robot.getCurrentTime();
                liney = new float[Integer.parseInt(month)+1];
                bary = new float[Integer.parseInt(month)+1];
                linex = new int[Integer.parseInt(month)+1];

                for (int cnt = 1; cnt<Integer.parseInt(month)+1; cnt++) {
                    linex[cnt] = cnt;
                }
            }
            else {
                h_time = ymd[0] + "-12-31 23:59:59";
                liney = new float[13];
                bary = new float[13];
                linex = new int[13];

                for (int cnt = 1; cnt<13; cnt++) {
                    linex[cnt] = cnt;
                }
            }

            //Log.d("date", h_time + "  " + ph_time);
            List<structure_Database> alldata = robot.getData("ALL", ph_time, h_time);

            if (alldata.size() == 0) {
                isDateSetNull = true;
            }
            else {
                isDateSetNull = false;
            }

            AccountTypes accountTypes = new AccountTypes(this);
            for (structure_Database sdata : alldata) {
                month = sdata.getTime().split(" ")[0].split("-")[1];
                if (!accountTypes.getGeneralType(sdata.getType()).equals("Income")) {
                    liney[Integer.parseInt(month)] += sdata.getAmount();
                }
                else {
                    incomey += sdata.getAmount();
                    bary[Integer.parseInt(month)] += sdata.getAmount();
                }
            }

            lineChartData.X = linex;
            lineChartData.Y = liney;

            //AccountTypes accountTypes = new AccountTypes(this);
            String[] piex = Arrays.copyOfRange(accountTypes.getGeneralTypeString(), 0, accountTypes.getGeneralTypeString().length-2);
            float[] piey = new float[piex.length];

            ArrayList<String> type_index = new ArrayList<String>(Arrays.asList(piex));

            for (structure_Database sdata : alldata) {
                //Log.d("type", accountTypes.getGeneralType(sdata.getType()));
                int pos = type_index.indexOf(accountTypes.getGeneralType(sdata.getType()));
                if (!accountTypes.getGeneralType(sdata.getType()).equals("Income")) {
                    if (pos >= 0 && pos < piex.length) {
                        piey[pos] += sdata.getAmount();
                    }
                }
            }

            pieChartData.X = piex;
            pieChartData.Y = piey;

            barChartData.X = linex;
            barChartData.Y = bary;

        } catch (Exception e) {
            e.printStackTrace();
        }

        ChartData chartData = new ChartData(lineChartData, pieChartData, barChartData, ph_time, "year");
        chartData.setTotalIncome(incomey);

        if (!isDateSetNull) {
            chartAdapter.addItem(chartData);
        }

    }


    private void showAllYearTab() {
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String datetime[] = robot.getCurrentTime().split(" ");
        String ymd[] = datetime[0].split("-");
        //int month = Integer.parseInt(ymd[1]);

        String ph_time;
        for(int year=2010; year<Integer.parseInt(ymd[0]); year++) {

            ph_time = String.format(Locale.getDefault(), "%4d", year) + "-" + String.format(Locale.getDefault(), "%02d", year) + "-" + ymd[2] + " " + datetime[1];
            //Log.d("time", ph_time);

            setYearTab(chartAdapter, ph_time, false);
        }
        ph_time = ymd[0] + "-" + String.format(Locale.getDefault(), "%02d", Integer.parseInt(ymd[1])) + "-" + ymd[2] + " " + datetime[1];
        setYearTab(chartAdapter, ph_time, true);

        recyclerView.smoothScrollToPosition(chartAdapter.getItemCount());
    }

}
