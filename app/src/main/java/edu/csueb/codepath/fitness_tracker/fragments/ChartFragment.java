package edu.csueb.codepath.fitness_tracker.fragments;

import android.content.pm.LabeledIntent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.XAxis;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.csueb.codepath.fitness_tracker.R;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import edu.csueb.codepath.fitness_tracker.Workout;

public class ChartFragment extends Fragment {

    private static final String TAG = "ChartFragment";
    private BarChart barChart;
    private PieChart pieChart;
    private List<String> workoutTypes = new ArrayList<>();
    private List<Integer> caloriesBurned = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chart, container, false);
        barChart = view.findViewById(R.id.barChart);
        pieChart = view.findViewById(R.id.pieChart);

        fetchWorkoutDataForBarChart();
        fetchWorkoutDataForPieChart();

        return view;
    }

    private void fetchWorkoutDataForBarChart() {
        ParseQuery<Workout> query = createWorkoutQuery();
        query.findInBackground((workouts, e) -> {
            if (handleParseError(e)) return;

            clearPreviousData();
            populateWorkoutData(workouts);
            if (workoutTypes.size() == caloriesBurned.size()) {
                loadBarChartData();
            }
        });
    }

    private void fetchWorkoutDataForPieChart() {
        ParseQuery<Workout> query = createWorkoutQuery();
        query.findInBackground((workouts, e) -> {
            if (handleParseError(e)) return;

            Map<String, Integer> workoutMap = aggregateWorkoutData(workouts);
            createPieChart(preparePieEntries(workoutMap));
        });
    }

    private ParseQuery<Workout> createWorkoutQuery() {
        ParseQuery<Workout> query = ParseQuery.getQuery("Workout");
        query.include(Workout.KEY_USER);
        query.whereEqualTo(Workout.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(20);
        return query;
    }

    private boolean handleParseError(ParseException e) {
        if (e != null) {
            Log.e(TAG, "Error fetching workouts", e);
            return true;
        }
        return false;
    }

    private void clearPreviousData() {
        workoutTypes.clear();
        caloriesBurned.clear();
    }

    private void populateWorkoutData(List<Workout> workouts) {
        for (Workout w : workouts) {
            String workoutType = w.getString("WorkoutType");
            Double calories = (Double) w.get("calories");

            if (workoutType != null && calories != null) {
                try {
                    int calorieCount = calories.intValue();

                    if (calorieCount != 0) {
                        int index = workoutTypes.indexOf(workoutType);
                        if (index == -1) {
                            // If workout type is not present, add it
                            workoutTypes.add(workoutType);
                            caloriesBurned.add(calorieCount);
                        } else {
                            // If workout type is present, update the calories
                            int currentCalories = caloriesBurned.get(index);
                            caloriesBurned.set(index, currentCalories + calorieCount);
                        }
                    }

                } catch (NumberFormatException nfe) {
                    Log.e(TAG, "Error parsing calories: " + nfe.getMessage());
                }
            }
        }
    }


    private void loadBarChartData() {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < caloriesBurned.size(); i++) {
            entries.add(new BarEntry(i, caloriesBurned.get(i)));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Calories Burned");
        dataSet.setColor(Color.parseColor("#007BFF"));
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        dataSet.setDrawValues(true);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barData.setBarWidth(0.4f);
        setupBarChartAxes();
        barChart.invalidate();
    }

    private void setupBarChartAxes() {
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(workoutTypes));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(0);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(12f);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisLineColor(Color.GRAY);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
//        leftAxis.setValueFormatter(new ValueFormatter() {
//            public String getAxisLabel(float value, AxisBase axis) {
//                // Display specific values only
//                if (value == 10 || value == 20 || value == 30 || value == 40 || value == 50 || value == 60) {
//                    return (int) value + " kcal"; // Append the unit
//                }
//                return ""; // Return empty for other values
//            }
//        });
        leftAxis.setValueFormatter(new DefaultValueFormatter(1));
        leftAxis.setGranularity(10f);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setTextSize(12f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.LTGRAY);

        barChart.getAxisRight().setEnabled(false);
        barChart.setDrawBorders(true);
        barChart.setBorderColor(Color.GRAY);
        barChart.setBorderWidth(1f);
        barChart.getDescription().setEnabled(false);
    }

    private Map<String, Integer> aggregateWorkoutData(List<Workout> workouts) {
        Map<String, Integer> workoutMap = new HashMap<>();
        for (Workout w : workouts) {
            String workoutType = w.getString("WorkoutType");

            // Retrieve calories as a Double
//            Double calories = (Double) w.get("calories");
               String durationStr = w.getString("duration");
            if (workoutType != null && durationStr != null) {
                // Convert Double to Integer (if necessary)
//                int calorieCount = calories.intValue();
                int minutes = Integer.parseInt(durationStr.split(":")[0]);
                    if (minutes <= 90) {
                        workoutMap.put(workoutType, workoutMap.getOrDefault(workoutType, 0) + minutes);
                    }
//                 if(calorieCount != 0){.`
//                 }
                // Update the workout map
            } else {
                // Log if workoutType or calories are null
                if (workoutType == null) {
                    Log.e(TAG, "Workout type is null for workout: " + w);
                }
//                if (calories == null) {
//                    Log.e(TAG, "Calories is null for workout: " + w);
//                }
            }
        }
        return workoutMap;
    }


    private int calculateCalories(String workoutType, int duration) {
        int caloriesPerMinute = 0;

        switch (workoutType) {
            case "Running":
                caloriesPerMinute = 10;
                break;
            case "Cycling":
                caloriesPerMinute = 8;
                break;
            case "Swimming":
                caloriesPerMinute = 7;
                break;
            default:
                caloriesPerMinute = 5;
        }

        return caloriesPerMinute * duration; // Total calories burned
    }


    private List<PieEntry> preparePieEntries(Map<String, Integer> workoutMap) {
        List<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : workoutMap.entrySet()) {
            pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }
        return pieEntries;
    }

    private void createPieChart(List<PieEntry> pieEntries) {
        if (pieEntries.isEmpty()) {
            Log.w(TAG, "No data available for pie chart.");
            return;
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        setupPieChartAppearance();
        pieChart.invalidate();
    }

    private void setupPieChartAppearance() {
        Description desc = barChart.getDescription();
        desc.setEnabled(true);
        desc.setText("Workout Duration Distribution");
        desc.setPosition(0,0);
        desc.setTextSize(08f);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setTextColor(Color.BLACK);
        legend.setTextSize(08f);
        
        pieChart.setEntryLabelTextSize(08f);
        pieChart.setEntryLabelColor(Color.BLACK);
    }
}
