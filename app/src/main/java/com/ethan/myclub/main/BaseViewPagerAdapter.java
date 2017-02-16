package com.ethan.myclub.main;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.ethan.myclub.user.UserFragment;

import java.util.ArrayList;

/**
 *
 */
public class BaseViewPagerAdapter extends FragmentPagerAdapter {

	private ArrayList<BaseFragment> fragments = new ArrayList<>();
	private BaseFragment currentFragment;

	public BaseViewPagerAdapter(FragmentManager fm) {
		super(fm);

		fragments.clear();
		fragments.add(new UserFragment());
		fragments.add(new UserFragment());
		fragments.add(new UserFragment());
	}

	@Override
	public BaseFragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		if (getCurrentFragment() != object) {
			currentFragment = ((BaseFragment) object);
		}
		super.setPrimaryItem(container, position, object);
	}

	/**
	 * Get the current fragment
	 */
	public BaseFragment getCurrentFragment() {
		return currentFragment;
	}
}