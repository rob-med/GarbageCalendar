package eu.pinnoo.garbagecalendar.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import eu.pinnoo.garbagecalendar.R;
import eu.pinnoo.garbagecalendar.util.GarbageCollection;
import eu.pinnoo.garbagecalendar.util.GarbageType;
import eu.pinnoo.garbagecalendar.util.LocalConstants;
import eu.pinnoo.garbagecalendar.view.MainActivity;

/**
 *
 * @author Wouter Pinnoo <pinnoo.wouter@gmail.com>
 */
public class NotificationRaiser extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = context.getString(R.string.putOutTrash);
        String msg = "";
        try {
            GarbageCollection col = (GarbageCollection) intent.getSerializableExtra("collection");
            
            msg += "(" + LocalConstants.getDateFormatter(LocalConstants.DateFormatType.SHORT_WEEKDAY, context).format(col.getDate()) + ") ";
            
            GarbageType[] types = col.getTypes();
            for (int i = 0; i < types.length; i++) {
                msg += types[i].shortStrValue(context);
                if (i < types.length - 1) {
                    msg += " - ";
                }
            }
        } catch (ClassCastException e) {
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent toStart = new Intent(context.getApplicationContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, toStart, 0);
        Notification notif = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(contentIntent)
                .build();
        notif.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notif);
    }
}
