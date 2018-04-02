package kumar.prince.instachksafetylib;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.graphics.PixelFormat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by prince on 28/3/18.
 */

public class AlertMessageServices {
    Context context;
    private WindowManager windowManager;

    public AlertMessageServices(Context context) {
        this.context = context;
    }

    public AlertDialog getAlert(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Test dialog");
      //  builder.setIcon(R.drawable.icon);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                //your code
            }
        });
            builder.setNegativeButton("Close",new DialogInterface.OnClickListener()

            {
                public void onClick(DialogInterface dialog,int whichButton){
                dialog.dismiss();
            }
            });

        final AlertDialog dialog = builder.create();
        final Window dialogWindow = dialog.getWindow();
        final WindowManager.LayoutParams dialogWindowAttributes = dialogWindow.getAttributes();

// Set fixed width (280dp) and WRAP_CONTENT height
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogWindowAttributes);
        lp.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 280, context.getResources().getDisplayMetrics());
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);

// Set to TYPE_SYSTEM_ALERT so that the Service can display it
        dialogWindow.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        //dialogWindowAttributes.windowAnimations = R.style.DialogAnimation;


          /*  AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);*/
        //alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            return dialog;
           // alert.show();

        }
    }
