package com.reis.semester_quiz.Unit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.reis.semester_quiz.Auth.Utility;
import com.reis.semester_quiz.DashboardActivity;
import com.reis.semester_quiz.Quiz.QuizActivity;
import com.reis.semester_quiz.R;
import com.reis.semester_quiz.Unit.Pages.UnitInfoActivity;
import com.reis.semester_quiz.Unit.Pages.UnitTeamActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class UnitActivity extends AppCompatActivity {

    private String UNIT_ID, UNIT_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unit_index);

        // get from bundle
        UNIT_ID = getIntent().getExtras().getString("unit_id");
        UNIT_NAME = getIntent().getExtras().getString("unit_name");
        getSupportActionBar().setTitle(UNIT_NAME);

        // invoke REST Call
        invokeWS();
    }

    @Override

    public void onBackPressed() {
        Intent backIntent = new Intent(this, DashboardActivity.class);
        startActivity(backIntent);
    }

    public void navigateToUnitInfo(View view) {
        Intent unitInfoIntent = new Intent(this, UnitInfoActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("unit_id", UNIT_ID);
        unitInfoIntent.putExtras(bundle);

        startActivity(unitInfoIntent);
    }

    public void navigateToTeamInfo(View view) {
        Intent unitTeamInfoIntent = new Intent(this, UnitTeamActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("unit_id", UNIT_ID);
        bundle.putString("unit_name", UNIT_NAME);
        unitTeamInfoIntent .putExtras(bundle);

        startActivity(unitTeamInfoIntent );
    }

    public void navigateToChat(View view) {
        Toast.makeText(this, "Test Chat Button", Toast.LENGTH_SHORT).show();
    }

    public void navigateToMain(View view) {
        Intent backIntent = new Intent(this, DashboardActivity.class);
        startActivity(backIntent);
    }

    public void navigateToAccount(View view) {
        Toast.makeText(getApplicationContext(), "Test Account Button", Toast.LENGTH_SHORT).show();
    }

    public void navigateToInbox(View view) {
        Toast.makeText(getApplicationContext(), "Test Inbox Button", Toast.LENGTH_SHORT).show();
    }

    public void navigateToCalendar(View view) {
        Toast.makeText(getApplicationContext(), "Test Calendar Button", Toast.LENGTH_SHORT).show();
    }

    /**
     * GET|HEAD
     * /api/quizzes/unit/{unit_id}
     * Get in JSONArray all quizzes available in this list - later to be separated into individual and group
     * */
    public void invokeWS(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Utility.API_URL() + "quizzes/unit/" + UNIT_ID + "?token=" + Utility.getToken() ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    // JSON Object
                    String jsonstring = new String (responseBody);
                    JSONArray jsonArray = new JSONArray(jsonstring);

                    // populate the quizzes
                    final ArrayList<HashMap<String, String>> individual_list_items = new ArrayList<HashMap<String, String>>();
                    final ArrayList<HashMap<String, String>> group_list_items = new ArrayList<HashMap<String, String>>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject quiz_data = jsonArray.getJSONObject(i);
                        JSONObject group = new JSONObject(quiz_data.getString("quiz"));
                        JSONObject quiz = new JSONObject(group.getString("quiz"));
                        JSONObject this_student = new JSONObject(quiz_data.getString("this_student"));

                        Boolean attempted = quiz_data.getBoolean("has_been_attempted");
                        Integer answers_count = quiz_data.getInt("answers_count");
                        Integer correct_count = quiz_data.getInt("correct_count");
                        Integer total_students = quiz_data.getInt("total_students");
                        Integer total_teams = quiz_data.getInt("total_teams");
                        Integer is_group_leader = this_student.getInt("is_group_leader");

                        Integer quiz_id = quiz.getInt("id");

                        HashMap<String, String> data = new HashMap<String, String>();
                        data.put("quiz_id", String.valueOf(quiz_id));
                        data.put("title", quiz.getString("title"));
                        data.put("type", quiz.getString("type"));
                        data.put("has_been_attempted", String.valueOf(attempted));
                        data.put("answers_count", String.valueOf(answers_count));
                        data.put("correct_count", String.valueOf(correct_count));
                        data.put("total_students", String.valueOf(total_students));
                        data.put("total_teams", String.valueOf(total_teams));
                        data.put("is_group_leader", String.valueOf(is_group_leader));

                        // if already attempted, this student should have rank. add it.
                        if (!quiz_data.isNull("rank")) {
                            JSONObject rank = new JSONObject(quiz_data.getString("rank"));
                            Integer ranking_no = rank.getInt("rank_no");
                            Integer ranking_score = rank.getInt("score");
                            data.put("rank_no", String.valueOf(ranking_no));
                            data.put("rank_score", String.valueOf(ranking_score));
                        }

                        // separate the quizzes into two
                        if (quiz.getString("type").toLowerCase().equals("individual")) {
                            individual_list_items.add(data);
                        }
                        else if (quiz.getString("type").toLowerCase().equals("group")) {
                            group_list_items.add(data);
                        }
                    }

                    /**
                     * IMPLEMENTATION CHANGE
                     * Retrieving data remains the same
                     * Adding into lists change to enabling the existing buttons.
                     * **/

                    /** It is okay to hardcode 1,2,3 because only 3 quizzes will be released every semester - business rules size constraints **/
                    // Enables the quiz based on the number of quiz
                    for (int i = 0; i < individual_list_items.size(); i++) {
                        populateIQuizzes(individual_list_items.get(i));
                    }

                    if (group_list_items.get(0).get("is_group_leader").equals("0")) {
                        TextView TQuiz1_status = (TextView) findViewById(R.id.TQuiz1_status);
                        TextView TQuiz2_status = (TextView) findViewById(R.id.TQuiz2_status);
                        TextView TQuiz3_status = (TextView) findViewById(R.id.TQuiz3_status);

                        String for_leader_only = "TEAM LEADER\nONLY";
                        TQuiz1_status.setText(for_leader_only);
                        TQuiz2_status.setText(for_leader_only);
                        TQuiz3_status.setText(for_leader_only);
                    } else {
                        for (int i = 0; i < group_list_items.size(); i++) {
                            populateTQuizzes(group_list_items.get(i));
                        }
                    }

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
                    Toast.makeText(getApplicationContext(), "(onFailure 403). Something is wrong with the token/authentication. Check other pages.", Toast.LENGTH_LONG).show();
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

            /**
             * go through from the loop and populate team quizzes
             * */
            private void populateTQuizzes(final HashMap<String, String> group_list_item) {
                String quiz_title = "t" + group_list_item.get("title");
                String is_attempted = group_list_item.get("has_been_attempted");
                Integer is_group_leader = Integer.parseInt(group_list_item.get("is_group_leader"));
                final String type = group_list_item.get("type");

                if (quiz_title.contains("1")) {
                    LinearLayout TQuiz1 = (LinearLayout) findViewById(R.id.TQuiz1);
                    TextView TQuiz1_status = (TextView) findViewById(R.id.TQuiz1_status);
                    TextView TQuiz1_title = (TextView) findViewById(R.id.TQuiz1_title);

                    // if already attempted, not allow click
                    if (is_attempted.toLowerCase().equals("true")) {
                        String rank = group_list_item.get("rank_no") + " / " + group_list_item.get("total_teams");
                        String score = group_list_item.get("rank_score") + "%";
                        String quiz_status = "RANK: "+ rank +"\nSCORE: " + score;

                        TQuiz1.setClickable(false);
                        TQuiz1_title.setText(quiz_title);
                        TQuiz1_status.setText(quiz_status);
                    } else {
                        // if not attempted, allow click and set text to "TEST NOW"
                        TQuiz1_status.setText("TEST NOW");
                        TQuiz1_title.setText(quiz_title);
                        TQuiz1.setClickable(true);
                        TQuiz1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String quiz_id = group_list_item.get("quiz_id");
                                String quiz_name = group_list_item.get("title");
                                navigateToQuiz(v, quiz_id, quiz_name, type);
                            }
                        });
                    }

                } else if (quiz_title.contains("2")) {
                    LinearLayout TQuiz2 = (LinearLayout) findViewById(R.id.TQuiz2);
                    TextView TQuiz2_status = (TextView) findViewById(R.id.TQuiz2_status);
                    TextView TQuiz2_title = (TextView) findViewById(R.id.TQuiz2_title);

                    // if already attempted, not allow click
                    if (is_attempted.toLowerCase().equals("true")) {
                        String rank = group_list_item.get("rank_no") + " / " + group_list_item.get("total_teams");
                        String score = group_list_item.get("rank_score") + "%";
                        String quiz_status = "RANK: "+ rank +"\nSCORE: " + score;

                        TQuiz2.setClickable(false);
                        TQuiz2_title.setText(quiz_title);
                        TQuiz2_status.setText(quiz_status);
                    } else {
                        // if not attempted, allow click and set text to "TEST NOW"
                        TQuiz2_status.setText("TEST NOW");
                        TQuiz2_title.setText(quiz_title);
                        TQuiz2.setClickable(true);
                        TQuiz2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String quiz_id = group_list_item.get("quiz_id");
                                String quiz_name = group_list_item.get("title");
                                navigateToQuiz(v, quiz_id, quiz_name, type);
                            }
                        });
                    }

                } else if (quiz_title.contains("3")) {
                    LinearLayout TQuiz3 = (LinearLayout) findViewById(R.id.TQuiz3);
                    TextView TQuiz3_status = (TextView) findViewById(R.id.TQuiz3_status);
                    TextView TQuiz3_title = (TextView) findViewById(R.id.TQuiz3_title);

                    // if already attempted, not allow click
                    if (is_attempted.toLowerCase().equals("true")) {
                        String rank = group_list_item.get("rank_no") + " / " + group_list_item.get("total_teams");
                        String score = group_list_item.get("rank_score") + "%";
                        String quiz_status = "RANK: "+ rank +"\nSCORE: " + score;

                        TQuiz3.setClickable(false);
                        TQuiz3_title.setText(quiz_title);
                        TQuiz3_status.setText(quiz_status);
                    } else {
                        // if not attempted, allow click and set text to "TEST NOW"
                        TQuiz3_status.setText("TEST NOW");
                        TQuiz3_title.setText(quiz_title);
                        TQuiz3.setClickable(true);
                        TQuiz3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String quiz_id = group_list_item.get("quiz_id");
                                String quiz_name = group_list_item.get("title");
                                navigateToQuiz(v, quiz_id, quiz_name, type);
                            }
                        });
                    }
                }

                /***
                 * else
                 * by default in .xml, the button/grid is set to "TEST CLOSED", not clickable
                 * */
            }

            /**
             * go through from the loop and populate individual quizzes
             * */
            private void populateIQuizzes(final HashMap<String, String> individual_list_item) {
                String quiz_title = "i" + individual_list_item.get("title");
                String is_attempted = individual_list_item.get("has_been_attempted");
                final String type = individual_list_item.get("type");

                if (quiz_title.contains("1")) {
                    LinearLayout IQuiz1 = (LinearLayout) findViewById(R.id.IQuiz1);
                    TextView IQuiz1_status = (TextView) findViewById(R.id.IQuiz1_status);
                    TextView IQuiz1_title = (TextView) findViewById(R.id.IQuiz1_title);

                    // if already attempted, not allow click
                    if (is_attempted.toLowerCase().equals("true")) {
                        String rank = individual_list_item.get("rank_no") + " / " + individual_list_item.get("total_teams");
                        String score = individual_list_item.get("rank_score") + "%";
                        String quiz_status = "RANK: "+ rank +"\nSCORE: " + score;


                        IQuiz1.setClickable(false);
                        IQuiz1_title.setText(quiz_title);
                        IQuiz1_status.setText(quiz_status);
                    } else {
                        // if not attempted, allow click and set text to "TEST NOW"
                        IQuiz1_status.setText("TEST NOW");
                        IQuiz1_title.setText(quiz_title);
                        IQuiz1.setClickable(true);
                        IQuiz1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String quiz_id = individual_list_item.get("quiz_id");
                                String quiz_name = individual_list_item.get("title");
                                navigateToQuiz(v, quiz_id, quiz_name, type);
                            }
                        });
                    }

                } else if (quiz_title.contains("2")) {
                    LinearLayout IQuiz2 = (LinearLayout) findViewById(R.id.IQuiz2);
                    TextView IQuiz2_status = (TextView) findViewById(R.id.IQuiz2_status);
                    TextView IQuiz2_title = (TextView) findViewById(R.id.IQuiz2_title);

                    // if already attempted, not allow click
                    if (is_attempted.toLowerCase().equals("true")) {
                        String rank = individual_list_item.get("rank_no") + " / " + individual_list_item.get("total_teams");
                        String score = individual_list_item.get("rank_score") + "%";
                        String quiz_status = "RANK: "+ rank +"\nSCORE: " + score;

                        IQuiz2.setClickable(false);
                        IQuiz2_title.setText(quiz_title);
                        IQuiz2_status.setText(quiz_status);
                    } else {
                        // if not attempted, allow click and set text to "TEST NOW"
                        IQuiz2_status.setText("TEST NOW");
                        IQuiz2_title.setText(quiz_title);
                        IQuiz2.setClickable(true);
                        IQuiz2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String quiz_id = individual_list_item.get("quiz_id");
                                String quiz_name = individual_list_item.get("title");
                                navigateToQuiz(v, quiz_id, quiz_name, type);
                            }
                        });
                    }

                } else if (quiz_title.contains("3")) {
                    LinearLayout IQuiz3 = (LinearLayout) findViewById(R.id.IQuiz3);
                    TextView IQuiz3_status = (TextView) findViewById(R.id.IQuiz3_status);
                    TextView IQuiz3_title = (TextView) findViewById(R.id.IQuiz3_title);

                    // if already attempted, not allow click
                    if (is_attempted.toLowerCase().equals("true")) {
                        String rank = individual_list_item.get("rank_no") + " / " + individual_list_item.get("total_teams");
                        String score = individual_list_item.get("rank_score") + "%";
                        String quiz_status = "RANK: "+ rank +"\nSCORE: " + score;

                        IQuiz3.setClickable(false);
                        IQuiz3_title.setText(quiz_title);
                        IQuiz3_status.setText(quiz_status);
                    } else {
                        // if not attempted, allow click and set text to "TEST NOW"
                        IQuiz3_status.setText("TEST NOW");
                        IQuiz3_title.setText(quiz_title);
                        IQuiz3.setClickable(true);
                        IQuiz3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String quiz_id = individual_list_item.get("quiz_id");
                                String quiz_name = individual_list_item.get("title");
                                navigateToQuiz(v, quiz_id, quiz_name, type);
                            }
                        });
                    }
                }

                /***
                 * else
                 * by default in .xml, the button/grid is set to "TEST CLOSED", not clickable
                 * */
            }

            public void navigateToQuiz(View view, String quiz_id, String quiz_name, String type) {
                // switch page intent
                Intent quizintent = new Intent(getApplicationContext(), QuizActivity.class);

                // add to bundle
                Bundle bundle = new Bundle();
                bundle.putString("quiz_id", quiz_id);
                bundle.putString("quiz_name", quiz_name);
                bundle.putString("quiz_type", type);
                quizintent.putExtras(bundle);

                startActivity(quizintent);
            }
        });
    }
}
