package tol.oulu.fi.serendipity.UI;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ExpandableListView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tol.oulu.fi.serendipity.Helper.ExpandableListAdapter;
import tol.oulu.fi.serendipity.R;

/**
 * Created by ashrafuzzaman on 10/02/2016.
 */
public class PlayListScreen  extends Activity {

	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_list_screen);

		// get the listview
		expListView = (ExpandableListView) findViewById(R.id.lvExp);

		// preparing list data
		prepareListData();

		listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

		// setting list adapter
		expListView.setAdapter(listAdapter);
	}

	/*
	 * Preparing the list data
	 */
	private void prepareListData() {
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		String path = Environment.getExternalStorageDirectory().toString()+"/serendipity/download";
		Log.d("Files", "Path: " + path);
		File f = new File(path);
		File file[] = f.listFiles();
		Log.d("Files", "Size: "+ file.length);
		for (int i=0; i < file.length; i++)
		{
			listDataHeader.add(file[i].getName());
			Log.d("Files", "FileName:" + file[i].getName());
		}


		// Adding child data
		List<String> top250 = new ArrayList<String>();
		top250.add("The Shawshank Redemption");

		List<String> nowShowing = new ArrayList<String>();
		nowShowing.add("The Conjuring");

		List<String> comingSoon = new ArrayList<String>();
		comingSoon.add("2 Guns");

		listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
		listDataChild.put(listDataHeader.get(1), nowShowing);
		listDataChild.put(listDataHeader.get(2), comingSoon);
	}
}