package com.momodupi.piggybank;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OthersFragment extends Fragment {

    private Context context;
    private Robot robot;

    private RecyclerView recyclerView;
    private ChartAdapter chartAdapter;


    OthersFragment(Context context, Robot robot) {
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

        this.showAllOthersTab();

        return rootView;
    }



    private void setOthersTab(ChartAdapter chartAdapter, String h_time) {
        LineChartData lineChartData = new LineChartData();
        PieChartData pieChartData = new PieChartData();
        BarChartData barChartData = new BarChartData();

        String ph_time = "2010-01-01 00:00:00";

        //Log.d("date", h_time + "  " + ph_time);
        List<structure_Database> alldata = robot.getData("ALL", ph_time, h_time);

        //boolean isDateSetNull = (alldata.size() == 0);

        AccountTypes accountTypes = new AccountTypes(this.context);
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
            //piey[accountTypes.findPositionBySring(sdata.getType())] += sdata.getAmount();

            if (!accountTypes.getGeneralType(sdata.getType()).equals("Income")) {
                piey[accountTypes.findPositionBySring(sdata.getType())] += sdata.getAmount();
            }
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

        chartAdapter = new ChartAdapter(this.context);
        recyclerView.setAdapter(chartAdapter);
        setOthersTab(chartAdapter, robot.getCurrentTime());
    }
}
