package com.momodupi.piggybank;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class ChartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private ChartAdapter chartAdapter;
    private Robot robot;

    //private String charttype = "month";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        TabLayout chartTabLayout;
        RecyclerView.LayoutManager layoutManager;

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
                        //chartAdapter.deleteAll();
                        showAllMonthTab();
                        break;
                    case 1:
                        //chartAdapter.deleteAll();
                        showAllYearTab();
                        break;
                    case 2:
                        //chartAdapter.deleteAll();
                        showAllOthersTab();
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
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(ChartActivity.this, MainActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            ChartActivity.this.finish();

            overridePendingTransition(R.anim.leftin, R.anim.rightout);
            return true;
        }
        else {
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

            isDateSetNull = (alldata.size() == 0);

            AccountTypes accountTypes = new AccountTypes(this);
            String[] piex = accountTypes.getGeneralTypeString();
            float[] piey = new float[piex.length];

            ArrayList<String> type_index = new ArrayList<>(Arrays.asList(piex));

            for (structure_Database sdata : alldata) {
                day = sdata.getTime().split(" ")[0].split("-")[2];
                if (!accountTypes.getGeneralType(sdata.getType()).equals("Income")) {
                    liney[Integer.parseInt(day)] += sdata.getAmount();
                }
                else {
                    incomem += sdata.getAmount();
                }

                int pos = type_index.indexOf(accountTypes.getGeneralType(sdata.getType()));
                if (pos >= 0 && pos < piex.length) {
                    if (!accountTypes.getGeneralType(sdata.getType()).equals("Income")) {
                        piey[pos] += sdata.getAmount();
                    }
                }
            }

            lineChartData.X = linex;
            lineChartData.Y = liney;

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
        chartAdapter = new ChartAdapter(this);
        recyclerView.setAdapter(chartAdapter);
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String[] datetime = robot.getCurrentTime().split(" ");
        String[] ymd = datetime[0].split("-");
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

            isDateSetNull = (alldata.size() == 0);

            AccountTypes accountTypes = new AccountTypes(this);
            String[] piex = Arrays.copyOfRange(accountTypes.getGeneralTypeString(), 0, accountTypes.getGeneralTypeString().length-2);
            float[] piey = new float[piex.length];
            ArrayList<String> type_index = new ArrayList<>(Arrays.asList(piex));

            for (structure_Database sdata : alldata) {
                month = sdata.getTime().split(" ")[0].split("-")[1];
                if (!accountTypes.getGeneralType(sdata.getType()).equals("Income")) {
                    liney[Integer.parseInt(month)] += sdata.getAmount();
                }
                else {
                    incomey += sdata.getAmount();
                    bary[Integer.parseInt(month)] += sdata.getAmount();
                }

                int pos = type_index.indexOf(accountTypes.getGeneralType(sdata.getType()));
                if (!accountTypes.getGeneralType(sdata.getType()).equals("Income")) {
                    if (pos >= 0 && pos < piex.length) {
                        piey[pos] += sdata.getAmount();
                    }
                }
            }

            lineChartData.X = linex;
            lineChartData.Y = liney;

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
        chartAdapter = new ChartAdapter(this);
        recyclerView.setAdapter(chartAdapter);
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String[] datetime = robot.getCurrentTime().split(" ");
        String[] ymd = datetime[0].split("-");
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


    private void setOthersTab(ChartAdapter chartAdapter, String h_time) {
        LineChartData lineChartData = new LineChartData();
        PieChartData pieChartData = new PieChartData();
        BarChartData barChartData = new BarChartData();

        String ph_time = "2010-01-01 00:00:00";

        //Log.d("date", h_time + "  " + ph_time);
        List<structure_Database> alldata = robot.getData("ALL", ph_time, h_time);

        boolean isDateSetNull = (alldata.size() == 0);

        AccountTypes accountTypes = new AccountTypes(this);
        //String type_string[] = new String[accountTypes.getTypeString().length];
        int[] linex = new int[24];
        float[] liney = new float[24];
        String[] piex = accountTypes.getTypeString();
        float[] piey = new float[accountTypes.getTypeString().length];
        int[] barx = new int[1];
        float[] bary = new float[1];

        for (int cnt=0; cnt<24; cnt++) {
            linex[cnt] = cnt;
        }

        for (structure_Database sdata : alldata) {
            String hour = sdata.getTime().split(" ")[1].split(":")[0];
            //bary[accountTypes.findPositionbySring(sdata.getType())] += sdata.getAmount();
            liney[Integer.valueOf(hour)] += sdata.getAmount();
            piey[accountTypes.findPositionbySring(sdata.getType())] += sdata.getAmount();
        }

        lineChartData.X = linex;
        lineChartData.Y = liney;

        pieChartData.X = piex;
        pieChartData.Y = piey;

        barChartData.X = barx;
        barChartData.Y = bary;

        ChartData chartData = new ChartData(lineChartData, pieChartData, barChartData, ph_time, "others");

        chartAdapter.addItem(chartData);
    }

    private void showAllOthersTab() {

        chartAdapter = new ChartAdapter(this);
        recyclerView.setAdapter(chartAdapter);
        setOthersTab(chartAdapter, robot.getCurrentTime());
    }
}
