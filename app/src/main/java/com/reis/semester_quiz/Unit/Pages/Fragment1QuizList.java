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

//    ProgressDialog prgDialog;
    String API_URL = "http://10.0.2.2:8000/api/";
    String _token, unit_id;
    ListView quiz_list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // get bundle from dashboard
        super.onCreate(savedInstanceState);
        unit_id = getActivity().getIntent().getExtras().getString("unit_id");

        // get token from shared preferences
        SharedPreferences preferences = this.getActivity().getSharedPreferences("semester_quiz", MODE_PRIVATE);
        _token = preferences.getString("_token", null);

//        prgDialog = new ProgressDialog(getContext());
//        prgDialog.setMessage("Please wait...");
//        prgDialog.setCancelable(false);

        // get the view and invoke the REST call
        View view = inflater.inflate(R.layout.unit_quiz_list, container, false);
        invokeWS(view);

        return view;
    }

    public void invokeWS(final View view){
        // Show Progress Dialog
//        prgDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(API_URL + "quizzes/unit/" + unit_id + "?token=" + _token ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Hide Progress Dialog
//                prgDialog.hide();
                try {
                    // JSON Object
                    String jsonstring = new String (responseBody);
                    JSONArray jsonArray = new JSONArray(jsonstring);

                    // populate the quizzes
                    final ArrayList<HashMap<String, String>> listitems = new ArrayList<HashMap<String, String>>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject quiz = jsonArray.getJSONObject(i);

                        Integer quiz_id = quiz.getInt("id");

                        HashMap<String, String> data = new HashMap<String, String>();
                        data.put("quiz_id", String.valueOf(quiz_id));
                        data.put("title", quiz.getString("title"));
                        data.put("type", quiz.getString("type"));
                        data.put("status", quiz.getString("status"));

                        listitems.add(data);
                    }

                    // adapter is AdapterUnitQuizList, quiz_list get from unit_quiz_list.xml, in array adapter is from unit_quiz_list_fragment
                    ArrayAdapter arrayAdapter = new AdapterUnitQuizList(getContext(), listitems);
                    quiz_list= (ListView) view.findViewById(R.id.mylist);
                    quiz_list.setAdapter(arrayAdapter);


                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Hide Progress Dialog
                //prgDialog.hide();
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
