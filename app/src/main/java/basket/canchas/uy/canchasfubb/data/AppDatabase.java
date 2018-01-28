package basket.canchas.uy.canchasfubb.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.ContentValues;
import android.content.Context;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

/**
 * Created by esteban on 1/25/2018.
 */

@Database(entities = {Place.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    static final String DATABASE_NAME = "canchas-db";
    public abstract PlaceDao placeDao();

}

