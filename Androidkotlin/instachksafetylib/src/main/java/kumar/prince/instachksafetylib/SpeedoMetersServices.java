package kumar.prince.instachksafetylib;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Locale;



/**
 * Created by prince on 13/3/18.
 */

public class SpeedoMetersServices extends Service implements LocationListener, ShakeEventListener.OnShakeListener,GpsStatus.Listener {
    private LocationManager mLocationManager;
    private Data data;
    private SensorManager mSensorManager;
    private ShakeEventListener mSensorListener;
    AlertSound alertSound;
    AlertMessageServices alertMessageServices;

    Location lastlocation = new Location("last");
//    Data data;

    double currentLon = 0;
    double currentLat = 0;
    double lastLon = 0;
    double lastLat = 0;
    private double calculatedSpeed = 0;
    PendingIntent contentIntent;
    private IntentFilter intentfilter;
    private static Speaker speaker;
    private TextToSpeech t1;
    private AlertDialog alert;

    @Override
    public void onCreate() {
        super.onCreate();
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });


/*
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
         cursorView = View.inflate(getBaseContext(), R.layout.custom_dialog_main, null);
        Button cancel = (Button) cursorView.findViewById(R.id.btn_yes);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Dialog","yes");
            }
        });
        Button ok = (Button) cursorView.findViewById(R.id.btn_no);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //whatever you want
                Log.i("Dialog","no");
            }
        });
        cursorLayout = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
//        cursorLayout.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;
        cursorLayout.gravity = Gravity.CENTER ;*/

/*
        cursorLayout.x = 0;
        cursorLayout.y = 0;
        location = new int[2];
        cursorView.getLocationOnScreen(location);*/
      //  windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);


      /*  int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
*/
        alertSound=new AlertSound(getApplicationContext());
        alertMessageServices=new AlertMessageServices(getApplicationContext());
        Intent notificationIntent = new Intent(this, SpeedoMetersServices.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        contentIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, 0);
        updateNotification(false);
     //   speaker=new Speaker(this);
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        mLocationManager.addGpsStatusListener(this);

        //Intent intent = this.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        SpeedoMetersServices.this.registerReceiver(broadcastreceiver,intentfilter);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();
        mSensorListener.setOnShakeListener(this);
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
    }







    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        data=SafetyServices.getData();

      if (data.isRunning()) {
            currentLat = location.getLatitude();
            currentLon = location.getLongitude();

            if (data.isFirstTime()) {
                lastlocation=location;
                data.setFirstTime(false);
            }

            lastlocation.setLatitude(lastLat);
            lastlocation.setLongitude(lastLon);
            float distance = lastlocation.distanceTo(location);
        //    Log.d("SPEED", distance + "  " + location.getAccuracy());

            long elapsedTime = 0;

            if (location.getAccuracy() < distance) {
                data.addDistance(distance);
                elapsedTime = ((location.getTime() - lastlocation.getTime())/1000);//milli second to second

                calculatedSpeed = (lastlocation.distanceTo(location) / elapsedTime)*(3.6);
                /*calculatedSpeed=calculatedSpeed*3.6;*/
                lastlocation=location;
            }


            //this.lastLocation = location;
            double speed = location.hasSpeed() ? location.getSpeed() : calculatedSpeed;

          //  Log.d("SPEED", distance + "  " + location.getSpeed() + " " +elapsedTime);
            String speed1 = String.format(Locale.ENGLISH, "%.0f", speed*3.6 ) + "km/h";

            Log.d("SPEED", distance + "  " + speed1 + " ");
            speed1=String.format(Locale.ENGLISH, "%.0f", speed*3.6 );
            if ((speed*3.6)>60){

                alertSound.playAlertSound(R.raw.seat_belt);
                sendBroadcastMessage(speed1,InstachkServices.SPEED_LIMIT_ALERT,Float.parseFloat(speed1),0.0f);

            }


            if (speed == 0.0) {
                new isStillStopped().execute();
            }
            if (location.hasSpeed()) {
                data.setCurSpeed(location.getSpeed() * 3.6);
                if (location.getSpeed() == 0) {
                    new isStillStopped().execute();
                }
            }
            data.update();
           // updateNotification(true);
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onGpsStatusChanged(int i) {

    }

    @Override
    public void onShake() {
        Log.d("Shake", "Phone is shaking ");
        sendBroadcastMessage("Phone is Shaking",InstachkServices.PHONE_SHAKE_ALERT,0.0f,0.0f);

    }

    class isStillStopped extends AsyncTask<Void, Integer, String> {
        int timer = 0;
        @Override
        protected String doInBackground(Void... unused) {
            try {
                while (data.getCurSpeed() == 0) {
                    Thread.sleep(1000);
                    timer++;
                }
            } catch (InterruptedException t) {
                return ("The sleep operation failed");
            }
            return ("return object when task is finished");
        }

        @Override
        protected void onPostExecute(String message) {
            data.setTimeStopped(timer);
        }
    }

    public void updateNotification(boolean asData){
   /*     Notification.Builder builder = new Notification.Builder(getBaseContext())
                .setContentTitle(getString(R.string.running))
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(contentIntent);

        if(asData){
            builder.setContentText(String.format(getString(R.string.notification), data.getMaxSpeed(), data.getDistance()));
        }else{
            builder.setContentText(String.format(getString(R.string.notification), '-', '-'));
        }
        Notification notification = builder.build();
        startForeground(R.string.noti_id, notification);*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationManager.removeUpdates(this);
        mLocationManager.removeGpsStatusListener(this);
       // stopForeground(true);
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        SpeedoMetersServices.this.unregisterReceiver(broadcastreceiver);
        mSensorManager.unregisterListener(mSensorListener);
    }

    private boolean alertGiven=true;
    private BroadcastReceiver broadcastreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //BatteryTemp = (float)(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0))/10;
            float  temp   = ((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0)) / 10;
            Log.d("Battery", String.valueOf(temp) + (char) 0x00B0 +"C");
            //windowManager.removeView(cursorView);

            if (temp>30&& alertGiven){
               // t1.speak("Battery"+ String.valueOf(temp) + (char) 0x00B0 +"C", TextToSpeech.QUEUE_FLUSH, null);
                alertGiven=true;

                //setAlertGiven();
                sendBroadcastMessage(String.valueOf(temp) + (char) 0x00B0 +"C",InstachkServices.BATTERY_HEAT_ALERT,
                        0.0f,temp);

            }


        }
    };

 /*   private void setAlertGiven(){
        windowManager.addView(cursorView, cursorLayout);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                windowManager.removeView(cursorView);
                alertGiven=true;
            }
        }, 50000);
    }
*/
    private void sendBroadcastMessage (String message,int alertType,float calculatedSpeed,float temp){
        Intent intent = new Intent ("message"); //put the same message as in the filter you used in the activity when registering the receiver
       // intent.putExtra("success", success);
        intent.putExtra(InstachkServices.ALERT_TYPE,alertType);
        intent.putExtra(InstachkServices.CURRENT_SPEED,calculatedSpeed);
        intent.putExtra(InstachkServices.TEMPERATURE_VALUE,temp);
        intent.putExtra(InstachkServices.OTHER_MESSAGE,message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


}



