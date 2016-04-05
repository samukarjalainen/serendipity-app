package tol.oulu.fi.serendipity.UI;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import tol.oulu.fi.serendipity.Helper.LocatePagerAdapter;
import tol.oulu.fi.serendipity.R;

/**
 * Created by ashrafuzzaman on 30/03/2016.
 */
public class LocateScreen extends FragmentActivity {
	ViewPager pager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locate_pager);

		LocatePagerAdapter pageAdapter = new LocatePagerAdapter(getSupportFragmentManager());
		pager = (ViewPager)findViewById(R.id.locate_view_pager);
		pager.setAdapter(pageAdapter);
	}
	public void setCurrentItem (int item, boolean smoothScroll) {
		pager.setCurrentItem(item, smoothScroll);
	}
}