package tol.oulu.fi.serendipity.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DataHandler extends SQLiteOpenHelper {

    private static final String TAG = "Serendipity-db";
    private static final int DATABASE_VERSION =1;
    private  static final String DATABASE_NAME= "Serendipity-db";
    private static DataHandler mInstance;

    private DataHandler(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DataHandler getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DataHandler(ctx.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQL statement to create settings table yet not fixed
        String CREATE_SETTINGS_TABLE = "CREATE TABLE settings ("
                + "username text,"
                + "password text,"	//username
                + "user_id text,"	//password
                + "auth_token text,"//authentication token
                + "last_longitude REAL,"
                + "last_latitude REAL,"
                + "selected_row int)";
        db.execSQL(CREATE_SETTINGS_TABLE);
        String SETTINGS_INITIAL_INSERT = "insert into settings("
                + "username,"
                + "password,"
                + "user_id,"
                + "auth_token,"
                + "last_longitude,"
                + "last_latitude,"
                + "selected_row)"
                + "values ('','','',null,0,0,0)";
        db.execSQL(SETTINGS_INITIAL_INSERT);
        //SQL statement to create sound table
        String CREATE_SOUND_TABLE = "CREATE TABLE sound ("
                + "sound_path varchar(10),"
                + "record_timestamp bigint,"
                + "sound_name String,"
                + "sound_description text,"
                + "longitude REAL,"
                + "latitude REAL,"
                + "user_id String)";
        db.execSQL(CREATE_SOUND_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    /**
     * This method updates username and password in settings table.
     * This method assumes validity check has been done for empty strings before
     * it is called.
     *
     * @param aUserName Username in string format.
     * @param aPassword Password in string format.
     */
    public boolean updateLoginCredentials(String aUserName, String aPassword) {
        boolean retval = false;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", aUserName);
        cv.put("password", aPassword);
        if (db.update("settings", cv, null, null) == 1) {
            retval = true;
        } else {
            Log.e(TAG, "updateLoginCredentials failed");

        }
        return retval;
    }

    /**
     * This method saves auth_token to the settings table.
     *
     * @param aToken authentication token in string format.
     * @return True if the query is successful.
     */
    public boolean storeAuthToken(String aToken) {
        boolean retval = false;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("auth_token", aToken);
        if (db.update("settings", cv, null, null) == 1) {
            retval = true;
        } else {
            Log.e(TAG, "storeAuthToken failed");
        }
        return retval;
    }
    /**
     * This method saves auth_token to the settings table.
     *
     * @param position authentication token in string format.
     * @return True if the query is successful.
     */
    public boolean storeSelectedItem(int position) {
        boolean retval = false;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("selected_row", position);
        if (db.update("settings", cv, null, null) == 1) {
            retval = true;
        } else {
            Log.e(TAG, "storeSelectedItem failed");
        }
        return retval;
    }

    public int getSelectedItem() {
        int retval = 0;
        SQLiteDatabase db = getReadableDatabase();
        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT selected_row FROM settings", null);
            try {
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        retval = cursor.getInt(0);
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return retval;
    }
    /**
     * This method retrieves username, password and user_id from settings and puts them in JSONObject
     * in order to send the login request to the server.
     *
     * @return JSONObject containing username and password.
     */
    public JSONObject authenticationEntity() {
        JSONObject authJson = new JSONObject();
        SQLiteDatabase db = getReadableDatabase();
        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT username, password, user_id FROM settings", null);
            try {
                if (cursor != null) {
                    if (cursor.moveToNext() && !cursor.isNull(0) && !cursor.isNull(1)) {
                        authJson.put("username", cursor.getString(0));
                        authJson.put("password", cursor.getString(1));
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            } finally {
                cursor.close();
            }
        }
        return authJson;
    }
    public JSONObject authCommsEntity() {
        JSONObject authJson = new JSONObject();

        try {
            authJson.put("lat", getlastLatitude());
            authJson.put("long", getlastLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return authJson;
    }

    public String getAuthToken() {
        String retval = "";
        SQLiteDatabase db = getReadableDatabase();
        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT auth_token FROM settings", null);
            try {
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        retval = cursor.getString(0);
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return retval;
    }
    public boolean storeLastLocation(double longitude, double latitude) {
        boolean retval = false;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("last_longitude", longitude);
        cv.put("last_latitude", latitude);
        if (db.update("settings", cv, null, null) == 1) {
            retval = true;
        } else {
            Log.e(TAG, "storeLocation failed");
        }
        return retval;
    }

    public String getlastLongitude() {
        String retval = "";
        SQLiteDatabase db = getReadableDatabase();
        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT last_longitude FROM settings", null);
            try {
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        retval = cursor.getString(0);
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return retval;
    }

    public String getlastLatitude() {
        String retval = "";
        SQLiteDatabase db = getReadableDatabase();
        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT last_latitude FROM settings", null);
            try {
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        retval = cursor.getString(0);
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return retval;
    }
    /**
     * This method retrieves username, password and user_id from settings and puts them in JSONObject
     * in order to send the login request to the server.
     *
     * @return JSONObject containing username and password.
     */
    public JSONObject soundUploadEntity(String path) {
        JSONObject soundUploadJson = new JSONObject();
        SQLiteDatabase db = getReadableDatabase();
        //TODO define json for sound upload ( need to pull the sound file from the storage and convert it to a uploadable format
        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT user_id, sound_path, sound_description, longitude, latitude  FROM sound WHERE sound_path ='" + path + "'", null);

            try {
                if (cursor.moveToFirst()) {
                    do {
                            soundUploadJson.put("title",  cursor.getString(1));
                            soundUploadJson.put("description",cursor.getString(2));
                            soundUploadJson.put("long", String.valueOf(cursor.getDouble(3)));
                            soundUploadJson.put("lat", String.valueOf(cursor.getDouble(4)));
                        Log.e(TAG, "soundUploadEntity" + path);

                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            } finally {
                cursor.close();
            }
        }
        Log.e(TAG,"soundUploadEntity"+ path);
        return soundUploadJson;
    }
    /**
     * This method retrieves sound_name.
     *
     * @param soundId generated ID of the catcher.
     * @return sound_name in String format
     */
    public String getSoundName(String soundId) {
        String retval = null;
        SQLiteDatabase db = getReadableDatabase();
        if (db != null) {
            Cursor cursor = db.rawQuery(
                    "SELECT sound_name FROM sound WHERE sound_path = '" + soundId + "'", null);

            if (cursor != null) {
                try {
                    if (cursor.moveToNext()) {
                        retval = cursor.getString(0);
                    }
                } finally {
                    cursor.close();
                }
            }
        }
        return retval;
    }

    public void insertSoundDetails(String soundPath){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("sound_path", soundPath);
        cv.put("record_timestamp", System.currentTimeMillis());
        cv.put("sound_name", "test");
        cv.put("sound_description", "sdh,nbnvbkhvgsgh");
        cv.put("longitude", 0.0);
        cv.put("latitude", 0.0);
        db.insert("sound", null, cv);
    }

    public void insertDownloadedSoundDetails(JSONArray jsonArray){
        String soundPath = "";
        String title = "";
        String description = "";
        double longitude = 0.0;
        double latitude =0.0;

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i<jsonArray.length(); i++){
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                Log.e("tag", id);
                title = jsonObject.getString("title");
                description = jsonObject.getString("description");
                latitude = jsonObject.getDouble("lat");
                longitude = jsonObject.getDouble("long");
                cv.put("sound_path", soundPath);
                cv.put("record_timestamp", System.currentTimeMillis());
                cv.put("sound_name", title);
                cv.put("sound_description", description);
                cv.put("longitude", longitude);
                cv.put("latitude", latitude);
                db.insert("sound", null, cv);
                Log.e("tag","success");
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
    public ArrayList<HashMap<String, Object>> getAllSoundData() {
        ArrayList<HashMap<String, Object>> catcherArrayList = new ArrayList<HashMap<String, Object>>();
        String selectQuery = "SELECT sound_name, longitude, latitude FROM sound";
        SQLiteDatabase database = getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        HashMap<String, Object> contactMap = new HashMap<String, Object>();
                        contactMap.put("sound_name", cursor.getString(0));
                        contactMap.put("longitude", cursor.getDouble(1));
                        contactMap.put("latitude", cursor.getDouble(2));
                        catcherArrayList.add(contactMap);
                    } while (cursor.moveToNext());
                }
            }
        } finally {
            cursor.close();
        }
        return catcherArrayList;
    }


    public void updateSoundDetails( String path, Double longitude, Double latitude, String newName ){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        if(latitude != null){
            cv.put("longitude", longitude);
            cv.put("latitude", latitude);
        }
        if(newName !=null ){
            cv.put("sound_name", newName);
        }
        if (db.update("sound", cv, "sound_path = ?", new String[]{path}) != 1) {
            Log.e(TAG, "Problem when updating sound data to DB");
        }

    }


    /**
     * Methods for getting all of the sound's data from sound table to an ArrayList.
     *
     * @return ArrayList
     */
    public ArrayList<HashMap<String, Object>> getSoundDetails(String path) {
        ArrayList<HashMap<String, Object>> soundArrayList = new ArrayList<HashMap<String, Object>>();
        String selectQuery = "SELECT user_id, sound_path, sound_description, longitude, latitude  FROM sound WHERE sound_path ='" + path + "'";
        SQLiteDatabase database = getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        HashMap<String, Object> contactMap = new HashMap<String, Object>();
                        contactMap.put("user_id", cursor.getString(0));
                        contactMap.put("sound_path", cursor.getString(1));
                        contactMap.put("sound_description", cursor.getString(2));
                        contactMap.put("longitude", cursor.getDouble(3));
                        contactMap.put("latitude", cursor.getDouble(4));
                        soundArrayList.add(contactMap);
                    } while (cursor.moveToNext());
                }
            }
        } finally {
            cursor.close();
        }
        return soundArrayList;

    }
}
