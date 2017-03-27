package com.reis.semester_quiz.Unit.Pages;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.reis.semester_quiz.Quiz.QuizActivity;
import com.reis.semester_quiz.R;

/**
 * Created by reis on 22/03/2017.
 */

public class CustomAdapter extends ArrayAdapter<String> {

    public CustomAdapter(@NonNull Context context, String [] values) {
        super(context, R.layout.testlayout, values);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.testlayout, parent, false);

        // set the values for each text views and buttons here
        Button attemptQuiz = (Button) view.findViewById(R.id.attemptQuiz);
        attemptQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent quizpageIntent = new Intent(getContext(), QuizActivity.class);
                getContext().startActivity(quizpageIntent);
            }
        });

        return view;
    }
}
