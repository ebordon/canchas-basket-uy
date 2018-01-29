package basket.canchas.uy.canchasfubb.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by esteban on 1/22/2018.
 */

@Entity(indices = {@Index(value = "name", unique = true)})
public class Place {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String address;
    private Double latitude;
    private Double longitude;

    private String markerName;

    public Place(String name, String address, Double latitude, Double longitude, String markerName) {
        this.name = name;
        this.address = address;
        this.markerName = markerName;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
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

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
