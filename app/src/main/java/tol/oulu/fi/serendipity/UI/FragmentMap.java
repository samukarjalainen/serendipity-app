package tol.oulu.fi.serendipity.UI;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import tol.oulu.fi.serendipity.Data.DataHandler;
import tol.oulu.fi.serendipity.R;

/**
 * Created by ashrafuzzaman on 30/03/2016.
 */
public class FragmentMap extends Fragment implements OnMapReadyCallback {
DataHandler mDataHandler;
	private GoogleMap mMap;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_map, container, false);

		mDataHandler = DataHandler.getInstance(getActivity());
		SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
				.findFragmentById(R.id.map2);
		mapFragment.getMapAsync(this);
		return view;
	}

	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera. In this case,
	 * we just add a marker near Sydney, Australia.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap) {

		mMap = googleMap;
		ArrayList<HashMap<String, Object>> soundData =mDataHandler. getAllSoundData();
		String[] title = new String[soundData.size()];
		Double[] longitude = new Double[soundData.size()];
		Double[] latitude = new Double[soundData.size()];

		for (int i = 0; i < soundData.size(); i++) {

			title[i] = (String) soundData.get(i).get("sound_name");
			latitude[i] = (Double) soundData.get(i).get("latitude");
			longitude[i] = (Double) soundData.get(i).get("longitude");

			Log.e(title[i] ,title[i] );
			// Add a marker in Sydney and move the camera
			LatLng sydney = new LatLng(latitude[i], longitude[i]);
			mMap.addMarker(new MarkerOptions().position(sydney).title(title[i])).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.location2));
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12.0f));
		}



		mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				String title = marker.getTitle();
				((LocateScreen)getActivity()).setCurrentItem (0, true);
				Log.e("marker", title);
				return true;
			}
		});

	}

}