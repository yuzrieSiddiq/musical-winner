package com.reis.semester_quiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.reis.semester_quiz.Quiz.QuizActivity;
import com.reis.semester_quiz.Unit.Pages.AdapterUnitList;
import com.reis.semester_quiz.Unit.UnitActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ProgressDialog prgDialog;
    TextView welcomeTextView;
    ListView unit_listingListView;

    String _token;
    String API_URL = "http://10.0.2.2:8000/api/";

    String user_firstname;
    String user_lastname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_index);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get token from shared preferences
        SharedPreferences preferences = getSharedPreferences("semester_quiz", MODE_PRIVATE);
        _token = preferences.getString("_token", null);

        // set views
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);

//        ArrayList<HashMap<String, String>> listitems = new ArrayList<HashMap<String, String>>();
//        listitems.add("");

        welcomeTextView = (TextView) findViewById(R.id.welcome_text);
        unit_listingListView = (ListView) findViewById(R.id.unit_listing);


        invokeWS();


        // sets the drawer and navigation view
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        Intent intent = new Intent(getApplicationContext(), UnitActivity.class);
//        startActivity(intent);

        if (id == R.id.nav_logout) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     *  REST call
     *  */
    public void invokeWS(){
        // Show Progress Dialog
        prgDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(API_URL + "students?token=" + _token, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Hide Progress Dialog
                prgDialog.hide();
                try {
                    // JSON Object
                    String jsonstring = new String (responseBody);

                    JSONArray jsonArray = new JSONArray(jsonstring);

                    final ArrayList<HashMap<String, String>> listitems = new ArrayList<HashMap<String, String>>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
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

                    JSONObject user = jsonArray.getJSONObject(0).getJSONObject("user");
                    user_firstname = user.getString("firstname");
                    user_lastname = user.getString("lastname");

                    welcomeTextView.setText("Welcome Back: " + user_firstname);

                    ArrayAdapter arrayAdapter = new DashboardUnitList(getApplicationContext(), listitems);
                    unit_listingListView = (ListView) findViewById(R.id.unit_listing);
                    unit_listingListView.setAdapter(arrayAdapter);
                    unit_listingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            Toast.makeText(DashboardActivity.this, listitems.get(position).get("unit_id"), Toast.LENGTH_SHORT).show();
                            navigatetoUnitActivity(listitems.get(position).get("unit_id"));
                        }
                    });

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Hide Progress Dialog
                prgDialog.hide();
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

    public void navigatetoUnitActivity(String unit_id){

        // switch page intent
        Intent unitintent = new Intent(getApplicationContext(), UnitActivity.class);

        // add to bundle
        Bundle bundle = new Bundle();
        bundle.putString("unit_id", unit_id);
        unitintent.putExtras(bundle);

        startActivity(unitintent  );
    }
}
