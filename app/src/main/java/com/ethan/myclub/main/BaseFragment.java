package com.ethan.myclub.main;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.ethan.myclub.R;


public abstract class BaseFragment extends Fragment {

    private ViewGroup mFragmentAnimationChild;
    public ViewGroup mFragmentRootLayout;
    public MainActivity mBaseActivity;
    /**
     * Called when a fragment will be displayed
     */
    public void willBeDisplayed() {
        if (mFragmentAnimationChild != null) {
            Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
            mFragmentAnimationChild.startAnimation(fadeIn);
        }

    }

    /**
     * Called when a fragment will be hidden
     */
    public void willBeHidden() {
        if (mFragmentAnimationChild != null) {
            Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
            mFragmentAnimationChild.startAnimation(fadeOut);
        }
    }

    public ViewDataBinding onCreateDataBindingView(LayoutInflater inflater, int layoutId, @Nullable ViewGroup container) {
        ViewDataBinding viewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false);
        mFragmentRootLayout = (ViewGroup) viewDataBinding.getRoot();
        mFragmentAnimationChild = mFragmentRootLayout;
        mBaseActivity = (MainActivity) getActivity();
        return viewDataBinding;

    }


    public View onCreateView(LayoutInflater inflater, int layoutId, @Nullable ViewGroup container) {
        ViewGroup view = (ViewGroup) inflater.inflate(layoutId, container, false);
        mFragmentRootLayout = view;
        mFragmentAnimationChild = view;
        mBaseActivity = (MainActivity) getActivity();
        return view;
    }


    public void refresh() {
    }
}
