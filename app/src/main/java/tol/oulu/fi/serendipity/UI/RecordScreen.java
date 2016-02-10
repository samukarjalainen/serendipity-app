package tol.oulu.fi.serendipity.UI;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;


import java.io.IOException;

import tol.oulu.fi.serendipity.R;

public class RecordScreen extends Activity{
 private static final String LOG= "AudioRecordTest";
 private MediaPlayer mMediaPlayer;
 private MediaRecorder mRecorder = null;
 private static String mFileName = null;
 ToggleButton recordToggleButton;
 ToggleButton playToggleButton;
 private Chronometer chronometer;
private SeekBar seekBar;
 private int mediaPos;
 private int mediaMax;
 private final Handler taskHandler = new Handler();
 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_record);
  chronometer = (Chronometer) findViewById(R.id.chronometer);
  seekBar = (SeekBar) findViewById(R.id.seekBar);
  recordToggleButton = (ToggleButton) findViewById(R.id.toggleRecord);
  playToggleButton = (ToggleButton) findViewById(R.id.togglePlay);
  recordToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
   @Override
   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    if (isChecked) {
     startRecording();
     chronometer.setBase(SystemClock.elapsedRealtime());
     chronometer.start();

    } else {
     stopRecording();
     chronometer.stop();
    }
   }
  });
  playToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
   @Override
   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    if (isChecked) {
     startPlaying();
    } else {
     pausePlaying();
    }
   }
  });
 }

 @Override
 protected void onPause() {
  super.onPause();
  if (isFinishing() && mMediaPlayer != null) {
   mMediaPlayer.release();
   mMediaPlayer = null;
  }
 }

 private void startRecording() {
  mRecorder = new MediaRecorder();
  mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
  mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
  mRecorder.setOutputFile(mFileName);
  mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

  try {
   mRecorder.prepare();
  } catch (IOException e) {
   Log.e(LOG, "prepare() failed");
  }

  mRecorder.start();
 }
 private void stopRecording() {
  mRecorder.stop();
  mRecorder.release();
  mRecorder = null;
 }
 public RecordScreen() {
  mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
  mFileName += "/audiorecordtest.3gp";
 }
 private void startPlaying() {
  setVolumeControlStream(AudioManager.STREAM_MUSIC);
  mMediaPlayer = new MediaPlayer();
  try {
   mMediaPlayer.setDataSource(mFileName);
   mMediaPlayer.prepare();

  } catch (IOException e) {
   Log.e(LOG, "error");
  }

  mMediaPlayer.start();
  mediaPos =mMediaPlayer.getCurrentPosition();
  mediaMax = mMediaPlayer.getDuration();

  seekBar.setMax(mediaMax); // Set the Maximum range of the
  seekBar.setProgress(mediaPos);// set current progress to song's

  taskHandler.removeCallbacks(moveSeekBarThread);
  taskHandler.postDelayed(moveSeekBarThread, 100);
 }
 private void pausePlaying() {
  //ToDO define pause and resume
  mMediaPlayer.pause();
 }

 private Runnable moveSeekBarThread = new Runnable() {

  public void run() {
   if(mMediaPlayer.isPlaying()){

    int mediaPos_new = mMediaPlayer.getCurrentPosition();
    int mediaMax_new = mMediaPlayer.getDuration();
    seekBar.setMax(mediaMax_new);
    seekBar.setProgress(mediaPos_new);

    taskHandler.postDelayed(this, 100); //Looping the thread after 0.1 second
    // seconds
   }
  }
 };

}