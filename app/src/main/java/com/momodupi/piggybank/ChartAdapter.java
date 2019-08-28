package com.momodupi.piggybank;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.os.Build;
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
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.math.BigDecimal;
import java.text.BreakIterator;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

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
        layoutParams = (LinearLayout.LayoutParams) hld.combinedframe.getLayoutParams();
        layoutParams.height = 0;

        layoutParams = (LinearLayout.LayoutParams) hld.pieframe.getLayoutParams();
        layoutParams.height = 0;

        layoutParams = (LinearLayout.LayoutParams) hld.barframe.getLayoutParams();
        layoutParams.height = 0;


        ChartData itemdata = this.dataSet.get(position);



        switch (itemdata.getTimeType()) {
            case "month":
                layoutParams = (LinearLayout.LayoutParams) hld.combinedframe.getLayoutParams();
                layoutParams.height = dpToPx(200);
                layoutParams = (LinearLayout.LayoutParams) hld.pieframe.getLayoutParams();
                layoutParams.height = dpToPx(200);
                layoutParams = (LinearLayout.LayoutParams) hld.barframe.getLayoutParams();
                layoutParams.height = 0;


                String month = "";
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = simpleDateFormat.parse(itemdata.getTime());

                    month = (String) DateFormat.format("MMM", date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                hld.chartlabel.setText(month);

                this.setCombinedChart(context, hld, itemdata);
                this.setPieChart(context, hld, itemdata);


                break;
            case "year":
                layoutParams = (LinearLayout.LayoutParams) hld.combinedframe.getLayoutParams();
                layoutParams.height = 0;
                layoutParams = (LinearLayout.LayoutParams) hld.pieframe.getLayoutParams();
                layoutParams.height = dpToPx(200);
                layoutParams = (LinearLayout.LayoutParams) hld.barframe.getLayoutParams();
                layoutParams.height = 200;

                String year = "";
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = simpleDateFormat.parse(itemdata.getTime());

                    year = (String) DateFormat.format("yyyy", date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                hld.chartlabel.setText(year);

                this.setCombinedChart(context, hld, itemdata);
                this.setPieChart(context, hld, itemdata);
                break;
            case "type":
                hld.chartlabel.setText("");

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


    private void setCombinedChart(Context context, ChartViewHolder hld, ChartData itemdata) {
        List<Entry> lineoutput = new ArrayList<>();
        List<BarEntry> baroutput = new ArrayList<>();

        float total = 0;

        int x_buf[] = new int[itemdata.getLineX().length];
        for (int cnt=0; cnt<itemdata.getLineX().length; cnt++) {
            x_buf[cnt] = itemdata.getLineX()[cnt];
        }

        int daysInMonth = 30;
        String[] ymd = itemdata.getTime().split(" ")[0].split("-");

        if (Build.VERSION.SDK_INT >= 26) {
            YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(ymd[0]), Integer.parseInt(ymd[1]));
            daysInMonth = yearMonthObject.lengthOfMonth();
        }
        else {
            Calendar mycal = new GregorianCalendar(Integer.parseInt(ymd[0]), Integer.parseInt(ymd[1]), Integer.parseInt(ymd[2]));
            daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        for (int i = 0; i < daysInMonth; i++) {
            if (i < itemdata.getLineY().length) {
                total += itemdata.getLineY()[i];
                lineoutput.add(new Entry(x_buf[i], total));
            }
        }


        String title_str = "Total: " + context.getResources().getString(R.string.moneyunit) + String.format("%.2f", total);
        hld.charttitle.setText(title_str);


        LineDataSet lineDataSet = new LineDataSet(lineoutput, context.getResources().getString(R.string.chartline));

        lineDataSet.setColor(context.getResources().getColor(R.color.chartorange300));
        //lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawCircleHole(false);
        //lineDataSet.setCircleRadius(1);
        lineDataSet.setLineWidth(2);
        //lineDataSet.setCircleColor(context.getResources().getColor(R.color.chartlightgreen800));
        lineDataSet.setCubicIntensity(0.15f);
        //lineDataSet.setValueTextSize(10);
        //lineDataSet.setValueTextColor(context.getResources().getColor(R.color.chartlightgreen300));
        lineDataSet.setDrawValues(false);
        //lineDataSet.setHighLightColor(context.getResources().getColor(R.color.chartcyanA400));
        lineDataSet.setHighlightEnabled(false);


        for (int i = 0; i < itemdata.getLineY().length; i++) {
            if (i < itemdata.getLineY().length) {
                //total += itemdata.getLineY()[i];
                baroutput.add(new BarEntry(x_buf[i], itemdata.getLineY()[i]));
            }
        }

        BarDataSet barDataSet = new BarDataSet(baroutput, context.getResources().getString(R.string.chartbar));

        barDataSet.setColor(context.getResources().getColor(R.color.chartorange800));
        barDataSet.setBarShadowColor(context.getResources().getColor(R.color.chartorange500));
        //lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        barDataSet.setHighlightEnabled(false);
        barDataSet.setValueTextSize(7f);
        barDataSet.setValueTextColor(context.getResources().getColor(R.color.colorAccentLight));


        XAxis xAxis = hld.combinedchart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(context.getResources().getColor(R.color.colorAccent));
        xAxis.setAxisLineColor(context.getResources().getColor(R.color.colorAccent));
        xAxis.setAxisLineWidth(2f);
        xAxis.setTextSize(6f);
        //xAxis.setLabelCount(itemdata.getLineX().length);
        xAxis.setLabelCount(daysInMonth);
        xAxis.setAxisMinimum(1);
        //xAxis.setAxisMaximum(itemdata.getLineX().length);
        xAxis.setAxisMaximum(daysInMonth);

        YAxis yAxis = hld.combinedchart.getAxisLeft();
        yAxis.setDrawGridLines(true);
        yAxis.setTextColor(context.getResources().getColor(R.color.colorAccent));
        //yAxis.setEnabled(false);
        yAxis.setDrawAxisLine(false);
        yAxis.setMinWidth(0);
        //yAxis.setMaxWidth(Math.round(max_y));
        yAxis.setGridColor(context.getResources().getColor(R.color.colorAccent));
        //yAxis.enableGridDashedLine(20, 40, 0);
        //yAxis.setDrawZeroLine(false);
        //yAxis.setZeroLineWidth(0);
        //yAxis.setZeroLineColor(context.getResources().getColor(R.color.colorPrimary));

        hld.combinedchart.getAxisRight().setEnabled(false);

        Legend legend = hld.combinedchart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setTextColor(context.getResources().getColor(R.color.colorAccent));

        Description description = new Description();
        //description.setText("test");
        //description.setTextColor(context.getResources().getColor(R.color.colorAccent));
        description.setEnabled(false);

        hld.combinedchart.setDescription(description);
        hld.combinedchart.getAxisRight().setEnabled(false);
        hld.combinedchart.animateXY(1000, 1000);
        hld.combinedchart.setTouchEnabled(false);

        LineData lineData = new LineData(lineDataSet);
        BarData barchart = new BarData(barDataSet);

        CombinedData combinedData = new CombinedData();
        combinedData.setData(lineData);
        combinedData.setData(barchart);
        hld.combinedchart.setData(combinedData);
    }


    private void setPieChart(final Context context, final ChartViewHolder hld, ChartData itemdata) {
        List<PieEntry> output = new ArrayList<>();

        for (int i = 0; i < itemdata.getPieY().length; i++) {
            output.add(new PieEntry(itemdata.getPieY()[i], itemdata.getPieX()[i]));
            //Log.d("amount", " "+itemdata.getPieY()[i]);
        }

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

        PieDataSet pieDataSet = new PieDataSet(output, "");

        AccountTypes accountTypes = new AccountTypes(context);

        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : accountTypes.getGeneralTypeColor()) {
            colors.add(context.getResources().getColor(c));
        }
        //Log.d("color", colors.toString());

        pieDataSet.setColors(colors);

        pieDataSet.setDrawValues(false);
        //pieDataSet.setSelectionShift(10f);
        pieDataSet.setValueTextColor(context.getResources().getColor(R.color.colorAccentLight));
        //pieDataSet.setValueTextSize(12f);
        //pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        pieDataSet.setValueLinePart1Length(0.6f);
        //pieDataSet.setValueLinePart2Length(.50f);
        //pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        //pieDataSet.setValueLineColor(context.getResources().getColor(R.color.colorAccent));

        hld.piechart.animateY(1000);

        hld.piechart.setDrawHoleEnabled(true);
        hld.piechart.setHoleColor(context.getResources().getColor(R.color.colorPrimary));
        hld.piechart.setHoleRadius(60);
        hld.piechart.setCenterText("");
        hld.piechart.setCenterTextSize(14);
        hld.piechart.setCenterTextColor(context.getResources().getColor(R.color.colorAccentLight));

        //hld.piechart.setEntryLabelColor(context.getResources().getColor(R.color.colorAccentLight));
        hld.piechart.setDrawEntryLabels(false);
        //hld.piechart.setEntryLabelTextSize(8f);
        hld.piechart.setTouchEnabled(true);

        Legend legend = hld.piechart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        //legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setTextColor(context.getResources().getColor(R.color.colorAccent));

        Description description = new Description();
        description.setEnabled(false);
        hld.piechart.setDescription(description);
        hld.piechart.setExtraOffsets(30, 0, 0, 0);

        PieData pieData = new PieData(pieDataSet);
        hld.piechart.setData(pieData);

        final ChartData getdata = itemdata;
        hld.piechart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry pie_e = (PieEntry) e;
                String cstr = pie_e.getLabel() + "\n"
                        + context.getResources().getString(R.string.moneyunit) + String.format("%.2f", e.getY());
                hld.piechart.setCenterText(cstr);
            }

            @Override
            public void onNothingSelected() {
                hld.piechart.setCenterText("");
            }
        });
    }


    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

}



class ChartViewHolder extends RecyclerView.ViewHolder {
    //public LineChart linechart;
    public CombinedChart combinedchart;
    public PieChart piechart;
    public BarChart barchart;
    public TextView chartlabel;
    public TextView charttitle;

    public LinearLayout combinedframe;
    public LinearLayout pieframe;
    public LinearLayout barframe;


    public ChartViewHolder(View itemView) {
        super(itemView);
        chartlabel = itemView.findViewById(R.id.chartlabel);
        charttitle = itemView.findViewById(R.id.charttitle);

        combinedchart = itemView.findViewById(R.id.combinedchart);
        piechart = itemView.findViewById(R.id.piechart);
        barchart = itemView.findViewById(R.id.barchart);

        combinedframe = itemView.findViewById(R.id.combinedframe);
        pieframe = itemView.findViewById(R.id.pieframe);
        barframe = itemView.findViewById(R.id.barframe);
    }
}

