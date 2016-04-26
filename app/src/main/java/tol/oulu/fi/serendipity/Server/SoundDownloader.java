package tol.oulu.fi.serendipity.Server;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import tol.oulu.fi.serendipity.Data.DataHandler;
import tol.oulu.fi.serendipity.R;
import tol.oulu.fi.serendipity.SerendipityService;
import tol.oulu.fi.serendipity.UI.LocateScreen;
import tol.oulu.fi.serendipity.UI.LoginScreen;
import tol.oulu.fi.serendipity.UI.SelectionScreen;

/**
 * Created by ashrafuzzaman on 08/03/2016.
 */
public class SoundDownloader extends AsyncTask<URL, Void, Void> {
	/** used as tag for Logs */
	private static final String TAG = "Serendipity-SD";
	SerendipityService ctx = null;
	DataHandler mDataHandler;
	public SoundDownloader (SerendipityService serendipityService) {
		mDataHandler = DataHandler.getInstance(serendipityService);
		ctx = serendipityService;
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
			int soundCount= 0;
			if( httpResponseCode >= 200 && httpResponseCode < 300 ) {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String result =  convertStreamToString(in);
				JSONArray jsonArray = new JSONArray(result);
				Log.e("tag", jsonArray.toString());

				mDataHandler.insertDownloadedSoundDetails(jsonArray);
				for (int i = 0; i<jsonArray.length(); i++){
					JSONObject jsonObject =jsonArray.getJSONObject(i);
					String id = jsonObject.getString("id");
					String title = jsonObject.getString("title");
					Log.e("tag", id);
					soundCount = i+1;
					downloadSound(i, id,title);

				}
				notification(soundCount);

				onPostExecute()	;
			} else {
				optionalErrorMessage = urlConnection.getResponseMessage();

			}
		} catch (Exception e) {
			Log.e(TAG, "HTTP traffic exception   " + e.toString());
		} finally {
			urlConnection.disconnect();
		}

		return null;
	}
	public static void downloadSound(int i, String id, String title) {
		int count;
		try  {

			URL url = new URL("http://46.101.104.38:3000/sounds/download");
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoOutput(true);
			urlConnection.setRequestProperty("soundid", id);
			urlConnection.connect();

			String PATH = Environment.getExternalStorageDirectory()
					+ "/serendipity/download";
			File file = new File(PATH);
			file.mkdirs();

			String fileName = "title"+ i+".mp3";

			File outputFile = new File(file, fileName);
			FileOutputStream fileOutputStream = new FileOutputStream(outputFile);

			InputStream inputStream = urlConnection.getInputStream();

			byte[] buffer = new byte[1024];
			int len1 = 0;
			while ((len1 = inputStream.read(buffer)) != -1) {
				fileOutputStream.write(buffer, 0, len1);
			}
			fileOutputStream.close();
			inputStream.close();

		}catch (IOException e) {
			Log.e(TAG, "HTTP traffic exception   " + e.toString());

		}
	}

	protected void onPostExecute() {
		//Toast.makeText(activity, Boolean.toString(result), Toast.LENGTH_LONG).show();
		Intent dialogIntent = new Intent(ctx, LocateScreen.class);
		dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(dialogIntent);

	}
	private void notification (int soundCount){
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.ctx);
		mBuilder.setSmallIcon(R.drawable.ic_launcher);
		mBuilder.setContentTitle("Serendipity");
		mBuilder.setContentText(soundCount + " sound(s) available!! click to play!!");
		NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(ctx.NOTIFICATION_SERVICE);

		Intent resultIntent = new Intent(ctx, LocateScreen.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx.getApplication());
		stackBuilder.addParentStack(LocateScreen.class);

// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		int notificationID = 999999;
// notificationID allows you to update the notification later on.
		mNotificationManager.notify(notificationID, mBuilder.build());
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
