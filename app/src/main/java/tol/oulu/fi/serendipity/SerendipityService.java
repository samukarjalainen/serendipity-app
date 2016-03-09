package tol.oulu.fi.serendipity;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import tol.oulu.fi.serendipity.Data.DataHandler;
import tol.oulu.fi.serendipity.Server.Login;
import tol.oulu.fi.serendipity.Server.SoundDownloader;

/**
 * Created by ashrafuzzaman on 07/03/2016.
 */
public class SerendipityService extends Service implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
	private static String TAG = "Serendipity-Service";

	private Location mLastLocation;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;

	private boolean mRequestLocationUpdates = false;
	private DataHandler mDataHandler;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mDataHandler = DataHandler.getInstance(this);
		Log.e(TAG, "onCreate");
		buildGoogleApiClient();

	}

	protected void createLocationRequest() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(10*60000);
		mLocationRequest.setFastestInterval(20*1000);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}
		if (intent != null) {
			String actionOfIntent = intent.getAction();
			if (actionOfIntent != null && actionOfIntent.equals(Intent.ACTION_RUN)) {
				String sound = intent.getStringExtra("soundName");
				displayLocation(sound);
			}
		}
		if (intent != null) {
			String actionOfIntent = intent.getAction();
			if (actionOfIntent != null && actionOfIntent.equals(Intent.ACTION_ASSIST)) {
				createLocationRequest();
			}
		}

		return START_STICKY;
	}

	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
	}

	private void displayLocation(String sound) {

		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if(mLastLocation != null) {
			double latitude = mLastLocation.getLatitude();
			double longitude = mLastLocation.getLongitude();
			mDataHandler.updateSoundDetails(sound, longitude, latitude,null);
			getSoundDetails(sound);
			Log.e(TAG, "Latitude:" + String.valueOf(latitude) + " Longitude:" + String.valueOf(longitude));
		} else {
		}
	}
	private void getSoundDetails(String sound) {
		ArrayList<HashMap<String, Object>> soundData = mDataHandler.getSoundDetails(sound);
		String[] soundPath= new String[soundData.size()];
		Double[] longitude = new Double[soundData.size()];
		Double[] latitude = new Double[soundData.size()];
		for (int i = 0; i < soundData.size(); i++) {
			soundPath[i] = (String) soundData.get(i).get("sound_path");
			longitude [i] = (Double) soundData.get(i).get("longitude");
			latitude [i] = (Double) soundData.get(i).get("latitude");
			Log.e(TAG, soundPath[i]+"Latitude:" + String.valueOf(latitude[i]) + " Longitude:" + String.valueOf(longitude[i]));

		}
	}
	@Override
	public void onConnected(Bundle bundle) {
		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
				mGoogleApiClient);
		if (mLastLocation != null) {

			Toast.makeText(this, "Latitude:" + mLastLocation.getLatitude()+", Longitude:"+mLastLocation.getLongitude(),Toast.LENGTH_LONG).show();

		}

		startLocationUpdates();

	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onLocationChanged(Location location) {
		mLastLocation = location;
		Toast.makeText(this, "Latitude:" + mLastLocation.getLatitude() + ", Longitude:" + mLastLocation.getLongitude(), Toast.LENGTH_LONG).show();
		Log.e(TAG, "Latitude:" + mLastLocation.getLatitude() + ", Longitude:" + mLastLocation.getLongitude());
		mDataHandler.storeLastLocation(mLastLocation.getLongitude(),mLastLocation.getLatitude());
		final URL[] requestURL = {null};
		try {
			requestURL[0] = new URL("http://46.101.104.38:3000/login");
		} catch (MalformedURLException e) {
			Log.e("LOG", "failed");
		}
		SoundDownloader serverSync = new SoundDownloader(SerendipityService.this);
		serverSync.execute(requestURL[0]);
	}


	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}

	protected void startLocationUpdates() {
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);
	}

	protected void stopLocationUpdates() {
		if (mGoogleApiClient != null) {
			LocationServices.FusedLocationApi.removeLocationUpdates(
					mGoogleApiClient, this);
		}
	}
}
