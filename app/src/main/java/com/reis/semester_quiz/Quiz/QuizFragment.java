package com.reis.semester_quiz.Quiz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.reis.semester_quiz.DashboardActivity;
import com.reis.semester_quiz.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;

public class QuizFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private static final String ARG_LENGTH = "length";
    private static final String ARG_QUESTION = "question";
    private static final String ARG_ANSWERS = "answers";
    private static final String ARG_QUIZ_ID = "quiz_id";

    private int position, length;
    String _token, quiz_id;
    ProgressDialog prgDialog;
    HashMap<String, String> question;
    ArrayList<HashMap<String, String>> answers;
//    String API_URL = "http://10.0.2.2:8000/api/";
    String API_URL = "http://192.168.43.2:8000/api/";

    public static QuizFragment newInstance(int position, int length, HashMap<String, String> question, ArrayList<HashMap<String, String>> answers, String quiz_id) {
        QuizFragment f = new QuizFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        b.putInt(ARG_LENGTH, length);
        b.putString(ARG_QUIZ_ID, quiz_id);
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
        quiz_id = getArguments().getString(ARG_QUIZ_ID);
        question = (HashMap<String, String>) getArguments().getSerializable(ARG_QUESTION);
        answers = (ArrayList<HashMap<String, String>>) getArguments().getSerializable(ARG_ANSWERS);

        // get token from shared preferences
        SharedPreferences preferences = getActivity().getSharedPreferences("semester_quiz", MODE_PRIVATE);
        _token = preferences.getString("_token", null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prgDialog = new ProgressDialog(getContext());
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);

        // generate page depends on question no (as long not submit page)
        if (question.get("question_no").equals("n")) {

            // TODO: update the questions no with their answer
            View view = inflater.inflate(R.layout.quiz_submit, container, false);

            ListView answersListView = (ListView) view.findViewById(R.id.answers_list);
            ArrayAdapter<HashMap<String, String>> answersadapter = new AdapterQuizAnswerList(getContext(), answers);
            answersListView.setAdapter(answersadapter);

            Button submit = (Button) view.findViewById(R.id.submit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<JSONObject> list_answer_json = new ArrayList<JSONObject>();
                    for (HashMap<String, String> answer : answers) {
                        JSONObject obj = new JSONObject(answer);
                        list_answer_json.add(obj);
                    }

                    JSONArray answers_json = new JSONArray(list_answer_json);
                    RequestParams params = new RequestParams();
                    params.put("answers", answers_json);
                    invokeWS(params);
                }
            });
            return view;
        } else {
            View view = inflater.inflate(R.layout.quiz_questions, container, false);

            final Integer question_no = Integer.valueOf(question.get("question_no"));

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

    public void invokeWS(RequestParams params){
        // Show Progress Dialog
        prgDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(API_URL + "quizzes/submit/" + quiz_id + "?token=" + _token, params ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Hide Progress Dialog
                prgDialog.hide();
                Intent backToDashboard = new Intent(getContext(), DashboardActivity.class);
                getContext().startActivity(backToDashboard);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(getContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getContext(), "Status: " + statusCode, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
