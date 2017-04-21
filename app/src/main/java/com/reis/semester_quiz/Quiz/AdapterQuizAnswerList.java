package com.reis.semester_quiz.Quiz;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.reis.semester_quiz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by reis on 22/03/2017.
 */

public class AdapterQuizAnswerList extends ArrayAdapter<HashMap<String, String>> {

    ArrayList<HashMap<String, String>> answers;

    public AdapterQuizAnswerList(@NonNull Context context, ArrayList<HashMap<String, String>> answers) {
        super(context, R.layout.quiz_submit_list_fragment, answers);
        this.answers= answers;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.quiz_submit_list_fragment, parent, false);

        TextView question_no= (TextView) view.findViewById(R.id.question_no);
        TextView question_answer = (TextView) view.findViewById(R.id.answer);

        Integer question_number = Integer.parseInt(answers.get(position).get("question_no"))+1;
        question_no.setText("QUESTION " + question_number);
        question_answer.setText(answers.get(position).get("answer"));

        return view;
    }
}
