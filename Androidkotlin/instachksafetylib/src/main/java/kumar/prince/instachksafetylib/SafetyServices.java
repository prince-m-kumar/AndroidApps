package kumar.prince.instachksafetylib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * @author prince
 * Responsible for start services and stop services
 *
 */

public class SafetyServices implements Data.onGpsServiceUpdate {
    Context context;
    private static Data data;
    private ServiceAlertInterface alertInterface;
    /**
     * @param context current base context for start services
     */
    public SafetyServices(Context context) {
        this.context = context;
        initGpsData();
    }

    public SafetyServices(Context context, ServiceAlertInterface alertInterface) {
        this.context = context;
        this.alertInterface = alertInterface;
        initGpsData();
    }

    /**
     * Start All Safety services which run in background
     */
    public void startSafetyServices(){
        if (context!=null){
            LocalBroadcastManager.getInstance(context).registerReceiver(bReceiver, new IntentFilter("message"));
            context.startService(new Intent(context, SpeedoMetersServices.class));
        }

    }


    /**
     * init gps basics data when car start moving
     */
    private void initGpsData(){
        data = new Data(this);

        data.setRunning(true);
        data.setFirstTime(true);
    }

    /**
     * Stop all Safety Services
     */
    public void stopSafetyServices(){
        if (context!=null)
            LocalBroadcastManager.getInstance(context).unregisterReceiver(bReceiver);
        //context.stopService(new Intent(context, SpeedoMetersServices.class));
    }

    public static Data getData() {
        if (data==null){
            data = new Data(new Data.onGpsServiceUpdate() {
                @Override
                public void update() {

                }
            });

            data.setRunning(true);
            data.setFirstTime(true);
        }

        return data;
    }

    @Override
    public void update() {

    }

    private BroadcastReceiver  bReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Data","Message recived");
            int alertType=intent.getIntExtra(InstachkServices.ALERT_TYPE,0);
            if (alertType==InstachkServices.BATTERY_HEAT_ALERT){
                Log.i("Data","Battery Health Alert");
                float temp=intent.getFloatExtra(InstachkServices.TEMPERATURE_VALUE,0);
                String message=intent.getStringExtra(InstachkServices.OTHER_MESSAGE);
                alertInterface.batteryOverHeating(message,temp);

            }else if (alertType==InstachkServices.SPEED_LIMIT_ALERT){

                Log.i("Data","Speed Limit alert");
                float speed=intent.getFloatExtra(InstachkServices.CURRENT_SPEED,0);
                String message=intent.getStringExtra(InstachkServices.OTHER_MESSAGE);
                alertInterface.overSpeed(message,speed);

            }else if (alertType==InstachkServices.PHONE_SHAKE_ALERT){
                Log.i("Data","Phone Shake alert");
                String message=intent.getStringExtra(InstachkServices.OTHER_MESSAGE);
                alertInterface.phoneShake(message);

            }else {

            }




            //put here whaterver you want your activity to do with the intent received
        }
    };
}
