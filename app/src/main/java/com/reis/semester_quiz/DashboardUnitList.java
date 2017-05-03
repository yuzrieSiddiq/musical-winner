package com.reis.semester_quiz;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.reis.semester_quiz.Quiz.QuizActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by reis on 22/03/2017.
 */

public class DashboardUnitList extends ArrayAdapter<HashMap<String, String>> {

    ArrayList<HashMap<String, String>> listitems;

    public DashboardUnitList(@NonNull Context context, ArrayList<HashMap<String, String>> values) {
        super(context, R.layout.dashboard_unit_listing_fragment, values);
        listitems = values;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.dashboard_unit_listing_fragment, parent, false);

//        TextView unitTextView = (TextView) view.findViewById(R.id.unit);
//        TextView semesterTextView = (TextView) view.findViewById(R.id.semester);
//        TextView yearTextView = (TextView) view.findViewById(R.id.year);
//
//        unitTextView.setText(listitems.get(position).get("unit_code") + " " + listitems.get(position).get("unit_name"));
//        semesterTextView.setText(listitems.get(position).get("semester"));
//        yearTextView.setText(listitems.get(position).get("year"));

        return view;
    }
}
