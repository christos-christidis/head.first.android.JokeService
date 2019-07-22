package com.hfad.jokeservice;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

// SOS: A service normally doesn't run on a separate thread, only IntentService does! When we start
// a service, Android checks to see if one's already running, otherwise it creates it (calling its
// onCreate etc). A service is a good way to expose functionality to other apps (here I set exported=
// false, so only my app can use it). There are 2 ways to use a service: start it like I do here, OR
// bind to it if I want to call various methods on it later.
public class DelayedMessageService extends IntentService {

    private static final String EXTRA_MESSAGE = "message";
    private static final int NOTIFICATION_ID = 5453;

    static Intent newIntent(Context context) {
        Intent intent = new Intent(context, DelayedMessageService.class);
        intent.putExtra(EXTRA_MESSAGE, context.getString(R.string.response));
        return intent;
    }

    public DelayedMessageService() {
        super("DelayedMessageService");
    }

    // SOS: IntentService runs on a single background thread so all calls to onHandleIntent are
    // serialized (ie if I'd exported the service and another app used it too, I'd not need to sync
    // anything).
    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String text = intent.getStringExtra(EXTRA_MESSAGE);
        showNotification(text);
    }

    private void showNotification(String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "MY_CHANNEL_ID")
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setContentTitle(getString(R.string.question))
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{0, 1000})
                .setAutoCancel(true);

        // SOS: PendingIntent is used when sth has to be started by the system. Here, the system will
        // open this app when I click on the not/ion. Which is why I use pending-intent's getActivity.
        // (there are also getBroadcast, getService etc depending on what must be started)
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
