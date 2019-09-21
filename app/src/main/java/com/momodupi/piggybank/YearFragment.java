package com.momodupi.piggybank;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class YearFragment extends Fragment {

    private Context context;
    private Robot robot;

    private RecyclerView recyclerView;
    private ChartAdapter chartAdapter;


    YearFragment(Context context, Robot robot) {
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

        this.showAllYearTab();

        return rootView;
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

            AccountTypes accountTypes = new AccountTypes(this.context);
            String[] piex = Arrays.copyOfRange(accountTypes.getGeneralTypeString(), 0, accountTypes.getGeneralTypeString().length-1);
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
        chartAdapter = new ChartAdapter(this.context);
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
}
