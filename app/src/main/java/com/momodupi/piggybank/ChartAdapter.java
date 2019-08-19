package com.momodupi.piggybank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class ChartAdapter extends RecyclerView.Adapter {

    List<ChartData> dataSet = new ArrayList<>();
    Context context;

    public ChartAdapter(Context context){
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.chart, parent, false);
        return new ChartViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChartViewHolder hld = (ChartViewHolder) holder;
        ChartData itemdata = this.dataSet.get(position);


        List<Entry> output = new ArrayList<>();

        for (int i = 0; i < itemdata.getY().length; i++) {
            output.add(new Entry(itemdata.getX()[i], itemdata.getY()[i]));
        }

        LineDataSet lineDataSet = new LineDataSet(output, "value");

        lineDataSet.setColor(context.getResources().getColor(R.color.colorAccent));
        //lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setCircleHoleRadius(4);
        lineDataSet.setCircleColor(context.getResources().getColor(R.color.colorAccent));
        lineDataSet.setCubicIntensity(0.15f);
        lineDataSet.setValueTextColor(context.getResources().getColor(R.color.colorAccent));

        XAxis xAxis = hld.linechart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(context.getResources().getColor(R.color.colorAccent));
        //xAxis.setLabelCount(itemdata.getX().length);
        xAxis.setAxisMinimum(itemdata.getX()[0]);
        xAxis.setAxisMaximum(itemdata.getX()[itemdata.getX().length-1]);

        YAxis yAxis = hld.linechart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setTextColor(context.getResources().getColor(R.color.colorAccent));

        Legend legend = hld.linechart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setTextColor(context.getResources().getColor(R.color.colorAccent));

        Description description = new Description();
        description.setText("test");
        description.setTextColor(context.getResources().getColor(R.color.colorAccent));
        description.setEnabled(false);

        hld.linechart.setDescription(description);
        hld.linechart.getAxisRight().setEnabled(false);
        hld.linechart.animateXY(300, 300);


        LineData lineData = new LineData(lineDataSet);
        hld.linechart.setData(lineData);

    }

    @Override
    public int getItemCount() {
        return this.dataSet.size();
    }

    public void refreshItems(List<ChartData> items) {
        dataSet.clear();
        dataSet.addAll(items);
        notifyDataSetChanged();
    }

    public void addItems(List<ChartData> items) {
        dataSet.addAll(items);
    }
    public void addItem(ChartData item){
        dataSet.add(item);
    }
    public void deleteItem(int position) {
        dataSet.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, dataSet.size() - 1);
    }
}

class ChartViewHolder extends RecyclerView.ViewHolder {
    public LineChart linechart;


    public ChartViewHolder(View itemView) {
        super(itemView);
        linechart = itemView.findViewById(R.id.chartline);
    }
}