package com.reis.semester_quiz.Unit.Pages;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.reis.semester_quiz.Quiz.QuizActivity;
import com.reis.semester_quiz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by reis on 22/03/2017.
 */

public class AdapterTeamList extends ArrayAdapter<HashMap<String, String>> {

    ArrayList<HashMap<String, String>> team_list;
    JSONObject this_student;
    Typeface typeface;

    public AdapterTeamList(@NonNull Context context, ArrayList<HashMap<String, String>> team_list, JSONObject this_student) {
        super(context, R.layout.unit_team_list_fragment, team_list);
        this.team_list = team_list;
        this.this_student = this_student;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.unit_team_list_fragment, parent, false);

        TextView studentnameTextView= (TextView) view.findViewById(R.id.student_name);
        TextView studentidTextView= (TextView) view.findViewById(R.id.student_id);
        ImageView remove_button = (ImageView) view.findViewById(R.id.remove_student_from_team);

        studentnameTextView.setText(team_list.get(position).get("user_name"));
        studentidTextView.setText(team_list.get(position).get("student_std_id"));

        try {
            if (this_student.getInt("is_group_leader") == 1) {
                remove_button.setVisibility(View.INVISIBLE);
            } else {
                remove_button.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AssetManager assetManager = getContext().getAssets();
        typeface = Typeface.createFromAsset(assetManager, "fonts/LucidaGrande-Regular.ttf");
        studentidTextView.setTypeface(typeface);
        studentnameTextView.setTypeface(typeface);

        return view;
    }
}
