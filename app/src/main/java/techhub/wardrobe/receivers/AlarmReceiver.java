package techhub.wardrobe.receivers;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import techhub.wardrobe.R;
import techhub.wardrobe.database.SQLiteHelper;
import techhub.wardrobe.ui.WardrobeActivity;

public class AlarmReceiver extends BroadcastReceiver {

    /**
     * This method gets called when alarm is started
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        Log.e("Alarm", "Triggered");
        if (new SQLiteHelper(context).getLastRecord() != null) {
            createAlarmNotification(context);
        }
        wl.release();
    }

    /**
     * This function sets alarm
     *
     * @param context
     */
    public void setAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        Calendar firingCal = Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();

        //Setting alarm at 6 AM
        firingCal.set(Calendar.HOUR_OF_DAY, 6);
        firingCal.set(Calendar.MINUTE, 0);
        firingCal.set(Calendar.SECOND, 0);

        long intendedTime = firingCal.getTimeInMillis();
        long currentTime = currentCal.getTimeInMillis();

        if (intendedTime >= currentTime) {
            //set from today
            am.setRepeating(AlarmManager.RTC_WAKEUP, intendedTime, AlarmManager.INTERVAL_DAY, pi);
        } else {
            //set from next day
            firingCal.add(Calendar.DAY_OF_MONTH, 1);
            intendedTime = firingCal.getTimeInMillis();

            am.setRepeating(AlarmManager.RTC_WAKEUP, intendedTime, AlarmManager.INTERVAL_DAY, pi);
        }
    }

    /**
     * This function notify user about new look
     *
     * @param context
     */
    private void createAlarmNotification(Context context) {
        // Prepare intent which is triggered if the notification is selected
        try {
            Intent intent = new Intent(context, WardrobeActivity.class);
            intent.putExtra("fromNotification", true);
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Build notification
            Notification noti = new Notification.Builder(context)
                    .setContentTitle("Wardrobe")
                    .setContentText("New Look Found").setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pIntent).setOnlyAlertOnce(true).build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // hide the notification after its selected
            noti.flags |= Notification.FLAG_AUTO_CANCEL;
            noti.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;
            notificationManager.notify(0, noti);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}