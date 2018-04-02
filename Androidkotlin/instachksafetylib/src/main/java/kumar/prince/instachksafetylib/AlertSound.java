package kumar.prince.instachksafetylib;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by prince on 28/3/18.
 */

public class AlertSound {
    MediaPlayer mPlayer;
    Context context;

    public AlertSound(Context context) {
        this.context = context;
    }

    public void playAlertSound(int audioResources){
        if (mPlayer!=null)
            mPlayer=null;
        mPlayer = MediaPlayer.create(context,audioResources);//Create MediaPlayer object with MP3 file under res/raw folder
        mPlayer.start();
    }


    public void stopAlertSound(){
        if(mPlayer!=null && mPlayer.isPlaying()){//If music is playing already
            mPlayer.stop();//Stop playing the music
        }

    }
}
