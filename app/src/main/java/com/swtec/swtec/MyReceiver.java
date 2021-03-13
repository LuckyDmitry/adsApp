package com.swtec.swtec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.os.PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() != null) {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (intent.getAction().equals(ACTION_DEVICE_IDLE_MODE_CHANGED) ||
                    intent.getAction().equals(Intent.ACTION_SCREEN_OFF) ||
                    intent.getAction().equals(Intent.ACTION_POWER_CONNECTED) ||
                    intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

                Log.d("MyReceiver", intent.getAction());
                context.startActivity(mainActivityIntent);
            }
        }
    }
}
