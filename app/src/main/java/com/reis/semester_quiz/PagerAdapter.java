package com.reis.semester_quiz;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by reis on 20/03/2017.
 */

public class PagerAdapter extends FragmentPagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return (position == 0)? "Tab 1" : "Tab2" ;
    }
    @Override
    public int getCount() {
        return 2;
    }
    @Override
    public Fragment getItem(int position) {
        return (position == 0)? new DashboardFragment() : new QuizFragment() ;
    }
}