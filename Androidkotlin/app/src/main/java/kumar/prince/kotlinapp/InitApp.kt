package kumar.prince.kotlinapp

import android.app.Application
import android.content.Context
import android.util.Log

/**
 * Created by prince on 6/3/18.
 */
class InitApp : Application(){
    var cont: Context?=null

    private val tag: String?="InitApp"

    override fun onCreate() {
        super.onCreate()
        cont=applicationContext
        Log.v(tag,"On Create App")
    }
}