package basket.canchas.uy.canchasfubb.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by esteban on 1/25/2018.
 */

@Dao
public interface PlaceDao {

    @Query("SELECT * FROM place")
    List<Place> getAll();

    @Query("SELECT * FROM place WHERE id IN (:placeIds)")
    List<Place> loadAllByIds(int[] placeIds);

    @Query("SELECT * FROM place WHERE name LIKE :name LIMIT 1")
    Place findByName(String name);

    @Query("SELECT * FROM place WHERE address LIKE :address LIMIT 1")
    Place findByAddress(String address);

    @Insert
    void insertAll(Place... users);

    @Delete
    void delete(Place place);

}

