package com.example.nadri4_edit1;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class StatsPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 3;

    StatsFragment1 statsFragment1;
    StatsFragment2 statsFragment2;
    StatsFragment3 statsFragment3;

    public StatsPagerAdapter(FragmentActivity fa) {
        super(fa);
        statsFragment1 = new StatsFragment1();
        statsFragment2 = new StatsFragment2();
        statsFragment3 = new StatsFragment3();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position==0) return statsFragment1;
        else if(position==1) return statsFragment2;
        else return statsFragment3;
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
