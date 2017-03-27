package com.reis.semester_quiz.Quiz;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by reis on 20/03/2017.
 */

public class QuizPagerAdapter extends FragmentPagerAdapter {
    private final String[] TITLES = { "Q1", "Q2", "Q3", "Q4", "SUBMIT" };

    public QuizPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        return QuizFragment.newInstance(position);
    }
}
