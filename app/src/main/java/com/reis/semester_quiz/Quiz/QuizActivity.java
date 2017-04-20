package com.reis.semester_quiz.Quiz;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.reis.semester_quiz.R;
import com.reis.semester_quiz.Unit.Pages.AdapterUnitQuizList;

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
    private QuizPagerAdapter adapter;

    String API_URL = "http://10.0.2.2:8000/api/";
    String _token, quiz_id;
    ListView quiz_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_index);

        quiz_id = getIntent().getExtras().getString("quiz_id");

        // get token from shared preferences
        SharedPreferences preferences = getSharedPreferences("semester_quiz", MODE_PRIVATE);
        _token = preferences.getString("_token", null);


        // set the original view
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);

        invokeWS();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            handler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            handler.removeCallbacks(what);
        }
    };

    public void invokeWS(){
        // Show Progress Dialog
//        prgDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(API_URL + "quizzes/" + quiz_id+ "?token=" + _token ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Hide Progress Dialog
//                prgDialog.hide();
                try {
                    // JSON Object
                    String jsonstring = new String (responseBody);
                    JSONArray jsonArray = new JSONArray(jsonstring);

                    // populate the quizzes
                    final ArrayList<HashMap<String, String>> questions = new ArrayList<HashMap<String, String>>();
                    Integer question_no = 0;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject question = jsonArray.getJSONObject(i);

                        Integer question_id = question.getInt("id");
                        question_no++;

                        HashMap<String, String> data = new HashMap<String, String>();
                        data.put("question_id", String.valueOf(question_id));
                        data.put("question_no", String.valueOf(question_no));
                        data.put("question", question.getString("question"));
                        data.put("answer1", question.getString("answer1"));
                        data.put("answer2", question.getString("answer2"));
                        data.put("answer3", question.getString("answer3"));
                        data.put("answer4", question.getString("answer4"));
                        data.put("answer5", question.getString("answer5"));
                        data.put("correct_answer", question.getString("correct_answer"));

                        questions.add(data);
                    }

                    HashMap<String, String> data = new HashMap<String, String>();
                    data.put("question_no", "n");
                    questions.add(data);

                    Toast.makeText(QuizActivity.this, questions.toString(), Toast.LENGTH_SHORT).show();

                    // adapter is AdapterUnitQuizList, quiz_list get from unit_quiz_list.xml, in array adapter is from unit_quiz_list_fragment
//                    ArrayAdapter arrayAdapter = new AdapterUnitQuizList(getApplicationContext(), questions);
//                    quiz_list= (ListView) view.findViewById(R.id.mylist);
//                    quiz_list.setAdapter(arrayAdapter);

                    adapter = new QuizPagerAdapter(getSupportFragmentManager(), questions);

                    pager.setAdapter(adapter);

                    final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                            .getDisplayMetrics());
                    pager.setPageMargin(pageMargin);

                    tabs.setViewPager(pager);


                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Hide Progress Dialog
                //prgDialog.hide();
                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getApplicationContext(), "Status: " + statusCode, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
