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

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

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
				displayLocation();
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

	private void displayLocation() {

		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if(mLastLocation != null) {
			double latitude = mLastLocation.getLatitude();
			double longtitude = mLastLocation.getLongitude();
			Log.e(TAG, "Latitude:" + String.valueOf(latitude) + " Longitude:" + String.valueOf(longtitude));
		} else {
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
