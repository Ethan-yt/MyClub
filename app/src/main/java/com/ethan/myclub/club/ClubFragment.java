package com.ethan.myclub.club;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ethan.myclub.R;
import com.ethan.myclub.main.BaseFragment;


public class ClubFragment extends BaseFragment {

    public ClubFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_club, container, false);


        return view;
    }

    @Override
    protected void setFragmentContainer() {
        View view = getView();
        if(view != null)
            mFragmentContainer = (ViewGroup) view.findViewById(R.id.fragment_container);
    }

}
