package com.ethan.myclub.main;

import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.ethan.myclub.R;


public abstract class BaseFragment extends Fragment {

    protected ViewGroup mFragmentContainer;

    protected abstract void setFragmentContainer();

    /**
     * Called when a fragment will be displayed
     */
    public void willBeDisplayed() {
        if (mFragmentContainer == null)
            setFragmentContainer();
        if (mFragmentContainer != null) {
            Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
            mFragmentContainer.startAnimation(fadeIn);
        }

    }

    /**
     * Called when a fragment will be hidden
     */
    public void willBeHidden() {
        if (mFragmentContainer != null) {
            Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
            mFragmentContainer.startAnimation(fadeOut);
        }
    }

    public void refresh() {
    }
}
