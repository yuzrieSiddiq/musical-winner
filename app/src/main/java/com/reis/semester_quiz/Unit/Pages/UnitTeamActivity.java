package com.reis.semester_quiz.Unit.Pages;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.reis.semester_quiz.Auth.Utility;
import com.reis.semester_quiz.R;
import com.reis.semester_quiz.Unit.UnitActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class UnitTeamActivity extends AppCompatActivity {

    String unit_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unit_team_info);
        unit_id = getIntent().getExtras().getString("unit_id");

        invokeWS();
    }

    /**
     * GET|HEAD
     * /api/team_info/{unit_id}
     * Get the list of team members of this student from this unit
     * */
    public void invokeWS(){
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Utility.API_URL() + "team_info/" + unit_id + "?token=" + Utility.getToken(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    // JSON Object
                    String jsonstring = new String (responseBody);
                    JSONObject team_info = new JSONObject(jsonstring);

                    final JSONObject this_student = new JSONObject(team_info.getString("this_student"));
                    JSONArray this_team = new JSONArray(team_info.getString("this_team"));
                    final JSONArray available_students = new JSONArray(team_info.getString("available_students"));

                    // get data this_team
                    final ArrayList<HashMap<String, String>> this_team_list = new ArrayList<HashMap<String, String>>();

                    for (int i = 0; i < this_team.length(); i++) {
                        JSONObject team_member = this_team.getJSONObject(i);

                        Integer student_id = team_member.getInt("student_id");
                        Integer user_id= team_member.getInt("user_id");
                        Integer is_group_leader = team_member.getInt("is_group_leader");
                        String student_std_id = team_member.getString("student_std_id");
                        String name = team_member.getString("user_name");

                        Integer team_number = 0;
                        if (!team_member.isNull("team_number")) {
                            team_number = team_member.getInt("team_number");
                        }

                        HashMap<String, String> data = new HashMap<String, String>();
                        data.put("student_id", String.valueOf(student_id));
                        data.put("team_number", String.valueOf(team_number));
                        data.put("is_group_leader", String.valueOf(is_group_leader));
                        data.put("user_id", String.valueOf(user_id));
                        data.put("student_std_id", student_std_id);
                        data.put("user_name", name);

                        this_team_list.add(data);
                    }

                    // set to list data: this_team if has team
                    if (this_student.isNull("team_number")) {
                        ListView teamListView = (ListView) findViewById(R.id.team_list);
                        teamListView.setVisibility(View.INVISIBLE);

                        TextView notAttached = (TextView) findViewById(R.id.not_attached);
                        notAttached.setVisibility(View.VISIBLE);

                        Button addNewMember = (Button) findViewById(R.id.enlist_new);
                        addNewMember.setVisibility(View.INVISIBLE);
                    } else {
                        ListView teamListView = (ListView) findViewById(R.id.team_list);
                        ArrayAdapter<HashMap<String, String>> adapter = new AdapterTeamList(getApplicationContext(), this_team_list, this_student);
                        teamListView.setAdapter(adapter);

                        if (this_student.getString("is_group_leader").equals("1")) {
                            teamListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                    if (position != 0) {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());

                                        // set title
                                        alertDialogBuilder.setTitle("Confirm remove member?");

                                        // set dialog message
                                        alertDialogBuilder
                                                .setMessage("Student ID: " + this_team_list.get(position).get("student_std_id") + "\n" +
                                                        "Name: " + this_team_list.get(position).get("user_name"))
                                                .setCancelable(true)
                                                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,int id) {
                                                        String student_id = this_team_list.get(position).get("student_id");
                                                        final String unit_id = getIntent().getExtras().getString("unit_id");
                                                        final String unit_name = getIntent().getExtras().getString("unit_name");

                                                        invokeWS(student_id, unit_id, unit_name);
                                                    }
                                                })
                                                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,int id) {
                                                        dialog.cancel();
                                                    }
                                                });

                                        // create alert dialog
                                        AlertDialog alertDialog = alertDialogBuilder.create();

                                        // show it
                                        alertDialog.show();
                                    }
                                }
                            });
                        }

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

                        Button addNewMember = (Button) findViewById(R.id.enlist_new);
                        addNewMember.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    JSONObject unitObject = new JSONObject(this_student.getString("unit"));
                                    Intent addMemberIntent = new Intent(UnitTeamActivity.this, AddNewMemberActivity.class);

                                    Bundle bundle = new Bundle();
                                    bundle.putString("unit_id", String.valueOf(unitObject.getInt("id")));
                                    bundle.putString("unit_name", unitObject.getString("name"));
                                    bundle.putSerializable("available_students_list", available_students_list);
                                    addMemberIntent.putExtras(bundle);

                                    startActivity(addMemberIntent);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "(onSuccess). Error Occured [Server's JSON response might be invalid]. Probably the JSON is []!", Toast.LENGTH_LONG).show();
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
        });
    }

    /**
     * POST
     * /api/delist/{student_id}/unit/{unit_id}
     * Removes a student from the team
     * */
    public void invokeWS(String student_id, final String unit_id, final String unit_name){
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(Utility.API_URL() + "delist/" + student_id + "/unit/" + unit_id + "?token=" + Utility.getToken(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Intent backToUnitIntent = new Intent(getApplicationContext(), UnitActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("unit_id", unit_id);
                bundle.putString("unit_name", unit_name);
                backToUnitIntent.putExtras(bundle);
                startActivity(backToUnitIntent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
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
