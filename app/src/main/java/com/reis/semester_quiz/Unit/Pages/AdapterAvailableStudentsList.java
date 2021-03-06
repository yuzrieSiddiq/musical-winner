package com.reis.semester_quiz.Unit.Pages;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.reis.semester_quiz.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by reis on 22/03/2017.
 */

public class AdapterAvailableStudentsList extends ArrayAdapter<HashMap<String, String>> {

    ArrayList<HashMap<String, String>> available_students_list;
    Typeface typeface;

    public AdapterAvailableStudentsList(@NonNull Context context, ArrayList<HashMap<String, String>> available_students_list) {
        super(context, R.layout.available_students_list_fragment, available_students_list);
        this.available_students_list= available_students_list;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.available_students_list_fragment, parent, false);

        TextView studentnameTextView= (TextView) view.findViewById(R.id.student_name);
        TextView studentidTextView= (TextView) view.findViewById(R.id.student_id);

        studentnameTextView.setText(available_students_list.get(position).get("user_name"));
        studentidTextView.setText(available_students_list.get(position).get("student_std_id"));

        AssetManager assetManager = getContext().getAssets();
        typeface = Typeface.createFromAsset(assetManager, "fonts/Roboto-Light.ttf");
        studentidTextView.setTypeface(typeface);
        studentnameTextView.setTypeface(typeface);

        return view;
    }
}
