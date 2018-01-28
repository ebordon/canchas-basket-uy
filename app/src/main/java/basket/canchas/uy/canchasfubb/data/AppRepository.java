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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by esteban on 1/27/2018.
 */

public class AppRepository {
    private static final String TAG = "AppRepository";

    private static AppRepository instance;
    private AppDatabase appDatabase;

    public static AppRepository getInstance(Context context) {
        if(instance == null){
            instance = new AppRepository(context);
        }
        return instance;
    }

    private AppRepository(Context context) {
        Log.d(TAG, "AppRepository: database name : " + AppDatabase.DATABASE_NAME);
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
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }

    private void insertPlaces(SupportSQLiteDatabase db){
        String[] names = new String[]{"Aguada", "Bigua", "Defensor Sp", "Olimpia"};
        String[] addresses = new String[]{
                "Av. Gral. San Martín 2261, 11800 Montevideo",
                "Jaime Zudáñez 2661, 11300 Montevideo",
                "Jaime Zudáñez 2661, 11300 Montevideo",
                "Av Gral Eugenio Garzón 1923, 12500 Montevideo"
        };
        String[] markers = new String[]{
                "aguada.png",
                "bigua.png",
                "defensor.png",
                "olimpia.png"
        };
        //Insert Places
        for (int i = 0; i< names.length; i++){
            ContentValues contentValuesPlace = new ContentValues();
            contentValuesPlace.put("name", names[i]);
            contentValuesPlace.put("address", addresses[i]);
            contentValuesPlace.put("markerName", markers[i]);
            db.insert("place", OnConflictStrategy.REPLACE, contentValuesPlace);
        }
    }

}