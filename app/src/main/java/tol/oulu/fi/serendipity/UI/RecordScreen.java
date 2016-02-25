package tol.oulu.fi.serendipity.UI;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import tol.oulu.fi.serendipity.R;
import tol.oulu.fi.serendipity.Server.Login;
import tol.oulu.fi.serendipity.Server.SoundUploader;

public class RecordScreen extends Activity {
 private static final String LOG = "AudioRecordTest";
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
  setContentView(R.layout.activity_record_screen);
  chronometer = (Chronometer) findViewById(R.id.chronometer);
  seekBar = (SeekBar) findViewById(R.id.seekBar);
  recordToggleButton = (ToggleButton) findViewById(R.id.toggleRecord);
  playToggleButton = (ToggleButton) findViewById(R.id.togglePlay);
  recordToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
   @Override
   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    if (isChecked) {
     startRecording();
     chronometer.setVisibility(View.VISIBLE);
     chronometer.setBase(SystemClock.elapsedRealtime());
     chronometer.start();
//TODO
    } else {
     chronometer.setVisibility(View.GONE);
     stopRecording();
     LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
     if (ActivityCompat.checkSelfPermission(RecordScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RecordScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      return;
     }
     Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
     //double longitude = location.getLongitude();
     //double latitude = location.getLatitude();
     playToggleButton.setVisibility(View.VISIBLE);
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
  mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
  mRecorder.setOutputFile(mFileName);
  mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

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
  mFileName += "/audiorecordtest3.mp3";
 }
 private void startPlaying() {
  setVolumeControlStream(AudioManager.STREAM_MUSIC);
  mMediaPlayer = new MediaPlayer();
  URL requestURL = null;
 try {
   requestURL = new URL("http://46.101.104.38:3000/upload");
  } catch (MalformedURLException e) {
   Log.e(LOG,"failed");
  }
  SoundUploader serverSync = new SoundUploader( this);
  serverSync.execute(String.valueOf(requestURL));



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