package basket.canchas.uy.canchasfubb.presenters;

import android.content.Context;
import android.util.Log;

import java.util.List;

import basket.canchas.uy.canchasfubb.data.AppRepository;
import basket.canchas.uy.canchasfubb.data.Place;


/**
 * Created by esteban on 1/27/2018.
 */

public class MainPresenter {
    private static final String TAG = "MainPresenter";

    private View view;
    private Context context;
    AppRepository appRepository;

    public MainPresenter(Context context){
        this.context = context;
        //this.view = view;
        appRepository = AppRepository.getInstance(context);
    }

    public void setup(){
        loadPlaces();
    }

    void loadPlaces(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                List<Place> places = appRepository.getAppDatabase().placeDao().getAll();
                Log.d(TAG, "run: places size " + String.valueOf(places.size()) );
                for (Place p : places){
                    Log.d(TAG, "run: " + p.getName() + " - " + p.getAddress());
                }
            }
        }).start();
    }

    interface View {
        void showPlaces(List<Place> places);
    }

}
