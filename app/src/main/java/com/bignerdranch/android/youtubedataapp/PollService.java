package com.bignerdranch.android.youtubedataapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;

public class PollService extends IntentService {

    private static final String TAG = "PollService";

//    private static final long POLL_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    private static final long POLL_INTERVAL = 1000 * 60;
    public static final String ACTION_SHOW_NOTIFICATION = "com.bignerdranch.android.photogallery.SHOW_NOTIFICATION";
    public static final String PERM_PRIVATE = "com.bignerdranch.android.photogallery.PRIVATE";
    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), POLL_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("DEBUG", "OnHandleIntent NetworkAvailableAndConnected " + isNetworkAvailableAndConnected());
        if (!isNetworkAvailableAndConnected()) {
            return;
        }
        new YouTubeAsync().updateVideos(getApplicationContext());
        checkVideos(getApplicationContext());
    }

    private void checkVideos(Context context){
        DataLab dataLab = DataLab.get(context);
        List<VideoItem> items = dataLab.getVideos();
        for (VideoItem item : items){
            if(item.getGoal() <= 0) {
                continue;
            }
            if(item.getGoal() <= item.getLikesCount()){
                showNotification("One of your videos has reached its goal!", item.getTitle(), (int)item.getId());
            }
        }
    }

    private void showNotification(String title, String message, int id){
        NotificationHelper mNotificationHelper = new NotificationHelper(getApplicationContext());
        NotificationCompat.Builder nb = mNotificationHelper.getChannelNotification(title, message);
        nb.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        mNotificationHelper.getManager().notify(id, nb.build());
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }

}
