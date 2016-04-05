package tol.oulu.fi.serendipity.Helper;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import tol.oulu.fi.serendipity.Data.DataHandler;
import tol.oulu.fi.serendipity.R;
import tol.oulu.fi.serendipity.UI.FragmentPlayList;

/**
 * Created by ashrafuzzaman on 30/03/2016.
 */
public class PlayListAdapter extends BaseAdapter {
	ArrayList<String> playItems;
DataHandler mDataHandler;
	int[] imge;
	Context context;
	private LayoutInflater inflater=null;
	PlayListAdapter() {

		playItems= null;
	}

	public PlayListAdapter(FragmentPlayList fragmentPlayLists, ArrayList<String> text) {
		context = fragmentPlayLists.getContext();
		playItems = text;
		mDataHandler = DataHandler.getInstance(fragmentPlayLists.getContext());
		notifyDataSetChanged();
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return playItems.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row;
		row = inflater.inflate(R.layout.play_list, parent, false);
		TextView title ;
		title = (TextView) row.findViewById(R.id.txt);
		title.setText(playItems.get(position));
		int highlightedPosition = mDataHandler.getSelectedItem();
		if (highlightedPosition == position) {
			row.setBackgroundColor(Color.LTGRAY);
		}
		return (row);
	}


}
