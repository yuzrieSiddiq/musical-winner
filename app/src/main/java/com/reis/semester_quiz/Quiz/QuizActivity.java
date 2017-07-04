package com.reis.semester_quiz.Quiz;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.reis.semester_quiz.Auth.Utility;
import com.reis.semester_quiz.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class QuizActivity extends AppCompatActivity {

    private final Handler handler = new Handler();

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;

    String quiz_id, quiz_name;
    ListView quiz_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_index);

        // get from bundle
        quiz_name = getIntent().getExtras().getString("quiz_name");
        quiz_id = getIntent().getExtras().getString("quiz_id");

        getSupportActionBar().setTitle(quiz_name);

        // set the original view
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setIndicatorColorResource(R.color.answerD);
        tabs.setIndicatorHeight(8);
        pager = (ViewPager) findViewById(R.id.pager);

        invokeWS();

        final ImageView leftarrow = (ImageView) findViewById(R.id.prevQuestion);
        final ImageView rightarrow = (ImageView) findViewById(R.id.nextQuestion);

        // on first question
        // set left invisible on first page
        leftarrow.setVisibility(View.INVISIBLE);

        // on left or right swipe
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            /**
             * Check on first and last page.
             * Hide the left and right arrow
             * */
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    // set left arrow invisible on first page
                    leftarrow.setVisibility(View.INVISIBLE);
                    rightarrow.setVisibility(View.VISIBLE);
                } else if (position == tabs.getTabCount()-1) {
                    // set right arrow invisible on last page
                    leftarrow.setVisibility(View.VISIBLE);
                    rightarrow.setVisibility(View.INVISIBLE);
                } else {
                    // by default, both arrows are visible
                    leftarrow.setVisibility(View.VISIBLE);
                    rightarrow.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void invokeWS() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Utility.API_URL() + "quizzes/" + quiz_id+ "?token=" + Utility.getToken() ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String jsonstring = new String (responseBody);
                    JSONArray jsonArray = new JSONArray(jsonstring);

                    // populate the quizzes
                    final ArrayList<HashMap<String, String>> questions = new ArrayList<HashMap<String, String>>();
                    final ArrayList<HashMap<String, String>> student_answers = new ArrayList<HashMap<String, String>>();

                    Integer question_no = 0;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject question = jsonArray.getJSONObject(i);

                        Integer question_id = question.getInt("id");

                        HashMap<String, String> data_question = new HashMap<String, String>();
                        data_question.put("question_id", String.valueOf(question_id));
                        data_question.put("question_no", String.valueOf(question_no));
                        data_question.put("answer_type", question.getString("answer_type"));
                        data_question.put("question", question.getString("question"));
                        data_question.put("answer1", question.getString("answer1"));
                        data_question.put("answer2", question.getString("answer2"));
                        data_question.put("answer3", question.getString("answer3"));
                        data_question.put("answer4", question.getString("answer4"));
                        data_question.put("answer5", question.getString("answer5"));
                        data_question.put("correct_answer", question.getString("correct_answer"));

                        questions.add(data_question);

                        HashMap<String, String> data_answers = new HashMap<String, String>();
                        data_answers.put("question_id", String.valueOf(question_id));
                        data_answers.put("question_no", String.valueOf(question_no));
                        data_answers.put("answer", "");

                        student_answers.add(data_answers);

                        question_no++;
                    }

                    HashMap<String, String> data = new HashMap<String, String>();
                    data.put("question_no", "n");
                    questions.add(data);

                    QuizPagerAdapter adapter = new QuizPagerAdapter(getSupportFragmentManager(), questions, student_answers, quiz_id);
                    pager.setAdapter(adapter);
                    pager.setOffscreenPageLimit(questions.size());

                    final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
                    pager.setPageMargin(pageMargin);
                    tabs.setViewPager(pager);

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "(onSuccess). Error Occured [Server's JSON response might be invalid]!. Probably JSON response is []", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), "(onFailure 404). Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getApplicationContext(), "(onFailure 500). Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 403){
                    Toast.makeText(getApplicationContext(), "(onFailure 403). Something is wrong with the token. Check other pages.", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 401){
                    Toast.makeText(getApplicationContext(), "(onFailure 401). Something is wrong with the authentication. Check login.", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getApplicationContext(), "(onFailure). Status: " + statusCode, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void navigatePreviousQuestion(View view) {
        pager.setCurrentItem(pager.getCurrentItem()-1);
    }

    public void navigateNextQuestion(View view) {
        pager.setCurrentItem(pager.getCurrentItem()+1);
    }
}
