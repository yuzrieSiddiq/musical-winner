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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.reis.semester_quiz.Auth.Utility;
import com.reis.semester_quiz.DashboardActivity;
import com.reis.semester_quiz.R;
import com.reis.semester_quiz.Unit.Pages.AdapterAvailableStudentsList;

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
            if (quiz_type.toLowerCase().equals("individual")) {
                View view = inflater.inflate(R.layout.quiz_questions_ranking, container, false);

                populateIQuestion(view);

                return view;

            } else {

                View view = inflater.inflate(R.layout.quiz_questions_team, container, false);
                populateGQuestion(view);

                return view;
            }
        }
    }

    /**
     * Implementation of answering the questions
     * Individual: Can choose only once, will light up only the chosen answer
     * Group: Can choose many times, will light up each chosen answers. Correct answers shows '*', Wrong answers shows 'x'
     * */

    /** Individual **/
    public void populateIQuestion(View view) {
        final Integer question_no = Integer.valueOf(question.get("question_no"));
        final Integer q_no = question_no+1;
        TextView questionTextView = (TextView) view.findViewById(R.id.question);
        questionTextView.setText("Q" + q_no + ". " + question.get("question"));

        final ArrayList<HashMap<String, String>> answers_ranking = new ArrayList<HashMap<String, String>>();

        for (int i = 4; i < 8; i++) {
            HashMap<String, String> answer = new HashMap<>();
            Integer answerPosition = i - 3;
            String answerAtPosition = "answer" + answerPosition;
            answer.put(answerAtPosition, question.get(answerAtPosition));   // 'answer1: sample answer'
            answer.put(answerAtPosition+"_rank", String.valueOf(0));        // 'answer1_rank': 0

            answers_ranking.add(answer);
        }

        final Integer[] current_rank = {0, 0, 0, 0};
        final String[] answerString = {"", "", "", ""};

        final ListView ranking_answers_list = (ListView) view.findViewById(R.id.ranking_answer_list);
        final ArrayAdapter ranking_answers_list_adapter = new AdapterRankingAnswerList(getContext(), answers_ranking);
        ranking_answers_list.setAdapter(ranking_answers_list_adapter);
        ranking_answers_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                final ArrayAdapter<String> dialogAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_item);

                Integer current_rank_total = 0;
                for (int i = 0; i < current_rank.length; i++) {
                    current_rank_total += current_rank[i];
                }

                /**
                 * Each questions has a maximum marks of 4
                 * If the student decides the answer on A, he can put 4 on the first answer
                 * Otherwise he can split the numbers, 2 on A, 2 on B, 0 on the rest
                 * If already selected 4, the dialog should show only 0...
                 * 0-4, 1-3, 2-2
                 * */
                if (current_rank_total == 4) { // if max
                    dialogAdapter.add("0");
                } else if (current_rank_total == 3) {
                    dialogAdapter.add("1");
                    dialogAdapter.add("0");
                } else if (current_rank_total == 2) {
                    dialogAdapter.add("2");
                    dialogAdapter.add("1");
                    dialogAdapter.add("0");
                } else if (current_rank_total == 1) {
                    dialogAdapter.add("3");
                    dialogAdapter.add("2");
                    dialogAdapter.add("1");
                    dialogAdapter.add("0");
                } else if (current_rank_total == 0) { // if min
                    dialogAdapter.add("4");
                    dialogAdapter.add("3");
                    dialogAdapter.add("2");
                    dialogAdapter.add("1");
                    dialogAdapter.add("0");
                }

                /**
                 * onclick one of the answer will show the dialog prompt
                 * */
                alertDialogBuilder.setAdapter(dialogAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String rank = dialogAdapter.getItem(which);
                        final Integer answerI = position;

                        /** the view here refers to the singulars inside the ListView - only get the rank_no **/
                        TextView rank_no = (TextView) view.findViewById(R.id.rank_no);

                        /** update current_rank and set the rank no on each answer **/
                        current_rank[answerI] = Integer.parseInt(rank);
                        rank_no.setText(rank);

                        /** string of answer to put on the last page **/
                        String answerFullString = "A(" +current_rank[0] + ") B(" + current_rank[1] + ") C(" + current_rank[2] + ") D(" + current_rank[3] +")";
                        answers.get(question_no).put("answer", answerFullString);
//                        Toast.makeText(getContext(), answerFullString, Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialogBuilder.show();
            }
        });
    }

    /** Group **/
    public void populateGQuestion(View view) {
        final Integer question_no = Integer.valueOf(question.get("question_no"));
        final Integer q_no = question_no+1;
        final String correct_answer = question.get("correct_answer");
        final String answer1 = question.get("answer1");
        final String answer2 = question.get("answer2");
        final String answer3 = question.get("answer3");
        final String answer4 = question.get("answer4");

        // get the xml layouts and set the texts
        TextView questionTextView = (TextView) view.findViewById(R.id.question);
        final Button answerA = (Button) view.findViewById(R.id.answerA);
        final Button answerB = (Button) view.findViewById(R.id.answerB);
        final Button answerC = (Button) view.findViewById(R.id.answerC);
        final Button answerD = (Button) view.findViewById(R.id.answerD);

        questionTextView.setText("Q" + q_no + ". " + question.get("question"));
        answerA.setText(answer1);
        answerB.setText(answer2);
        answerC.setText(answer3);
        answerD.setText(answer4);


        final Button answerA_color_green = (Button) view.findViewById(R.id.answerA_color_green);
        final Button answerA_color_red   = (Button) view.findViewById(R.id.answerA_color_red);
        final Button answerB_color_green = (Button) view.findViewById(R.id.answerB_color_green);
        final Button answerB_color_red   = (Button) view.findViewById(R.id.answerB_color_red);
        final Button answerC_color_green = (Button) view.findViewById(R.id.answerC_color_green);
        final Button answerC_color_red   = (Button) view.findViewById(R.id.answerC_color_red);
        final Button answerD_color_green = (Button) view.findViewById(R.id.answerD_color_green);
        final Button answerD_color_red   = (Button) view.findViewById(R.id.answerD_color_red);

        /**
         * onclick: if correct, button turns green
         * if wrong answer, button turns red
         * */
        answerA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answer1.equals(correct_answer)) {
                    answerA_color_green.setVisibility(View.VISIBLE);
                    answerA_color_green.setText(answerA.getText());
                    answerA.setVisibility(View.GONE);
                } else {
                    answerA_color_red.setVisibility(View.VISIBLE);
                    answerA_color_red.setText(answerA.getText());
                    answerA.setVisibility(View.GONE);

                    addIncorrectCount(question_no);
                }
                addAnswersPoints(question_no);
            }
        });

        answerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answer2.equals(correct_answer)) {
                    answerB_color_green.setVisibility(View.VISIBLE);
                    answerB_color_green.setText(answerB.getText());
                    answerB.setVisibility(View.GONE);
                } else {
                    answerB_color_red.setVisibility(View.VISIBLE);
                    answerB_color_red.setText(answerB.getText());
                    answerB.setVisibility(View.GONE);

                    addIncorrectCount(question_no);
                }
                addAnswersPoints(question_no);
            }
        });

        answerC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answer3.equals(correct_answer)) {
                    answerC_color_green.setVisibility(View.VISIBLE);
                    answerC_color_green.setText(answerC.getText());
                    answerC.setVisibility(View.GONE);
                } else {
                    answerC_color_red.setVisibility(View.VISIBLE);
                    answerC_color_red.setText(answerC.getText());
                    answerC.setVisibility(View.GONE);

                    addIncorrectCount(question_no);
                }
                addAnswersPoints(question_no);
            }
        });

        answerD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answer4.equals(correct_answer)) {
                    answerD_color_green.setVisibility(View.VISIBLE);
                    answerD_color_green.setText(answerD.getText());
                    answerD.setVisibility(View.GONE);
                } else {
                    answerD_color_red.setVisibility(View.VISIBLE);
                    answerD_color_red.setText(answerD.getText());
                    answerD.setVisibility(View.GONE);
                    addIncorrectCount(question_no);
                }
                addAnswersPoints(question_no);
            }
        });

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
