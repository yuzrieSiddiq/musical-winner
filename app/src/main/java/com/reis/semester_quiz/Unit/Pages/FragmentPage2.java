package com.reis.semester_quiz.Unit.Pages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reis.semester_quiz.R;

/**
 * Created by reis on 22/03/2017.
 */

public class FragmentPage2 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.unit_info_fragment, container, false);
        // probably cast to ViewGroup and find your ListView

        return view;
    }
}
