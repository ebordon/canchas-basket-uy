package basket.canchas.uy.canchasfubb.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by esteban on 1/27/2018.
 */

public class AppRepository {
    private static final String TAG = "AppRepository";

    private static AppRepository instance;
    private AppDatabase appDatabase;
    private JSONObject mData = null;

    public static AppRepository getInstance(Context context) {
        if(instance == null){
            instance = new AppRepository(context);
        }
        return instance;
    }

    private AppRepository(Context context){
        Log.d(TAG, "AppRepository: database name : " + AppDatabase.DATABASE_NAME);
        try {
            mData = new JSONObject(readJSONFromAsset(context));
            Log.d(TAG, "AppRepository: got json: " + mData.getString("teams"));
        appDatabase = Room.databaseBuilder(context,AppDatabase.class, AppDatabase.DATABASE_NAME)
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Log.d(TAG, "onCreate: Initializing db");
                        insertPlaces(db);
                    }
                })
                .build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }

    private void insertPlaces(SupportSQLiteDatabase db){
        try {
            //TODO: Get the data from a service that invoke a web api
            JSONArray teams = mData.getJSONArray("teams");
            for (int i = 0; i<teams.length();i++){
                JSONObject obj = teams.getJSONObject(i);
                Log.d(TAG, "insertPlaces: team: " + obj.get("name"));
                ContentValues contentValuesPlace = new ContentValues();
                contentValuesPlace.put("name", obj.getString("name"));
                contentValuesPlace.put("address", obj.getString("address"));
                contentValuesPlace.put("markerName", obj.getString("marker"));
                contentValuesPlace.put("latitude", obj.getDouble("latitude"));
                contentValuesPlace.put("longitude", obj.getDouble("longitude"));
                db.insert("place", OnConflictStrategy.REPLACE, contentValuesPlace);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String readJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("teams.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}