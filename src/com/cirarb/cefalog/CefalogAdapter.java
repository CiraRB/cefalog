package com.cirarb.cefalog;

import java.util.List;

import com.cirarb.cefalog.fragments.EntriesFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CefalogAdapter extends FragmentPagerAdapter {
	private List<Fragment> fragments;

	public CefalogAdapter(FragmentManager fm, List<Fragment> fragments) {
	    super(fm);
	    this.fragments = fragments;
	}
	
	@Override 
	public Fragment getItem(int position) {
		if (position == 0)
			return new EntriesFragment();
		else 
			return this.fragments.get(position);
	}

	@Override
	public int getCount() {
		return this.fragments.size();
	}
}