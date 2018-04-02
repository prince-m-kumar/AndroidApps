package kumar.prince.kotlinapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import kumar.prince.instachksafetylib.Data;
import kumar.prince.instachksafetylib.SafetyServices;
import kumar.prince.instachksafetylib.ServiceAlertInterface;


/**
 * @author prince on 14/3/18.
 */

public class MainActivity extends AppCompatActivity implements Data.onGpsServiceUpdate,ServiceAlertInterface {
    private static final int OVERLAY_PERMISSION_REQ_CODE = 1001;
    SafetyServices safetyServices;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=getBaseContext();
      //  checkTTS();
        safetyServices=new SafetyServices(getBaseContext(),this);

         Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                 //       startService(new Intent(getBaseContext(), SpeedoMetersServices.class));

                        startMouseService();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
       // safetyServices.stopSafetyServices();
       // stopService(new Intent(getBaseContext(), SpeedoMetersServices.class));
    }


    @Override
    public void update() {

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startMouseService() {
        if ((Build.VERSION.SDK_INT >= 23)) {
            if (!Settings.canDrawOverlays(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            } else if (Settings.canDrawOverlays(context)) {
                safetyServices.startSafetyServices();
            }
        }
        else
        {
            safetyServices.startSafetyServices();
        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            safetyServices.startSafetyServices();

        }
    }


    @Override
    public void overSpeed(String message, float speed) {
        Log.i("Main",message+" "+speed);
    }

    @Override
    public void phoneShake(String message) {
        Log.i("Main",message);

    }

    @Override
    public void batteryOverHeating(String message, float temp) {
        Log.i("Main",message+" "+temp);

    }
}
