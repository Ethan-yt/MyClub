package com.ethan.myclub.views.discover;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ethan.myclub.R;
import com.ethan.myclub.views.main.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverFragment extends BaseFragment {


    public DiscoverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        fragmentContainer = (ViewGroup) view.findViewById(R.id.fragment_container);

        return view;
    }

}
