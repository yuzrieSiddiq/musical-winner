package com.reis.semester_quiz.Quiz;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by reis on 20/03/2017.
 */

public class QuizPagerAdapter extends FragmentPagerAdapter {
    private final String[] TITLES = { "Q1", "Q2", "Q3", "Q4", "SUBMIT" };
    ArrayList<HashMap<String, String>> questions;

    public QuizPagerAdapter(FragmentManager fm, ArrayList<HashMap<String, String>> questions) {
        super(fm);
        this.questions = questions;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return questions.get(position).get("question_no");
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    @Override
    public Fragment getItem(int position) {
        return QuizFragment.newInstance(position, getCount(), questions.get(position));
    }
}
