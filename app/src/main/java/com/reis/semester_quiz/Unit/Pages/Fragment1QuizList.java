package com.reis.semester_quiz.Unit.Pages;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.reis.semester_quiz.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by reis on 22/03/2017.
 */

public class Fragment1QuizList extends Fragment {

    String _token, unit_id;
    ListView individual_quiz_list, group_quiz_list;
    String API_URL = "http://192.168.43.2:8000/api/";
//    String API_URL = "http://10.0.2.2:8000/api/";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // get bundle from dashboard
        super.onCreate(savedInstanceState);
        unit_id = getActivity().getIntent().getExtras().getString("unit_id");

        // get token from shared preferences
        SharedPreferences preferences = this.getActivity().getSharedPreferences("semester_quiz", MODE_PRIVATE);
        _token = preferences.getString("_token", null);

        // get the view and invoke the REST call
        View view = inflater.inflate(R.layout.unit_quiz_list, container, false);
        invokeWS(view);

        return view;
    }

    public void invokeWS(final View view){
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(API_URL + "quizzes/unit/" + unit_id + "?token=" + _token ,new AsyncHttpResponseHandler() {
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
                        JSONObject quiz = new JSONObject(quiz_data.getString("quiz"));
                        Boolean attempted = quiz_data.getBoolean("has_been_attempted");
                        Integer answers_count = quiz_data.getInt("answers_count");
                        Integer correct_count = quiz_data.getInt("correct_count");
                        Integer total_students = quiz_data.getInt("total_students");

                        Integer quiz_id = quiz.getInt("id");

                        HashMap<String, String> data = new HashMap<String, String>();
                        data.put("quiz_id", String.valueOf(quiz_id));
                        data.put("title", quiz.getString("title"));
                        data.put("type", quiz.getString("type"));
                        data.put("status", quiz.getString("status"));
                        data.put("has_been_attempted", String.valueOf(attempted));
                        data.put("answers_count", String.valueOf(answers_count));
                        data.put("correct_count", String.valueOf(correct_count));
                        data.put("total_students", String.valueOf(total_students));

                        if (!quiz_data.isNull("rank")) {
                            JSONObject rank = new JSONObject(quiz_data.getString("rank"));
                            Integer ranking_no = rank.getInt("rank_no");
                            data.put("rank_no", String.valueOf(ranking_no));
                        }

                        if (quiz.getString("type").toLowerCase().equals("individual")) {
                            individual_list_items.add(data);
                        }
                        else if (quiz.getString("type").toLowerCase().equals("group")) {
                            group_list_items.add(data);
                        }
                    }

                    // adapter is AdapterUnitQuizList, quiz_list get from unit_quiz_list.xml, in array adapter is from unit_quiz_list_fragment
                    ArrayAdapter individualListAdapter = new AdapterUnitQuizList(getContext(), individual_list_items);
                    individual_quiz_list = (ListView) view.findViewById(R.id.individual_list);
                    individual_quiz_list.setAdapter(individualListAdapter);

                    ArrayAdapter groupListAdapter = new AdapterUnitQuizList(getContext(), group_list_items);
                    group_quiz_list = (ListView) view.findViewById(R.id.group_list);
                    group_quiz_list.setAdapter(groupListAdapter);


                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
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
