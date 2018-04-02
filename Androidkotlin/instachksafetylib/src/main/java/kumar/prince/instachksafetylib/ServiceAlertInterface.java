package kumar.prince.instachksafetylib;

/**
 * Created by prince on 2/4/18.
 */

public interface ServiceAlertInterface {

    void overSpeed(String message,float speed);

    void phoneShake(String message);

    void batteryOverHeating(String message,float temp);



}
