package com.reis.semester_quiz.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.reis.semester_quiz.DashboardActivity;
import com.reis.semester_quiz.R;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    Typeface typeface;
    EditText email, password;
    String Email, Password;
    ProgressDialog prgDialog;
    String API_URL = "http://52.220.127.134/api/";
//    String API_URL = "http://10.0.2.2:8000/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_index);
        getSupportActionBar().hide();

        AssetManager assetManager = getAssets();
        typeface = Typeface.createFromAsset(assetManager, "fonts/Roboto-Light.ttf");

        TextView app_title = (TextView) findViewById(R.id.app_title);
        app_title.setTypeface(typeface);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Authenticating...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void loginUser(View view){
        Email = email.getText().toString();
        Password = password.getText().toString();
        // Instantiate Http Request Param Object
        RequestParams params = new RequestParams();
        // When Email Edit View and Password Edit View have values other than Null
        if(Utility.isNotNull(Email) && Utility.isNotNull(Password)){
            // When Email entered is Valid
            if(Utility.validate(Email)){
                // Put Http parameter username with value of Email Edit View control
                params.put("email", Email);
                // Put Http parameter password with value of Password Edit Value control
                params.put("password", Password);
                // Invoke RESTful Web Service with Http parameters
                invokeWS(params);
            }
            // When Email is invalid
            else{
                Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_LONG).show();
            }
        } else{
            Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Method that performs RESTful webservice invocations
     *
     * @param params
     */
    public void invokeWS(RequestParams params){
        // Show Progress Dialog
        prgDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(API_URL + "auth/login", params ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Hide Progress Dialog
                prgDialog.hide();
                try {
                    // JSON Object
                    String jsonstring = new String (responseBody);
                    JSONObject obj = new JSONObject(jsonstring);

                    // add token to app
                    SharedPreferences.Editor preferences_editor= getSharedPreferences("semester_quiz", MODE_PRIVATE).edit();
                    preferences_editor.putString("_token", obj.getString("token"));
                    preferences_editor.apply();

                    navigatetoHomeActivity(jsonstring);
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

    /**
     * Method which navigates from Login Activity to Home Activity
     */
    public void navigatetoHomeActivity(String json_data){

        try {
            JSONObject obj = new JSONObject(json_data);
            JSONObject user = obj.getJSONObject("user");

            // add token to app
            SharedPreferences.Editor preferences_editor= getSharedPreferences("semester_quiz", MODE_PRIVATE).edit();
            preferences_editor.putString("_token", obj.getString("token"));
            preferences_editor.apply();

            // switch page intent
            Intent homeIntent = new Intent(getApplicationContext(),DashboardActivity.class);

            // add to bundle
            Bundle bundle = new Bundle();
            bundle.putString("user_id", user.getString("id"));
            homeIntent.putExtras(bundle);

            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

