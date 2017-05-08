package com.reis.semester_quiz.Quiz;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.reis.semester_quiz.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by reis on 22/03/2017.
 */

public class AdapterRankingAnswerList extends ArrayAdapter<HashMap<String, String>> {

    ArrayList<HashMap<String, String>> answers;
    Typeface typeface;

    public AdapterRankingAnswerList(@NonNull Context context, ArrayList<HashMap<String, String>> answers) {
        super(context, R.layout.quiz_questions_ranking_fragment, answers);
        this.answers = answers;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.quiz_questions_ranking_fragment, parent, false);

        TextView answer = (TextView) view.findViewById(R.id.answer);
        TextView rank_no = (TextView) view.findViewById(R.id.rank_no);

        Integer answerNumber = position+1;
        String theAnswer = "answer" + answerNumber;
        answer.setText(answers.get(position).get(theAnswer));

        AssetManager assetManager = getContext().getAssets();
        typeface = Typeface.createFromAsset(assetManager, "fonts/Roboto-Light.ttf");
        answer.setTypeface(typeface);
        rank_no.setTypeface(typeface);

        return view;
    }
}
