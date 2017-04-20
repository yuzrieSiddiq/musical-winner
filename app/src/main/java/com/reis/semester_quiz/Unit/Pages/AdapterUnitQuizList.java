package com.reis.semester_quiz.Unit.Pages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.unit_quiz_list_fragment, parent, false);

        // set the views
        LinearLayout left_side = (LinearLayout) view.findViewById(R.id.left_side);
        LinearLayout right_side = (LinearLayout) view.findViewById(R.id.right_side);

        TextView left_quiz_title = (TextView) view.findViewById(R.id.left_quiz_title);
        TextView left_quiz_status = (TextView) view.findViewById(R.id.left_quiz_status);
        TextView left_quiz_rank = (TextView) view.findViewById(R.id.left_quiz_rank);
        TextView left_quiz_score = (TextView) view.findViewById(R.id.left_quiz_score);

        TextView right_quiz_title = (TextView) view.findViewById(R.id.right_quiz_title);
        TextView right_quiz_status = (TextView) view.findViewById(R.id.right_quiz_status);
        TextView right_quiz_rank = (TextView) view.findViewById(R.id.right_quiz_rank);
        TextView right_quiz_score = (TextView) view.findViewById(R.id.right_quiz_score);

        Button left_attempt_button = (Button) view.findViewById(R.id.left_quiz_attempt);
        Button right_attempt_button = (Button) view.findViewById(R.id.right_quiz_attempt);

        if (quiz_item.get(position).get("type").toLowerCase().equals("individual")) {
            left_quiz_title.setText(quiz_item.get(position).get("title"));
            left_quiz_status.setText(quiz_item.get(position).get("status"));
            left_quiz_rank.setText("-");    // only available to do after quiz can be submitted and can see results
            left_quiz_score.setText("-");   // only available to do after quiz can be submitted and can see results

            left_attempt_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigatetoUnitActivity(quiz_item.get(position).get("quiz_id"));
                }
            });

        } else if (quiz_item.get(position).get("type").toLowerCase().equals("group")) {
            right_quiz_title.setText(quiz_item.get(position).get("title"));
            right_quiz_status.setText(quiz_item.get(position).get("status"));
            right_quiz_rank.setText("-");    // only available to do after quiz can be submitted and can see results
            right_quiz_score.setText("-");   // only available to do after quiz can be submitted and can see results

            right_attempt_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigatetoUnitActivity(quiz_item.get(position).get("quiz_id"));
                }
            });

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
