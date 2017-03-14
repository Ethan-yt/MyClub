package com.ethan.myclub.views.main;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.ethan.myclub.R;


public class BaseFragment extends Fragment {

    protected ViewGroup fragmentContainer;

    public void setFragmentContainer() {
        View view = getView();
        if(view != null)
            fragmentContainer = (ViewGroup) view.findViewById(R.id.fragment_container);
    }

    /**
     * Called when a fragment will be displayed
     */
    public void willBeDisplayed() {
        setFragmentContainer();
        if (fragmentContainer != null) {
            Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
            fragmentContainer.startAnimation(fadeIn);
        }
    }

    /**
     * Called when a fragment will be hidden
     */
    public void willBeHidden() {
        if (fragmentContainer != null) {
            Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
            fragmentContainer.startAnimation(fadeOut);
        }
    }

    public void refresh(){}
}
