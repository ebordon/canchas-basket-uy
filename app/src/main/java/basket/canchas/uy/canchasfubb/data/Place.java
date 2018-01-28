package basket.canchas.uy.canchasfubb.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by esteban on 1/22/2018.
 */

@Entity(indices = {@Index(value = "name", unique = true)})
public class Place {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String address;

    private String markerName;

    public Place(String name, String address, String markerName) {
        this.name = name;
        this.address = address;
        this.markerName = markerName;
    }
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getMarkerName() {
        return markerName;
    }
}
