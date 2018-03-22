package kumar.prince.chatbot;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by prince on 21/3/18.
 */

public class InitApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
