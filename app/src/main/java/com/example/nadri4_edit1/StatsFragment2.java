package com.example.nadri4_edit1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class StatsFragment2 extends Fragment {

    public static BarChart lChart, tfChart;

    //데이터셋에 넣을 리스트
    public static ArrayList<BarEntry> lList = new ArrayList<>(), tfList = new ArrayList<>();

    //서버에서 받아온 값
    public static ArrayList<JSONObject> localityCnt = new ArrayList<>();
    public static ArrayList<JSONObject> thoroughfareCnt = new ArrayList<>();

    public static TextView tfTextView;

    int tfIdx = 0;

    final int LOCALITY = 3;
    final int THOROUGHFARE = 4;

    public StatsFragment2(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stats_fragment2, container, false);

        lChart = view.findViewById(R.id.localChart);
        tfChart = view.findViewById(R.id.tfChart);

        tfTextView = view.findViewById(R.id.tfTextView);
        tfTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    tfIdx = (tfIdx + 1) % thoroughfareCnt.size();
                    String l = thoroughfareCnt.get(tfIdx).getString("locality");
                    tfTextView.setText(l);

                    tfList.clear();
                    JSONArray tf = thoroughfareCnt.get(tfIdx).getJSONArray("thoroughfares");
                    for(int i = 0; i < tf.length(); i++){
                        JSONObject t = tf.getJSONObject(i);
                        tfList.add(new BarEntry(i, t.getInt("count")));
                    }

                    setData(THOROUGHFARE, tfChart, tfList);
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

        try {
            if (thoroughfareCnt.size() != 0) {
                tfTextView.setText(thoroughfareCnt.get(0).getString("locality"));
                JSONArray tf = thoroughfareCnt.get(0).getJSONArray("thoroughfares");
                for (int i = 0; i < tf.length(); i++) {
                    JSONObject t = tf.getJSONObject(i);
                    tfList.add(new BarEntry(i, t.getInt("count")));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        setData(LOCALITY, lChart, lList);
        setData(THOROUGHFARE, tfChart, tfList);

        return view;
    }

    synchronized void initChart(int code, BarChart bChart, ArrayList<BarEntry> bList){
        bChart.getDescription().setEnabled(false);    //"Description Chart" 지우기
        bChart.getLegend().setEnabled(false); //레이블 설명 지우기
        bChart.setScaleEnabled(false);
        bChart.setVisibleXRangeMaximum(8f);
        bChart.setHorizontalScrollBarEnabled(true);

        XAxis xAxis = bChart.getXAxis();  //x축 가져오기
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  //위치
        xAxis.setDrawGridLines(false);  //격자무늬 없애기
        xAxis.setLabelCount(bList.size());  //x축 개수
        xAxis.setGranularity(1f);
        xAxis.setTextSize(10f);
        if(code == LOCALITY){
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    try {
                        if(localityCnt.size() != 0) {
                            String sValue = localityCnt.get((int) value).getString("locality");
                            return sValue;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return "";
                }
            });
        }
        else if(code == THOROUGHFARE){
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    try {
                        if(thoroughfareCnt.size() != 0) {
                            String sValue = thoroughfareCnt.get(tfIdx).getJSONArray("thoroughfares").getJSONObject((int) value).getString("name");
                            return sValue;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return "";
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
