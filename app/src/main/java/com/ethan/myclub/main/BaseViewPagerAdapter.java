package com.ethan.myclub.main;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.ethan.myclub.club.ClubFragment;
import com.ethan.myclub.discover.DiscoverFragment;
import com.ethan.myclub.user.main.view.UserFragment;

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
		fragments.add(new DiscoverFragment());
		fragments.add(new ClubFragment());
		fragments.add(new UserFragment());
		currentFragment = fragments.get(0);
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