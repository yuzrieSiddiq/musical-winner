package com.reis.semester_quiz;

import android.content.Intent;
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
import android.widget.TextClock;
import android.widget.TextView;

public class UnitFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private int position;

    public static UnitFragment newInstance(int position) {
        UnitFragment f = new UnitFragment();
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

        LayoutParams params_match_match = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        LayoutParams params_match_wrap = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LayoutParams params_wrap_wrap = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        FrameLayout fl = new FrameLayout(getActivity());
        fl.setLayoutParams(params_match_match);

        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
                .getDisplayMetrics());

        if (position == 0) {
            // CREATE QUIZ LIST
            ScrollView scrollView = new ScrollView(getActivity());
            scrollView.setLayoutParams(params_match_wrap);

            // inside scrollView
            LinearLayout rootlayout = new LinearLayout(getActivity());
            rootlayout.setLayoutParams(params_match_wrap);
            rootlayout.setOrientation(LinearLayout.VERTICAL);
            scrollView.addView(rootlayout);

            // inside root layout
            LinearLayout firstparent = new LinearLayout(getActivity());
            firstparent.setLayoutParams(params_match_wrap);
            firstparent.setOrientation(LinearLayout.VERTICAL);
            firstparent.setPadding(10,10,10,10);
            rootlayout.addView(firstparent);

            // inside firstparent
            TextView quizheader = new TextView(getActivity());
            quizheader.setLayoutParams(params_match_wrap);
            quizheader.setText("QUIZ 1: SAMPLE QUIZ");

            LinearLayout quizcontainer = new LinearLayout(getActivity());
            quizcontainer.setLayoutParams(params_match_wrap);
            quizcontainer.setOrientation(LinearLayout.HORIZONTAL);
            quizcontainer.setWeightSum(2);

            firstparent.addView(quizheader);
            firstparent.addView(quizcontainer);

            // inside quizcontainer
            LinearLayout leftcontainer = new LinearLayout(getActivity());
            leftcontainer.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));
            leftcontainer.setOrientation(LinearLayout.VERTICAL);
            leftcontainer.setPadding(4,4,4,4);
            quizcontainer.addView(leftcontainer);

            TextView lefttype = new TextView(getActivity());
            lefttype.setLayoutParams(params_match_wrap);
            lefttype.setGravity(View.TEXT_ALIGNMENT_CENTER);
            lefttype.setText("INDIVIDUAL");

            TextView leftquizstatus = new TextView(getActivity());
            leftquizstatus.setLayoutParams(params_match_wrap);
            leftquizstatus.setText("Status: Untested");

            TextView leftquizrank = new TextView(getActivity());
            leftquizrank.setLayoutParams(params_match_wrap);
            leftquizrank.setText("Rank: N/A");

            TextView leftquizscore = new TextView(getActivity());
            leftquizscore.setLayoutParams(params_match_wrap);
            leftquizscore.setText("Score: N/A");

            Button leftquizattempt = new Button(getActivity());
            leftquizattempt.setLayoutParams(params_match_wrap);
            leftquizattempt.setText("ATTEMPT QUIZ");

            leftcontainer.addView(lefttype);
            leftcontainer.addView(leftquizstatus);
            leftcontainer.addView(leftquizrank);
            leftcontainer.addView(leftquizscore);
            leftcontainer.addView(leftquizattempt);
            // end left container content

            LinearLayout rightcontainer = new LinearLayout(getActivity());
            rightcontainer.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));
            rightcontainer.setOrientation(LinearLayout.VERTICAL);
            rightcontainer.setPadding(4,4,4,4);
            quizcontainer.addView(rightcontainer);

            TextView righttype = new TextView(getActivity());
            righttype.setLayoutParams(params_match_wrap);
            righttype.setGravity(View.TEXT_ALIGNMENT_CENTER);
            righttype.setText("GROUP");

            TextView rightquizstatus = new TextView(getActivity());
            rightquizstatus.setLayoutParams(params_match_wrap);
            rightquizstatus.setText("Status: Untested");

            TextView rightquizrank = new TextView(getActivity());
            rightquizrank.setLayoutParams(params_match_wrap);
            rightquizrank.setText("Rank: N/A");

            TextView rightquizscore = new TextView(getActivity());
            rightquizscore.setLayoutParams(params_match_wrap);
            rightquizscore.setText("Score: N/A");

            Button rightquizattempt = new Button(getActivity());
            rightquizattempt.setLayoutParams(params_match_wrap);
            rightquizattempt.setText("CLOSED");

            rightcontainer.addView(righttype);
            rightcontainer.addView(rightquizstatus);
            rightcontainer.addView(rightquizrank);
            rightcontainer.addView(rightquizscore);
            rightcontainer.addView(rightquizattempt);
            // end right container content

            fl.addView(scrollView);

            leftquizattempt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // should pass an id here to get the quiz content from online
                    Intent intent = new Intent(getContext(), QuizActivity.class);
                    startActivity(intent);
                }
            });

        } else if (position == 1) {
            // UNIT INFO
            TextView v = new TextView(getActivity());
            params_match_match.setMargins(margin, margin, margin, margin);
            v.setLayoutParams(params_match_match);
            v.setLayoutParams(params_match_match);
            v.setGravity(Gravity.CENTER);
            v.setBackgroundResource(R.drawable.background_card);
            v.setText("UNIT INFO");

            fl.addView(v);
        } else if (position == 2) {
            // TEAM INFO
            TextView v = new TextView(getActivity());
            params_match_match.setMargins(margin, margin, margin, margin);
            v.setLayoutParams(params_match_match);
            v.setLayoutParams(params_match_match);
            v.setGravity(Gravity.CENTER);
            v.setBackgroundResource(R.drawable.background_card);
            v.setText("TEAM INFO");

            fl.addView(v);
        }

        return fl;
    }
}
