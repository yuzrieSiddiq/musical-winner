package com.reis.semester_quiz.Quiz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;
import static com.reis.semester_quiz.R.id.answer;

public class QuizFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private static final String ARG_LENGTH = "length";
    private static final String ARG_QUESTION = "question";
    private static final String ARG_ANSWERS = "answers";
    private static final String ARG_QUIZ_ID = "quiz_id";

    private String color_darkgrey= "#4B4B4B";
    private String color_white   = "#FFFFFF";
    private String color_answerA = "#679E36";
    private String color_answerB = "#C64B46";
    private String color_answerC = "#F3822C";
    private String color_answerD = "#3F9EDE";
    private String color_answerE = "#4D62BB";
    private String color_maintheme = "#5160BB";

    private int position, length;
    String _token, quiz_id;
    Boolean quiz_tutorial;
    ProgressDialog prgDialog;
    HashMap<String, String> question;
    ArrayList<HashMap<String, String>> answers;

//    String API_URL = "http://10.0.2.2:8000/api/";
    String API_URL = "http://52.220.127.134/api/";
    Typeface typeface, typeface2;

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
        setRetainInstance(true);
        position = getArguments().getInt(ARG_POSITION);
        length = getArguments().getInt(ARG_LENGTH);
        quiz_id = getArguments().getString(ARG_QUIZ_ID);
        question = (HashMap<String, String>) getArguments().getSerializable(ARG_QUESTION);
        answers = (ArrayList<HashMap<String, String>>) getArguments().getSerializable(ARG_ANSWERS);

        // get token from shared preferences
        SharedPreferences preferences = getActivity().getSharedPreferences("semester_quiz", MODE_PRIVATE);
        _token = preferences.getString("_token", null);
        quiz_tutorial = preferences.getBoolean("quiz_tutorial", true);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prgDialog = new ProgressDialog(getContext());
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);

        AssetManager assetManager = getContext().getAssets();
        typeface = Typeface.createFromAsset(assetManager, "fonts/Roboto-Light.ttf");
        typeface = Typeface.createFromAsset(assetManager, "fonts/Roboto-Regular.ttf");

        // generate page depends on question no (as long not submit page)
        if (question.get("question_no").equals("n")) {
            View view = inflater.inflate(R.layout.quiz_submit, container, false);

            final ListView answersListView = (ListView) view.findViewById(R.id.answers_list);
            final ArrayAdapter<HashMap<String, String>> answersadapter = new AdapterQuizAnswerList(getContext(), answers);
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

            final SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    answersadapter.notifyDataSetChanged();
                    answersListView.setAdapter(answersadapter);
                    swipeContainer.setRefreshing(false);

//                    Toast.makeText(getContext(), answers.toString(), Toast.LENGTH_SHORT).show();
                }
            });

            submit.setTypeface(typeface);

            return view;
        } else {
            if (question.get("answer_type").toLowerCase().equals("ranking")) {
                View view = inflater.inflate(R.layout.quiz_questions_ranking, container, false);
                populateRankingQuestion(view);

                return view;
            } else {
                View view = inflater.inflate(R.layout.quiz_questions, container, false);
                populateMCQQuestion(view);

                return view;
            }
        }
    }

    public void populateRankingQuestion(View view) {
        final Integer question_no = Integer.valueOf(question.get("question_no"));

        // overlay tutorial on first time use
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

        TextView questionTextView = (TextView) view.findViewById(R.id.question);
        questionTextView.setText(question.get("question"));

        final ArrayList<HashMap<String, String>> answers_ranking = new ArrayList<HashMap<String, String>>();
        for (int i = 4; i < 9; i++) {
            HashMap<String, String> answer = new HashMap<>();
            Integer answerPosition = i - 3;
            String answerAtPosition = "answer" + answerPosition;
            answer.put(answerAtPosition, question.get(answerAtPosition));   // 'answer1: sample answer'
            answer.put(answerAtPosition+"_rank", String.valueOf(0));        // 'answer1_rank': 0

            answers_ranking.add(answer);
        }

        final Integer[] current_rank = {0, 0, 0, 0, 0};
        final String[] answerString = {"", "", "", "", ""};

        final ListView ranking_answers_list = (ListView) view.findViewById(R.id.ranking_answer_list);
        final ArrayAdapter ranking_answers_list_adapter = new AdapterRankingAnswerList(getContext(), answers_ranking);
        ranking_answers_list.setAdapter(ranking_answers_list_adapter);
        ranking_answers_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                final ArrayAdapter<String> dialogAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_item);
                dialogAdapter.add("1");
                dialogAdapter.add("2");
                dialogAdapter.add("3");
                dialogAdapter.add("4");
                dialogAdapter.add("5");

                alertDialogBuilder.setAdapter(dialogAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String strName = dialogAdapter.getItem(which);
                        final Integer answerI = position+1;
                        String answerAtPositionI = "answer" + answerI;
                        String answerAtPositionI_rank = "answer" + answerI + "_rank";

                        TextView answer = (TextView) view.findViewById(R.id.answer);
                        TextView rank_no = (TextView) view.findViewById(R.id.rank_no);
                        rank_no.setText(strName);

                        if (rank_no.getText().equals("1")) {
                            answerString[0] = answer.getText().toString();
                        }

                        if (rank_no.getText().equals("2")) {
                            answerString[1] = answer.getText().toString();
                        }

                        if (rank_no.getText().equals("3")) {
                            answerString[2] = answer.getText().toString();
                        }

                        if (rank_no.getText().equals("4")) {
                            answerString[3] = answer.getText().toString();
                        }

                        if (rank_no.getText().equals("5")) {
                            answerString[4] = answer.getText().toString();
                        }

                        String answerFullString = answerString[0] + ", " + answerString[1] + ", " + answerString[2] + ", " + answerString[3] + ", " + answerString[4];
                        answers.get(question_no).put("answer", answerFullString);
                    }
                });
                alertDialogBuilder.show();
            }
        });
    }

    public void populateMCQQuestion(View view) {
        final Integer question_no = Integer.valueOf(question.get("question_no"));

        // overlay tutorial on first time use
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

        TextView questionTextView = (TextView) view.findViewById(R.id.question);
        final Button answerA = (Button) view.findViewById(R.id.answerA);
        final Button answerB = (Button) view.findViewById(R.id.answerB);
        final Button answerC = (Button) view.findViewById(R.id.answerC);
        final Button answerD = (Button) view.findViewById(R.id.answerD);

        questionTextView.setText(question.get("question"));
        answerA.setText(question.get("answer1"));
        answerB.setText(question.get("answer2"));
        answerC.setText(question.get("answer3"));
        answerD.setText(question.get("answer4"));


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

        questionTextView.setTypeface(typeface);
        answerA.setTypeface(typeface2);
        answerB.setTypeface(typeface2);
        answerC.setTypeface(typeface2);
        answerD.setTypeface(typeface2);
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
//                String jsonstring = new String (responseBody);
//                Toast.makeText(getContext(), jsonstring, Toast.LENGTH_LONG).show();
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
                    String jsonstring = new String (responseBody);
                    Toast.makeText(getContext(), jsonstring, Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getContext(), "Status: " + statusCode, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
