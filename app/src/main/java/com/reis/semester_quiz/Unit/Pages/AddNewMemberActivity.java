package com.reis.semester_quiz.Unit.Pages;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.reis.semester_quiz.R;

import java.util.ArrayList;
import java.util.HashMap;

public class AddNewMemberActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_member);

        final ArrayList<HashMap<String, String>> available_students_list =
                (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("available_students_list");

        ListView availablestudentsListView = (ListView) findViewById(R.id.available_list);
        ArrayAdapter<HashMap<String, String>> adapter = new AdapterAvailableStudentsList(this, available_students_list);
        availablestudentsListView .setAdapter(adapter);

        availablestudentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddNewMemberActivity.this);

                // set title
                alertDialogBuilder.setTitle("Confirm adding new member?");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Student ID: " + available_students_list.get(position).get("student_std_id") + "\n" +
                            "Name: " + available_students_list.get(position).get("user_name"))
                        .setCancelable(true)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                Toast.makeText(AddNewMemberActivity.this, available_students_list.get(position).get("student_std_id"), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });
    }
}
