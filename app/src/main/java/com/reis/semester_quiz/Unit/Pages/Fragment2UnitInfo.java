package com.reis.semester_quiz.Unit.Pages;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import static android.R.attr.type;
import static android.R.attr.typeface;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by reis on 22/03/2017.
 */

public class Fragment2UnitInfo extends Fragment {

    String _token, unit_id;
    String API_URL = "http://192.168.43.2:8000/api/";
//    String API_URL = "http://10.0.2.2:8000/api/";
    Typeface typeface, typeface2, typeface3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        unit_id = getActivity().getIntent().getExtras().getString("unit_id");

        // get token from shared preferences
        SharedPreferences preferences = this.getActivity().getSharedPreferences("semester_quiz", MODE_PRIVATE);
        _token = preferences.getString("_token", null);

        // get the view and invoke the REST call
        View view = inflater.inflate(R.layout.unit_info_fragment, container, false);
        invokeWS(view);

        return view;
    }

    public void invokeWS(final View view){
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(API_URL + "units/" + unit_id + "?token=" + _token ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    // JSON Object
                    String jsonstring = new String (responseBody);
                    JSONObject unit_json= new JSONObject(jsonstring);

                    String unit = unit_json.getString("code") + " " + unit_json.getString("name");
//                    Toast.makeText(getContext(), unit, Toast.LENGTH_SHORT).show();

                    TextView unit_nameTextView = (TextView) view.findViewById(R.id.unit_name);
                    TextView unit_descriptionTextView = (TextView) view.findViewById(R.id.unit_description);
                    unit_nameTextView.setText(unit);

                    AssetManager assetManager = getContext().getAssets();
                    typeface = Typeface.createFromAsset(assetManager, "fonts/Roboto-Light.ttf");
                    typeface2 = Typeface.createFromAsset(assetManager, "fonts/Roboto-Regular.ttf");
                    typeface3 = Typeface.createFromAsset(assetManager, "fonts/Roboto-Medium.ttf");

                    unit_nameTextView.setTypeface(typeface3);
                    unit_descriptionTextView.setTypeface(typeface);

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
