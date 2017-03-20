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

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        FrameLayout fl = new FrameLayout(getActivity());
        fl.setLayoutParams(params);

        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
                .getDisplayMetrics());

        if (position == 0) {
            // AVAILABLE QUIZZES
            TextView v = new TextView(getActivity());
            params.setMargins(margin, margin, margin, margin);
            v.setLayoutParams(params);
            v.setLayoutParams(params);
            v.setGravity(Gravity.CENTER);
            v.setBackgroundResource(R.drawable.background_card);
            v.setText("LIST OF AVAILABLE QUIZZES");

            // CREATE ANSWERS
            Button samplequiz = new Button(getActivity());
            samplequiz.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            samplequiz.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            samplequiz.setGravity(Gravity.CENTER);
            samplequiz.setText("SAMPLE QUIZ: iRAT1");

            fl.addView(v);
            fl.addView(samplequiz);

            samplequiz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), QuizActivity.class);
                    startActivity(intent);
                }
            });

        } else if (position == 1) {
            // UNIT INFO
            TextView v = new TextView(getActivity());
            params.setMargins(margin, margin, margin, margin);
            v.setLayoutParams(params);
            v.setLayoutParams(params);
            v.setGravity(Gravity.CENTER);
            v.setBackgroundResource(R.drawable.background_card);
            v.setText("UNIT INFO");

            fl.addView(v);
        } else if (position == 2) {
            // TEAM INFO
            TextView v = new TextView(getActivity());
            params.setMargins(margin, margin, margin, margin);
            v.setLayoutParams(params);
            v.setLayoutParams(params);
            v.setGravity(Gravity.CENTER);
            v.setBackgroundResource(R.drawable.background_card);
            v.setText("TEAM INFO");

            fl.addView(v);
        }

        return fl;
    }
}
