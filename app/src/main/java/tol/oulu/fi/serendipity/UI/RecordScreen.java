package tol.oulu.fi.serendipity.UI;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import tol.oulu.fi.serendipity.Data.DataHandler;
import tol.oulu.fi.serendipity.R;
import tol.oulu.fi.serendipity.SerendipityService;
import tol.oulu.fi.serendipity.Server.SoundUploader;

public class RecordScreen extends Activity {
 private static final String TAG = "Serendipity-RS";
 private MediaPlayer mMediaPlayer;
 private MediaRecorder mRecorder = null;
 private static String mFileName = null;
 ToggleButton recordToggleButton;
 ToggleButton playToggleButton;
 ImageButton uploadButton;
 private Chronometer chronometer;
 private SeekBar seekBar;
 private int mediaPos;
 private int mediaMax;
 private final Handler taskHandler = new Handler();
 private float x1,x2;
 static final int MIN_DISTANCE = 150;
 private DataHandler mDataHandler;
 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_record_screen);
  mDataHandler = DataHandler.getInstance(this);
  chronometer = (Chronometer) findViewById(R.id.chronometer);
  uploadButton = (ImageButton) findViewById(R.id.imageButton);
  seekBar = (SeekBar) findViewById(R.id.seekBar);
  recordToggleButton = (ToggleButton) findViewById(R.id.toggleRecord);
  playToggleButton = (ToggleButton) findViewById(R.id.togglePlay);
  recordToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
   @Override
   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    if (isChecked) {
     startRecording();

     uploadButton.setVisibility(View.GONE);
     playToggleButton.setVisibility(View.GONE);
     chronometer.setVisibility(View.VISIBLE);
     chronometer.setBase(SystemClock.elapsedRealtime());
     chronometer.start();
//TODO
    } else {
     chronometer.setVisibility(View.GONE);
     uploadButton.setVisibility(View.VISIBLE);
     stopRecording();

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
  uploadButton.setOnClickListener(new ImageButton.OnClickListener(){

   @Override
   public void onClick(View v) {
    uploadDialog();
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
   Log.e(TAG, "prepare() failed");
  }

  mRecorder.start();
 }
 private void stopRecording() {
  mRecorder.stop();
  mRecorder.release();
  mRecorder = null;

  Intent intentForService = new Intent(RecordScreen.this, SerendipityService.class);
  intentForService.setAction(Intent.ACTION_RUN);
  intentForService.putExtra("soundName", mFileName);
  startService(intentForService);

 }
 public RecordScreen() {
  mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
  mFileName += "/audiorecordtest2.mp3";
  mDataHandler = DataHandler.getInstance(this);
  mDataHandler.insertSoundDetails(mFileName);
 }
 private void startPlaying() {
  setVolumeControlStream(AudioManager.STREAM_MUSIC);
  mMediaPlayer = new MediaPlayer();
  try {
   mMediaPlayer.setDataSource(mFileName);
   mMediaPlayer.prepare();

  } catch (IOException e) {
   Log.e(TAG, "error");
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
 private void uploadDialog(){
  final Dialog dialog = new Dialog(this);
  dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
  dialog.setContentView(R.layout.dialog_upload);
  TextView dialogMessage = (TextView) dialog.findViewById(R.id.dialog_title);
  dialogMessage.setText(getResources().getString(R.string.upload_text));

  Button dialogButtonOk = (Button) dialog.findViewById(R.id.dialogButtonLogout);
  // if button is clicked, close the custom dialog
  dialogButtonOk.setOnClickListener(new View.OnClickListener() {
   @Override
   public void onClick(View v) {
    //timerHandler.CancelAlarm(getApplicationContext());
    synchronized (DataHandler.class) {

     DataHandler mDataHandler = DataHandler.getInstance(RecordScreen.this);
     String authToken = mDataHandler.getAuthToken();

     SoundUploader serverSync = new SoundUploader( RecordScreen.this);
     serverSync.execute(mFileName);

    }

    dialog.dismiss();
   }
  });
  Button dialogButtonCancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
  // if button is clicked, close the custom dialog
  dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
   @Override
   public void onClick(View v) {
    dialog.dismiss();
   }
  });
  dialog.show();

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

 @Override
 public boolean onTouchEvent(MotionEvent event)
 {
  switch(event.getAction())
  {
   case MotionEvent.ACTION_DOWN:
    x1 = event.getX();
    break;
   case MotionEvent.ACTION_UP:
    x2 = event.getX();
    float deltaX = x2 - x1;

    if (Math.abs(deltaX) > MIN_DISTANCE)
    {
     // Left to Right swipe action
     if (x2 > x1)
     {
      Toast.makeText(this, "Left to Right swipe [Next]", Toast.LENGTH_SHORT).show ();
     }

     // Right to left swipe action
     else
     {
      Intent intent = new Intent(this,LoginScreen.class );
      this.startActivity(intent);
      Toast.makeText(this, "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show ();
     }

    }
    else
    {
     // consider as something else - a screen tap for example
    }
    break;
  }
  return super.onTouchEvent(event);
 }

 }

