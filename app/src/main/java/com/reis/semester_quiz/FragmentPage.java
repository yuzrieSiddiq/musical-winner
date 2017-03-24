package com.reis.semester_quiz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by reis on 22/03/2017.
 */

public class FragmentPage extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String[] testarray = {"Test 1", "Test 2", "Test 3"};

        View view = inflater.inflate(R.layout.activity_test_list, container, false);
        ListAdapter listAdapter = new CustomAdapter(getContext(), testarray);

        ListView mylist = (ListView) view.findViewById(R.id.mylist);
        mylist.setAdapter(listAdapter);


        return view;
    }
}
