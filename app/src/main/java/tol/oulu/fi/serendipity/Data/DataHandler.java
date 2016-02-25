package tol.oulu.fi.serendipity.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONObject;

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
                + "auth_token text)";	//authentication token
        db.execSQL(CREATE_SETTINGS_TABLE);
        String SETTINGS_INITIAL_INSERT = "insert into settings("
                + "username,"
                + "password,"
                + "user_id,"
                + "auth_token)"
                + "values ('','','','')";
        db.execSQL(SETTINGS_INITIAL_INSERT);
        //SQL statement to create sound table
        String CREATE_SOUND_TABLE = "CREATE TABLE sound ("
                +"sound_id int"
                + "record_timestamp bigint,"
                + "sound_name String,"
                + "sound_description text,"
                + "sound_file blob,"
                + "longitude int,"
                + "latitude int," +
                "user_id String)";
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

    /**
     * This method retrieves username, password and user_id from settings and puts them in JSONObject
     * in order to send the login request to the server.
     *
     * @return JSONObject containing username and password.
     */
    public JSONObject soundUploadEntity() {
        JSONObject authJson = new JSONObject();
        SQLiteDatabase db = getReadableDatabase();
        //TODO define json for sound upload ( need to pull the sound file from the storage and convert it to a uploadable format
        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT username, password, user_id FROM settings", null);
            try {
                if (cursor != null) {
                    if (cursor.moveToNext() && !cursor.isNull(0) && !cursor.isNull(1)) {
                        authJson.put("username", cursor.getString(0));
                        authJson.put("password", cursor.getString(1));
                        authJson.put("password", cursor.getString(2));
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
}
