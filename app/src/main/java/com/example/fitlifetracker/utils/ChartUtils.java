package com.example.fitlifetracker.utils;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ChartUtils {

    public static void setupBarChart(BarChart chart, List<Float> dataValues, List<String> xAxisLabels, String descriptionText, String dataSetName) {
        // --- IMPORTANT FIX: Moved setDrawValueAboveBar to the BarChart object ---
        chart.setDrawValueAboveBar(true); // <-- THIS LINE IS NEW/MOVED HERE
        // ---------------------------------------------------------------------

        chart.setDrawBarShadow(false);
        chart.setMaxVisibleValueCount(60);
        chart.setPinchZoom(false); // Disable zoom
        chart.setDrawGridBackground(false); // No grid background

        Description description = new Description();
        description.setText(descriptionText);
        description.setEnabled(false);
        chart.setDescription(description);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(xAxisLabels.size(), false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(dataValues.size() - 0.5f);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);

        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < dataValues.size(); i++) {
            entries.add(new BarEntry(i, dataValues.get(i)));
        }

        BarDataSet dataSet;

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            dataSet = (BarDataSet) chart.getData().getDataSetByIndex(0);
            dataSet.setValues(entries);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            dataSet = new BarDataSet(entries, dataSetName);
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            // dataSet.setDrawValueAboveBar(true); // <-- THIS LINE IS REMOVED FROM HERE
            // The setting is now on the 'chart' object directly

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(dataSet);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);
            chart.setData(data);
        }

        chart.setFitBars(true);
        chart.invalidate();
    }
}