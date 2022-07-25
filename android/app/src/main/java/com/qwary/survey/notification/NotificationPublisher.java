package com.qwary.survey.notification;

import static android.provider.BaseColumns._ID;
import static com.qwary.survey.notification.Notification.NotificationEntry.NOTIFICATION_ACTIONS;
import static com.qwary.survey.notification.Notification.NotificationEntry.NOTIFICATION_ACTIONS_COLLAPSE;
import static com.qwary.survey.notification.Notification.NotificationEntry.NOTIFICATION_ACTIONS_DISMISS;
import static com.qwary.survey.notification.Notification.NotificationEntry.NOTIFICATION_ACTIONS_TEXT;
import static com.qwary.survey.notification.Notification.NotificationEntry.NOTIFICATION_COLOR;
import static com.qwary.survey.notification.Notification.NotificationEntry.NOTIFICATION_CONTENT_TEXT;
import static com.qwary.survey.notification.Notification.NotificationEntry.NOTIFICATION_DSTART;
import static com.qwary.survey.notification.Notification.NotificationEntry.NOTIFICATION_LARGE_ICON;
import static com.qwary.survey.notification.Notification.NotificationEntry.NOTIFICATION_LED_COLOR;
import static com.qwary.survey.notification.Notification.NotificationEntry.NOTIFICATION_LOADER_TEXT;
import static com.qwary.survey.notification.Notification.NotificationEntry.NOTIFICATION_MODAL_TEXT;
import static com.qwary.survey.notification.Notification.NotificationEntry.NOTIFICATION_RRULE;
import static com.qwary.survey.notification.Notification.NotificationEntry.NOTIFICATION_SMALL_ICON;
import static com.qwary.survey.notification.Notification.NotificationEntry.NOTIFICATION_TITLE_TEXT;
import static com.qwary.survey.notification.Notification.NotificationEntry.NOTIFICATION_TOKEN_TEXT;
import static com.qwary.survey.notification.Notification.NotificationEntry.TABLE_NAME;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.qwary.survey.activity.SurveyActivity;

/**
 * Created by jbonk on 6/16/2018.
 */

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification_id";

    @SuppressLint({"Range", "NotificationTrampoline"})
    @Override
    public void onReceive(final Context context, Intent intent) {
        String notificationId = intent.getStringExtra(NOTIFICATION_ID);
        com.qwary.survey.notification.Notification.NotificationDBHelper mDbHelper = new com.qwary.survey.notification.Notification.NotificationDBHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Cursor data = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+_ID+" = "+notificationId,null);
        data.moveToFirst();
        String title = data.getString(data.getColumnIndex(NOTIFICATION_TITLE_TEXT));
        String token = data.getString(data.getColumnIndex(NOTIFICATION_TOKEN_TEXT));
        int loader = data.getInt(data.getColumnIndex(NOTIFICATION_LOADER_TEXT));
        int modal = data.getInt(data.getColumnIndex(NOTIFICATION_MODAL_TEXT));
        String content = data.getString(data.getColumnIndex(NOTIFICATION_CONTENT_TEXT));
        String rrule = data.getString(data.getColumnIndex(NOTIFICATION_RRULE));
        long dstart = data.getLong(data.getColumnIndex(NOTIFICATION_DSTART));
        String str_actions = data.getString(data.getColumnIndex(NOTIFICATION_ACTIONS));
        String str_actions_text = data.getString(data.getColumnIndex(NOTIFICATION_ACTIONS_TEXT));
        String str_actions_dismiss = data.getString(data.getColumnIndex(NOTIFICATION_ACTIONS_DISMISS));
        String str_actions_collapse = data.getString(data.getColumnIndex(NOTIFICATION_ACTIONS_COLLAPSE));
        int led_color = data.getInt(data.getColumnIndex(NOTIFICATION_LED_COLOR));
        int small_icon = data.getInt(data.getColumnIndex(NOTIFICATION_SMALL_ICON));
        int large_icon = data.getInt(data.getColumnIndex(NOTIFICATION_LARGE_ICON));
        int color = data.getInt(data.getColumnIndex(NOTIFICATION_COLOR));
        String[] actions = NotifyMe.convertStringToArray(str_actions);
        String[] actions_text = NotifyMe.convertStringToArray(str_actions_text);
        String[] actions_dismiss = NotifyMe.convertStringToArray(str_actions_dismiss);
        String[] actions_collapse = NotifyMe.convertStringToArray(str_actions_collapse);
        data.close();
        db.close();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,notificationId);
        if(small_icon != -1) {
            mBuilder.setSmallIcon(small_icon);
        }else{
            mBuilder.setSmallIcon(android.R.drawable.ic_menu_agenda);
        }
        if(large_icon != -1) {
            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), large_icon);
            mBuilder.setLargeIcon(largeIcon);
        }
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(content);
        mBuilder.setColor(color);
        mBuilder.setVibrate(new long[] { 1000,1000,1000 });
        for (int i = 0; i < actions.length; i++) {
            try {
                Intent tent = new Intent(context, ActionReceiver.class);
                tent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                tent.putExtra("_id",notificationId);
                tent.putExtra("rrule",rrule);
                tent.putExtra("dstart",dstart);
                tent.putExtra("index",i);
                tent.putExtra("action",actions[i]);
                tent.putExtra("collapse",Boolean.parseBoolean(actions_collapse[i]));
                tent.putExtra("dismiss",Boolean.parseBoolean(actions_dismiss[i]));
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,Integer.parseInt(notificationId)*3+i,tent,PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.addAction(android.R.drawable.ic_menu_agenda,actions_text[i],pendingIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(uri);
        Intent deleteIntent = new Intent(context,DeletePendingIntent.class);
        deleteIntent.putExtra("_id",notificationId);
        deleteIntent.putExtra("rrule",rrule);
        deleteIntent.putExtra("dstart",dstart);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,Integer.parseInt(notificationId),deleteIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setDeleteIntent(pendingIntent);
        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(context, SurveyActivity.class);
        resultIntent.putExtra("domain", title);
        resultIntent.putExtra("token", token);
        boolean mLoader = loader == 1;
        resultIntent.putExtra("loader", mLoader);
        boolean mModal = modal == 1;
        resultIntent.putExtra("modal", mModal);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        Notification notification = mBuilder.build();

        notification.ledARGB = led_color;
        notification.flags = Notification.FLAG_SHOW_LIGHTS;
        notification.ledOnMS = 300;
        notification.ledOffMS = 1000;
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(notificationId,notificationId,NotificationManager.IMPORTANCE_HIGH);
            nc.enableLights(true);
            nc.setLightColor(led_color);
            mNotificationManager.createNotificationChannel(nc);
        }

        mNotificationManager.notify(Integer.parseInt(notificationId), notification);
    }


}