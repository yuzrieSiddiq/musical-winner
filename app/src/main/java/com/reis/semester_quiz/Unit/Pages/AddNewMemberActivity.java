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
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.reis.semester_quiz.Auth.Utility;
import com.reis.semester_quiz.DashboardUnitList;
import com.reis.semester_quiz.R;
import com.reis.semester_quiz.Unit.UnitActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class AddNewMemberActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Enlist New Member");
        setContentView(R.layout.activity_add_new_member);

        // get available students list from previous activity
        final ArrayList<HashMap<String, String>> available_students_list =
                (ArrayList<HashMap<String, String>>) getIntent().getExtras().getSerializable("available_students_list");

        ListView availablestudentsListView = (ListView) findViewById(R.id.available_list);
        ArrayAdapter<HashMap<String, String>> adapter = new AdapterAvailableStudentsList(this, available_students_list);
        availablestudentsListView .setAdapter(adapter);

        availablestudentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddNewMemberActivity.this);

                // set title
                alertDialogBuilder.setTitle("Confirm adding new member?");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Student ID: " + available_students_list.get(position).get("student_std_id") + "\n" +
                            "Name: " + available_students_list.get(position).get("user_name"))
                        .setCancelable(true)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String student_id = available_students_list.get(position).get("student_id");
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
        });
    }

    /**
     * POST | App\Api\V1\Controllers\StudentController@enlist_new_member
     * /api/enlist/{student_id}/unit/{unit_id}
     * Enlist/Add another chosen student into this team
     **/
    public void invokeWS(String student_id, final String unit_id, final String unit_name){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(Utility.API_URL() + "enlist/" + student_id + "/unit/" + unit_id + "?token=" + Utility.getToken(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Intent backToUnitTeamIntent = new Intent(AddNewMemberActivity.this, UnitTeamActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("unit_id", unit_id);
                bundle.putString("unit_name", unit_name);
                backToUnitTeamIntent.putExtras(bundle);
                startActivity(backToUnitTeamIntent);
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
}
