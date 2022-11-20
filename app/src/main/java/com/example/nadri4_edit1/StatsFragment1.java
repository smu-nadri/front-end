package com.example.nadri4_edit1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StatsFragment1 extends Fragment {

    public static BarChart yChart, mChart, dChart;
    public static BarDataSet yDataSet, mDataSet, dDataSet;
    public static BarData yData, mData, dData;

    //데이터셋에 넣을 리스트
    public static ArrayList<BarEntry> yList = new ArrayList<>(), mList = new ArrayList<>(), dList = new ArrayList<>();
    //서버에서 받아온 값
    public static ArrayList<JSONObject> yearCnt = new ArrayList<>();
    public static ArrayList<JSONObject> monthCnt = new ArrayList<>();
    public static ArrayList<JSONObject> dayCnt = new ArrayList<>();

    int mIdx = 0, dIdx = 0;

    final int YEAR = 0;
    final int MONTH = 1;
    final int DAY = 2;

    public static TextView mTextView;

    public StatsFragment1(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stats_fragment1, container, false);

        yChart = view.findViewById(R.id.yearChart);
        mChart = view.findViewById(R.id.monthChart);
        dChart = view.findViewById(R.id.dayChart);

        yDataSet = new BarDataSet(yList, "_year");
        yData = new BarData();

        mDataSet = new BarDataSet(mList, "_month");
        mData = new BarData();

        dDataSet = new BarDataSet(dList, "_dayOfWeek");
        dData = new BarData();

        mTextView = view.findViewById(R.id.mTextView);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mIdx = (mIdx+1) % monthCnt.size();
                    dIdx = (dIdx+1) % dayCnt.size();
                    String y = monthCnt.get(mIdx).getString("year") + "년";
                    mTextView.setText(y);

                    mList.clear();
                    dList.clear();
                    JSONObject m = monthCnt.get(mIdx).getJSONObject("month");
                    JSONObject d = dayCnt.get(dIdx).getJSONObject("dayofweek");
                    for(int i = 1; i < 13; i++){
                        String k = String.valueOf(i);
                        mList.add(new BarEntry(i, m.getInt(k)));
                    }
                    for(int i = 1; i < 8; i++){
                        String k = String.valueOf(i);
                        dList.add(new BarEntry(i, d.getInt(k)));
                    }

                    updateChart(mDataSet, mData, mChart);
                    updateChart(dDataSet, dData, dChart);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    private void updateChart(BarDataSet bDataSet, BarData bData, BarChart bChart) {
        bDataSet.notifyDataSetChanged();
        bData.notifyDataChanged();
        bChart.notifyDataSetChanged();
        bChart.invalidate();
    }
}
