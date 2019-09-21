package com.momodupi.piggybank;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MonthFragment extends Fragment {

    private Context context;
    private Robot robot;

    private RecyclerView recyclerView;
    private ChartAdapter chartAdapter;


    MonthFragment(Context context, Robot robot) {
        this.context = context;
        this.robot = robot;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chart_frame, container, false);
        recyclerView = rootView.findViewById(R.id.chartframe);

        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(this.context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chartAdapter);

        this.showAllMonthTab();

        return rootView;
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

            AccountTypes accountTypes = new AccountTypes(this.context);
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
        chartAdapter = new ChartAdapter(this.context);
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

}
