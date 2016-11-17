package techhub.wardrobe.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This class helps to receive phone reboot event for setting alarm
 */
public class PhoneBootReceiver extends BroadcastReceiver {

    private AlarmReceiver alarmReceiver = new AlarmReceiver();

    /**
     * This function gets call when phone reboot event is received
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            alarmReceiver.setAlarm(context);
        }
    }
}