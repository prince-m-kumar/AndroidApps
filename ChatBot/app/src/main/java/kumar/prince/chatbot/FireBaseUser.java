package kumar.prince.chatbot;

import java.util.Map;

/**
 * Created by prince on 22/3/18.
 */

public class FireBaseUser {
    Map<String,UserInfo> data;


    public FireBaseUser(Map<String, UserInfo> data) {
        this.data = data;
    }


    public Map<String, UserInfo> getData() {
        return data;
    }

    public void setData(Map<String, UserInfo> data) {
        this.data = data;
    }
}
