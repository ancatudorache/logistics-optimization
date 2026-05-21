package com.example.logisticsoptimization;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManagerDashboardActivity extends BaseActivity {

    private RecyclerView rvDriverRanking;
    private DriverRankingAdapter adapter;
    private List<DriverRanking> driverList = new ArrayList<>();

    private BarChart chartDistribution;
    private LineChart chartCostTrends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

        rvDriverRanking = findViewById(R.id.rvDriverRanking);
        rvDriverRanking.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DriverRankingAdapter(this, driverList);
        rvDriverRanking.setAdapter(adapter);

        chartDistribution = findViewById(R.id.chartDistribution);
        chartCostTrends = findViewById(R.id.chartCostTrends);

        loadDriverRanking();
        loadDeliveriesDistribution();
        loadCostTrends();
    }

    private void loadDriverRanking() {
        String url = "http://192.168.1.245:3000/api/manager/driver-ranking";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        driverList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            driverList.add(new DriverRanking(
                                    obj.getInt("id"),
                                    obj.getString("name"),
                                    obj.getInt("total_deliveries"),
                                    obj.getDouble("avg_cost"),
                                    obj.getDouble("avg_time"),
                                    obj.getDouble("cost_efficiency_percent"),
                                    obj.getDouble("time_efficiency_percent"),
                                    obj.getString("average_delivery_cost_level"),
                                    obj.getString("average_delivery_time_level")
                            ));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("MANAGER", "JSON Error: " + e.getMessage());
                    }
                },
                error -> {
                    Toast.makeText(this, "Eroare la încărcarea datelor", Toast.LENGTH_SHORT).show();
                    Log.e("MANAGER", "Error: " + error.toString());
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void loadDeliveriesDistribution() {
        String url = "http://192.168.1.245:3000/api/manager/deliveries-distribution";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        ArrayList<BarEntry> entries = new ArrayList<>();
                        ArrayList<String> labels = new ArrayList<>();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            entries.add(new BarEntry(i, obj.getInt("delivery_count")));
                            labels.add(obj.getString("driver_name"));
                        }

                        BarDataSet dataSet = new BarDataSet(entries, "Deliveries per Driver");
                        dataSet.setColor(Color.parseColor("#6200EE"));
                        dataSet.setValueTextSize(12f);

                        BarData barData = new BarData(dataSet);
                        chartDistribution.setData(barData);

                        XAxis xAxis = chartDistribution.getXAxis();
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setGranularity(1f);
                        xAxis.setGranularityEnabled(true);

                        chartDistribution.getDescription().setEnabled(false);
                        chartDistribution.animateY(1000);
                        chartDistribution.invalidate();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("MANAGER", "Distribution Error: " + e.getMessage());
                    }
                },
                error -> Log.e("MANAGER", "Distribution Error: " + error.toString())
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void loadCostTrends() {
        String url = "http://192.168.1.245:3000/api/manager/cost-trends";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.d("MANAGER", "Cost trends response: " + response.toString());

                    try {
                        ArrayList<Entry> entries = new ArrayList<>();
                        ArrayList<String> labels = new ArrayList<>();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String avgCostStr = obj.getString("avg_cost_per_delivery");
                            float avgCost = Float.parseFloat(avgCostStr);
                            entries.add(new Entry(i, avgCost));
                            labels.add(obj.getString("month"));
                        }

                        LineDataSet dataSet = new LineDataSet(entries, "Avg Cost per Delivery");
                        dataSet.setColor(Color.parseColor("#03DAC5"));
                        dataSet.setCircleColor(Color.parseColor("#03DAC5"));
                        dataSet.setLineWidth(2f);
                        dataSet.setCircleRadius(4f);
                        dataSet.setValueTextSize(10f);

                        LineData lineData = new LineData(dataSet);
                        chartCostTrends.setData(lineData);

                        XAxis xAxis = chartCostTrends.getXAxis();
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setGranularity(1f);

                        chartCostTrends.getDescription().setEnabled(false);
                        chartCostTrends.animateX(1000);
                        chartCostTrends.invalidate();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("MANAGER", "Trends Error: " + e.getMessage());
                    }
                },
                error -> Log.e("MANAGER", "Trends Error: " + error.toString())
        );

        Volley.newRequestQueue(this).add(request);
    }
}