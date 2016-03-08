package tol.oulu.fi.serendipity.Server;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import tol.oulu.fi.serendipity.Data.DataHandler;
import tol.oulu.fi.serendipity.SerendipityService;
import tol.oulu.fi.serendipity.UI.LoginScreen;
import tol.oulu.fi.serendipity.UI.RecordScreen;

/**
 * Created by ashrafuzzaman on 08/03/2016.
 */
public class SoundDownloader extends AsyncTask<URL, Void, Void> {
	/** used as tag for Logs */
	private static final String TAG = "Serendipity-SD";
	LoginScreen ctx = null;
	DataHandler mDataHandler;
	public SoundDownloader (SerendipityService serendipityService) {
		mDataHandler = DataHandler.getInstance(serendipityService);

	}

	@Override
	protected Void doInBackground(URL... urls) {
		Log.d(TAG, "doInBackground()");
		boolean successValue = false;
		int httpResponseCode;
		String optionalErrorMessage = null;
		String authToken = null;
		JSONObject dataToSend =mDataHandler.authCommsEntity();
		HttpURLConnection urlConnection = null;
		try {
			authToken = mDataHandler.getAuthToken();
			URL realUrl = new URL("http://46.101.104.38:3000/api/sounds/get-all-by-location");
			urlConnection = (HttpURLConnection)realUrl.openConnection();
			urlConnection.setReadTimeout(10 * 1000 /* milliseconds */);
			urlConnection.setConnectTimeout(15 * 1000 /* milliseconds */);
			urlConnection.setDoOutput(true);
			urlConnection.setRequestProperty("authorization", "Bearer " + authToken);
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setUseCaches(false);
			urlConnection.setRequestProperty("Accept", "application/json");
			urlConnection.setRequestProperty("Connection", "close");

			urlConnection.setChunkedStreamingMode(0);
			OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
			byte[] data = dataToSend.toString().getBytes();
			out.write(data);
			out.flush();
			out.close();
			httpResponseCode = urlConnection.getResponseCode();

			Log.e(TAG, String.valueOf(httpResponseCode) );

			if( httpResponseCode >= 200 && httpResponseCode < 300 ) {


			} else {
				//Login failed
				optionalErrorMessage = urlConnection.getResponseMessage();

			}
		} catch (Exception e) {
			Log.e(TAG, "HTTP traffic exception   " + e.toString());
		} finally {
			urlConnection.disconnect();
		}

		return null;
	}


	private String convertStreamToString(InputStream isds) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(isds));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				isds.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}
