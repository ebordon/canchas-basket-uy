package basket.canchas.uy.canchasfubb.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.List;

import javax.security.auth.login.LoginException;

import basket.canchas.uy.canchasfubb.R;
import basket.canchas.uy.canchasfubb.data.AppRepository;

import basket.canchas.uy.canchasfubb.data.Place;
import basket.canchas.uy.canchasfubb.presenters.MainPresenter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;
    AppRepository appRepository;

    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainPresenter(this);

        presenter.setup();

        if (isServicesOk()){
            init();
        }
    }

    private void init(){
        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
        appRepository = AppRepository.getInstance(this);

    }

    public boolean isServicesOk(){
        Log.d(TAG, "isServiceOk: checking if the phone has google services installed");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServiceOk: Google Play Services is working");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServiceOk: Error occured but is resolvable");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else {
            Log.d(TAG, "isServiceOk: Google Play Services is not installed");
            Toast.makeText(this, "We can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
