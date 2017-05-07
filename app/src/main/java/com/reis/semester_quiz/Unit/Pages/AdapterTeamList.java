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

        studentnameTextView.setText(team_list.get(position).get("user_name"));
        if (team_list.get(position).get("is_group_leader").equals("1")) {
            studentidTextView.setText(team_list.get(position).get("student_std_id") + " [Group Leader]");
        } else {
            studentidTextView.setText(team_list.get(position).get("student_std_id"));
        }

        AssetManager assetManager = getContext().getAssets();
        typeface = Typeface.createFromAsset(assetManager, "fonts/Roboto-Light.ttf");
        studentidTextView.setTypeface(typeface);
        studentnameTextView.setTypeface(typeface);

        return view;
    }
}
