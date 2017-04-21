package com.reis.semester_quiz.Unit.Pages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.reis.semester_quiz.Quiz.QuizActivity;
import com.reis.semester_quiz.R;
import com.reis.semester_quiz.Unit.UnitActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by reis on 22/03/2017.
 */

public class AdapterUnitQuizList extends ArrayAdapter<HashMap<String, String>> {

    ArrayList<HashMap<String, String>> quiz_item;

    public AdapterUnitQuizList(@NonNull Context context, ArrayList<HashMap<String, String>> values) {
        super(context, R.layout.unit_quiz_list_fragment, values);
        quiz_item = values;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.unit_quiz_list_fragment, parent, false);


        TextView quiz_title = (TextView) view.findViewById(R.id.quiz_title);
        TextView quiz_type = (TextView) view.findViewById(R.id.quiz_type);
        TextView quiz_status = (TextView) view.findViewById(R.id.quiz_status);
        TextView quiz_rank = (TextView) view.findViewById(R.id.quiz_rank);
        TextView quiz_score = (TextView) view.findViewById(R.id.quiz_score);

        Button attempt_button = (Button) view.findViewById(R.id.quiz_attempt);

        quiz_title.setText(quiz_item.get(position).get("title"));

        // if quiz is not yet attempted, its open
        if (quiz_item.get(position).get("has_been_attempted").toLowerCase().equals("false")) {
            quiz_type.setText("TYPE: " + quiz_item.get(position).get("type").toUpperCase());
            quiz_status.setText("STATUS: " + quiz_item.get(position).get("status").toUpperCase());
            quiz_rank.setText("RANK: -");
            quiz_score.setText("SCORE: -");

            attempt_button.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.answerA));
            attempt_button.setTextColor(getContext().getResources().getColor(R.color.white));
            attempt_button.setText("OPEN");
            attempt_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigatetoUnitActivity(quiz_item.get(position).get("quiz_id"));
                }
            });
        } else {
            String score = quiz_item.get(position).get("correct_count") + "/" + quiz_item.get(position).get("answers_count");
            Integer score_percentage = (Integer.parseInt(quiz_item.get(position).get("correct_count")) * 100 / Integer.parseInt(quiz_item.get(position).get("answers_count")));
            quiz_type.setText("TYPE: " + quiz_item.get(position).get("type").toUpperCase());
            quiz_status.setText("STATUS: ATTEMPTED");
            quiz_rank.setText("RANK: -");
            quiz_score.setText("SCORE: " + score_percentage + "% (" + score + ")");

            attempt_button.setText("CLOSED");
        }

        return view;
    }

    public void navigatetoUnitActivity(String quiz_id){

        // switch page intent
        Intent quizintent = new Intent(getContext(), QuizActivity.class);

        // add to bundle
        Bundle bundle = new Bundle();
        bundle.putString("quiz_id", quiz_id);
        quizintent.putExtras(bundle);

        getContext().startActivity(quizintent);
    }
}
