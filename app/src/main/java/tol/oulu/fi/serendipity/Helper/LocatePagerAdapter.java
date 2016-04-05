package tol.oulu.fi.serendipity.Helper;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import tol.oulu.fi.serendipity.UI.FragmentMap;
import tol.oulu.fi.serendipity.UI.FragmentPlayList;

/**
 * Created by ashrafuzzaman on 30/03/2016.
 */
public class LocatePagerAdapter extends FragmentPagerAdapter {

	private List<Fragment> fragments;
	public LocatePagerAdapter(FragmentManager fm) {
		super(fm);
		this.fragments = new ArrayList<Fragment>();
		fragments.add(new FragmentPlayList());
		fragments.add(new FragmentMap());
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}
}
