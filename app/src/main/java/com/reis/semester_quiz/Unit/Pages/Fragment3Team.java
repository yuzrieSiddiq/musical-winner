package com.reis.semester_quiz.Unit.Pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
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

public class Fragment3Team extends Fragment {

    String _token, unit_id;
    String API_URL = "http://192.168.43.2:8000/api/";
    //    String API_URL = "http://10.0.2.2:8000/api/";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        unit_id = getActivity().getIntent().getExtras().getString("unit_id");

        // get token from shared preferences
        SharedPreferences preferences = this.getActivity().getSharedPreferences("semester_quiz", MODE_PRIVATE);
        _token = preferences.getString("_token", null);

        View view = inflater.inflate(R.layout.unit_team_fragment, container, false);
        invokeWS(view);

        return view;
    }

    public void invokeWS(final View view){
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(API_URL + "team_info/" + unit_id + "?token=" + _token ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    // JSON Object
                    String jsonstring = new String (responseBody);
                    JSONObject team_info = new JSONObject(jsonstring);

                    JSONObject this_student = new JSONObject(team_info.getString("this_student"));
                    JSONArray this_team = new JSONArray(team_info.getString("this_team"));
                    final JSONArray available_students = new JSONArray(team_info.getString("available_students"));

                    // get data this_team
                    final ArrayList<HashMap<String, String>> this_team_list = new ArrayList<HashMap<String, String>>();

                    for (int i = 0; i < this_team.length(); i++) {
                        JSONObject team_member = this_team.getJSONObject(i);

                        Integer student_id = team_member.getInt("student_id");
                        Integer user_id= team_member.getInt("user_id");
                        Integer team_number = team_member.getInt("team_number");
                        Integer is_group_leader = team_member.getInt("is_group_leader");
                        String student_std_id = team_member.getString("student_std_id");
                        String name = team_member.getString("user_name");

                        HashMap<String, String> data = new HashMap<String, String>();
                        data.put("student_id", String.valueOf(student_id));
                        data.put("team_number", String.valueOf(team_number));
                        data.put("is_group_leader", String.valueOf(is_group_leader));
                        data.put("user_id", String.valueOf(user_id));
                        data.put("student_std_id", student_std_id);
                        data.put("user_name", name);

                        this_team_list.add(data);
                    }

                    // set to list data: this_team
                    ListView teamListView = (ListView) view.findViewById(R.id.team_list);
                    ArrayAdapter<HashMap<String, String>> adapter = new AdapterTeamList(getContext(), this_team_list, this_student);
                    teamListView.setAdapter(adapter);


                    final ArrayList<HashMap<String, String>> available_students_list = new ArrayList<HashMap<String, String>>();

                    for (int i = 0; i < available_students.length(); i++) {
                        JSONObject student = available_students.getJSONObject(i);

                        Integer student_id = student.getInt("student_id");
                        String student_std_id = student.getString("student_std_id");
                        String name = student.getString("user_name");

                        HashMap<String, String> data = new HashMap<String, String>();
                        data.put("student_id", String.valueOf(student_id));
                        data.put("student_std_id", student_std_id);
                        data.put("user_name", name);

                        available_students_list.add(data);
                    }

                    Button addNewMember = (Button) view.findViewById(R.id.enlist_new);
                    addNewMember.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent addMemberIntent = new Intent(getContext(), AddNewMemberActivity.class);
                            addMemberIntent.putExtra("available_students_list", available_students_list);
                            startActivity(addMemberIntent);
                        }
                    });

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
