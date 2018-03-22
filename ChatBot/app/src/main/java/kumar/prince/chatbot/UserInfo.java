package kumar.prince.chatbot;

/**
 * Created by prince on 22/3/18.
 */

public class UserInfo {
    private String name;
    private String type;
    private String email;

    public UserInfo(String name, String type, String email) {
        this.name = name;
        this.type = type;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
