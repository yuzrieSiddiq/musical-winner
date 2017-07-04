package com.reis.semester_quiz.Unit;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.reis.semester_quiz.DashboardActivity;
import com.reis.semester_quiz.R;

public class UnitActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unit_index);

        // get from bundle
        String unit_name = getIntent().getExtras().getString("unit_name");
        getSupportActionBar().setTitle(unit_name);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(this, DashboardActivity.class);
        startActivity(backIntent);
    }

    public void navigateToUnitInfo(View view) {
        Toast.makeText(this, "Test Unit Info Button", Toast.LENGTH_SHORT).show();
    }

    public void navigateToTeamInfo(View view) {
        Toast.makeText(this, "Test Team Info Button", Toast.LENGTH_SHORT).show();
    }

    public void navigateToChat(View view) {
        Toast.makeText(this, "Test Chat Button", Toast.LENGTH_SHORT).show();
    }

    public void navigateToIQuiz1(View view) {
        Toast.makeText(this, "Test iRAT1 Button", Toast.LENGTH_SHORT).show();
    }

    public void navigateToIQuiz2(View view) {
        Toast.makeText(this, "Test iRAT2 Button", Toast.LENGTH_SHORT).show();
    }

    public void navigateToIQuiz3(View view) {
        Toast.makeText(this, "Test iRAT3 Button", Toast.LENGTH_SHORT).show();
    }

    public void navigateToTQuiz1(View view) {
        Toast.makeText(this, "Test tRAT1 Button", Toast.LENGTH_SHORT).show();
    }

    public void navigateToTQuiz2(View view) {
        Toast.makeText(this, "Test tRAT2 Button", Toast.LENGTH_SHORT).show();
    }

    public void navigateToTQuiz3(View view) {
        Toast.makeText(this, "Test tRAT3 Button", Toast.LENGTH_SHORT).show();
    }

    public void navigateToMain(View view) {
        Toast.makeText(this, "Test Main Button", Toast.LENGTH_SHORT).show();
    }

    public void navigateToAccount(View view) {
        Toast.makeText(this, "Test Account Button", Toast.LENGTH_SHORT).show();
    }

    public void navigateToInbox(View view) {
        Toast.makeText(this, "Test Inbox Button", Toast.LENGTH_SHORT).show();
    }

    public void navigateToCalendar(View view) {
        Toast.makeText(this, "Test Calendar Button", Toast.LENGTH_SHORT).show();
    }
}
