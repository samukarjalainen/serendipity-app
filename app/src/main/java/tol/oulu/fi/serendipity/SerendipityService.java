package tol.oulu.fi.serendipity;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;

import tol.oulu.fi.serendipity.Data.DataHandler;

/**
 * Created by ashrafuzzaman on 07/03/2016.
 */
public class SerendipityService extends Service implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, LocationListener {
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
		return super.onStartCommand(intent, flags, startId);
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

	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}
}
