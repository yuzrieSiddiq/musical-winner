package com.reis.semester_quiz;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

/**
 * Created by reis on 20/03/2017.
 */

public class MainFragment extends Fragment {

    private FragmentActivity thisContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments


        ViewPager pager = (ViewPager) getView().findViewById(R.id.pager);
        pager.setAdapter(new PagerAdapter(thisContext.getSupportFragmentManager()));

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) getView().findViewById(R.id.tabs);
        tabs.setViewPager(pager);

        View view = inflater.inflate(R.layout.activity_main_fragment, container, false);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Dashboard");
    }

    @Override
    public void onAttach(Context context) {
        thisContext = (FragmentActivity) context;
        super.onAttach(context);
    }
}