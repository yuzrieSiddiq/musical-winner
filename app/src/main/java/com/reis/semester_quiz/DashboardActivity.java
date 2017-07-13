package com.reis.semester_quiz;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.reis.semester_quiz.Auth.LoginActivity;
import com.reis.semester_quiz.Auth.Utility;
import com.reis.semester_quiz.Unit.Pages.AddNewMemberActivity;
import com.reis.semester_quiz.Unit.UnitActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class DashboardActivity extends AppCompatActivity {

    TextView nameTextView, idTextView;
    ListView unit_listingListView;

    String user_firstname;
    String user_lastname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_index);
        getSupportActionBar().setTitle(R.string.app_name);

        nameTextView = (TextView) findViewById(R.id.student_name);
        idTextView = (TextView) findViewById(R.id.student_id);
        unit_listingListView = (ListView) findViewById(R.id.unit_listing);

        invokeWS();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DashboardActivity.this);
            alertDialogBuilder
                    .setTitle("LOGOUT")
                    .setMessage("Confirm logout?")
                    .setCancelable(true)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // clear all items in preferences after logout (to refresh new content when login)
                            Utility.clearPreferences();

                            // let there be light! to go back
                            Intent backToLogin = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(backToLogin);
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
        return super.onOptionsItemSelected(item);
    }

    /**
     *  GET|HEAD | App\Api\V1\Controllers\StudentController@index
     *  /api/students
     *  Get from the "students" table which have their units. Lists out all units currently taken by the student
     *  */
    public void invokeWS(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Utility.API_URL() + "students?token=" + Utility.getToken(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonstring = new String(responseBody);
                try {
                    JSONObject jsonresponse = new JSONObject(jsonstring);

                    // get user part
                    // return api response: User::with('student_info')
                    JSONObject user = jsonresponse.getJSONObject("user");
                    JSONObject student_info = user.getJSONObject("student_info");

                    user_firstname = user.getString("firstname");
                    user_lastname = user.getString("lastname");

                    nameTextView.setText(user_firstname + " " + user_lastname);
                    idTextView.setText("Student ID: " + student_info.getString("student_id"));

                    // get this_student
                    // return api response: Student::with('unit, user')
                    String this_student = jsonresponse.getString("this_student");
                    JSONArray units = new JSONArray(this_student);

                    final ArrayList<HashMap<String, String>> listitems = new ArrayList<HashMap<String, String>>();

                    for (int i = 0; i < units.length(); i++) {
                        JSONObject obj = new JSONObject(units.get(i).toString());
                        JSONObject unit = obj.getJSONObject("unit");

                        Integer student_id = obj.getInt("id");
                        Integer unit_id = obj.getInt("unit_id");
                        Integer user_id = obj.getInt("user_id");

                        HashMap<String, String> data = new HashMap<String, String>();
                        data.put("unit_code", unit.getString("code"));
                        data.put("unit_name", unit.getString("name"));
                        data.put("unit_id", String.valueOf(unit_id));
                        data.put("student_id", String.valueOf(student_id));
                        data.put("semester", obj.getString("semester"));
                        data.put("year", obj.getString("year"));

                        listitems.add(data);
                    }

                    ArrayAdapter arrayAdapter = new DashboardUnitList(getApplicationContext(), listitems);
                    unit_listingListView = (ListView) findViewById(R.id.unit_listing);
                    unit_listingListView.setAdapter(arrayAdapter);
                    unit_listingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String unit_id = listitems.get(position).get("unit_id");
                            String unit_title = listitems.get(position).get("unit_code") + " " + listitems.get(position).get("unit_name");
                            navigatetoUnitActivity(unit_id, unit_title);
                        }
                    });

                } catch (JSONException e) {
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

    public void navigatetoUnitActivity(String unit_id, String unit_name){

        // switch page intent
        Intent unitintent = new Intent(getApplicationContext(), UnitActivity.class);

        // add to bundle
        Bundle bundle = new Bundle();
        bundle.putString("unit_id", unit_id);
        bundle.putString("unit_name", unit_name);
        unitintent.putExtras(bundle);

        startActivity(unitintent  );
    }
}
