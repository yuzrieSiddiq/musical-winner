package com.reis.semester_quiz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.reis.semester_quiz.Unit.Pages.CustomAdapter;

public class TestListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list);

        String [] samples = {"A", "B", "C"};

        ListAdapter listAdapter = new CustomAdapter(this, samples);

        ListView mylist = (ListView) findViewById(R.id.mylist);
        mylist.setAdapter(listAdapter);
    }
}
