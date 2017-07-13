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
    EditText username, password;
    String Username, Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_index);
        getSupportActionBar().hide();

        AssetManager assetManager = getAssets();
        typeface = Typeface.createFromAsset(assetManager, "fonts/Roboto-Light.ttf");

        TextView app_title = (TextView) findViewById(R.id.app_title);
        app_title.setTypeface(typeface);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
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
        Username = username.getText().toString();
        Password = password.getText().toString();
        // Instantiate Http Request Param Object
        RequestParams params = new RequestParams();
        // When Email Edit View and Password Edit View have values other than Null
        if(Utility.isNotNull(Username) && Utility.isNotNull(Password)){
            // Put Http parameter username with value of Email Edit View control
            params.put("username", Username);
            // Put Http parameter password with value of Password Edit Value control
            params.put("password", Password);
            // Invoke RESTful Web Service with Http parameters
            invokeWS(params);

        } else{
            Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
        }

    }

    /**
     *  POST | App\Api\V1\Controllers\LoginController@login
     *  /api/auth/login
     *  Sends username and password through api to retrieve token
     */
    public void invokeWS(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(Utility.API_URL() + "auth/login", params ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    // JSON Object
                    String jsonstring = new String (responseBody);
                    JSONObject obj = new JSONObject(jsonstring);

                    // add token and context to app
                    Utility.setToken(getApplicationContext(), obj);

                    navigatetoHomeActivity(jsonstring);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
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

    public void navigatetoHomeActivity(String json_data){

        try {
            JSONObject obj = new JSONObject(json_data);
            JSONObject user = obj.getJSONObject("user");

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

