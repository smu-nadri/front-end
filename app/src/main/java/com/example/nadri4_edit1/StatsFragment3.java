package com.example.nadri4_edit1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StatsFragment3 extends Fragment {

    public static BarChart tChart, fChart;

    public static ArrayList<BarEntry> tList = new ArrayList<>(), fList = new ArrayList<>();

    public static ArrayList<JSONObject> tagCnt = new ArrayList<>();
    public static ArrayList<JSONObject> faceCnt = new ArrayList<>();

    final int TAG = 5;
    final int FACE = 6;

    public StatsFragment3(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stats_fragment3, container, false);

        tChart = view.findViewById(R.id.tagChart);
        fChart = view.findViewById(R.id.faceChart);

        setData(TAG, tChart, tList);
        setData(FACE, fChart, fList);

        return view;
    }

    synchronized void initChart(int code, BarChart bChart, ArrayList<BarEntry> bList){
        bChart.getDescription().setEnabled(false);    //"Description Chart" 지우기
        bChart.getLegend().setEnabled(false); //레이블 설명 지우기
        bChart.setScaleEnabled(false);
        bChart.setVisibleXRangeMaximum(8f);

        XAxis xAxis = bChart.getXAxis();  //x축 가져오기
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  //위치
        xAxis.setDrawGridLines(false);  //격자무늬 없애기
        xAxis.setLabelCount(bList.size());  //x축 개수
        xAxis.setGranularity(1f);
        xAxis.setTextSize(10f);

        if(code == TAG){
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    try {
                        if(tagCnt.size() != 0) {
                            String sValue = tagCnt.get((int) value).getString("tag");
                            return sValue;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return "";
                }
            });
        }
        else if(code == FACE){
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    String sValue = "";
                    try {
                        if(faceCnt.size() != 0) {
                            sValue = faceCnt.get((int) value).getString("name");
                            return sValue;
                        }
                    } catch (JSONException e) { //초기화된 얼굴일 경우
                        e.printStackTrace();
                    }
                    return sValue;
                }
            });
        }

        YAxis yAxis = bChart.getAxisLeft();
        yAxis.setDrawLabels(false);
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);
        yAxis.setGranularity(1f);   //간격
        yAxis.setAxisMinimum(0f);

        //오른쪽 축 값 삭제
        YAxis rAxis = bChart.getAxisRight();
        rAxis.setDrawLabels(false);
        rAxis.setDrawAxisLine(false);
        rAxis.setDrawGridLines(false);
    }

    private void setData(int code, BarChart bChart, ArrayList<BarEntry> bList) {
        bChart.clear();
        bChart.invalidate();
        initChart(code, bChart, bList);

        BarData bData = new BarData();

        BarDataSet bDataSet = new BarDataSet(bList, "_thoroughfare");

        bDataSet.setColor(getResources().getColor(R.color.chart_bar));
        bDataSet.setValueTextSize(10f);

        bData.addDataSet(bDataSet);
        bData.setValueTextSize(10f);
        bData.setBarWidth(0.4f);
        bData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String sValue = String.valueOf((int)value);
                return sValue;
            }
        });

        bChart.setData(bData);
        bChart.setVisibleXRangeMaximum(8f);
        bChart.notifyDataSetChanged();
        bChart.invalidate();

    }
}
