package kumar.prince.chatbot;

/**
 * Created by prince on 21/3/18.
 */

public class ChatMessage {
    private String msgText;
    private String msgUser;
    private long msgTime;



    public ChatMessage(String msgText, String msgUser,long msgTime){
        this.msgText = msgText;
        this.msgUser = msgUser;
        this.msgTime=msgTime;

    }


    public ChatMessage(){

    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public String getMsgUser() {
        return msgUser;
    }

    public void setMsgUser(String msgUser) {
        this.msgUser = msgUser;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }
}
