package com.reis.semester_quiz.Unit.Pages;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.reis.semester_quiz.Auth.Utility;
import com.reis.semester_quiz.R;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class UnitInfoActivity extends AppCompatActivity {

    String unit_id;
    Typeface typeface, typeface2, typeface3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unit_info);

        unit_id = getIntent().getExtras().getString("unit_id");

        // get the view and invoke the REST call
        invokeWS();
    }

    public void invokeWS() {
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Utility.API_URL() + "units/" + unit_id + "?token=" + Utility.getToken() ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    // JSON Object
                    String jsonstring = new String (responseBody);
                    JSONObject unit_json= new JSONObject(jsonstring);

                    String unit = unit_json.getString("code") + " " + unit_json.getString("name");
//                    Toast.makeText(getContext(), unit, Toast.LENGTH_SHORT).show();

                    TextView unit_nameTextView = (TextView) findViewById(R.id.unit_name);
                    TextView unit_descriptionTextView = (TextView) findViewById(R.id.unit_description);
                    unit_nameTextView.setText(unit);

                    AssetManager assetManager = getAssets();
                    typeface = Typeface.createFromAsset(assetManager, "fonts/Roboto-Light.ttf");
                    typeface2 = Typeface.createFromAsset(assetManager, "fonts/Roboto-Regular.ttf");
                    typeface3 = Typeface.createFromAsset(assetManager, "fonts/Roboto-Medium.ttf");

                    unit_nameTextView.setTypeface(typeface3);
                    unit_descriptionTextView.setTypeface(typeface);

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
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
