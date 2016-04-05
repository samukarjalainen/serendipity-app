package tol.oulu.fi.serendipity.UI;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import tol.oulu.fi.serendipity.Data.DataHandler;
import tol.oulu.fi.serendipity.Helper.PlayListAdapter;
import tol.oulu.fi.serendipity.R;
import static com.google.android.gms.internal.zzir.runOnUiThread;

/**
 * Created by ashrafuzzaman on 30/03/2016.
 */
public class FragmentPlayList  extends Fragment {
	ListView lv;
	TextView titleTv,timerTv;
	ProgressBar progressBar;
	private static String mFileName = null;
	private int mediaPos = 0;
	private int mediaMax;
	ArrayList<String> playItems;
	ArrayList<String> playItemsPath;
	private MediaPlayer mMediaPlayer;
	private static String TAG = "fragmnetPlayList";
	private ToggleButton playToggleButton;
	private int nowPlaying= 0;
	private int nowSelected = 0;
	private boolean playState = false;
	DataHandler mDataHandler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_play_list, container, false);
		mDataHandler = DataHandler.getInstance(this.getContext());
		mMediaPlayer = new MediaPlayer();
		playItems = new ArrayList<String>();
		playItemsPath = new ArrayList<String>();
		titleTv = (TextView) view.findViewById(R.id.title_tv);
		timerTv = (TextView) view.findViewById(R.id.timer);
		playToggleButton = (ToggleButton) view.findViewById(R.id.play_list_toggle);


		String path = Environment.getExternalStorageDirectory().toString()+"/serendipity/download";
		Log.d("Files", "Path: " + path);
		File f = new File(path);
		File file[] = f.listFiles();
		Log.d("Files", "Size: "+ file.length);
		for (int i=0; i < file.length; i++)
		{
			playItems.add( file[i].getName());
			playItemsPath.add(file[i].getAbsolutePath());
			mFileName = file[i].getAbsolutePath();
			Log.d("Files", "FileName:" + file[i].getName());
		}
		playToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if(!playState){
						startPlaying();
					} else if (nowPlaying == nowSelected ){
						resumePlaying();
					} else {
						stopPlaying();
						startPlaying();
					}
				} else {
					if (nowPlaying == nowSelected ){
						pausePlaying();
					} else {
						stopPlaying();
					}
				}
			}
		});
		progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
		progressBar.setProgress(0);

		//progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
		// get the listview
		lv=(ListView) view.findViewById(R.id.listView);
		lv.setAdapter(new PlayListAdapter(this, playItems));
		lv.setSelection(0);
		lv.performItemClick(lv, 1, lv.getItemIdAtPosition(0));
		titleTv.setText(playItems.get(0));
		mFileName = playItemsPath.get(0);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View myView, int position, long id) {
				mDataHandler.storeSelectedItem(position);
				mFileName = playItemsPath.get(position);
				nowSelected = position;
				lv.setAdapter(new PlayListAdapter(FragmentPlayList.this, playItems));
			}
		});
		return view;
	}

	private void stopPlaying(){
		playState = false;
		Log.e(TAG, "onStop");
		mMediaPlayer.stop();
	}

	private void pausePlaying(){

		Log.e( TAG,"onPause");
		mMediaPlayer.pause();
		mediaPos = mMediaPlayer.getCurrentPosition();
	}
	private void resumePlaying(){

		Log.e( TAG,"onResume");
		mMediaPlayer.seekTo(mediaPos);
		mMediaPlayer.start();
	}

	private void startPlaying(){
		nowPlaying = nowSelected;
		titleTv.setText(playItems.get(nowPlaying));
		mMediaPlayer = new MediaPlayer();
		playState = true;
		Log.e( TAG,"onStart");
		Log.d("Files", "FileName:" + mFileName);
		try {
			mMediaPlayer.setDataSource(mFileName);
			mMediaPlayer.prepare();

		} catch (IOException e) {
			Log.e(TAG, "error");
		}
		mMediaPlayer.start();
		mediaPos =mMediaPlayer.getCurrentPosition();
		mediaMax = mMediaPlayer.getDuration();
		progressBar.setMax(mediaMax);
		progressBar.setProgress(mediaPos);
		mHandler.post(debugRunnable);
		//
	}

	private Handler mHandler = new Handler();
	private final Runnable debugRunnable = new Runnable() {
		public void run() {
			runOnUiThread(new Runnable() {
				public void run() {
					int mCurrentPosition = mMediaPlayer.getCurrentPosition() ;
					progressBar.setProgress(mCurrentPosition);
					timerTv.setText(milisecondsToSeconds( mMediaPlayer.getCurrentPosition()));
					if(mCurrentPosition == mediaMax){
						playToggleButton.toggle();
					}
				}

			});
			mHandler.postDelayed(debugRunnable,1000);
		}
	};
	private String milisecondsToSeconds(int miliseconds) {
		String time = "";
		DateFormat dateFormat = new SimpleDateFormat("mm:ss");
		time = (dateFormat.format(miliseconds));
		return  time;
	}


}