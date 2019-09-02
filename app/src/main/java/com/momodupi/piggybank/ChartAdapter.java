package com.momodupi.piggybank;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
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
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class ChartAdapter extends RecyclerView.Adapter {

    private List<ChartData> dataSet = new ArrayList<>();
    private Context context;

    ChartAdapter(Context context){
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.context).inflate(R.layout.chart, parent, false);
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
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    Date date = simpleDateFormat.parse(itemdata.getTime());

                    month = (String) DateFormat.format("MMM", date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                hld.chartlabel.setText(month);

                this.setCombinedChart(hld, itemdata);
                this.setPieChart(hld, itemdata);


                break;
            case "year":
                layoutParams = (LinearLayout.LayoutParams) hld.combinedframe.getLayoutParams();
                layoutParams.height = dpToPx(200);
                layoutParams = (LinearLayout.LayoutParams) hld.pieframe.getLayoutParams();
                layoutParams.height = dpToPx(200);
                layoutParams = (LinearLayout.LayoutParams) hld.barframe.getLayoutParams();
                layoutParams.height = 0;

                String year = "";
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    Date date = simpleDateFormat.parse(itemdata.getTime());

                    year = (String) DateFormat.format("yyyy", date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                hld.chartlabel.setText(year);

                this.setCombinedChart(hld, itemdata);
                this.setPieChart(hld, itemdata);
                break;
            case "others":
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
        notifyDataSetChanged();
    }

    void addItem(ChartData item){
        dataSet.add(item);
        notifyDataSetChanged();
    }

    public void addItemToTop(ChartData item){
        dataSet.add(0, item);
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        dataSet.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, dataSet.size() - 1);
    }

    void deleteAll() {
        dataSet.clear();
        notifyDataSetChanged();
    }


    private void setCombinedChart(ChartViewHolder hld, ChartData itemdata) {
        List<Entry> lineoutput = new ArrayList<>();
        List<BarEntry> baroutput = new ArrayList<>();
        List<BarEntry> baroutput_income = new ArrayList<>();


        float income = itemdata.getTotalIncome();
        float total = 0;

        if (itemdata.getLineX() == null) {
            return;
        }

        int[] x_buf = new int[itemdata.getLineX().length];
        for (int cnt=0; cnt<itemdata.getLineX().length; cnt++) {
            x_buf[cnt] = itemdata.getLineX()[cnt];
        }

        int Xcount = 0;
        float XAxisTextSize = 0f;
        float BarTextSize = 0f;

        if (itemdata.getTimeType().equals("month")) {
            //int daysInMonth;
            String[] ymd = itemdata.getTime().split(" ")[0].split("-");

            if (Build.VERSION.SDK_INT >= 26) {
                YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(ymd[0]), Integer.parseInt(ymd[1]));
                Xcount = yearMonthObject.lengthOfMonth();
            }
            else {
                Calendar cal = new GregorianCalendar(Integer.parseInt(ymd[0]), Integer.parseInt(ymd[1]), Integer.parseInt(ymd[2]));
                Xcount = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            }

            for (int i = 0; i < Xcount+1; i++) {
                if (i < itemdata.getLineY().length) {
                    total += itemdata.getLineY()[i];
                    lineoutput.add(new Entry(x_buf[i], total));
                }
            }

            XAxisTextSize = 6f;
            BarTextSize = 7f;
        }
        else if (itemdata.getTimeType().equals("year")) {
            //String[] ymd = itemdata.getTime().split(" ")[0].split("-");
            Xcount = 12;

            for (int i = 0; i < Xcount+1; i++) {
                if (i < itemdata.getLineY().length) {
                    total += itemdata.getLineY()[i];
                    lineoutput.add(new Entry(x_buf[i], total));
                }
            }

            XAxisTextSize = 10f;
            BarTextSize = 7f;
        }

/*
        String title_str = context.getResources().getString(R.string.charttotal) + "    " + context.getResources().getString(R.string.moneyunit)
                + String.format(Locale.getDefault(), "%.2f", total);

 */
        String title_str = context.getResources().getString(R.string.chartoutcome) + "\n" + context.getResources().getString(R.string.moneyunit)
                + String.format(Locale.getDefault(), "%.2f", total);
        hld.titleoutcome.setText(title_str);
        title_str = context.getResources().getString(R.string.chartincome) + "\n" + context.getResources().getString(R.string.moneyunit)
                + String.format(Locale.getDefault(), "%.2f", income);
        hld.titleincome.setText(title_str);


        LineDataSet lineDataSet = new LineDataSet(lineoutput, context.getResources().getString(R.string.chartline));

        lineDataSet.setColor(context.getColor(R.color.chartorange300));
        //lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawCircleHole(false);
        //lineDataSet.setCircleRadius(1);
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleColor(context.getColor(R.color.chartorange300));
        lineDataSet.setCubicIntensity(0.15f);
        //lineDataSet.setValueTextSize(10);
        //lineDataSet.setValueTextColor(context.getResources().getColor(R.color.chartlightgreen300));
        lineDataSet.setDrawValues(false);
        //lineDataSet.setHighLightColor(context.getResources().getColor(R.color.chartcyanA400));
        lineDataSet.setHighlightEnabled(false);

        LineData lineData = new LineData(lineDataSet);

        for (int i = 0; i < itemdata.getLineY().length; i++) {
            if (i < itemdata.getLineY().length) {
                baroutput.add(new BarEntry(x_buf[i], itemdata.getLineY()[i]));
                baroutput_income.add(new BarEntry(x_buf[i], itemdata.getBarY()[i]));
            }
        }

        BarDataSet barDataSet;
        BarDataSet barDataSet_income;

        if (itemdata.getTimeType().equals("month")) {
            barDataSet = new BarDataSet(baroutput, context.getResources().getString(R.string.chartbard));
            barDataSet_income = null;
        }
        else {
            barDataSet = new BarDataSet(baroutput, context.getResources().getString(R.string.chartoutcome));
            barDataSet_income = new BarDataSet(baroutput_income, context.getResources().getString(R.string.chartincome));

            barDataSet_income.setColor(context.getColor(R.color.chartblue500Transparent));
            barDataSet_income.setBarShadowColor(context.getColor(R.color.chartblue500Transparent));
            barDataSet_income.setHighlightEnabled(false);
            barDataSet_income.setValueTextSize(BarTextSize);
            barDataSet_income.setValueTextColor(context.getColor(R.color.colorAccentLight));
            //barDataSet_income.setHighLightAlpha(50);
        }

        barDataSet.setColor(context.getColor(R.color.chartorange800Transparent));
        barDataSet.setBarShadowColor(context.getColor(R.color.chartorange800Transparent));
        barDataSet.setHighlightEnabled(false);
        barDataSet.setValueTextSize(BarTextSize);
        barDataSet.setValueTextColor(context.getColor(R.color.colorAccentLight));

        BarData barchart;
        if (itemdata.getTimeType().equals("year")) {
            barchart = new BarData(barDataSet, barDataSet_income);
            barchart.setBarWidth(0.4f);
            barchart.groupBars(0f, 0.2f, 0f);
        }
        else {
            barchart = new BarData(barDataSet);
        }

        XAxis xAxis = hld.combinedchart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        xAxis.setTextColor(context.getColor(R.color.colorAccent));
        xAxis.setAxisLineColor(context.getColor(R.color.colorAccent));
        xAxis.setAxisLineWidth(1f);
        xAxis.setTextSize(XAxisTextSize);
        //xAxis.setLabelCount(itemdata.getLineX().length);
        xAxis.setLabelCount(Xcount);

        //xAxis.setAxisMaximum(itemdata.getLineX().length);
        if (itemdata.getTimeType().equals("year")) {
            xAxis.setAxisMinimum(1);
            xAxis.setAxisMaximum(13);
            xAxis.setGranularityEnabled(true);
            xAxis.setCenterAxisLabels(true);
            //xAxis.setSpaceMax(0.1f);
            //xAxis.setGranularity(1);
        }
        else {
            xAxis.setAxisMinimum(0.4f);
            xAxis.setAxisMaximum(Xcount+0.6f);
        }

        if (itemdata.getTimeType().equals("year")) {
            ValueFormatter xAxisFormatter = new DayAxisValueFormatter();
            xAxis.setValueFormatter(xAxisFormatter);
        }
        else {
            xAxis.setValueFormatter(null);
        }

        YAxis yAxis = hld.combinedchart.getAxisLeft();
        yAxis.setDrawGridLines(true);
        yAxis.setTextColor(context.getColor(R.color.colorAccent));
        //yAxis.setEnabled(false);
        yAxis.setDrawAxisLine(false);
        yAxis.setMinWidth(0);
        yAxis.setLabelCount(5);
        //yAxis.setMaxWidth(Math.round(max_y));
        yAxis.setGridColor(context.getColor(R.color.colorAccent));
        //yAxis.enableGridDashedLine(20, 40, 0);
        //yAxis.setDrawZeroLine(false);
        //yAxis.setZeroLineWidth(0);
        //yAxis.setZeroLineColor(context.getResources().getColor(R.color.colorPrimary));

        hld.combinedchart.getAxisRight().setEnabled(false);

        Legend legend = hld.combinedchart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setTextColor(context.getColor(R.color.colorAccent));

        Description description = new Description();
        //description.setText("test");
        //description.setTextColor(context.getResources().getColor(R.color.colorAccent));
        description.setEnabled(false);

        hld.combinedchart.setDescription(description);
        hld.combinedchart.getAxisRight().setEnabled(false);
        hld.combinedchart.animateXY(500, 500);
        hld.combinedchart.setTouchEnabled(false);


        //barchart.setBarWidth(0.4f);

        CombinedData combinedData = new CombinedData();
        if (itemdata.getTimeType().equals("month")) {
            combinedData.setData(lineData);
        }

        combinedData.setData(barchart);

        hld.combinedchart.setScaleEnabled(true);
        hld.combinedchart.setData(combinedData);
    }


    private void setPieChart(final ChartViewHolder hld, ChartData itemdata) {
        List<PieEntry> output = new ArrayList<>();

        if (itemdata.getLineX() == null) {
            return;
        }

        for (int i = 0; i < itemdata.getPieY().length; i++) {
            output.add(new PieEntry(itemdata.getPieY()[i], itemdata.getPieX()[i]));
            //Log.d("amount", " "+itemdata.getPieY()[i]);
        }

        switch (itemdata.getTimeType()) {
            case "month" :
                String month = "";
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
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
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
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

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : accountTypes.getGeneralTypeColor()) {
            colors.add(context.getColor(c));
        }
        //Log.d("color", colors.toString());

        pieDataSet.setColors(colors);

        pieDataSet.setDrawValues(false);
        //pieDataSet.setSelectionShift(10f);
        pieDataSet.setValueTextColor(context.getColor(R.color.colorAccentLight));
        //pieDataSet.setValueTextSize(12f);
        //pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        pieDataSet.setValueLinePart1Length(0.6f);
        //pieDataSet.setValueLinePart2Length(.50f);
        //pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        //pieDataSet.setValueLineColor(context.getResources().getColor(R.color.colorAccent));

        hld.piechart.animateY(500);

        hld.piechart.setDrawHoleEnabled(true);
        hld.piechart.setHoleColor(context.getColor(R.color.colorPrimary));
        hld.piechart.setHoleRadius(60);
        hld.piechart.setCenterText("");
        hld.piechart.setCenterTextSize(14);
        hld.piechart.setCenterTextColor(context.getColor(R.color.colorAccentLight));

        //hld.piechart.setEntryLabelColor(context.getResources().getColor(R.color.colorAccentLight));
        hld.piechart.setDrawEntryLabels(false);
        //hld.piechart.setEntryLabelTextSize(8f);
        hld.piechart.setTouchEnabled(true);

        Legend legend = hld.piechart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        //legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setTextColor(context.getColor(R.color.colorAccent));

        Description description = new Description();
        description.setEnabled(false);
        hld.piechart.setDescription(description);
        hld.piechart.setExtraOffsets(30, 0, 0, 0);

        PieData pieData = new PieData(pieDataSet);
        hld.piechart.setData(pieData);

        hld.piechart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry pie_e = (PieEntry) e;
                String cstr = pie_e.getLabel() + "\n"
                        + context.getResources().getString(R.string.moneyunit) + String.format(Locale.getDefault(), "%.2f", e.getY());
                hld.piechart.setCenterText(cstr);
            }

            @Override
            public void onNothingSelected() {
                hld.piechart.setCenterText("");
            }
        });
    }

/*
    private static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

 */

    private static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

}



class ChartViewHolder extends RecyclerView.ViewHolder {
    CombinedChart combinedchart;
    PieChart piechart;
    BarChart barchart;
    TextView chartlabel;
    TextView titleoutcome;
    TextView titleincome;

    LinearLayout combinedframe;
    LinearLayout pieframe;
    LinearLayout barframe;


    ChartViewHolder(View itemView) {
        super(itemView);
        chartlabel = itemView.findViewById(R.id.chartlabel);
        titleoutcome = itemView.findViewById(R.id.chartoutcome);
        titleincome = itemView.findViewById(R.id.chartincome);

        combinedchart = itemView.findViewById(R.id.combinedchart);
        piechart = itemView.findViewById(R.id.piechart);
        barchart = itemView.findViewById(R.id.barchart);

        combinedframe = itemView.findViewById(R.id.combinedframe);
        pieframe = itemView.findViewById(R.id.pieframe);
        barframe = itemView.findViewById(R.id.barframe);
    }
}


class DayAxisValueFormatter extends ValueFormatter {

    @Override
    public String getFormattedValue(float value) {

        String format = "%tb";

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(Calendar.MONTH, Math.round(value)-1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        return String.format(Locale.getDefault(), format, calendar);
    }
}