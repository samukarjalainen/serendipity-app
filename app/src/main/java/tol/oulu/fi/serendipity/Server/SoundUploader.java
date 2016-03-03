package tol.oulu.fi.serendipity.Server;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import tol.oulu.fi.serendipity.UI.RecordScreen;

/**
 * Created by Ashraf Noor on 12/02/2016.
 */
public class SoundUploader extends AsyncTask<String, Void, Void> {


    private static String mFileName = null;
    private static String boundary = "----WebKitFormBoundaryqfAPjY5oxyXq8rXF";
    DataOutputStream dOutputStream = null;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1 * 1024 * 1024;
    File sourceFile = null;
    String serverResponseMessage = null;
Context ctx;
    public SoundUploader(RecordScreen recordScreen) {
        ctx = recordScreen.getApplicationContext();
        initFile();
        sourceFile = new File(mFileName);
    }

    @Override
    protected Void doInBackground(String... param) {
        JSONObject dataToSend =authCommsEntity();

        try {
            String authToken = param[0];
            FileInputStream fileInputStream = new FileInputStream(sourceFile.getPath());
            URL url  = new URL("http://46.101.104.38:3000/api/sounds/upload");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true); // Allow Inputs
            urlConnection.setDoOutput(true); // Allow Outputs
            urlConnection.setUseCaches(false); // Don't use a Cached Copy
            urlConnection.setRequestProperty("authorization", "Bearer " + authToken);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            urlConnection.setRequestProperty("file", sourceFile.getName());
            urlConnection.setRequestProperty("body", String.valueOf(dataToSend));
            dOutputStream = new DataOutputStream(urlConnection.getOutputStream());
            dOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + "file" + "\";filename="
                    + sourceFile.getName() + lineEnd);
            dOutputStream.writeBytes(lineEnd);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {

                dOutputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            }
            dOutputStream.writeBytes(lineEnd);
            dOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//TODO
            int serverResponseCode = urlConnection.getResponseCode();
            serverResponseMessage = urlConnection.getResponseMessage();
            Log.e("uploadFile", "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);
            if (serverResponseCode <= 200) {
               makeToast.post(runnableForToast);
            } else {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                Log.e("SERVER REPLIED:", in.toString());
            }
            fileInputStream.close();
            dOutputStream.flush();
            dOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException ex) {

            ex.printStackTrace();


        } catch (IOException e) {

        }

        return null;
    }

    /** Handler to delay the next task */
    private final Handler makeToast = new Handler();
    /** Runnable that sets the next task type in BtGwService */
    private final Runnable runnableForToast = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(ctx, "Upload Successful!!", Toast.LENGTH_SHORT).show();

        }
    };
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
    public void initFile() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest2.mp3";

    }
    public JSONObject authCommsEntity() {
        JSONObject authJson = new JSONObject();

        try {
            authJson.put("title", "MySound");
            authJson.put("description", "Yeah, very cool sound wohoo!!");
            authJson.put("lat", "25524");
            authJson.put("long", "24465");
        } catch (JSONException e) {
            e.printStackTrace();
        }

//TODO
        return authJson;
    }

}