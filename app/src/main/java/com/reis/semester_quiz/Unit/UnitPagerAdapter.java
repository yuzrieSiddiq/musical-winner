package com.reis.semester_quiz.Unit;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.reis.semester_quiz.Quiz.QuizFragment;

/**
 * Created by reis on 20/03/2017.
 */

public class UnitPagerAdapter extends FragmentPagerAdapter {
    private final String[] TITLES = { "AVAILABLE QUIZZES", "UNIT INFO", "TEAM INFO" };

    public UnitPagerAdapter(FragmentManager fm) {
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
        switch (position) {
            case 0:
                return new Fragment1QuizList();
            case 1:
                return new Fragment2UnitInfo();
            case 2:
                return new Fragment3Team();
            default:
                return new QuizFragment();
        }
    }
}
