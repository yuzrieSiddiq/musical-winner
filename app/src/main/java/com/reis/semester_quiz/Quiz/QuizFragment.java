package com.reis.semester_quiz.Quiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.reis.semester_quiz.Auth.LoginActivity;
import com.reis.semester_quiz.Auth.Utility;
import com.reis.semester_quiz.DashboardActivity;
import com.reis.semester_quiz.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;

public class QuizFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private static final String ARG_LENGTH   = "length";
    private static final String ARG_QUESTION = "question";
    private static final String ARG_ANSWERS  = "answers";
    private static final String ARG_QUIZ_ID  = "quiz_id";
    private static final String ARG_QUIZ_TYPE= "quiz_type";

    private String color_darkgrey= "#4B4B4B";
    private String color_white   = "#FFFFFF";
    private String color_green   = "#679E36";
    private String color_red     = "#C64B46";
    private String color_answerA = "#679E36";
    private String color_answerB = "#C64B46";
    private String color_answerC = "#F3822C";
    private String color_answerD = "#3F9EDE";
    private String color_answerE = "#4D62BB";
    private String color_maintheme = "#5160BB";

    private int position, length;
    private String quiz_id, quiz_type;
    private Boolean quiz_tutorial;
    private HashMap<String, String> question;
    private ArrayList<HashMap<String, String>> answers;
    private Typeface typeface, typeface2;

    public static QuizFragment newInstance(int position, int length, HashMap<String, String> question, ArrayList<HashMap<String, String>> answers, String quiz_id, String quiz_type) {
        QuizFragment f = new QuizFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        b.putInt(ARG_LENGTH, length);
        b.putString(ARG_QUIZ_ID, quiz_id);
        b.putSerializable(ARG_QUESTION, question);
        b.putSerializable(ARG_ANSWERS, answers);
        b.putString(ARG_QUIZ_TYPE, quiz_type);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        position = getArguments().getInt(ARG_POSITION);
        length = getArguments().getInt(ARG_LENGTH);
        quiz_id = getArguments().getString(ARG_QUIZ_ID);
        quiz_type = getArguments().getString(ARG_QUIZ_TYPE);
        question = (HashMap<String, String>) getArguments().getSerializable(ARG_QUESTION);
        answers = (ArrayList<HashMap<String, String>>) getArguments().getSerializable(ARG_ANSWERS);

        // get quiz_tutorial from shared preferences
        SharedPreferences preferences = getActivity().getSharedPreferences("semester_quiz", MODE_PRIVATE);
        quiz_tutorial = preferences.getBoolean("quiz_tutorial", true);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * Changing the fonts used:
         * Question: Roboto-Regular
         * Answers: Roboto-Light
         * */
        AssetManager assetManager = getContext().getAssets();
        typeface = Typeface.createFromAsset(assetManager, "fonts/Roboto-Light.ttf");
        typeface = Typeface.createFromAsset(assetManager, "fonts/Roboto-Regular.ttf");

        /**
         * Generate two kinds of layouts
         * 1. If this is last page, generate submit page
         * 2. If this is not last page, generate questions
         * */
        if (question.get("question_no").equals("n")) {
            // 1. If this is last page, generate submit page
            View view = inflater.inflate(R.layout.quiz_submit, container, false);

            final ListView answersListView = (ListView) view.findViewById(R.id.answers_list);
            final ArrayAdapter<HashMap<String, String>> answersadapter = new AdapterQuizAnswerList(getContext(), answers);
            answersListView.setAdapter(answersadapter);

            // sends a POST request on submit
            // TODO: Confirmation Dialog Prompt to submit
            Button submit = (Button) view.findViewById(R.id.submit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    alertDialogBuilder
                            .setTitle("SUBMISSION")
                            .setMessage("Confirm submission?")
                            .setCancelable(true)
                            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
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
                            })
                            .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });

                    // create the dialog prompt
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });

            // pull to refresh
            final SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    answersadapter.notifyDataSetChanged();
                    answersListView.setAdapter(answersadapter);
                    swipeContainer.setRefreshing(false);
                }
            });

            submit.setTypeface(typeface);
            return view;

        } else {
            // 2. If this is not last page, generate questions
            // separate implementation for individual and group quizzes

            View view = inflater.inflate(R.layout.quiz_questions, container, false);
            populateIQuestion(view);

            return view;
        }
    }

    public void populateIQuestion(View view) {
        final Integer question_no = Integer.valueOf(question.get("question_no"));
        final String correct_answer = question.get("correct_answer");
        final String answer1 = question.get("answer1");
        final String answer2 = question.get("answer2");
        final String answer3 = question.get("answer3");
        final String answer4 = question.get("answer4");

        // overlay tutorial on first time use
        // TODO: remove this tutorial
        if (position == 0) {
            final RelativeLayout overlayLayout = (RelativeLayout) view.findViewById(R.id.rlOverlay);

            if (quiz_tutorial) {
                overlayLayout.setVisibility(View.VISIBLE);
            } else {
                overlayLayout.setVisibility(View.GONE);
            }

            overlayLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor preferences_editor= getActivity().getSharedPreferences("semester_quiz", MODE_PRIVATE).edit();
                    preferences_editor.putBoolean("quiz_tutorial", false);
                    preferences_editor.apply();

                    Animation fadeOut = new AlphaAnimation(1, 0);
                    fadeOut.setInterpolator(new AccelerateInterpolator());
                    fadeOut.setDuration(300);

                    fadeOut.setAnimationListener(new Animation.AnimationListener()
                    {
                        public void onAnimationEnd(Animation animation)
                        {
                            overlayLayout.setVisibility(View.GONE);
                        }
                        public void onAnimationRepeat(Animation animation) {}
                        public void onAnimationStart(Animation animation) {}
                    });

                    overlayLayout.startAnimation(fadeOut);
                }
            });
        }

        // get the xml layouts and set the texts
        TextView questionTextView = (TextView) view.findViewById(R.id.question);
        final Button answerA = (Button) view.findViewById(R.id.answerA);
        final Button answerB = (Button) view.findViewById(R.id.answerB);
        final Button answerC = (Button) view.findViewById(R.id.answerC);
        final Button answerD = (Button) view.findViewById(R.id.answerD);

        questionTextView.setText(question.get("question"));
        answerA.setText(answer1);
        answerB.setText(answer2);
        answerC.setText(answer3);
        answerD.setText(answer4);

        /**
         * Implementation of answering the questions
         * Individual: Can choose only once, will light up only the chosen answer
         * Group: Can choose many times, will light up each chosen answers. Correct answers shows '*', Wrong answers shows 'x'
         * */

        if (quiz_type.toLowerCase().equals("individual")) {
            // 1. individual quiz
            answerA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerA.getBackground().setColorFilter(Color.parseColor(color_answerA), PorterDuff.Mode.MULTIPLY);
                    answerB.getBackground().setColorFilter(Color.parseColor(color_darkgrey), PorterDuff.Mode.MULTIPLY);
                    answerC.getBackground().setColorFilter(Color.parseColor(color_darkgrey), PorterDuff.Mode.MULTIPLY);
                    answerD.getBackground().setColorFilter(Color.parseColor(color_darkgrey), PorterDuff.Mode.MULTIPLY);
                    answers.get(question_no).put("answer", question.get("answer1"));
                }
            });

            answerB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerA.getBackground().setColorFilter(Color.parseColor(color_darkgrey), PorterDuff.Mode.MULTIPLY);
                    answerB.getBackground().setColorFilter(Color.parseColor(color_answerB), PorterDuff.Mode.MULTIPLY);
                    answerC.getBackground().setColorFilter(Color.parseColor(color_darkgrey), PorterDuff.Mode.MULTIPLY);
                    answerD.getBackground().setColorFilter(Color.parseColor(color_darkgrey), PorterDuff.Mode.MULTIPLY);
                    answers.get(question_no).put("answer", question.get("answer2"));
                }
            });

            answerC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerA.getBackground().setColorFilter(Color.parseColor(color_darkgrey), PorterDuff.Mode.MULTIPLY);
                    answerB.getBackground().setColorFilter(Color.parseColor(color_darkgrey), PorterDuff.Mode.MULTIPLY);
                    answerC.getBackground().setColorFilter(Color.parseColor(color_answerC), PorterDuff.Mode.MULTIPLY);
                    answerD.getBackground().setColorFilter(Color.parseColor(color_darkgrey), PorterDuff.Mode.MULTIPLY);
                    answers.get(question_no).put("answer", question.get("answer3"));
                }
            });

            answerD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerA.getBackground().setColorFilter(Color.parseColor(color_darkgrey), PorterDuff.Mode.MULTIPLY);
                    answerB.getBackground().setColorFilter(Color.parseColor(color_darkgrey), PorterDuff.Mode.MULTIPLY);
                    answerC.getBackground().setColorFilter(Color.parseColor(color_darkgrey), PorterDuff.Mode.MULTIPLY);
                    answerD.getBackground().setColorFilter(Color.parseColor(color_answerD), PorterDuff.Mode.MULTIPLY);
                    answers.get(question_no).put("answer", question.get("answer4"));
                }
            });

        } else {
            // 2. group quiz

            // default color: darkgrey
            answerA.getBackground().setColorFilter(Color.parseColor(color_darkgrey), PorterDuff.Mode.MULTIPLY);
            answerB.getBackground().setColorFilter(Color.parseColor(color_darkgrey), PorterDuff.Mode.MULTIPLY);
            answerC.getBackground().setColorFilter(Color.parseColor(color_darkgrey), PorterDuff.Mode.MULTIPLY);
            answerD.getBackground().setColorFilter(Color.parseColor(color_darkgrey), PorterDuff.Mode.MULTIPLY);

            // onclick: if correct, button turns green
            // if wrong answer, button turns red
            answerA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (answer1.equals(correct_answer)) {
                        answerA.getBackground().setColorFilter(Color.parseColor(color_green), PorterDuff.Mode.MULTIPLY);
                    } else {
                        answerA.getBackground().setColorFilter(Color.parseColor(color_red), PorterDuff.Mode.MULTIPLY);
                        addIncorrectCount(question_no);
                    }
                    addAnswersPoints(question_no);
                }
            });

            answerB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (answer2.equals(correct_answer)) {
                        answerB.getBackground().setColorFilter(Color.parseColor(color_green), PorterDuff.Mode.MULTIPLY);
                    } else {
                        answerB.getBackground().setColorFilter(Color.parseColor(color_red), PorterDuff.Mode.MULTIPLY);
                        addIncorrectCount(question_no);
                    }
                    addAnswersPoints(question_no);
                }
            });

            answerC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (answer3.equals(correct_answer)) {
                        answerC.getBackground().setColorFilter(Color.parseColor(color_green), PorterDuff.Mode.MULTIPLY);
                    } else {
                        answerC.getBackground().setColorFilter(Color.parseColor(color_red), PorterDuff.Mode.MULTIPLY);
                        addIncorrectCount(question_no);
                    }
                    addAnswersPoints(question_no);
                }
            });

            answerD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (answer4.equals(correct_answer)) {
                        answerD.getBackground().setColorFilter(Color.parseColor(color_green), PorterDuff.Mode.MULTIPLY);
                    } else {
                        answerD.getBackground().setColorFilter(Color.parseColor(color_red), PorterDuff.Mode.MULTIPLY);
                        addIncorrectCount(question_no);
                    }
                    addAnswersPoints(question_no);
                }
            });
        }

        questionTextView.setTypeface(typeface);
        answerA.setTypeface(typeface2);
        answerB.setTypeface(typeface2);
        answerC.setTypeface(typeface2);
        answerD.setTypeface(typeface2);
    }

    private void addIncorrectCount(Integer question_no) {
        int incorrect_count = Integer.parseInt(answers.get(question_no).get("incorrect_count"));
        incorrect_count++;

        answers.get(question_no).put("incorrect_count", String.valueOf(incorrect_count));
    }

    private void addAnswersPoints(Integer question_no) {
        int incorrect_count = Integer.parseInt(answers.get(question_no).get("incorrect_count"));
        if (incorrect_count == 0) {
            answers.get(question_no).put("answer", "4 POINTS");
        } else if (incorrect_count == 1) {
            answers.get(question_no).put("answer", "2 POINTS");
        } else if (incorrect_count == 2) {
            answers.get(question_no).put("answer", "1 POINTS");
        } else if (incorrect_count >= 3) {
            answers.get(question_no).put("answer", "0 POINTS");
        }
    }

    /**
     * POST | App\Api\V1\Controllers\QuizController@submit_answers
     * /api/quizzes/submit/{quiz_id}           |
     * Sends a POST request to submit the quiz
     * */
    public void invokeWS(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(Utility.API_URL() + "quizzes/submit/" + quiz_id + "?token=" + Utility.getToken(), params ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Intent backToDashboard = new Intent(getContext(), DashboardActivity.class);
                getContext().startActivity(backToDashboard);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(getContext(), "(onFailure 404). Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getContext(), "(onFailure 500). Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 403){
                    Toast.makeText(getContext(), "(onFailure 403). Something is wrong with the token/authentication. Check other pages.", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 401){
                    Toast.makeText(getContext(), "(onFailure 401). Something is wrong with the authentication. Check login.", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getContext(), "(onFailure). Status: " + statusCode, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
