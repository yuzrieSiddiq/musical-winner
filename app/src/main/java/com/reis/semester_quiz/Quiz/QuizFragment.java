package com.reis.semester_quiz.Quiz;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.reis.semester_quiz.R;

public class QuizFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private String [] questions = new String[] {
            "Stereotypes are useful as they allow us to categorize lots of information easily. Rigid stereotypes about people generally lead to prejudice.  Stereotyping is considered as ",
            "Highly prejudiced people tend to have what is referred to by psychologists as an authoritarian personality. Which one of the following is not considered as one of the characteristics of authoritarian personality:",
            "We may learn to be prejudiced from home, school, government, workplace, place of worship, and the media. Which of the following is related to the media?",
            "Different people may express prejudice differently. There are people who often disclose outwardly how they are opposed to unequal treatment, but their inner feelings may suggest otherwise. They may say they are egalitarian and use that open display as an excuse when they act in a way that is not in the interests of diversity. This way of expressing prejudice is known as"
    };

    private int position;

    public static QuizFragment newInstance(int position) {
        QuizFragment f = new QuizFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (position < 4) {
            // settings
            final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            // width: match_parent, height: match_parent
            LayoutParams hw_match_match_param= new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            hw_match_match_param.setMargins(margin,margin,margin,margin);

            LayoutParams hw_match_wrap_param= new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

            FrameLayout fl = new FrameLayout(getActivity());
            fl.setLayoutParams(hw_match_match_param);

            // 1st parent - rootlayout is a scrollview
            ScrollView rootlayout = new ScrollView(getContext());
            rootlayout.setLayoutParams(hw_match_match_param);
            fl.addView(rootlayout);

            // 2nd parent - scrollview 1 child
            LinearLayout parent = new LinearLayout(getContext());
            parent.setLayoutParams(hw_match_wrap_param);
            parent.setOrientation(LinearLayout.VERTICAL);
            rootlayout.addView(parent);

            // 2 children in the 2nd parent - 1 Text View and 1 Linear Layout
            // child - question is a TextView
            TextView question = new TextView(getActivity());
            question.setLayoutParams(hw_match_match_param);
            question.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            question.setBackgroundResource(R.drawable.background_card);
            question.setText(questions[position]);
            question.setTextSize(16);
            question.setPadding(12,20,12,20);

            // child - answerlayout is a vertical LinearLayout
            LinearLayout answerlayout = new LinearLayout(getContext());
            answerlayout.setLayoutParams(hw_match_wrap_param);
            answerlayout.setOrientation(LinearLayout.VERTICAL);

            // add to parent
            parent.addView(question);
            parent.addView(answerlayout);

            // CREATE ANSWERS
            Button answerA = new Button(getActivity());
            answerA.setLayoutParams(hw_match_wrap_param);
            answerA.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            answerA.setText("A: SAMPLE ANSWER");
            answerA.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.answerA)));

            Button answerB = new Button(getActivity());
            answerB.setLayoutParams(hw_match_wrap_param);
            answerB.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            answerB.setText("B: SAMPLE ANSWER");
            answerB.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.answerB)));

            Button answerC = new Button(getActivity());
            answerC.setLayoutParams(hw_match_wrap_param);
            answerC.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            answerC.setText("C: SAMPLE ANSWER");
            answerC.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.answerC)));

            Button answerD = new Button(getActivity());
            answerD.setLayoutParams(hw_match_wrap_param);
            answerD.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            answerD.setText("D: SAMPLE ANSWER");
            answerD.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.answerD)));

            Button answerE = new Button(getActivity());
            answerE.setLayoutParams(hw_match_wrap_param);
            answerE.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            answerE.setText("E: SAMPLE ANSWER");
            answerE.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.answerE)));

            answerlayout.addView(answerA);
            answerlayout.addView(answerB);
            answerlayout.addView(answerC);
            answerlayout.addView(answerD);
            answerlayout.addView(answerE);

            return fl;
        } else {
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

            FrameLayout fl = new FrameLayout(getActivity());
            fl.setLayoutParams(params);

            final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
                    .getDisplayMetrics());

            TextView v = new TextView(getActivity());
            params.setMargins(margin, margin, margin, margin);
            v.setLayoutParams(params);
            v.setLayoutParams(params);
            v.setGravity(Gravity.CENTER);
            v.setBackgroundResource(R.drawable.background_card);
            v.setText("SUBMIT");

            fl.addView(v);
            return fl;
        }
    }
}
