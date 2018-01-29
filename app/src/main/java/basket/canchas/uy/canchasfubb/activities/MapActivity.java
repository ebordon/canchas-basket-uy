package basket.canchas.uy.canchasfubb.activities;

import android.Manifest;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import basket.canchas.uy.canchasfubb.adapters.PlaceAutocompleteAdapter;
import basket.canchas.uy.canchasfubb.R;
import basket.canchas.uy.canchasfubb.data.AppRepository;

/**
 * Created by esteban on 1/20/2018.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    //TODO: Modify bounds to fit only Uruguay
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40,-178),
            new LatLng(71,136)
    );

    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    protected GeoDataClient mGeoDataClient;

    private List<basket.canchas.uy.canchasfubb.data.Place> mPlaces;

    //database
    AppRepository appRepository;

    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);

        appRepository = AppRepository.getInstance(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();
        init();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onMapReady: Map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            for (basket.canchas.uy.canchasfubb.data.Place p : mPlaces){
                Log.d(TAG, "run: " + p.getName() + " - " + p.getAddress() + " - " + p.getLatitude());
                addMarker(p);
            }

        }
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);

    }

    private void init(){
        Log.d(TAG, "init: initializing MapActivity stuff");
        loadPlaces();

        // Construct a GeoDataClient for the Google Places API for Android.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient,
                LAT_LNG_BOUNDS, null);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);
        mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });
        hideSoftKeyboard();
    }

    void loadPlaces(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                mPlaces = appRepository.getAppDatabase().placeDao().getAll();
                Log.d(TAG, "run: places size " + String.valueOf(mPlaces.size()) );
            }
        }).start();
    }

    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> addressList = new ArrayList<>();
        try {
            addressList = geocoder.getFromLocationName(searchString,1);
        } catch (IOException e){
            Log.d(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (addressList.size() > 0){
            Address address = addressList.get(0);
            Log.d(TAG, "geoLocate: Found location " + address.toString());
            Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
            LatLng position = new LatLng(address.getLatitude(),address.getLongitude());
            addMarker(position, address.getAddressLine(0));
            moveCamera(position, DEFAULT_ZOOM);
        }
    }

    private void addMarker(basket.canchas.uy.canchasfubb.data.Place place){
        Log.d(TAG, "addMarker: Adding Marker for place: " + place.getName());
        //Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.ic_marker_olimpia);
        //Resources resources = context.getResources();
        final int resourceId = getResources().getIdentifier(place.getMarkerName(), "drawable",
                getPackageName());
        Bitmap bm = BitmapFactory.decodeResource(getResources(), resourceId);
        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(place.getLatitude(), place.getLongitude()))
                .title(place.getName())
                .snippet(place.getAddress())
                .icon(BitmapDescriptorFactory.fromBitmap(bm));
        mMap.addMarker(options);
    }

    private void addMarker(LatLng latLng, String title){
        Log.d(TAG, "addMarker: adding marker to " + title);
        Log.d(TAG, "addMarker: latLng: " + latLng.latitude + " " + latLng.longitude);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);
        mMap.addMarker(options);
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: Getting current location");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    moveCamera(new LatLng(location.getLatitude(), location.getLongitude()),
                                            DEFAULT_ZOOM);
                                } else {
                                    Log.d(TAG, "onComplete: current location is null");
                                    Toast.makeText(MapActivity.this, "Could't get device location",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: security exception: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        //Set the camera in the desired position.
        Log.d(TAG, "moveCamera: moving the camera to: lat:" + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        hideSoftKeyboard();

    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: Checking location permissions");
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};
        if ((ContextCompat.checkSelfPermission(
                this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            mLocationPermissionGranted = true;
            initMap();
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permissions granted!");
                    mLocationPermissionGranted = true;
                    // initialize the map
                    initMap();
                }
            }
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /* Google Places autocomplete suggestions */
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data Client to retrieve a Place object with
             additional details about the place.
              */
            Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(placeId);
            placeResult.addOnCompleteListener(mUpdatePlaceDetailsCallback);

            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data Client query that shows the first place result in
     * the details view on screen.
     */
    private OnCompleteListener<PlaceBufferResponse> mUpdatePlaceDetailsCallback
            = new OnCompleteListener<PlaceBufferResponse>() {
        @Override
        public void onComplete(Task<PlaceBufferResponse> task) {
            Log.d(TAG, "onComplete: getting the selected place");
            hideSoftKeyboard();
            try {
                PlaceBufferResponse places = task.getResult();

                // Get the Place object from the buffer.
                final Place place = places.get(0);
                addMarker(place.getLatLng(),place.getName().toString());
                moveCamera(place.getLatLng(), DEFAULT_ZOOM);

                places.release();
            } catch (RuntimeRemoteException e) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete.", e);
                return;
            }
        }
    };

}
