package com.ethan.myclub.main;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
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
    private ViewGroup mFragmentRootLayout;

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
        return viewDataBinding;

    }


    public View onCreateView(LayoutInflater inflater, int layoutId, @Nullable ViewGroup container) {
        ViewGroup view = (ViewGroup) inflater.inflate(layoutId, container, false);
        mFragmentRootLayout = view;
        mFragmentAnimationChild = view;
        return view;
    }


    public void refresh() {
    }

    public static class ToolbarWrapper {

        private BaseFragment mBaseFragment;
        private int mMenuResId = -1;
        private boolean mIsShowBackIcon = false;
        private String mTittle;
        private Toolbar.OnMenuItemClickListener mOnMenuItemClickListener;

        public ToolbarWrapper(BaseFragment baseFragment, String tittle) {
            mBaseFragment = baseFragment;
            if (mBaseFragment.mFragmentAnimationChild == null)
                throw new RuntimeException("You must call setContentView before initToolbar!");
            mTittle = tittle;
        }

        public ToolbarWrapper setMenuAndListener(@MenuRes int resId, Toolbar.OnMenuItemClickListener onMenuItemClickListener) {
            mMenuResId = resId;
            mOnMenuItemClickListener = onMenuItemClickListener;
            return this;
        }

        public ToolbarWrapper showBackIcon() {
            mIsShowBackIcon = true;
            return this;
        }

        public void show() {

            View appBarLayout = View.inflate(mBaseFragment.getContext(), R.layout.view_toolbar, mBaseFragment.mFragmentRootLayout);
            Toolbar toolbar = (Toolbar) appBarLayout.findViewById(R.id.toolbar);
            toolbar.setTitle(mTittle);
            if (mMenuResId != -1 && mOnMenuItemClickListener != null) {
                toolbar.inflateMenu(mMenuResId);
                toolbar.setOnMenuItemClickListener(mOnMenuItemClickListener);
            }
            mBaseFragment.mFragmentAnimationChild = getAnimationChild(mBaseFragment.mFragmentRootLayout);

            if (mIsShowBackIcon) {
                final TypedValue typedValueAttr = new TypedValue();
                mBaseFragment.getActivity().getTheme().resolveAttribute(R.attr.homeAsUpIndicator, typedValueAttr, true);
                toolbar.setNavigationIcon(typedValueAttr.resourceId);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //ActivityCompat.finishAfterTransition(BaseActivity.this);
                        mBaseFragment.getActivity().onBackPressed();
                    }
                });
            }
        }

        private ViewGroup getAnimationChild(ViewGroup rootLayout) {
            int childCount = rootLayout.getChildCount();
            if (childCount == 0)
                throw new RuntimeException("Root view group must have a child to move down and become a animation object!");
            ViewGroup child = (ViewGroup) rootLayout.getChildAt(0);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) child.getLayoutParams();

            TypedValue typedValue = new TypedValue();
            mBaseFragment.getActivity().getTheme().resolveAttribute(R.attr.actionBarSize, typedValue, true);
            layoutParams.topMargin = mBaseFragment.getActivity().getResources().getDimensionPixelSize(typedValue.resourceId);
            child.setLayoutParams(layoutParams);
            return child;
        }


    }

}
