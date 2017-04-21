package com.reis.semester_quiz.Quiz;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.reis.semester_quiz.DashboardActivity;
import com.reis.semester_quiz.R;

import java.util.ArrayList;
import java.util.HashMap;

public class QuizFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private static final String ARG_LENGTH = "length";
    private static final String ARG_QUESTION = "question";
    private static final String ARG_ANSWERS = "answers";
    private String [] questions = new String[] {
            "Stereotypes are useful as they allow us to categorize lots of information easily. Rigid stereotypes about people generally lead to prejudice.  Stereotyping is considered as ",
            "Highly prejudiced people tend to have what is referred to by psychologists as an authoritarian personality. Which one of the following is not considered as one of the characteristics of authoritarian personality:",
            "We may learn to be prejudiced from home, school, government, workplace, place of worship, and the media. Which of the following is related to the media?",
            "Different people may express prejudice differently. There are people who often disclose outwardly how they are opposed to unequal treatment, but their inner feelings may suggest otherwise. They may say they are egalitarian and use that open display as an excuse when they act in a way that is not in the interests of diversity. This way of expressing prejudice is known as"
    };

    private int position, length;
    HashMap<String, String> question;
    ArrayList<HashMap<String, String>> answers;

    public static QuizFragment newInstance(int position, int length, HashMap<String, String> question, ArrayList<HashMap<String, String>> answers) {
        QuizFragment f = new QuizFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        b.putInt(ARG_LENGTH, length);
        b.putSerializable(ARG_QUESTION, question);
        b.putSerializable(ARG_ANSWERS, answers);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        length = getArguments().getInt(ARG_LENGTH);
        question = (HashMap<String, String>) getArguments().getSerializable(ARG_QUESTION);
        answers = (ArrayList<HashMap<String, String>>) getArguments().getSerializable(ARG_ANSWERS);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // start counting from 0, hence -1
        if (question.get("question_no").equals("n")) {

            // TODO: update the questions no with their answer
            View view = inflater.inflate(R.layout.quiz_submit, container, false);
            Button submit = (Button) view.findViewById(R.id.submit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), answers.toString(), Toast.LENGTH_SHORT).show();
//                    Intent backToDashboard = new Intent(getContext(), DashboardActivity.class);
//                    getContext().startActivity(backToDashboard);
//                    Toast.makeText(getContext(), "Quiz has successfully been submitted", Toast.LENGTH_SHORT).show();
                }
            });
            return view;
        } else {
            View view = inflater.inflate(R.layout.quiz_questions, container, false);

            final Integer question_no = Integer.valueOf(question.get("question_no"));
            Toast.makeText(getContext(), String.valueOf(question_no), Toast.LENGTH_SHORT).show();

            TextView questionTextView = (TextView) view.findViewById(R.id.question);
            Button answerA = (Button) view.findViewById(R.id.answerA);
            Button answerB = (Button) view.findViewById(R.id.answerB);
            Button answerC = (Button) view.findViewById(R.id.answerC);
            Button answerD = (Button) view.findViewById(R.id.answerD);

            questionTextView.setText(question.get("question"));
            answerA.setText(question.get("answer1"));
            answerB.setText(question.get("answer2"));
            answerC.setText(question.get("answer3"));
            answerD.setText(question.get("answer4"));


            answerA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answers.get(question_no).put("answer", question.get("answer1"));
                }
            });

            answerB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answers.get(question_no).put("answer", question.get("answer2"));
                }
            });

            answerC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answers.get(question_no).put("answer", question.get("answer3"));
                }
            });

            answerD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answers.get(question_no).put("answer", question.get("answer4"));
                }
            });

            return view;
        }
    }
}
