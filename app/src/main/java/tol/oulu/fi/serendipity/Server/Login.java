package tol.oulu.fi.serendipity.Server;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import tol.oulu.fi.serendipity.Data.DataHandler;
import tol.oulu.fi.serendipity.UI.LoginScreen;
import tol.oulu.fi.serendipity.UI.RecordScreen;

/**
 * Created by ashrafuzzaman on 22/02/2016.
 */
public class Login extends AsyncTask<URL, Void, Void> {
    /** used as tag for Logs */
    private static final String TAG = "BtGw-as";
   LoginScreen ctx = null;
    DataHandler mDataHandler;
    public Login(LoginScreen recordScreen) {
        mDataHandler = DataHandler.getInstance(recordScreen);
        ctx = recordScreen;
    }

    @Override
    protected Void doInBackground(URL... urls) {
        Log.d(TAG, "doInBackground()");
        boolean successValue = false;
        int httpResponseCode;
        String optionalErrorMessage = null;
        String authToken = null;
        JSONObject dataToSend =mDataHandler.authenticationEntity();


        URL realUrl = urls[0];
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection)realUrl.openConnection();
            urlConnection.setReadTimeout(10 * 1000 /* milliseconds */);
            urlConnection.setConnectTimeout(15 * 1000 /* milliseconds */);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Connection", "close");

            //TODO for best performance setFixedLengthStreamingMode(int) should be used instead of setChunkedStreamingMode(0)
            urlConnection.setChunkedStreamingMode(0);
            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            byte[] data = dataToSend.toString().getBytes();
           Log.e(TAG, data.toString());
            //TODO use gzip if defined by BtGwService.mCompressionWithServerInUse
            out.write(data);
            out.flush();
            out.close();
            httpResponseCode = urlConnection.getResponseCode();

            Log.e(TAG, String.valueOf(httpResponseCode) + urlConnection.getResponseMessage());
            //TODO check that connection did not make redirection (hotel wifi)

            if( httpResponseCode >= 200 && httpResponseCode < 300 ) {
                //Response code was 2xx
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String result =  convertStreamToString(in);
                JSONObject jsonObject = new JSONObject(result);
                Log.e("tag", jsonObject.getString("token"));
                authToken = jsonObject.getString("token");
                mDataHandler.storeAuthToken(authToken);
                onPostExecute();
            } else {
                //Login failed
                optionalErrorMessage = urlConnection.getResponseMessage();

            }
        } catch (Exception e) {
            Log.e(TAG, "HTTP traffic exception   " + e.toString());
        } finally {
            urlConnection.disconnect();
        }



     //   SoundUploader serverSync = new SoundUploader(this.ctx);
    //    serverSync.execute(authToken);


        return null;
    }


    protected void onPostExecute() {
        //Toast.makeText(activity, Boolean.toString(result), Toast.LENGTH_LONG).show();

        ctx.startActivity(new Intent(ctx, RecordScreen.class));
        ctx.finish();
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
