package com.momodupi.piggybank;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.DashPathEffect;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChartAdapter extends RecyclerView.Adapter {

    List<ChartData> dataSet = new ArrayList<>();
    Context context;

    public ChartAdapter(Context context){
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.chart, parent, false);
        return new ChartViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChartViewHolder hld = (ChartViewHolder) holder;

        LinearLayout.LayoutParams layoutParams;
        layoutParams = (LinearLayout.LayoutParams) hld.lineframe.getLayoutParams();
        layoutParams.height = 0;

        layoutParams = (LinearLayout.LayoutParams) hld.pieframe.getLayoutParams();
        layoutParams.height = 0;

        layoutParams = (LinearLayout.LayoutParams) hld.barframe.getLayoutParams();
        layoutParams.height = 0;


        ChartData itemdata = this.dataSet.get(position);

        switch (itemdata.getCharType()) {
            case "line" :
                this.setLineChart(context, hld, itemdata);
                break;
            case "pie" :
                Log.d("chart type", "I'm pie!");
                this.setPieChart(context, hld, itemdata);
                break;
            case "bar" :
                break;
            default:
                break;
        }


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


    private void setLineChart(Context context, ChartViewHolder hld, ChartData itemdata) {
        List<Entry> output = new ArrayList<>();

        //float max_y = 0;
        float total = 0;

        float x_buf[] = new float[itemdata.getX().length];
        for (int cnt=0; cnt<itemdata.getX().length; cnt++) {
            x_buf[cnt] = Float.parseFloat(itemdata.getX()[cnt]);
        }

        for (int i = 0; i < itemdata.getY().length; i++) {
            output.add(new Entry(x_buf[i], itemdata.getY()[i]));
            /*
            if (itemdata.getY()[i] > max_y) {
                max_y = itemdata.getY()[i];
            }
            */
            total += itemdata.getY()[i];
        }

        LinearLayout.LayoutParams layoutParams;
        layoutParams = (LinearLayout.LayoutParams) hld.lineframe.getLayoutParams();
        layoutParams.height = dpToPx(200);

        switch (itemdata.getTimeType()) {
            case "month" :
                String month = "";
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = simpleDateFormat.parse(itemdata.getTime());

                    month = (String) DateFormat.format("MMM", date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                hld.chartlabel.setText(month);
                break;
            case "year":
                String year = "";
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = simpleDateFormat.parse(itemdata.getTime());

                    year = (String) DateFormat.format("yyyy", date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                hld.chartlabel.setText(year);
                break;
            default:
                hld.chartlabel.setText("");
        }


        String title_str = "Total: " + context.getResources().getString(R.string.moneyunit) + total;
        hld.charttitle.setText(title_str);


        LineDataSet lineDataSet = new LineDataSet(output, "Total");

        lineDataSet.setColor(context.getResources().getColor(R.color.chartlightgreen800));
        //lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setCircleRadius(1);
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleColor(context.getResources().getColor(R.color.chartlightgreen800));
        lineDataSet.setCubicIntensity(0.15f);
        lineDataSet.setValueTextSize(10);
        lineDataSet.setValueTextColor(context.getResources().getColor(R.color.chartlightgreen300));
        //lineDataSet.setDrawValues(false);
        //lineDataSet.setHighLightColor(context.getResources().getColor(R.color.chartcyanA400));
        lineDataSet.setHighlightEnabled(false);

        XAxis xAxis = hld.linechart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(context.getResources().getColor(R.color.colorAccent));
        xAxis.setAxisLineColor(context.getResources().getColor(R.color.colorAccent));
        xAxis.setAxisLineWidth(2f);
        xAxis.setTextSize(6f);
        xAxis.setLabelCount(itemdata.getX().length);
        xAxis.setAxisMinimum(1);
        xAxis.setAxisMaximum(itemdata.getX().length);

        YAxis yAxis = hld.linechart.getAxisLeft();
        yAxis.setDrawGridLines(true);
        yAxis.setTextColor(context.getResources().getColor(R.color.colorAccent));
        yAxis.setEnabled(false);
        yAxis.setDrawAxisLine(false);
        yAxis.setMinWidth(0);
        //yAxis.setMaxWidth(Math.round(max_y));
        yAxis.setGridColor(context.getResources().getColor(R.color.colorAccent));
        //yAxis.enableGridDashedLine(20, 40, 0);
        yAxis.setDrawZeroLine(false);
        //yAxis.setZeroLineWidth(0);
        //yAxis.setZeroLineColor(context.getResources().getColor(R.color.colorPrimary));

        hld.linechart.getAxisRight().setEnabled(false);

        Legend legend = hld.linechart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setTextColor(context.getResources().getColor(R.color.colorAccent));

        Description description = new Description();
        //description.setText("test");
        //description.setTextColor(context.getResources().getColor(R.color.colorAccent));
        description.setEnabled(false);

        hld.linechart.setDescription(description);
        hld.linechart.getAxisRight().setEnabled(false);
        hld.linechart.animateXY(1000, 1000);

        LineData lineData = new LineData(lineDataSet);
        hld.linechart.setData(lineData);
    }


    private void setPieChart(Context context, ChartViewHolder hld, ChartData itemdata) {
        List<PieEntry> output = new ArrayList<>();

        float max_y = 0;
        float total = 0;

        for (int i = 0; i < itemdata.getY().length; i++) {
            output.add(new PieEntry(itemdata.getY()[i], itemdata.getX()[i]));
            Log.d("amount", " "+itemdata.getY()[i]);
        }

        LinearLayout.LayoutParams layoutParams;
        layoutParams = (LinearLayout.LayoutParams) hld.pieframe.getLayoutParams();
        layoutParams.height = dpToPx(200);

        switch (itemdata.getTimeType()) {
            case "month" :
                String month = "";
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = simpleDateFormat.parse(itemdata.getTime());

                    month = (String) DateFormat.format("MMM", date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                hld.chartlabel.setText(month);
                break;
            case "year":
                String year = "";
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = simpleDateFormat.parse(itemdata.getTime());

                    year = (String) DateFormat.format("yyyy", date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                hld.chartlabel.setText(year);
                break;
            default:
                hld.chartlabel.setText("");
        }


        String title_str = "Total: " + context.getResources().getString(R.string.moneyunit) + total;
        hld.charttitle.setText(title_str);


        PieDataSet pieDataSet = new PieDataSet(output, "Types");

        AccountTypes accountTypes = new AccountTypes(context);

        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : accountTypes.getGeneralTypeColor()) {
            colors.add(context.getResources().getColor(c));
        }
        Log.d("color", colors.toString());

        pieDataSet.setColors(colors);

        pieDataSet.setDrawValues(true);
        //pieDataSet.setSelectionShift(10f);
        pieDataSet.setValueTextColor(context.getResources().getColor(R.color.colorAccentLight));
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        pieDataSet.setValueLinePart1Length(0.6f);
        //pieDataSet.setValueLinePart2Length(.50f);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueLineColor(context.getResources().getColor(R.color.colorAccent));

        hld.piechart.animateY(1000);

        hld.piechart.setDrawHoleEnabled(false);
        hld.piechart.setEntryLabelColor(context.getResources().getColor(R.color.colorAccentLight));
        //hld.piechart.setDrawEntryLabels(false);
        hld.piechart.setEntryLabelTextSize(4f);

        Legend legend = hld.piechart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        //legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setTextColor(context.getResources().getColor(R.color.colorAccent));

        Description description = new Description();
        description.setEnabled(false);
        hld.piechart.setDescription(description);

        PieData pieData = new PieData(pieDataSet);
        hld.piechart.setData(pieData);
    }


    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

}



class ChartViewHolder extends RecyclerView.ViewHolder {
    public LineChart linechart;
    public PieChart piechart;
    public BarChart barchart;
    public TextView chartlabel;
    public TextView charttitle;

    public LinearLayout lineframe;
    public LinearLayout pieframe;
    public LinearLayout barframe;


    public ChartViewHolder(View itemView) {
        super(itemView);
        chartlabel = itemView.findViewById(R.id.chartlabel);
        charttitle = itemView.findViewById(R.id.charttitle);

        linechart = itemView.findViewById(R.id.linechart);
        piechart = itemView.findViewById(R.id.piechart);
        barchart = itemView.findViewById(R.id.barchart);

        lineframe = itemView.findViewById(R.id.lineframe);
        pieframe = itemView.findViewById(R.id.pieframe);
        barframe = itemView.findViewById(R.id.barframe);
    }
}