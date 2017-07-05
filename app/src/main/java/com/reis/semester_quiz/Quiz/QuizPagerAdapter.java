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
    ArrayList<HashMap<String, String>> questions;
    ArrayList<HashMap<String, String>> answers;
    String quiz_id, quiz_type;

    public QuizPagerAdapter(FragmentManager fm, ArrayList<HashMap<String, String>> questions, ArrayList<HashMap<String, String>> answers, String quiz_id, String quiz_type) {
        super(fm);
        this.questions = questions;
        this.answers = answers;
        this.quiz_id = quiz_id;
        this.quiz_type = quiz_type;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // if not at question_no "n", translate the question no to QNumber
        if (position != questions.size()-1) {
            Integer question_no = Integer.valueOf(questions.get(position).get("question_no")) + 1;
            return "Q" + question_no;
        } else {
            return "SUBMIT";
        }
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    @Override
    public Fragment getItem(int position) {
        return QuizFragment.newInstance(position, getCount(), questions.get(position), answers, quiz_id, quiz_type);
    }
}
